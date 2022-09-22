import com.sun.xml.internal.ws.resources.SenderMessages;
import lombok.SneakyThrows;
import org.telegram.telegrambots.bots.DefaultBotOptions;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.methods.send.SendSticker;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

public class Echo extends TelegramLongPollingBot {

    String RafailChatId = "429272623";
    String EvgeniiChatId = "159619887";

    @Override
    public String getBotUsername() {
        return "@crypto00700bot";
    }

    @Override
    public String getBotToken() {
        return "5583673029:AAHy-SwuMOvWtSkZUK1UjhA3l8ArlzlFZnE";
    }

    @Override
    @SneakyThrows
    public void onUpdateReceived(Update update) {
        if (update.hasMessage()) {
            Message message = update.getMessage();
//            if (message.getChatId().toString().equals(RafailChatId)) {
//                execute(SendMessage.builder()
//                        .chatId(EvgeniiChatId)
//                        .text(message.getText())
//                        .build()
//                );
//            }
//            if (message.getChatId().toString().equals(EvgeniiChatId)) {
//                execute(SendMessage.builder()
//                        .chatId(RafailChatId)
//                        .text(message.getText())
//                        .build()
//                );
//            }


            System.out.println(message.getPhoto().get(0).getFileId());
            execute(SendPhoto.builder()
                    .chatId(message.getChatId().toString())
                    .photo(new InputFile(message.getPhoto().get(0).getFileId()))
                    .build()
            );

        }

    }

    @SneakyThrows
    public static void main(String[] args) {
        Echo bot = new Echo();
        TelegramBotsApi telegramBotsApi = new TelegramBotsApi(DefaultBotSession.class);
        telegramBotsApi.registerBot(bot);
    }


}
