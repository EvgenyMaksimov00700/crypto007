import com.sun.xml.internal.ws.resources.SenderMessages;
import lombok.SneakyThrows;
import org.telegram.telegrambots.bots.DefaultBotOptions;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
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

import java.lang.reflect.Array;
import java.util.*;

public class Hello extends TelegramLongPollingBot {
    String lastOperation;
    String lastCurrency;

    boolean isBuyOrder = false;
    boolean isSellOrder = false;
    // create Parse object -> Parse parse = new ...;
    @Override
    public String getBotUsername() {
        return "@crypto00700bot";
    }

    @Override
    public String getBotToken() {
        return "5583673029:AAGg4kts3sl06SQXKPHxNIMbNMWB8Rp25sM";
    }

    @SneakyThrows
    protected void handleCallback(CallbackQuery callbackQuery) {
        String data = callbackQuery.getData(); // buy|btc
        String[] splitedData = data.split(":"); // ["order", "sell"]
        Message message = callbackQuery.getMessage();

        if ("buy".equals(splitedData[0])) {

            execute(SendMessage.builder()
                    .chatId(message.getChatId().toString())
                    .text("Cколько валюты " + splitedData[1] + " Вам надо купить?")
                    .build()
            );
            lastOperation = "buy";
            lastCurrency = splitedData[1].toUpperCase(Locale.ROOT) + "RUB";
        }
        if ("sell".equals(splitedData[0])) {
            execute(SendMessage.builder()
                    .chatId(message.getChatId().toString())
                    .text("Cколько валюты " + splitedData[1] + " Вам надо продать?")
                    .build()
            );
            lastOperation = "sell";
            lastCurrency = splitedData[1].toUpperCase(Locale.ROOT) + "RUB";
        }
        if ("order".equals(splitedData[0])){
            // TODO add to db make order
            if ("buy".equals(splitedData[1])) {
                execute(SendMessage.builder()
                        .chatId(message.getChatId().toString())
                        .text("Введите адрес Вашего кошелька\n\nПримечание: Будьте внимательны с выбором сети монеты, иначе возможна потеря средств.")
                        .build()
                );
                isBuyOrder = true;
            }
            if ("sell".equals(splitedData[1])) {
                execute(SendMessage.builder()
                        .chatId(message.getChatId().toString())
                        .text("Введите номер Вашей карты\n\nПримечание: Средства поступят в течение тридцати минут")
                        .build()
                );
                isSellOrder = true;
            }
        }
    }

    @Override
    @SneakyThrows
    public void onUpdateReceived(Update update) {
        // бизнес-процесс
        if (update.hasCallbackQuery()) {
            handleCallback(update.getCallbackQuery());
        } else if (update.hasMessage()) {
            Message message = update.getMessage();
            ReplyKeyboardMarkup replyKeyboardMarkup = new ReplyKeyboardMarkup();

            List<KeyboardRow> buttons = new ArrayList<>();
            KeyboardRow firstRow = new KeyboardRow();
//            KeyboardRow secondRow = new KeyboardRow();

            KeyboardButton sellButton = new KeyboardButton("Продать");
            KeyboardButton buyButton = new KeyboardButton("Купить");
//            KeyboardButton sell1Button = new KeyboardButton("Продать еще");


            firstRow.add(sellButton);
            firstRow.add(buyButton);
//            secondRow.add(sell1Button);

            buttons.add(firstRow);
//            buttons.add(secondRow);

            replyKeyboardMarkup.setKeyboard(buttons);
            replyKeyboardMarkup.setResizeKeyboard(true);

            if (message.getText().equals("/start")) {
                execute(SendMessage.builder()
                        .chatId(message.getChatId().toString())
                        .text("Привет!")
                        .replyMarkup(replyKeyboardMarkup)
                        .build()
                );
            } else if (message.getText().equals("Продать")) {
                Message typing = execute(SendSticker.builder()
                        .chatId(message.getChatId().toString())
                        .sticker(new InputFile("CAACAgIAAxkBAAPzYxNKV2Iwmm35fNo4cpcAASn3I6Y2AAI4CwACTuSZSzKxR9LZT4zQKQQ"))
                        .build());

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
                String text = "Выберите валюту\n\n" +
                        "USDT -> RUB: " + Parce.courseSell("USDTRUB") + "\n" +
                        "BTC -> RUB: " + Parce.courseSell("BTCRUB") + "\n" +
                        "ETH -> RUB: " + Parce.courseSell("ETHRUB") + "\n";

                execute(SendMessage.builder()
                        .chatId(message.getChatId().toString())
                        .text(text)
                        .replyMarkup(inlineKeyboardMarkup)
                        .build());

                execute(DeleteMessage.builder()
                        .chatId(typing.getChatId().toString())
                        .messageId(typing.getMessageId())
                        .build());
            } else if (message.getText().equals("Купить")) {
                Message typing = execute(SendSticker.builder()
                        .chatId(message.getChatId().toString())
                        .sticker(new InputFile("CAACAgIAAxkBAAPzYxNKV2Iwmm35fNo4cpcAASn3I6Y2AAI4CwACTuSZSzKxR9LZT4zQKQQ"))
                        .build());

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
                String text = "Выберите валюту\n\n" +
                        "USDT -> RUB: " + Parce.courseBuy("USDTRUB") + "\n" +
                        "BTC -> RUB: " + Parce.courseBuy("BTCRUB") + "\n" +
                        "ETH -> RUB: " + Parce.courseBuy("ETHRUB") + "\n";

                execute(SendMessage.builder()
                        .chatId(message.getChatId().toString())
                        .text(text)
                        .replyMarkup(inlineKeyboardMarkup)
                        .build());

                execute(DeleteMessage.builder()
                        .chatId(typing.getChatId().toString())
                        .messageId(typing.getMessageId())
                        .build());
            } else if (isDigits(message.getText()) && !isSellOrder && !isBuyOrder) {
                InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
                List<List<InlineKeyboardButton>> buttonsArray = new ArrayList<>();
                InlineKeyboardButton makeOrder = new InlineKeyboardButton("Cоздать заявку");

                if (Objects.equals(lastOperation, "sell")) {
                    makeOrder.setCallbackData("order:sell:"+message.getText());
                    buttonsArray.add(Collections.singletonList(makeOrder));
                    inlineKeyboardMarkup.setKeyboard(buttonsArray);

                    execute(SendMessage.builder()
                            .chatId(message.getChatId().toString())
                            .text("Вы получите на карту " + Double.parseDouble(message.getText()) * Parce.courseSell(lastCurrency) + " рублей")
                            .replyMarkup(inlineKeyboardMarkup)
                            .build());
                } else if (Objects.equals(lastOperation, "buy")) {
                    makeOrder.setCallbackData("order:buy:"+message.getText());
                    buttonsArray.add(Collections.singletonList(makeOrder));
                    inlineKeyboardMarkup.setKeyboard(buttonsArray);

                    execute(SendMessage.builder()
                            .chatId(message.getChatId().toString())
                            .text("Вaм нужно оплатить " + Double.parseDouble(message.getText()) * Parce.courseBuy(lastCurrency) + " рублей")
                            .replyMarkup(inlineKeyboardMarkup)
                            .build());
                }
            } else if(isBuyOrder){
                // TODO add to db wallet number
                execute(SendMessage.builder()
                        .chatId(message.getChatId().toString())
                        .text("Переведите средства на карту Тинькофф : 2222 3333 4444 5555")
                        .build());
                isBuyOrder = false;
            } else if(isSellOrder){
                execute(SendMessage.builder()
                        .chatId(message.getChatId().toString())
                        .text("Ваша заявка принята, ожидайте оплаты в течение 30 минут")
                        .build());
                isSellOrder = false;
            }
            else {
                execute(SendMessage.builder()
                        .chatId(message.getChatId().toString())
                        .text("Неправильное значение")
                        .build());
            }
        }
    }

    // утилита
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
        Hello bot = new Hello();
        TelegramBotsApi telegramBotsApi = new TelegramBotsApi(DefaultBotSession.class);
        telegramBotsApi.registerBot(bot);
    }
}

