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

        // Флаг для отладки
        console.log('🔄 Начинаю парсинг Telegram...');

        const TELEGRAM_API_URL = https://api.telegram.org/bot/getUpdates;
        const response = await axios.get(TELEGRAM_API_URL, { params: { offset: -1, limit: 10 } });
        const messages = response.data.result;

        console.log(📩 Получено сообщений: );

        // ФИЛЬТР: Оставляем только те сообщения, у которых есть message и text
        const validMessages = messages.filter(msg => msg.message && msg.message.text);
        console.log(📝 Из них с текстом: );

        if (validMessages.length === 0) {
            console.log('ℹ️ Новых текстовых сообщений не найдено. Парсер завершён.');
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
                image_url = https://api.telegram.org/file/bot/;
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
        
        // Создаём папку, если её нет
        const dir = path.dirname(filePath);
        if (!fs.existsSync(dir)) {
            fs.mkdirSync(dir, { recursive: true });
        }

        fs.writeFileSync(filePath, JSON.stringify(news, null, 2));
        console.log(✅ Новости обновлены! Загружено  постов.);

    } catch (error) {
        console.error('❌ Ошибка при парсинге:', error.message);
    }
}

fetchNews();
setInterval(fetchNews, 60000);
