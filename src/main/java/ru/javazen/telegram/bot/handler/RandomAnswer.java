package ru.javazen.telegram.bot.handler;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.Assert;
import ru.javazen.telegram.bot.entity.request.Update;
import ru.javazen.telegram.bot.method.TelegramMethod;
import ru.javazen.telegram.bot.service.MessageHelper;

import java.util.Collections;
import java.util.Map;
import java.util.Random;

public class RandomAnswer implements UpdateHandler {

    @Autowired
    private Random random;

    private Map<String, Integer> answers = Collections.emptyMap();
    private int sum;

    public TelegramMethod handle(Update update) {
        String text = update.getMessage().getText();
        if (text == null) return null;

        String answer = solveAnswer(text);
        if (answer == null) return null;

        return MessageHelper.answerWithReply(update.getMessage(), answer);
    }

    private String solveAnswer(String text){
        random.setSeed(text.hashCode());
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
}
