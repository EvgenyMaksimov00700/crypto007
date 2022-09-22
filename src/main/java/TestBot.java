import lombok.SneakyThrows;
import org.telegram.telegrambots.bots.DefaultAbsSender;
import org.telegram.telegrambots.bots.DefaultBotOptions;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

public class TestBot extends DefaultAbsSender {
    protected TestBot(DefaultBotOptions options) {
        super(options);
    }

    @Override
    // TODO Enter Bot Token
    public String getBotToken() {
        return "5583673029:AAHy-SwuMOvWtSkZUK1UjhA3l8ArlzlFZnE";
    }

    @SneakyThrows
    public static void main(String[] args) {
        TestBot testBot = new TestBot(new DefaultBotOptions());
        // TODO Enter chat id
        testBot.execute(SendMessage.builder().chatId("159619887").text("Привет, МИР").build());
    }
}
