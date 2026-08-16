package com.novikon.app;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.Toolbar;
import com.bumptech.glide.Glide;
import org.json.JSONArray;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ArticleActivity extends AppCompatActivity {
    private TextView titleView, textView, dateView, viewsView;
    private ImageView imageView;
    private Button likeBtn, dislikeBtn;
    private ExecutorService executor = Executors.newSingleThreadExecutor();
    private Handler mainHandler = new Handler(Looper.getMainLooper());
    private int updateId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        SharedPreferences prefs = getSharedPreferences("settings", MODE_PRIVATE);
        boolean isDarkTheme = prefs.getBoolean("dark_theme", false);
        if (isDarkTheme) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        }

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_article);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Новость");
        }

        titleView = findViewById(R.id.articleTitle);
        textView = findViewById(R.id.articleText);
        dateView = findViewById(R.id.articleDate);
        viewsView = findViewById(R.id.articleViews);
        imageView = findViewById(R.id.articleImage);
        likeBtn = findViewById(R.id.likeBtn);
        dislikeBtn = findViewById(R.id.dislikeBtn);

        // Получаем ID статьи из Intent
        updateId = getIntent().getIntExtra("updateId", 0);

        if (updateId == 0) {
            Toast.makeText(this, "Ошибка: ID статьи не найден", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Загружаем полные данные статьи
        loadArticle(updateId);

        likeBtn.setOnClickListener(v -> Toast.makeText(this, "❤️ Спасибо за лайк!", Toast.LENGTH_SHORT).show());
        dislikeBtn.setOnClickListener(v -> Toast.makeText(this, "👎 Спасибо за отзыв!", Toast.LENGTH_SHORT).show());
    }

    private void loadArticle(int id) {
        executor.execute(() -> {
            try {
                URL url = new URL("https://ganjo1st.github.io/NovikonApp/data/news.json");
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");
                connection.setConnectTimeout(10000);
                connection.setReadTimeout(10000);
                connection.setRequestProperty("Cache-Control", "no-cache");

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

                // Ищем статью с нужным ID
                for (int i = 0; i < result.length(); i++) {
                    JSONObject post = result.getJSONObject(i);
                    if (post.getInt("update_id") == id) {
                        JSONObject channelPost = post.getJSONObject("channel_post");
                        String caption = channelPost.getString("caption");
                        int date = channelPost.getInt("date");

                        String title = caption.split("\n")[0];
                        String fullText = caption;

                        // Получаем фото
                        String photoUrl = null;
                        if (channelPost.has("photo")) {
                            JSONArray photos = channelPost.getJSONArray("photo");
                            if (photos.length() > 0) {
                                JSONObject lastPhoto = photos.getJSONObject(photos.length() - 1);
                                photoUrl = "https://picsum.photos/seed/" + id + "/800/400";
                            }
                        }

                        final String finalTitle = title;
                        final String finalFullText = fullText;
                        final String finalPhotoUrl = photoUrl;
                        final int finalDate = date;

                        mainHandler.post(() -> displayArticle(finalTitle, finalFullText, finalPhotoUrl, finalDate));
                        return;
                    }
                }

                mainHandler.post(() -> {
                    Toast.makeText(this, "Статья не найдена", Toast.LENGTH_SHORT).show();
                    finish();
                });

            } catch (Exception e) {
                e.printStackTrace();
                mainHandler.post(() -> {
                    Toast.makeText(this, "Ошибка загрузки статьи", Toast.LENGTH_SHORT).show();
                    finish();
                });
            }
        });
    }

    private void displayArticle(String title, String fullText, String photoUrl, int date) {
        titleView.setText(title != null ? title : "Без названия");
        textView.setText(fullText != null ? fullText : "Текст не доступен");

        if (date > 0) {
            SimpleDateFormat sdf = new SimpleDateFormat("dd MMM yyyy HH:mm", new Locale("ru"));
            dateView.setText(sdf.format(new Date(date * 1000L)));
        }

        if (photoUrl != null && !photoUrl.isEmpty()) {
            Glide.with(this).load(photoUrl).into(imageView);
            imageView.setVisibility(ImageView.VISIBLE);
        } else {
            imageView.setVisibility(ImageView.GONE);
        }

        int views = (int) (Math.random() * 100) + 10;
        viewsView.setText("👁 " + views);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
