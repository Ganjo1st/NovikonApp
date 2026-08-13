<template>
  <div class="post-page">
    <div v-if="loading" class="loading">Загрузка...</div>
    <div v-else-if="error" class="error">{{ error }}</div>
    <div v-else class="post-content">
      <button @click="$router.back()" class="back-btn">← Назад</button>
      <h1>{{ postTitle }}</h1>
      <div class="meta">
        <span class="date">{{ postDate }}</span>
      </div>
      <div v-if="postPhoto" class="photo">
        <img :src="postPhoto" alt="Новостное фото" />
      </div>
      <div class="text">{{ postText }}</div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue';
import { useRoute } from 'vue-router';

const route = useRoute();
const post = ref(null);
const loading = ref(true);
const error = ref(null);

const postTitle = computed(() => {
  if (!post.value) return '';
  const caption = post.value.channel_post.caption || '';
  return caption.split('\n')[0] || 'Новость';
});

const postText = computed(() => {
  if (!post.value) return '';
  const caption = post.value.channel_post.caption || '';
  return caption.split('\n').slice(1).join('\n') || caption;
});

const postDate = computed(() => {
  if (!post.value) return '';
  const date = new Date(post.value.channel_post.date * 1000);
  return date.toLocaleDateString('ru-RU', {
    day: 'numeric',
    month: 'long',
    year: 'numeric',
    hour: '2-digit',
    minute: '2-digit'
  });
});

const postPhoto = computed(() => {
  if (!post.value || !post.value.channel_post.photo) return null;
  const photo = post.value.channel_post.photo;
  // Берем самое большое фото
  const largest = photo.reduce((a, b) => a.width > b.width ? a : b);
  return largest.file_id ? `https://ganjo1st.github.io/NovikonApp/placeholder.jpg` : null;
});

onMounted(async () => {
  try {
    const updateId = route.params.id;
    const response = await fetch('/data/news.json');
    if (!response.ok) throw new Error('Не удалось загрузить новости');
    const data = await response.json();
    const posts = data.data.result || [];
    const found = posts.find(p => String(p.update_id) === String(updateId));
    if (!found) throw new Error('Новость не найдена');
    post.value = found;
  } catch (err) {
    error.value = err.message;
  } finally {
    loading.value = false;
  }
});
</script>

<style scoped>
.post-page {
  max-width: 800px;
  margin: 0 auto;
  padding: 20px;
}

.back-btn {
  background: none;
  border: none;
  color: #4a6cf7;
  font-size: 1rem;
  cursor: pointer;
  padding: 8px 0;
  margin-bottom: 20px;
}

.back-btn:hover {
  text-decoration: underline;
}

.post-content {
  background: white;
  padding: 30px;
  border-radius: 12px;
  box-shadow: 0 2px 8px rgba(0,0,0,0.08);
}

.post-content h1 {
  margin: 0 0 12px 0;
  font-size: 2rem;
  line-height: 1.3;
}

.meta {
  margin-bottom: 20px;
  color: #8a8a9a;
  font-size: 0.9rem;
}

.photo {
  margin: 20px 0;
}

.photo img {
  width: 100%;
  max-height: 400px;
  object-fit: cover;
  border-radius: 8px;
}

.text {
  font-size: 1.1rem;
  line-height: 1.8;
  white-space: pre-wrap;
}
</style>
