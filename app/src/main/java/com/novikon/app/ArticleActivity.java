package com.novikon.app;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.Toolbar;
import com.bumptech.glide.Glide;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class ArticleActivity extends AppCompatActivity {
    private TextView titleView, textView, dateView, viewsView;
    private ImageView imageView;
    private Button likeBtn, dislikeBtn;
    private int updateId;
    private int views = 0;
    private boolean isLiked = false;
    private boolean isDisliked = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Применяем тему
        SharedPreferences prefs = getSharedPreferences("settings", MODE_PRIVATE);
        boolean isDarkTheme = prefs.getBoolean("dark_theme", false);
        if (isDarkTheme) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        }

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_article);

        // Настройка Toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Новость");
        }

        // Инициализация элементов
        titleView = findViewById(R.id.articleTitle);
        textView = findViewById(R.id.articleText);
        dateView = findViewById(R.id.articleDate);
        viewsView = findViewById(R.id.articleViews);
        imageView = findViewById(R.id.articleImage);
        likeBtn = findViewById(R.id.likeBtn);
        dislikeBtn = findViewById(R.id.dislikeBtn);

        // Получаем данные из Intent
        String title = getIntent().getStringExtra("title");
        String fullText = getIntent().getStringExtra("fullText");
        String photoUrl = getIntent().getStringExtra("photoUrl");
        int date = getIntent().getIntExtra("date", 0);
        updateId = getIntent().getIntExtra("updateId", 0);

        // Устанавливаем данные
        titleView.setText(title != null ? title : "Без названия");
        textView.setText(fullText != null ? fullText : "Текст не доступен");

        if (date > 0) {
            SimpleDateFormat sdf = new SimpleDateFormat("dd MMM yyyy HH:mm", new Locale("ru"));
            dateView.setText(sdf.format(new Date(date * 1000L)));
        }

        // Загружаем картинку
        if (photoUrl != null && !photoUrl.isEmpty()) {
            Glide.with(this)
                    .load(photoUrl)
                    .into(imageView);
            imageView.setVisibility(View.VISIBLE);
        } else {
            imageView.setVisibility(View.GONE);
        }

        // Счетчик просмотров
        views = (int) (Math.random() * 100) + 10; // Имитация просмотров
        viewsView.setText("👁 " + views);

        // Обработчики реакций
        likeBtn.setOnClickListener(v -> {
            if (isLiked) {
                isLiked = false;
                likeBtn.setBackgroundResource(android.R.drawable.btn_default);
                Toast.makeText(this, "Лайк снят", Toast.LENGTH_SHORT).show();
            } else {
                isLiked = true;
                isDisliked = false;
                likeBtn.setBackgroundResource(android.R.drawable.btn_star_big_on);
                dislikeBtn.setBackgroundResource(android.R.drawable.btn_default);
                Toast.makeText(this, "❤️ Спасибо за лайк!", Toast.LENGTH_SHORT).show();
            }
        });

        dislikeBtn.setOnClickListener(v -> {
            if (isDisliked) {
                isDisliked = false;
                dislikeBtn.setBackgroundResource(android.R.drawable.btn_default);
                Toast.makeText(this, "Дизлайк снят", Toast.LENGTH_SHORT).show();
            } else {
                isDisliked = true;
                isLiked = false;
                dislikeBtn.setBackgroundResource(android.R.drawable.btn_star_big_on);
                likeBtn.setBackgroundResource(android.R.drawable.btn_default);
                Toast.makeText(this, "👎 Спасибо за отзыв!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
