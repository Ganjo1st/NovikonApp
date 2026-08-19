<template>
  <div v-if="loading" class="loading">Загрузка...</div>
  <div v-else-if="error" class="error">{{ error }}</div>
  <div v-else class="post-page">
    <h1>{{ post.title }}</h1>
    <div class="meta">
      <span class="date">{{ post.date }}</span>
      <span class="views">👁 {{ post.views }}</span>
    </div>
    <img v-if="post.image" :src="post.image" class="post-image" />
    <div class="content">{{ post.content }}</div>
    <div class="reactions">
      <button @click="like" class="like-btn">❤️ {{ post.likes }}</button>
      <button @click="dislike" class="dislike-btn">👎 {{ post.dislikes }}</button>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue';
import { useRoute } from 'vue-router';

const route = useRoute();
const post = ref({});
const loading = ref(true);
const error = ref(null);

onMounted(async () => {
  try {
    const id = route.params.id;
    const response = await fetch('/NovikonApp/data/news.json');
    const data = await response.json();
    const posts = data.data.result || [];
    const found = posts.find(p => String(p.update_id) === String(id));
    if (!found) throw new Error('Статья не найдена');

    const caption = found.channel_post.caption || '';
    const title = caption.split('\n')[0] || 'Без названия';
    const content = caption.split('\n').slice(1).join('\n') || caption;

    post.value = {
      title,
      content,
      date: new Date(found.channel_post.date * 1000).toLocaleDateString('ru-RU', {
        day: 'numeric', month: 'long', year: 'numeric', hour: '2-digit', minute: '2-digit'
      }),
      views: Math.floor(Math.random() * 100) + 10,
      likes: Math.floor(Math.random() * 20),
      dislikes: Math.floor(Math.random() * 5),
      image: 'https://picsum.photos/seed/' + found.update_id + '/800/400'
    };
  } catch (err) {
    error.value = err.message;
  } finally {
    loading.value = false;
  }
});

const like = () => post.value.likes++;
const dislike = () => post.value.dislikes++;
</script>

<style scoped>
.post-page {
  max-width: 800px;
  margin: 0 auto;
  padding: 20px;
  background: white;
  border-radius: 12px;
  box-shadow: 0 2px 8px rgba(0,0,0,0.1);
}
.post-page h1 {
  font-size: 1.8rem;
  line-height: 1.3;
  margin-bottom: 12px;
}
.meta {
  display: flex;
  gap: 16px;
  color: #666;
  font-size: 0.9rem;
  margin-bottom: 16px;
}
.post-image {
  width: 100%;
  max-height: 400px;
  object-fit: cover;
  border-radius: 8px;
  margin-bottom: 16px;
}
.content {
  font-size: 1.1rem;
  line-height: 1.8;
  white-space: pre-wrap;
}
.reactions {
  display: flex;
  gap: 12px;
  margin-top: 24px;
}
.reactions button {
  padding: 8px 20px;
  border: none;
  border-radius: 20px;
  font-size: 1rem;
  cursor: pointer;
}
.like-btn {
  background: #ff6b6b;
  color: white;
}
.dislike-btn {
  background: #4a6cf7;
  color: white;
}
.loading, .error {
  text-align: center;
  padding: 40px;
  color: #666;
}
</style>
