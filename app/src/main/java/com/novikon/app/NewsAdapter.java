package com.novikon.app;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import com.bumptech.glide.Glide;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class NewsAdapter extends ArrayAdapter<NewsItem> {
    private Context context;
    private List<NewsItem> items;
    private boolean isDarkTheme;

    public NewsAdapter(Context context, List<NewsItem> items) {
        super(context, 0, items);
        this.context = context;
        this.items = items;
        SharedPreferences prefs = context.getSharedPreferences("settings", Context.MODE_PRIVATE);
        isDarkTheme = prefs.getBoolean("dark_theme", false);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.news_item, parent, false);
        }

        NewsItem item = items.get(position);

        TextView titleView = convertView.findViewById(R.id.newsTitle);
        TextView descView = convertView.findViewById(R.id.newsDescription);
        TextView dateView = convertView.findViewById(R.id.newsDate);
        ImageView imageView = convertView.findViewById(R.id.newsImage);

        titleView.setText(item.title);
        descView.setText(item.description);

        SimpleDateFormat sdf = new SimpleDateFormat("dd MMM yyyy HH:mm", new Locale("ru"));
        dateView.setText(sdf.format(new Date(item.date * 1000L)));

        // Явно задаем цвета в зависимости от темы
        if (isDarkTheme) {
            // Тёмная тема — белый текст
            titleView.setTextColor(Color.parseColor("#FFFFFF"));
            descView.setTextColor(Color.parseColor("#CCCCCC"));
            dateView.setTextColor(Color.parseColor("#888888"));
            // Фон элемента — тёмный
            convertView.setBackgroundColor(Color.parseColor("#1A1A2E"));
        } else {
            // Светлая тема — чёрный текст
            titleView.setTextColor(Color.parseColor("#1A1A2E"));
            descView.setTextColor(Color.parseColor("#4A4A5A"));
            dateView.setTextColor(Color.parseColor("#8A8A9A"));
            // Фон элемента — светлый
            convertView.setBackgroundColor(Color.parseColor("#FFFFFF"));
        }

        // Загружаем картинку
        if (item.photoUrl != null && !item.photoUrl.isEmpty()) {
            Glide.with(context)
                    .load(item.photoUrl)
                    .into(imageView);
            imageView.setVisibility(View.VISIBLE);
        } else {
            imageView.setVisibility(View.GONE);
        }

        return convertView;
    }
}
