package ru.javazen.telegram.bot.handler;

import org.springframework.beans.factory.annotation.Autowired;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.bots.AbsSender;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import ru.javazen.telegram.bot.handler.base.TextMessageHandler;
import ru.javazen.telegram.bot.util.SizedItemsContainer;

import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.function.BiFunction;

public class RandomAnswer implements TextMessageHandler {
    private Random random;
    private List<BiFunction<Message, String, String>> preprocessors;
    private SizedItemsContainer<String> container;

    @Override
    public boolean handle(Message message, String text, AbsSender sender) throws TelegramApiException {
        if (preprocessors != null && !preprocessors.isEmpty()) {
            for (BiFunction<Message, String, String> preprocessor : preprocessors) {
                text = preprocessor.apply(message, text);
            }
        }
        random.setSeed(text.toLowerCase().hashCode());
        String answer = container.get(random.nextDouble() * container.size());
        if (answer == null) return false;

        SendMessage sendMessage = new SendMessage(message.getChatId().toString(), answer);
        sendMessage.setMessageThreadId(message.getMessageThreadId());
        sender.execute(sendMessage);
        return true;
    }

    @Autowired
    public void setRandom(Random random) {
        this.random = random;
    }

    @Autowired
    public void setContainer(SizedItemsContainer<String> container) {
        this.container = container;
    }

    public void setAnswers(Map<String, Double> answers) {
        answers.forEach((option, ratio) -> container.put(option, ratio));
    }

    public void setPreprocessors(List<BiFunction<Message, String, String>> preprocessors) {
        this.preprocessors = preprocessors;
    }
}
