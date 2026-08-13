<template>
  <div class="news-list">
    <div v-for="post in posts" :key="post.update_id" class="news-item">
      <h2>
        <router-link :to="`/post/${post.update_id}`">
          {{ getTitle(post) }}
        </router-link>
      </h2>
      <div v-if="getPhoto(post)" class="news-photo">
        <img :src="getPhoto(post)" alt="Новостное фото" />
      </div>
      <p>{{ getDescription(post) }}</p>
      <div class="meta">
        <span class="date">{{ formatDate(post.channel_post.date) }}</span>
        <span class="read-more">
          <router-link :to="`/post/${post.update_id}`">Читать далее →</router-link>
        </span>
      </div>
    </div>
  </div>
</template>

<script setup>
import { defineProps } from 'vue';

const props = defineProps(['posts']);

const formatDate = (timestamp) => {
  const date = new Date(timestamp * 1000);
  return date.toLocaleDateString('ru-RU', {
    day: 'numeric',
    month: 'long',
    year: 'numeric',
    hour: '2-digit',
    minute: '2-digit'
  });
};

const getTitle = (post) => {
  const caption = post.channel_post.caption || '';
  return caption.split('\n')[0] || 'Новость';
};

const getDescription = (post) => {
  const caption = post.channel_post.caption || '';
  const lines = caption.split('\n');
  if (lines.length > 1) {
    return lines.slice(1).join(' ').substring(0, 200) + '...';
  }
  return caption.substring(0, 200) + '...';
};

const getPhoto = (post) => {
  if (!post.channel_post.photo || !post.channel_post.photo.length) return null;
  // Берем самое большое фото
  const largest = post.channel_post.photo.reduce((a, b) => a.width > b.width ? a : b);
  // В реальном проекте здесь должен быть реальный URL картинки
  // Для демонстрации используем плейсхолдер
  return `https://picsum.photos/seed/${post.update_id}/800/400`;
};
</script>

<style scoped>
.news-list {
  display: flex;
  flex-direction: column;
  gap: 24px;
}

.news-item {
  background: white;
  padding: 24px;
  border-radius: 12px;
  box-shadow: 0 2px 8px rgba(0,0,0,0.08);
  transition: transform 0.2s;
}

.news-item:hover {
  transform: translateY(-2px);
}

.news-item h2 {
  margin: 0 0 12px 0;
  font-size: 1.4rem;
  line-height: 1.4;
}

.news-item h2 a {
  color: #1a1a2e;
  text-decoration: none;
}

.news-item h2 a:hover {
  color: #4a6cf7;
}

.news-photo {
  margin: 12px 0;
}

.news-photo img {
  width: 100%;
  max-height: 300px;
  object-fit: cover;
  border-radius: 8px;
}

.news-item p {
  margin: 0 0 16px 0;
  color: #4a4a5a;
  line-height: 1.6;
}

.meta {
  display: flex;
  justify-content: space-between;
  align-items: center;
  font-size: 0.9rem;
  color: #8a8a9a;
}

.date {
  background: #f0f2f5;
  padding: 4px 12px;
  border-radius: 20px;
}

.read-more a {
  color: #4a6cf7;
  text-decoration: none;
  font-weight: 500;
}

.read-more a:hover {
  text-decoration: underline;
}
</style>
