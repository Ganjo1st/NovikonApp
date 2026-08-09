package com.novikon.app;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class NewsAdapter extends RecyclerView.Adapter<NewsAdapter.ViewHolder> {

    private List<NewsItem> newsList;

    public NewsAdapter(List<NewsItem> newsList) {
        this.newsList = newsList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.news_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        NewsItem item = newsList.get(position);
        holder.title.setText(item.getTitle());
        holder.content.setText(item.getContent());
        holder.date.setText("Сегодня, 10:30");
        holder.likes.setText(String.valueOf(item.getLikes()));
        holder.views.setText(String.valueOf(item.getViews()));
    }

    @Override
    public int getItemCount() {
        return newsList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView title, content, date, likes, views;
        ImageView likeButton, viewsIcon;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.postTitle);
            content = itemView.findViewById(R.id.postText);
            date = itemView.findViewById(R.id.postDate);
            likes = itemView.findViewById(R.id.likeCount);
            views = itemView.findViewById(R.id.viewsCount);
            likeButton = itemView.findViewById(R.id.likeButton);
            viewsIcon = itemView.findViewById(R.id.viewsIcon);
        }
    }
}
