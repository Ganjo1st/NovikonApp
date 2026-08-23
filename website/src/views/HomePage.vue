<template>
  <div class="home">
    <div class="header">
      <h1>📰 Novikon News</h1>
      <p class="subtitle">Актуальные новости со всего мира</p>
    </div>
    <div v-if="loading" class="loading">Загрузка новостей...</div>
    <div v-else-if="error" class="error">{{ error }}</div>
    <NewsList v-else :posts="newsData" />
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue';
import NewsList from '../components/NewsList.vue';

const newsData = ref([]);
const loading = ref(true);
const error = ref(null);

onMounted(async () => {
  try {
    // Исправленный путь — без /data/, так как файл лежит в корне public/
    const response = await fetch('/NovikonApp/data/news.json');
    if (!response.ok) throw new Error('Не удалось загрузить новости');
    const data = await response.json();
    newsData.value = data.data.result || [];
  } catch (err) {
    error.value = err.message;
    console.error('Ошибка загрузки новостей:', err);
  } finally {
    loading.value = false;
  }
});
</script>

<style scoped>
.home {
  max-width: 800px;
  margin: 0 auto;
  padding: 20px;
}

.header {
  text-align: center;
  margin-bottom: 40px;
  padding: 40px 20px;
  background: linear-gradient(135deg, #1a1a2e 0%, #16213e 100%);
  border-radius: 16px;
  color: white;
}

.header h1 {
  margin: 0;
  font-size: 2.5rem;
}

.subtitle {
  margin: 8px 0 0 0;
  opacity: 0.8;
  font-size: 1.1rem;
}

.loading, .error {
  text-align: center;
  padding: 40px;
  color: #666;
}
</style>
