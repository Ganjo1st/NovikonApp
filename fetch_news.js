const axios = require('axios');
const fs = require('fs');
const path = require('path');

const BOT_TOKEN = process.env.BOT_TOKEN;
const CHANNEL_ID = process.env.CHANNEL_ID;

async function fetchNews() {
    try {
        if (!BOT_TOKEN || !CHANNEL_ID) {
            console.error('❌ Ошибка: Не найдены BOT_TOKEN или CHANNEL_ID.');
            return;
        }

        console.log('🔄 Загружаю посты из канала...');
        const url = `https://api.telegram.org/bot${BOT_TOKEN}/getUpdates`;
        const response = await axios.get(url, { params: { limit: 50, timeout: 30 } });
        const updates = response.data.result;

        // Оставляем только посты с текстом
        const validPosts = updates
            .filter(u => u.channel_post && u.channel_post.text)
            .map(u => u.channel_post);

        if (validPosts.length === 0) {
            console.log('ℹ️ Новых постов не найдено.');
            return;
        }

        // Переводим сообщения в статьи
        const news = validPosts.map(msg => {
            const text = msg.text || '';
            const lines = text.split('\n');
            const title = lines[0] || 'Новость из канала';
            const content = lines.slice(1).join('\n') || text;

            return {
                id: msg.message_id,
                title: title,
                content: content,
                image_url: null,
                published_at: new Date(msg.date * 1000).toISOString().split('T')[0]
            };
        });

        // СОРТИРОВКА: от новых к старым и оставляем ТОЛЬКО 30 последних
        news.sort((a, b) => new Date(b.published_at) - new Date(a.published_at));
        const latestNews = news.slice(0, 30);

        // ОБЯЗАТЕЛЬНО: удаляем все старые данные и сохраняем только последние 30
        const filePath = path.join(__dirname, 'website', 'public', 'data', 'news.json');
        fs.writeFileSync(filePath, JSON.stringify(latestNews, null, 2));
        console.log(`✅ Новости обновлены! Загружено ${latestNews.length} постов.`);
    } catch (error) {
        console.error('❌ Ошибка при парсинге:', error.message);
    }
}

fetchNews();
