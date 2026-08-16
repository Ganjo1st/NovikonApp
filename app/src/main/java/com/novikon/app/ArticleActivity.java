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

        TextView titleView = findViewById(R.id.articleTitle);
        TextView textView = findViewById(R.id.articleText);
        TextView dateView = findViewById(R.id.articleDate);
        TextView viewsView = findViewById(R.id.articleViews);
        ImageView imageView = findViewById(R.id.articleImage);
        Button likeBtn = findViewById(R.id.likeBtn);
        Button dislikeBtn = findViewById(R.id.dislikeBtn);

        String title = getIntent().getStringExtra("title");
        String fullText = getIntent().getStringExtra("fullText");
        String photoUrl = getIntent().getStringExtra("photoUrl");
        int date = getIntent().getIntExtra("date", 0);

        titleView.setText(title != null ? title : "Без названия");
        textView.setText(fullText != null ? fullText : "Текст не доступен");

        if (date > 0) {
            SimpleDateFormat sdf = new SimpleDateFormat("dd MMM yyyy HH:mm", new Locale("ru"));
            dateView.setText(sdf.format(new Date(date * 1000L)));
        }

        if (photoUrl != null && !photoUrl.isEmpty()) {
            Glide.with(this)
                    .load(photoUrl)
                    .placeholder(R.drawable.ic_launcher)
                    .into(imageView);
            imageView.setVisibility(View.VISIBLE);
        } else {
            imageView.setVisibility(View.GONE);
        }

        int views = (int) (Math.random() * 100) + 10;
        viewsView.setText("👁 " + views);

        likeBtn.setOnClickListener(v -> Toast.makeText(this, "❤️ Спасибо за лайк!", Toast.LENGTH_SHORT).show());
        dislikeBtn.setOnClickListener(v -> Toast.makeText(this, "👎 Спасибо за отзыв!", Toast.LENGTH_SHORT).show());
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
