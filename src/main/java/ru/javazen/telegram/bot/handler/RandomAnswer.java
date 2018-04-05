package ru.javazen.telegram.bot.handler;

import org.springframework.beans.factory.annotation.Autowired;
import org.telegram.telegrambots.api.objects.Update;
import org.telegram.telegrambots.bots.AbsSender;
import org.telegram.telegrambots.exceptions.TelegramApiException;
import ru.javazen.telegram.bot.container.SizedItemsContainer;
import ru.javazen.telegram.bot.util.MessageHelper;

import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.function.BiFunction;

public class RandomAnswer implements UpdateHandler {
    private Random random;
    private List<BiFunction<Update, String, String>> preprocessors;
    private SizedItemsContainer<String> container;

    @Override
    public boolean handle(Update update, AbsSender sender) throws TelegramApiException {
        String text = MessageHelper.getActualText(update.getMessage());
        if (text == null) return false;

        if (preprocessors != null && !preprocessors.isEmpty()){
            for (BiFunction<Update, String, String> preprocessor : preprocessors) {
                text = preprocessor.apply(update, text);
            }
        }
        random.setSeed(text.toLowerCase().hashCode());
        String answer = container.get(random.nextDouble() * container.size());
        if (answer == null) return false;

        sender.execute(MessageHelper.answer(update.getMessage(), answer, true));
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

    public void setAnswers(Map<String, Double> answers){
        answers.forEach((option, ratio) -> container.put(option, ratio));
    }

    public void setPreprocessors(List<BiFunction<Update, String, String>> preprocessors) {
        this.preprocessors = preprocessors;
    }
}
