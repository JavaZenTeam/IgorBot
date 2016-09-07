package ru.javazen.telegram.bot.handler;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.Assert;
import ru.javazen.telegram.bot.Bot;
import ru.javazen.telegram.bot.entity.request.Update;
import ru.javazen.telegram.bot.service.MessageHelper;
import ru.javazen.telegram.bot.service.TelegramBotService;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.function.BiFunction;

public class RandomAnswer implements UpdateHandler {

    @Autowired
    private TelegramBotService botService;

    @Autowired
    private Random random;

    private Map<String, Integer> answers = Collections.emptyMap();
    private List<BiFunction<Update, String, String>> preprocessors;
    private int sum;

    @Override
    public boolean handle(Update update) {
        String text = update.getMessage().getText();
        if (text == null) return false;

        if (preprocessors != null && !preprocessors.isEmpty()){
            for (BiFunction<Update, String, String> preprocessor : preprocessors) {
                text = preprocessor.apply(update, text);
            }
        }

        String answer = solveAnswer(text);
        if (answer == null) return false;

        botService.sendMessage(MessageHelper.answer(update.getMessage(), answer, true));
        return true;
    }

    private String solveAnswer(String text){
        random.setSeed(text.toLowerCase().hashCode());
        int rand = random.nextInt(sum);
        for (Map.Entry<String, Integer> entry : answers.entrySet()) {
            if (rand < entry.getValue()){
                return entry.getKey();
            }
            rand -= entry.getValue();
        }
        return null;
    }

    public void setAnswers(Map<String, Integer> answers) {
        Assert.notNull(answers, "answers can not be null");

        int newSum = 0;
        for (Integer ratio: answers.values()) {
            Assert.isTrue(ratio >= 0, "ratio of answer can not be negative");
            newSum += ratio;
        }
        sum = newSum;
        this.answers = answers;
    }

    public void setPreprocessors(List<BiFunction<Update, String, String>> preprocessors) {
        this.preprocessors = preprocessors;
    }
}
