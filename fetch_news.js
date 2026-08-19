const axios = require('axios');
const fs = require('fs');
const path = require('path');

const TELEGRAM_BOT_TOKEN = process.env.BOT_TOKEN;
const TELEGRAM_CHANNEL_ID = process.env.CHANNEL_ID;

async function fetchNews() {
    try {
        if (!TELEGRAM_BOT_TOKEN || !TELEGRAM_CHANNEL_ID) {
            console.error('❌ Ошибка: Не найдены BOT_TOKEN или CHANNEL_ID.');
            return;
        }

        console.log('🔄 Начинаю парсинг Telegram...');

        const TELEGRAM_API_URL = `https://api.telegram.org/bot${TELEGRAM_BOT_TOKEN}/getUpdates`;
        const response = await axios.get(TELEGRAM_API_URL, { params: { offset: -1, limit: 10 } });
        const messages = response.data.result || [];

        console.log(`📩 Получено сырых событий: ${messages.length}`);

        // ГЛАВНАЯ ЗАЩИТА: Игнорируем всё, что не является сообщением с текстом
        const validMessages = messages.filter(msg => 
            msg.message && typeof msg.message.text === 'string' && msg.message.text.trim().length > 0
        );

        console.log(`📝 Найдено постов с текстом: ${validMessages.length}`);

        if (validMessages.length === 0) {
            console.log('ℹ️ Новых текстовых постов не найдено.');
            return;
        }

        const news = validMessages.map(msg => {
            const text = msg.message.text || '';
            const lines = text.split('\n');
            const title = lines[0] || 'Новость из канала';
            const content = lines.slice(1).join('\n') || text;
            
            let image_url = null;
            if (msg.message.photo) {
                const file_id = msg.message.photo[msg.message.photo.length - 1].file_id;
                image_url = `https://api.telegram.org/file/bot${TELEGRAM_BOT_TOKEN}/${file_id}`;
            }

            return {
                id: msg.message.message_id,
                title: title,
                content: content,
                image_url: image_url,
                published_at: new Date(msg.message.date * 1000).toISOString().split('T')[0]
            };
        });

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
setInterval(fetchNews, 60000);