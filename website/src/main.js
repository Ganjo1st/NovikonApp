import { createApp } from 'vue';
import { createRouter, createWebHistory } from 'vue-router';
import App from './App.vue';
import HomePage from './views/HomePage.vue';

const routes = [
  { path: '/', component: HomePage },
  { path: '/:pathMatch(.*)*', redirect: '/' },
];

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes,
});

const app = createApp(App);
app.use(router);
app.mount('#app');
