<script setup>
import { ref, onMounted } from 'vue';

const articles = ref([]);
const loading = ref(true);
const darkMode = ref(false);

const toggleTheme = () => {
  darkMode.value = !darkMode.value;
  document.body.classList.toggle('dark-theme');
};

const LogoIcon = `
<svg viewBox="0 0 100 100" xmlns="http://www.w3.org/2000/svg" style="height:50px; margin-right:12px;">
  <defs>
    <linearGradient id="grad1" x1="0%" y1="0%" x2="100%" y2="100%">
      <stop offset="0%" style="stop-color:#0099cc;stop-opacity:1" />
      <stop offset="100%" style="stop-color:#0d47a1;stop-opacity:1" />
    </linearGradient>
    <linearGradient id="grad2" x1="0%" y1="0%" x2="100%" y2="100%">
      <stop offset="0%" style="stop-color:#ff9900;stop-opacity:1" />
      <stop offset="100%" style="stop-color:#ff5722;stop-opacity:1" />
    </linearGradient>
  </defs>
  <circle cx="50" cy="45" r="30" fill="url(#grad1)" />
  <path d="M25 20 C 40 10, 60 10, 75 20" stroke="#ffffff" stroke-width="2" fill="none" />
  <path d="M20 35 C 30 25, 70 25, 80 35" stroke="#ffffff" stroke-width="2" fill="none" />
  <path d="M25 55 C 40 65, 60 65, 75 55" stroke="#ffffff" stroke-width="2" fill="none" />
  <path d="M50 25 L 60 55 L 45 48 L 35 60 Z" fill="url(#grad2)" />
  <path d="M70 20 C 75 25, 80 30, 82 35" stroke="#0099cc" stroke-width="3" fill="none" />
  <path d="M75 15 C 82 22, 88 30, 90 38" stroke="#0099cc" stroke-width="3" fill="none" />
</svg>`;

onMounted(() => {
  fetch('/NovikonApp/data/news.json')
    .then(res => res.json())
    .then(data => {
      articles.value = data;
      loading.value = false;
    })
    .catch(() => {
      loading.value = false;
    });
});
</script>

<template>
  <div class="app-container" :class="{ 'dark-theme': darkMode }">
    <header class="navbar">
      <a href="/NovikonApp/" class="logo-link">
        <div v-html="LogoIcon" class="nav-logo"></div>
        <div class="logo-text-group">
          <h1 class="logo-main">НОВИКОН</h1>
          <span class="logo-sub">АКТУАЛЬНЫЕ НОВОСТИ</span>
        </div>
      </a>
      <button @click="toggleTheme" class="theme-btn">
        {{ darkMode ? '☀️' : '🌙' }}
      </button>
    </header>

    <main class="main-content">
      <div v-if="loading" class="loading-text">Загрузка новостей...</div>
      
      <div v-else>
        <div v-for="article in articles" :key="article.id" class="news-card">
          <img v-if="article.image_url" :src="article.image_url" alt="" class="news-image" referrerpolicy="no-referrer" />
          <h2 class="news-title">{{ article.title }}</h2>
          <p class="news-content">{{ article.content }}</p>
          <div class="news-footer">
            <span class="news-date">{{ article.published_at }}</span>
            <a :href="'/NovikonApp/article/' + article.id" target="_blank" class="read-more">Читать далее →</a>
          </div>
        </div>
      </div>
    </main>
  </div>
</template>

<style>
body { margin: 0; background-color: #ffffff; transition: background-color 0.3s ease; }
body.dark-theme { background-color: #121212; }
.app-container { min-height: 100vh; transition: background-color 0.3s ease; background-color: #f8fbff; }
.app-container.dark-theme { background-color: #121212; }
.navbar { display: flex; align-items: center; justify-content: space-between; padding: 16px 24px; background-color: #ffffff; border-bottom: 1px solid #e0e0e0; transition: background-color 0.3s ease; }
.app-container.dark-theme .navbar { background-color: #1e1e1e; border-bottom: 1px solid #333; }
.logo-link { display: flex; align-items: center; text-decoration: none; cursor: pointer; }
.nav-logo { display: flex; align-items: center; }
.logo-text-group { display: flex; flex-direction: column; justify-content: center; }
.logo-main { color: #0d47a1; font-size: 26px; font-weight: 800; margin: 0; letter-spacing: 1.5px; transition: color 0.3s; }
.app-container.dark-theme .logo-main { color: #4fc3f7; }
.logo-sub { color: #1565c0; font-size: 12px; font-weight: 400; letter-spacing: 2px; margin-top: -2px; transition: color 0.3s; }
.app-container.dark-theme .logo-sub { color: #81d4fa; }
.theme-btn { background: transparent; border: 1px solid #ccc; padding: 5px 10px; border-radius: 8px; cursor: pointer; font-size: 18px; }
.app-container.dark-theme .theme-btn { border-color: #555; background: #333; color: #fff; }
.main-content { padding: 24px; max-width: 800px; margin: 0 auto; width: 100%; }
.loading-text { text-align: center; padding: 40px; color: #888; }
.news-card { background-color: #ffffff; border: 1px solid #e0e0e0; border-radius: 12px; margin-bottom: 16px; padding: 20px; transition: all 0.3s ease; }
.app-container.dark-theme .news-card { background-color: #1e1e1e; border: 1px solid #333; }
.news-image { width: 100%; border-radius: 8px; margin-bottom: 12px; object-fit: cover; display: block; }
.news-title { font-size: 20px; color: #0d47a1; margin: 0 0 8px 0; line-height: 1.4; transition: color 0.3s; }
.app-container.dark-theme .news-title { color: #ffffff; }
.news-content { font-size: 16px; line-height: 1.6; color: #444; margin: 0 0 12px 0; transition: color 0.3s; }
.app-container.dark-theme .news-content { color: #b0b0b0; }
.news-footer { display: flex; justify-content: space-between; font-size: 14px; color: #666; border-top: 1px solid #eee; padding-top: 14px; transition: all 0.3s; }
.app-container.dark-theme .news-footer { border-top-color: #333; color: #999; }
.read-more { color: #1565c0; font-weight: 500; text-decoration: none; transition: color 0.3s; }
.app-container.dark-theme .read-more { color: #4fc3f7; }
</style>