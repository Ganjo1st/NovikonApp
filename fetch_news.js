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

        console.log('🔄 Загружаю посты из канала (лимит 50)...');

        // УВЕЛИЧИВАЕМ ЛИМИТ до 50, чтобы поймать реальные посты
        const url = `https://api.telegram.org/bot${BOT_TOKEN}/getUpdates`;
        const response = await axios.get(url, { params: { limit: 50, timeout: 30 } });
        const updates = response.data.result;

        // ФИЛЬТРУЕМ: оставляем только реальные посты с текстом из нашего канала
        const channelPosts = updates
            .filter(u => u.channel_post && String(u.channel_post.chat.id) === String(CHANNEL_ID))
            .map(u => u.channel_post);

        console.log(`📩 Найдено сырых постов: ${channelPosts.length}`);

        if (channelPosts.length === 0) {
            console.log('ℹ️ Новых постов в канале не найдено.');
            return;
        }

        // Группируем по ID (убираем дубликаты)
        const uniqueMessages = [];
        const seenIds = new Set();

        for (const msg of channelPosts) {
            if (!seenIds.has(msg.message_id)) {
                seenIds.add(msg.message_id);
                uniqueMessages.push(msg);
            }
        }

        console.log(`✅ Уникальных постов: ${uniqueMessages.length}`);

        // Парсим посты + ГЕНЕРИРУЕМ СТАТИСТИКУ
        const news = uniqueMessages.map(msg => {
            const text = msg.text || msg.caption || '';
            const lines = text.split('\n');
            const title = lines[0] || 'Новость из канала';
            const content = lines.slice(1).join('\n') || text;
            
            let image_url = null;
            if (msg.photo) {
                const file_id = msg.photo[msg.photo.length - 1].file_id;
                image_url = `https://api.telegram.org/file/bot${BOT_TOKEN}/${file_id}`;
            }

            // ЭМУЛЯЦИЯ ПРОСМОТРОВ И РЕАКЦИЙ (работает без Telegram API)
            const randViews = Math.floor(Math.random() * (5000 - 500 + 1)) + 500; // 500 - 5000 просмотров
            const randLikes = Math.floor(randViews * (Math.random() * (0.2 - 0.02) + 0.02)); // 2% - 20% лайков от просмотров

            return {
                id: msg.message_id,
                title: title,
                content: content,
                image_url: image_url,
                published_at: new Date(msg.date * 1000).toISOString().split('T')[0],
                views: randViews,
                likes: randLikes
            };
        });

        // СОРТИРОВКА ОТ НОВЫХ К СТАРЫМ
        news.sort((a, b) => new Date(b.published_at) - new Date(a.published_at));

        const filePath = path.join(__dirname, 'website', 'public', 'data', 'news.json');
        const dir = path.dirname(filePath);
        if (!fs.existsSync(dir)) {
            fs.mkdirSync(dir, { recursive: true });
        }

        fs.writeFileSync(filePath, JSON.stringify(news, null, 2));
        console.log(`✅ Новости обновлены! Загружено ${news.length} постов.`);

    } catch (error) {
        console.error('❌ Ошибка при парсинге:', error.message);
    }
}

fetchNews();