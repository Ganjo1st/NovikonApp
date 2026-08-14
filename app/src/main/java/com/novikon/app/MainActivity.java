package com.novikon.app;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.ToggleButton;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import org.json.JSONArray;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Locale;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MainActivity extends AppCompatActivity {
    private ListView newsListView;
    private ProgressBar progressBar;
    private ToggleButton themeToggle;
    private NewsAdapter adapter;
    private ArrayList<NewsItem> newsList = new ArrayList<>();
    private ExecutorService executor = Executors.newSingleThreadExecutor();
    private Handler mainHandler = new Handler(Looper.getMainLooper());
    private SharedPreferences prefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Загружаем тему
        prefs = getSharedPreferences("settings", MODE_PRIVATE);
        boolean isDarkTheme = prefs.getBoolean("dark_theme", false);
        if (isDarkTheme) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        }

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        newsListView = findViewById(R.id.newsListView);
        progressBar = findViewById(R.id.progressBar);
        themeToggle = findViewById(R.id.themeToggle);

        themeToggle.setChecked(isDarkTheme);
        themeToggle.setTextOn("🌙");
        themeToggle.setTextOff("☀️");

        themeToggle.setOnCheckedChangeListener((buttonView, isChecked) -> {
            prefs.edit().putBoolean("dark_theme", isChecked).apply();
            recreate();
        });

        adapter = new NewsAdapter(this, newsList);
        newsListView.setAdapter(adapter);

        loadNews();

        newsListView.setOnItemClickListener((parent, view, position, id) -> {
            NewsItem item = newsList.get(position);
            Intent intent = new Intent(MainActivity.this, WebViewActivity.class);
            // Используем index.html с параметром post
            String postUrl = "https://ganjo1st.github.io/NovikonApp/index.html?post=" + item.updateId;
            intent.putExtra("url", postUrl);
            intent.putExtra("title", item.title);
            startActivity(intent);
        });
    }

    private void loadNews() {
        progressBar.setVisibility(ProgressBar.VISIBLE);
        executor.execute(() -> {
            try {
                URL url = new URL("https://ganjo1st.github.io/NovikonApp/data/news.json");
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");
                connection.setConnectTimeout(10000);
                connection.setReadTimeout(10000);
                connection.setRequestProperty("Cache-Control", "no-cache");

                int responseCode = connection.getResponseCode();
                if (responseCode != 200) {
                    throw new Exception("HTTP error: " + responseCode);
                }

                BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                StringBuilder response = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    response.append(line);
                }
                reader.close();
                connection.disconnect();

                JSONObject json = new JSONObject(response.toString());
                JSONObject data = json.getJSONObject("data");
                JSONArray result = data.getJSONArray("result");

                newsList.clear();

                for (int i = 0; i < result.length(); i++) {
                    JSONObject post = result.getJSONObject(i);
                    JSONObject channelPost = post.getJSONObject("channel_post");
                    String caption = channelPost.getString("caption");
                    int date = channelPost.getInt("date");

                    String title = caption.split("\n")[0];
                    String description = caption.length() > 150 ? caption.substring(0, 150) + "..." : caption;

                    String photoUrl = null;
                    if (channelPost.has("photo")) {
                        JSONArray photos = channelPost.getJSONArray("photo");
                        if (photos.length() > 0) {
                            JSONObject lastPhoto = photos.getJSONObject(photos.length() - 1);
                            photoUrl = "https://picsum.photos/seed/" + post.getInt("update_id") + "/800/400";
                        }
                    }

                    NewsItem item = new NewsItem(
                            post.getInt("update_id"),
                            title,
                            description,
                            caption,
                            date,
                            photoUrl,
                            caption
                    );
                    newsList.add(item);
                }

                // Сортировка: сначала свежие (по убыванию date)
                // Оставляем как есть — данные из Telegram уже в правильном порядке
                // Но на всякий случай сортируем вручную
                newsList.sort((a, b) -> Integer.compare(b.date, a.date));

                mainHandler.post(() -> {
                    adapter.notifyDataSetChanged();
                    progressBar.setVisibility(ProgressBar.GONE);
                });

            } catch (Exception e) {
                Log.e("Novikon", "Ошибка загрузки новостей", e);
                mainHandler.post(() -> {
                    progressBar.setVisibility(ProgressBar.GONE);
                    Toast.makeText(MainActivity.this, "Не удалось загрузить новости", Toast.LENGTH_LONG).show();
                });
            }
        });
    }
}
