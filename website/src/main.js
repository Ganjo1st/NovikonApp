import { createApp } from 'vue';
import { createRouter, createWebHistory } from 'vue-router';
import App from './App.vue';
import HomePage from './views/HomePage.vue';
import PostPage from './views/PostPage.vue';

// Определяем базовый путь для GitHub Pages
const basePath = '/NovikonApp/';

// Создаем роутер
const router = createRouter({
  history: createWebHistory(basePath),
  routes: [
    { path: '/', component: HomePage },
    { path: '/post/:id', component: PostPage, props: true },
    { path: '/:pathMatch(.*)*', redirect: '/' },
  ],
});

// Обработка параметра path из URL (для 404.html)
const urlParams = new URLSearchParams(window.location.search);
const pathParam = urlParams.get('path');
if (pathParam) {
  // Если есть параметр path, перенаправляем на него
  const cleanPath = pathParam.replace(/^\/+/, '');
  if (cleanPath.startsWith('post/')) {
    const id = cleanPath.replace('post/', '');
    router.push(`/post/${id}`);
  }
}

const app = createApp(App);
app.use(router);
app.mount('#app');
