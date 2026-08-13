package com.novikon.app;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import org.json.JSONArray;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MainActivity extends AppCompatActivity {
    private ListView newsListView;
    private ProgressBar progressBar;
    private NewsAdapter adapter;
    private ArrayList<NewsItem> newsList = new ArrayList<>();
    private ExecutorService executor = Executors.newSingleThreadExecutor();
    private Handler mainHandler = new Handler(Looper.getMainLooper());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        newsListView = findViewById(R.id.newsListView);
        progressBar = findViewById(R.id.progressBar);

        adapter = new NewsAdapter(this, newsList);
        newsListView.setAdapter(adapter);

        loadNews();

        newsListView.setOnItemClickListener((parent, view, position, id) -> {
            NewsItem item = newsList.get(position);
            // Открываем статью в WebView
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
                connection.setConnectTimeout(5000);
                connection.setReadTimeout(5000);

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
                    String fullText = caption;
                    String description = caption.length() > 150 ? caption.substring(0, 150) + "..." : caption;

                    // Получаем фото
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
