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
    private static final String TAG = "NovikonApp";

    private String getNewsUrl() {
        // Токен вшит в приложение
        String token = "8832915986:AAHc9q42Ux7RC5t37AtvCsfQMGNd_h5uGAA";
        
        Log.d(TAG, "=== getNewsUrl() вызван ===");
        Log.d(TAG, "Токен: " + token);
        
        if (token == null || token.isEmpty()) {
            Log.e(TAG, "ОШИБКА: Токен пустой!");
            runOnUiThread(() -> Toast.makeText(this, "Ошибка: токен не найден", Toast.LENGTH_LONG).show());
            return null;
        }
        
        String url = "https://api.telegram.org/bot" + token + "/getUpdates?chat_id=@Novikon_news&limit=20";
        Log.d(TAG, "URL: " + url);
        return url;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        Log.d(TAG, "=== onCreate() вызван ===");

        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Log.d(TAG, "Toolbar настроен");

        recyclerView = findViewById(R.id.newsRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new NewsAdapter(newsList);
        recyclerView.setAdapter(adapter);
        Log.d(TAG, "RecyclerView настроен");

        // Добавляем тестовую новость
        newsList.add(new NewsItem(
            "Загрузка...",
            "Пожалуйста, подождите, новости загружаются...",
            0, 0
        ));
        adapter.notifyDataSetChanged();
        Log.d(TAG, "Добавлена тестовая новость");

        loadNews();
        startAutoUpdate();
        Log.d(TAG, "onCreate() завершен");
    }

    private void loadNews() {
        Log.d(TAG, "=== loadNews() вызван ===");
        String url = getNewsUrl();
        if (url == null) {
            Log.e(TAG, "URL == null, выход");
            return;
        }

        Log.d(TAG, "Начинаем загрузку с URL: " + url);
        
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder().url(url).build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.e(TAG, "!!! onFailure: Ошибка сети !!!");
                Log.e(TAG, "Сообщение: " + e.getMessage());
                Log.e(TAG, "Причина: " + e.toString());
                e.printStackTrace();
                
                runOnUiThread(() -> {
                    Toast.makeText(MainActivity.this, "Ошибка сети: " + e.getMessage(), Toast.LENGTH_LONG).show();
                    newsList.clear();
                    newsList.add(new NewsItem(
                        "Ошибка загрузки",
                        "Не удалось подключиться к серверу. Проверьте интернет.\n\nОшибка: " + e.getMessage(),
                        0, 0
                    ));
                    adapter.notifyDataSetChanged();
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                Log.d(TAG, "=== onResponse() вызван ===");
                Log.d(TAG, "Код ответа: " + response.code());
                Log.d(TAG, "Успешный ответ: " + response.isSuccessful());

                if (response.isSuccessful()) {
                    try {
                        String json = response.body().string();
                        Log.d(TAG, "JSON получен, длина: " + json.length() + " символов");
                        Log.d(TAG, "JSON (первые 500 символов): " + json.substring(0, Math.min(500, json.length())));
                        
                        JSONObject obj = new JSONObject(json);
                        Log.d(TAG, "JSONObject создан");
                        
                        boolean ok = obj.getBoolean("ok");
                        Log.d(TAG, "ok: " + ok);
                        
                        if (!ok) {
                            String error = obj.getString("description");
                            Log.e(TAG, "Telegram вернул ошибку: " + error);
                            runOnUiThread(() -> {
                                Toast.makeText(MainActivity.this, "Telegram: " + error, Toast.LENGTH_LONG).show();
                                newsList.clear();
                                newsList.add(new NewsItem(
                                    "Ошибка Telegram",
                                    error,
                                    0, 0
                                ));
                                adapter.notifyDataSetChanged();
                            });
                            return;
                        }

                        JSONArray results = obj.getJSONArray("result");
                        Log.d(TAG, "Найдено новостей: " + results.length());
                        
                        newsList.clear();
                        
                        if (results.length() == 0) {
                            Log.w(TAG, "Новостей нет!");
                            runOnUiThread(() -> {
                                newsList.add(new NewsItem(
                                    "Нет новостей",
                                    "В канале @Novikon_news пока нет постов.",
                                    0, 0
                                ));
                                adapter.notifyDataSetChanged();
                            });
                            return;
                        }

                        for (int i = 0; i < results.length(); i++) {
                            JSONObject message = results.getJSONObject(i).getJSONObject("message");
                            String text = message.getString("text");
                            Log.d(TAG, "Новость " + i + ": " + text.substring(0, Math.min(50, text.length())) + "...");
                            
                            String title = text.length() > 50 ? text.substring(0, 50) + "..." : text;
                            int views = message.optInt("views", 0);
                            int likes = (int) (Math.random() * 100);

                            NewsItem item = new NewsItem(title, text, views, likes);
                            newsList.add(item);
                        }

                        Log.d(TAG, "Всего добавлено новостей: " + newsList.size());
                        
                        runOnUiThread(() -> {
                            adapter.notifyDataSetChanged();
                            Toast.makeText(MainActivity.this, "Загружено " + newsList.size() + " новостей", Toast.LENGTH_SHORT).show();
                            Log.d(TAG, "UI обновлен");
                        });

                    } catch (Exception e) {
                        Log.e(TAG, "!!! Ошибка парсинга JSON !!!");
                        Log.e(TAG, "Сообщение: " + e.getMessage());
                        Log.e(TAG, "Тип ошибки: " + e.getClass().getName());
                        e.printStackTrace();
                        
                        runOnUiThread(() -> {
                            Toast.makeText(MainActivity.this, "Ошибка парсинга: " + e.getMessage(), Toast.LENGTH_LONG).show();
                            newsList.clear();
                            newsList.add(new NewsItem(
                                "Ошибка парсинга",
                                "Не удалось обработать данные от Telegram.\n\nОшибка: " + e.getMessage(),
                                0, 0
                            ));
                            adapter.notifyDataSetChanged();
                        });
                    }
                } else {
                    Log.e(TAG, "!!! Ответ не успешный, код: " + response.code());
                    try {
                        String errorBody = response.body().string();
                        Log.e(TAG, "Тело ошибки: " + errorBody);
                    } catch (Exception e) {
                        Log.e(TAG, "Не удалось прочитать тело ошибки");
                    }
                    
                    runOnUiThread(() -> {
                        Toast.makeText(MainActivity.this, "Ошибка сервера: " + response.code(), Toast.LENGTH_LONG).show();
                        newsList.clear();
                        newsList.add(new NewsItem(
                            "Ошибка сервера",
                            "Код ошибки: " + response.code() + "\nПопробуйте позже.",
                            0, 0
                        ));
                        adapter.notifyDataSetChanged();
                    });
                }
            }
        });
    }

    private void startAutoUpdate() {
        Log.d(TAG, "=== startAutoUpdate() вызван ===");
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                Log.d(TAG, "=== Автообновление ===");
                loadNews();
                handler.postDelayed(this, 300000); // 5 минут
            }
        }, 300000);
        Log.d(TAG, "Автообновление настроено на 5 минут");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "=== onDestroy() вызван ===");
        handler.removeCallbacksAndMessages(null);
    }
}
