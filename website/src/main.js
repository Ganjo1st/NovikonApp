import { createApp } from 'vue';
import { createRouter, createWebHistory } from 'vue-router';
import App from './App.vue';
import HomePage from './views/HomePage.vue';
import PostPage from './views/PostPage.vue';

const routes = [
  { path: '/', component: HomePage },
  { path: '/post/:id', component: PostPage, props: true },
  { path: '/:pathMatch(.*)*', redirect: '/' },
];

const router = createRouter({
  history: createWebHistory('/NovikonApp/'),
  routes,
});

const app = createApp(App);
app.use(router);
app.mount('#app');
