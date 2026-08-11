package com.novikon.app;

import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.appbar.MaterialToolbar;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private NewsAdapter adapter;
    private List<NewsItem> newsList = new ArrayList<>();
    private Handler handler = new Handler();

    // Загружаем данные с GitHub Pages (без VPN!)
    private String getNewsUrl() {
        return "https://ganjo1st.github.io/NovikonApp/data/news.json";
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        recyclerView = findViewById(R.id.newsRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new NewsAdapter(newsList);
        recyclerView.setAdapter(adapter);

        loadNews();
        startAutoUpdate();
    }

    private void loadNews() {
        String url = getNewsUrl();
        if (url == null) return;

        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(url)
                .addHeader("Cache-Control", "no-cache")
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                runOnUiThread(() -> {
                    String errorMsg = "Ошибка загрузки: " + e.getMessage();
                    Toast.makeText(MainActivity.this, errorMsg, Toast.LENGTH_LONG).show();
                    Log.e("NovikonApp", "Ошибка загрузки", e);
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    try {
                        String json = response.body().string();
                        Log.d("NovikonApp", "Ответ: " + json);
                        
                        // Парсим новый формат: {"last_update": "...", "data": {...}}
                        JSONObject obj = new JSONObject(json);
                        JSONObject data = obj.getJSONObject("data");
                        JSONArray results = data.getJSONArray("result");

                        newsList.clear();
                        for (int i = 0; i < results.length(); i++) {
                            // Получаем объект сообщения
                            JSONObject update = results.getJSONObject(i);
                            JSONObject message = null;
                            
                            // Проверяем, есть ли поле "message" или "channel_post"
                            if (update.has("message")) {
                                message = update.getJSONObject("message");
                            } else if (update.has("channel_post")) {
                                message = update.getJSONObject("channel_post");
                            } else {
                                continue; // Пропускаем, если нет сообщения
                            }
                            
                            // Получаем текст
                            String text = message.optString("caption", message.optString("text", ""));
                            if (text.isEmpty()) {
                                continue; // Пропускаем, если нет текста
                            }
                            
                            // Получаем название (первые 50 символов)
                            String title = text.length() > 50 ? text.substring(0, 50) + "..." : text;
                            
                            // Получаем просмотры (если есть)
                            int views = message.optInt("views", 0);
                            
                            // Генерируем случайные лайки для примера
                            int likes = (int) (Math.random() * 100);

                            NewsItem item = new NewsItem(title, text, views, likes);
                            newsList.add(item);
                        }

                        runOnUiThread(() -> {
                            if (newsList.isEmpty()) {
                                Toast.makeText(MainActivity.this, "Новостей пока нет", Toast.LENGTH_SHORT).show();
                            }
                            adapter.notifyDataSetChanged();
                        });

                    } catch (Exception e) {
                        Log.e("NovikonApp", "Ошибка парсинга", e);
                        runOnUiThread(() -> Toast.makeText(MainActivity.this, "Ошибка парсинга данных", Toast.LENGTH_LONG).show());
                    }
                } else {
                    runOnUiThread(() -> Toast.makeText(MainActivity.this, "Ошибка сервера: " + response.code(), Toast.LENGTH_LONG).show());
                }
            }
        });
    }

    private void startAutoUpdate() {
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                loadNews();
                handler.postDelayed(this, 300000); // 5 минут
            }
        }, 300000);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        handler.removeCallbacksAndMessages(null);
    }
}