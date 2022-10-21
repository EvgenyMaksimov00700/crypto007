import lombok.SneakyThrows;

import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

import org.telegram.telegrambots.meta.api.methods.send.SendSticker;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.DeleteMessage;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class Bot extends TelegramLongPollingBot {
    DataBase db = new DataBase("Exchange");         // Обьект класса базы данных
    DateTimeFormatter dft = DateTimeFormatter.ISO_DATE;    //  Формат даты: 2022-10-16 22:31:06.372+03


    String lastOperation;
    String lastCurrency;

    boolean isBuyOrder = false;
    boolean isSellOrder = false;

    /**
     * Переопределяем метод, возвращающий имя пользователя Бота
     *
     * @return имя пользователя
     */
    @Override
    public String getBotUsername() {
        return "@crypto00700bot";
    }

    /**
     * Переопределяем метод, возвращающий токен бота
     *
     * @return Токен Бота
     */
    @Override
    public String getBotToken() {
        return "5583673029:AAGg4kts3sl06SQXKPHxNIMbNMWB8Rp25sM";
    }

    /**
     * Обрабатывает нажатие кнопки под сообщением
     *
     * @param callbackQuery Очередь нажатия
     */
    @SneakyThrows
    protected void handleCallback(CallbackQuery callbackQuery) {
        String data = callbackQuery.getData(); // buy:btc
        String[] splittedData = data.split(":"); // ["buy", "btc"]
        Message message = callbackQuery.getMessage();

        if ("buy".equals(splittedData[0])) {
            execute(SendMessage.builder().chatId(message.getChatId().toString()).text("Cколько валюты " +
                    splittedData[1] + " Вам надо купить?").build());
            lastOperation = "buy";
            lastCurrency = splittedData[1].toUpperCase(Locale.ROOT) + "RUB";
        }
        if ("sell".equals(splittedData[0])) {
            execute(SendMessage.builder().chatId(message.getChatId().toString()).text("Cколько валюты " +
                    splittedData[1] + " Вам надо продать?").build());
            lastOperation = "sell";
            lastCurrency = splittedData[1].toUpperCase(Locale.ROOT) + "RUB";
        }
        if ("order".equals(splittedData[0])) {
            Connection connection = db.getConnection();

            LocalDateTime now = LocalDateTime.now();
            PreparedStatement preparedStatement = connection.prepareStatement("INSERT INTO orders(\"UserName\", " +
                    "\"SellBuy\",\"Date\", \"ValueRub\", \"CurrencyType\") VALUES (?,?,?,?,?)");
            preparedStatement.setString(1, callbackQuery.getFrom().getUserName());
            preparedStatement.setString(2, splittedData[1]);
            preparedStatement.setTimestamp(3, new Timestamp(System.currentTimeMillis()));
            preparedStatement.setDouble(4, Double.parseDouble(splittedData[2]));
            preparedStatement.setString(5, lastCurrency);

            preparedStatement.executeUpdate();
            if ("buy".equals(splittedData[1])) {
                execute(SendMessage.builder().chatId(message.getChatId().toString()).text("Введите адрес Вашего " +
                        "кошелька\n\nПримечание: Будьте внимательны с выбором сети монеты, иначе возможна " +
                        "потеря средств.").build());
                isBuyOrder = true;
            }
            if ("sell".equals(splittedData[1])) {
                execute(SendMessage.builder().chatId(message.getChatId().toString()).text("Переведите валюту на " +
                        "кошелек: 0x439bdfee6bd6c57e386883e5532d3bf9f64498db \n\n " + "Введите номер Вашей карты\n\n" +
                        "Примечание: Средства поступят в течение тридцати минут").build());
                isSellOrder = true;
            }
        }
    }

    /**
     * Метод, обрабатывающий обновление действий, происходящих в боте (новое сообщение, нажатие на кнопку и т.д.)
     *
     * @param update обновление действий
     */
    @Override
    @SneakyThrows
    public void onUpdateReceived(Update update) {

        if (update.hasCallbackQuery()) {
            handleCallback(update.getCallbackQuery());
        } else if (update.hasMessage()) {
            Message message = update.getMessage();
            ReplyKeyboardMarkup replyKeyboardMarkup = new ReplyKeyboardMarkup();

            List<KeyboardRow> buttons = new ArrayList<>();
            KeyboardRow firstRow = new KeyboardRow();

            KeyboardButton sellButton = new KeyboardButton("Продать");
            KeyboardButton buyButton = new KeyboardButton("Купить");

            firstRow.add(sellButton);
            firstRow.add(buyButton);

            buttons.add(firstRow);

            replyKeyboardMarkup.setKeyboard(buttons);
            replyKeyboardMarkup.setResizeKeyboard(true);

            if (message.getText().equals("/start")) {
                execute(SendMessage.builder().chatId(message.getChatId().toString()).text("Привет!")
                        .replyMarkup(replyKeyboardMarkup).build());
            } else if (message.getText().equals("Продать")) {
                isSellOrder = false;
                Message typing = execute(SendSticker.builder().chatId(message.getChatId().toString())
                        .sticker(new InputFile("CAACAgIAAxkBAAPzYxNKV2Iwmm35fNo4cpcAASn3I6Y2AAI4Cw" +
                                "ACTuSZSzKxR9LZT4zQKQQ")).build());

                InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
                List<List<InlineKeyboardButton>> currency = new ArrayList<>();

                InlineKeyboardButton usdt = new InlineKeyboardButton("USDT");
                usdt.setCallbackData("sell:usdt");
                InlineKeyboardButton btc = new InlineKeyboardButton("BTC");
                btc.setCallbackData("sell:btc");
                InlineKeyboardButton eth = new InlineKeyboardButton("ETH");
                eth.setCallbackData("sell:eth");

                currency.add(Arrays.asList(usdt, btc, eth));

                inlineKeyboardMarkup.setKeyboard(currency);
                String text = "Выберите валюту\n\n" + "USDT -> RUB: " + ParserCurrency.courseSell("USDTRUB") +
                        "\n" + "BTC -> RUB: " + ParserCurrency.courseSell("BTCRUB") + "\n" + "ETH -> RUB: " +
                        ParserCurrency.courseSell("ETHRUB") + "\n";

                execute(SendMessage.builder().chatId(message.getChatId().toString()).text(text)
                        .replyMarkup(inlineKeyboardMarkup).build());

                execute(DeleteMessage.builder().chatId(typing.getChatId().toString())
                        .messageId(typing.getMessageId()).build());
            } else if (message.getText().equals("Купить")) {
                isSellOrder = false;
                Message typing = execute(SendSticker.builder().chatId(message.getChatId().toString())
                        .sticker(new InputFile("CAACAgIAAxkBAAPzYxNKV2Iwmm35fNo4cpcAASn3I6Y2AAI4CwACTuSZSz" +
                                "KxR9LZT4zQKQQ")).build());

                InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
                List<List<InlineKeyboardButton>> currency = new ArrayList<>();

                InlineKeyboardButton usdt = new InlineKeyboardButton("USDT");
                usdt.setCallbackData("buy:usdt");
                InlineKeyboardButton btc = new InlineKeyboardButton("BTC");
                btc.setCallbackData("buy:btc");
                InlineKeyboardButton eth = new InlineKeyboardButton("ETH");
                eth.setCallbackData("buy:eth");

                currency.add(Arrays.asList(usdt, btc, eth));

                inlineKeyboardMarkup.setKeyboard(currency);
                String text = "Выберите валюту\n\n" + "USDT -> RUB: " + ParserCurrency.courseBuy("USDTRUB") +
                        "\n" + "BTC -> RUB: " + ParserCurrency.courseBuy("BTCRUB") + "\n" + "ETH -> RUB: " +
                        ParserCurrency.courseBuy("ETHRUB") + "\n";

                execute(SendMessage.builder().chatId(message.getChatId().toString()).text(text)
                        .replyMarkup(inlineKeyboardMarkup).build());

                execute(DeleteMessage.builder().chatId(typing.getChatId().toString())
                        .messageId(typing.getMessageId()).build());
            } else if (isDigits(message.getText()) && !isSellOrder && !isBuyOrder) {
                InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
                List<List<InlineKeyboardButton>> buttonsArray = new ArrayList<>();
                InlineKeyboardButton makeOrder = new InlineKeyboardButton("Создать заявку");

                if (Objects.equals(lastOperation, "sell")) {
                    makeOrder.setCallbackData("order:sell:" + message.getText());
                    buttonsArray.add(Collections.singletonList(makeOrder));
                    inlineKeyboardMarkup.setKeyboard(buttonsArray);

                    execute(SendMessage.builder().chatId(message.getChatId().toString()).text("Вы получите на карту "
                            + Double.parseDouble(message.getText()) * ParserCurrency.courseSell(lastCurrency) +
                            " рублей").replyMarkup(inlineKeyboardMarkup).build());
                } else if (Objects.equals(lastOperation, "buy")) {
                    makeOrder.setCallbackData("order:buy:" + message.getText());
                    buttonsArray.add(Collections.singletonList(makeOrder));
                    inlineKeyboardMarkup.setKeyboard(buttonsArray);

                    execute(SendMessage.builder().chatId(message.getChatId().toString()).text("Вaм нужно оплатить " +
                            Double.parseDouble(message.getText()) * ParserCurrency.courseBuy(lastCurrency) +
                            " рублей").replyMarkup(inlineKeyboardMarkup).build());
                }
            } else if (isBuyOrder) {
                execute(SendMessage.builder().chatId(message.getChatId().toString()).text("Переведите средства на " +
                        "карту Тинькофф : 2222 3333 4444 5555\n\n После поступления средств ожидайте 30 минут для " +
                        "поступления криптовалюты").build());
                isBuyOrder = false;
            } else if (isSellOrder) {
                execute(SendMessage.builder().chatId(message.getChatId().toString()).text("Ваша заявка принята, " +
                        "ожидайте оплаты в течение 30 минут").build());
                isSellOrder = false;
            } else {
                execute(SendMessage.builder().chatId(message.getChatId().toString()).text("Неправильное значение")
                        .build());
            }
        }
    }

    /**
     * Утилита для проверки, является ли строка дробным числом
     *
     * @param s строка
     * @return true если является числом, в противном случае false
     */
    private boolean isDigits(String s) {
        int amountDots = 0;
        for (int i = 0; i < s.length(); i++) {
            if (s.charAt(i) == '.') {
                if (amountDots == 0) {
                    amountDots += 1;
                } else {
                    System.out.println("not digit");
                    return false;
                }
            } else if (!Character.isDigit(s.charAt(i))) { // 'a' -> True
                System.out.println("not digit");
                return false;
            }
        }
        return true;
    }

    @SneakyThrows
    public static void main(String[] args) {
        Bot bot = new Bot();                            // создаем обьект класса Бот
        if (bot.db.getConnection() != null) {           // проверка подключения к базе данных
            System.out.println("Connect to database");
        }
        TelegramBotsApi telegramBotsApi = new TelegramBotsApi(DefaultBotSession.class);
        telegramBotsApi.registerBot(bot);               // Регистрируем Бота
    }
}

