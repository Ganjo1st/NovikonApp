<template>
  <div id="app" :class="{ 'dark-theme': darkMode }">
    <header>
      <nav>
        <div class="logo-container">
          <span class="logo-icon">📰</span>
          <span class="logo-text">Новикон</span>
        </div>
        <div class="nav-right">
          <button @click="toggleTheme" class="theme-toggle">
            {{ darkMode ? '☀️' : '🌙' }}
          </button>
        </div>
      </nav>
    </header>
    <main>
      <router-view />
    </main>
    <footer>
      <p>&copy; 2026 Новикон. Все новости взяты из открытых источников.</p>
    </footer>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue';

const darkMode = ref(false);

onMounted(() => {
  const saved = localStorage.getItem('novikon-theme');
  if (saved === 'dark') {
    darkMode.value = true;
    document.documentElement.classList.add('dark-theme');
  }
});

const toggleTheme = () => {
  darkMode.value = !darkMode.value;
  localStorage.setItem('novikon-theme', darkMode.value ? 'dark' : 'light');
  document.documentElement.classList.toggle('dark-theme');
};
</script>

<style>
* {
  margin: 0;
  padding: 0;
  box-sizing: border-box;
}

body {
  font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, Oxygen, Ubuntu, sans-serif;
  background: #f5f7fa;
  color: #1a1a2e;
  line-height: 1.6;
  transition: background 0.3s, color 0.3s;
}

body.dark-theme {
  background: #1a1a2e;
  color: #e8e8e8;
}

header {
  background: #1a1a2e;
  padding: 12px 20px;
  box-shadow: 0 2px 8px rgba(0,0,0,0.1);
  position: sticky;
  top: 0;
  z-index: 100;
}

nav {
  max-width: 800px;
  margin: 0 auto;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.logo-container {
  display: flex;
  align-items: center;
  gap: 12px;
}

.logo-icon {
  font-size: 1.8rem;
}

.logo-text {
  color: white;
  font-size: 1.4rem;
  font-weight: 700;
  letter-spacing: 1px;
}

.nav-right {
  display: flex;
  align-items: center;
  gap: 16px;
}

.theme-toggle {
  background: rgba(255,255,255,0.15);
  border: none;
  color: white;
  padding: 8px 12px;
  border-radius: 50%;
  cursor: pointer;
  font-size: 1.2rem;
  width: 40px;
  height: 40px;
  display: flex;
  align-items: center;
  justify-content: center;
  transition: background 0.2s;
}

.theme-toggle:hover {
  background: rgba(255,255,255,0.25);
}

main {
  max-width: 800px;
  margin: 20px auto;
  padding: 0 20px;
}

footer {
  text-align: center;
  padding: 20px;
  color: #8a8a9a;
  font-size: 0.9rem;
  border-top: 1px solid rgba(0,0,0,0.05);
}

.dark-theme footer {
  color: #5a5a7a;
  border-top: 1px solid rgba(255,255,255,0.05);
}

.dark-theme .news-item,
.dark-theme .post-page {
  background: #2a2a4a !important;
  box-shadow: 0 2px 8px rgba(0,0,0,0.3) !important;
}

.dark-theme .news-item h2 a,
.dark-theme .post-page h1 {
  color: #e8e8e8 !important;
}

.dark-theme .news-item p,
.dark-theme .post-page .content {
  color: #b8b8c8 !important;
}

.dark-theme .date {
  background: #3a3a5a !important;
  color: #b8b8c8 !important;
}

.dark-theme .meta {
  color: #8a8a9a !important;
}

.dark-theme .read-more {
  color: #6c8cff !important;
}
</style>
