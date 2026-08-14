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
import java.util.Date;
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
    private boolean isDarkTheme = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Загружаем сохраненную тему ДО создания Activity
        prefs = getSharedPreferences("settings", MODE_PRIVATE);
        isDarkTheme = prefs.getBoolean("dark_theme", false);

        // Применяем тему через делегат
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

        // Устанавливаем состояние кнопки
        themeToggle.setChecked(isDarkTheme);
        themeToggle.setTextOn("🌙");
        themeToggle.setTextOff("☀️");

        // Обработчик переключения темы
        themeToggle.setOnCheckedChangeListener((buttonView, isChecked) -> {
            prefs.edit().putBoolean("dark_theme", isChecked).apply();
            // Перезапускаем Activity для применения темы
            recreate();
        });

        adapter = new NewsAdapter(this, newsList);
        newsListView.setAdapter(adapter);

        // Загружаем новости
        loadNews();

        // Обработчик клика по новости
        newsListView.setOnItemClickListener((parent, view, position, id) -> {
            NewsItem item = newsList.get(position);
            Intent intent = new Intent(MainActivity.this, WebViewActivity.class);
            intent.putExtra("url", "https://ganjo1st.github.io/NovikonApp/post/" + item.updateId);
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
                SimpleDateFormat sdf = new SimpleDateFormat("dd MMM yyyy HH:mm", new Locale("ru"));

                for (int i = 0; i < result.length(); i++) {
                    JSONObject post = result.getJSONObject(i);
                    JSONObject channelPost = post.getJSONObject("channel_post");
                    String caption = channelPost.getString("caption");
                    int date = channelPost.getInt("date");

                    String title = caption.split("\n")[0];
                    String fullText = caption;
                    String description = caption.length() > 150 ? caption.substring(0, 150) + "..." : caption;

                    // Получаем фото
                    String photoUrl = null;
                    if (channelPost.has("photo")) {
                        JSONArray photos = channelPost.getJSONArray("photo");
                        if (photos.length() > 0) {
                            JSONObject lastPhoto = photos.getJSONObject(photos.length() - 1);
                            // Используем picsum.photos для генерации картинки по ID новости
                            photoUrl = "https://picsum.photos/seed/" + post.getInt("update_id") + "/800/400";
                        }
                    }

                    NewsItem item = new NewsItem(
                            post.getInt("update_id"),
                            title,
                            description,
                            fullText,
                            date,
                            photoUrl,
                            caption
                    );
                    newsList.add(item);
                }

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
