const TelegramBot = require('node-telegram-bot-api').default;const fs = require('fs');
const path = require('path');

const BOT_TOKEN = process.env.BOT_TOKEN;
const CHANNEL_ID = process.env.CHANNEL_ID;

const bot = new TelegramBot.default(BOT_TOKEN, { polling: false });
async function fetchNews() {
    try {
        if (!BOT_TOKEN || !CHANNEL_ID) {
            console.error('❌ Ошибка: Не найдены BOT_TOKEN или CHANNEL_ID.');
            return;
        }

        console.log('🔄 Загружаю посты из канала...');

        // Получаем последние 50 сообщений из канала
        const updates = await bot.getUpdates({ limit: 50, timeout: 0 });
        
        // Фильтруем только посты из нужного канала
        const channelMessages = updates
            .filter(u => u.channel_post && String(u.channel_post.chat.id) === String(CHANNEL_ID))
            .map(u => u.channel_post);

        console.log(`📩 Найдено сырых постов: ${channelMessages.length}`);

        if (channelMessages.length === 0) {
            console.log('ℹ️ Новых постов в канале не найдено.');
            return;
        }

        // Группируем по ID, чтобы убрать дубликаты
        const uniqueMessages = [];
        const seenIds = new Set();

        for (const msg of channelMessages) {
            if (!seenIds.has(msg.message_id)) {
                seenIds.add(msg.message_id);
                uniqueMessages.push(msg);
            }
        }

        console.log(`✅ Уникальных постов: ${uniqueMessages.length}`);

        // Парсим посты
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

            return {
                id: msg.message_id,
                title: title,
                content: content,
                image_url: image_url,
                published_at: new Date(msg.date * 1000).toISOString().split('T')[0]
            };
        });

        // Сохраняем в JSON
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