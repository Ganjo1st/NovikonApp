<template>
  <div id="app" :class="{ 'dark-theme': darkMode }">
    <header>
      <nav>
        <router-link to="/">📰 Novikon News</router-link>
        <div class="nav-right">
          <button @click="toggleTheme" class="theme-toggle">
            {{ darkMode ? '☀️ Светлая' : '🌙 Тёмная' }}
          </button>
        </div>
      </nav>
    </header>
    <main>
      <router-view />
    </main>
    <footer>
      <p>&copy; 2026 Novikon. Все новости взяты из открытых источников.</p>
    </footer>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue';

const darkMode = ref(false);

onMounted(() => {
  // Загружаем сохраненную тему
  const saved = localStorage.getItem('novikon-theme');
  if (saved === 'dark') {
    darkMode.value = true;
  }
});

const toggleTheme = () => {
  darkMode.value = !darkMode.value;
  localStorage.setItem('novikon-theme', darkMode.value ? 'dark' : 'light');
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

a {
  color: #4a6cf7;
  text-decoration: none;
}

a:hover {
  text-decoration: underline;
}

header {
  background: #1a1a2e;
  color: white;
  padding: 16px 20px;
  box-shadow: 0 2px 8px rgba(0,0,0,0.1);
}

nav {
  max-width: 800px;
  margin: 0 auto;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

nav a {
  color: white;
  font-size: 1.2rem;
  font-weight: 600;
  text-decoration: none;
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
  padding: 6px 14px;
  border-radius: 20px;
  cursor: pointer;
  font-size: 0.9rem;
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
}

/* Темная тема */
.dark-theme {
  --bg: #1a1a2e;
  --text: #e8e8e8;
  --card-bg: #2a2a4a;
  --card-shadow: rgba(0,0,0,0.3);
  --meta-bg: #3a3a5a;
}

.dark-theme body {
  background: #1a1a2e;
  color: #e8e8e8;
}

.dark-theme .news-item {
  background: #2a2a4a;
  box-shadow: 0 2px 8px rgba(0,0,0,0.3);
}

.dark-theme .news-item h2 a {
  color: #e8e8e8;
}

.dark-theme .news-item p {
  color: #b8b8c8;
}

.dark-theme .date {
  background: #3a3a5a;
  color: #b8b8c8;
}

.dark-theme .post-content {
  background: #2a2a4a;
  box-shadow: 0 2px 8px rgba(0,0,0,0.3);
}

.dark-theme .post-content h1 {
  color: #e8e8e8;
}

.dark-theme .text {
  color: #b8b8c8;
}

.dark-theme footer {
  color: #5a5a7a;
}
</style>
