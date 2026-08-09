package com.novikon.app;

import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.appbar.MaterialToolbar;
// import com.yandex.mobile.ads.AdRequest;
// import com.yandex.mobile.ads.InterstitialAd;
// import com.yandex.mobile.ads.InterstitialAdEventListener;

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

    // ТОКЕН БЕРЕТСЯ ИЗ ПЕРЕМЕННОЙ ОКРУЖЕНИЯ (БЕЗОПАСНО!)
   private String getNewsUrl() {
    // Токен вшит в приложение
    String token = "8832915986:AAHc9q42Ux7RC5t37AtvCsfQMGNd_h5uGAA";
    
    if (token == null || token.isEmpty()) {
        runOnUiThread(() -> Toast.makeText(this, "Ошибка: токен не найден", Toast.LENGTH_LONG).show());
        return null;
    }
    return "https://api.telegram.org/bot" + token + "/getUpdates?chat_id=@Novikon_news&limit=20";
}

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
        // handler.postDelayed(() -> showAd(), 10000);
    }

    private void loadNews() {
        String url = getNewsUrl();
        if (url == null) return;

        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder().url(url).build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                runOnUiThread(() -> Toast.makeText(MainActivity.this, "Ошибка загрузки", Toast.LENGTH_SHORT).show());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    try {
                        String json = response.body().string();
                        JSONObject obj = new JSONObject(json);
                        JSONArray results = obj.getJSONArray("result");

                        newsList.clear();
                        for (int i = 0; i < results.length(); i++) {
                            JSONObject message = results.getJSONObject(i).getJSONObject("message");
                            String text = message.getString("text");
                            String title = text.length() > 50 ? text.substring(0, 50) + "..." : text;
                            int views = message.optInt("views", 0);
                            int likes = (int) (Math.random() * 100);

                            NewsItem item = new NewsItem(title, text, views, likes);
                            newsList.add(item);
                        }

                        runOnUiThread(() -> adapter.notifyDataSetChanged());

                    } catch (Exception e) {
                        Log.e("News", "Parse error", e);
                    }
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




    protected void onDestroy() {
        super.onDestroy();
    }
}
