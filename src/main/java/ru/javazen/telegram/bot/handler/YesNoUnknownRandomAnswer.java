package ru.javazen.telegram.bot.handler;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;
import ru.javazen.telegram.bot.entity.SendMessage;
import ru.javazen.telegram.bot.entity.Update;
import ru.javazen.telegram.bot.method.SendMessageMethod;
import ru.javazen.telegram.bot.method.TelegramMethod;

import java.util.Random;
import java.util.regex.Pattern;

@Component
public class YesNoUnknownRandomAnswer implements UpdateHandler {

    @Autowired
    private Random random;

    private Pattern pattern = Pattern.compile("bot,.*?");;

    private byte yesRatio = 1;
    private byte noRatio = 1;
    private byte unknownRatio = 1;

    private String yesAnswer = "Yes";
    private String noAnswer = "No";
    private String unknownAnswer = "I don`t know";

    public TelegramMethod handle(Update update) {
        String text = update.getMessage().getText();
        if (text == null) return null;

        text = text.toLowerCase();
        if (!pattern.matcher(text).matches()) return null;

        String answer = solveAnswer(text);

        SendMessage message = new SendMessage();
        message.setChatId(update.getMessage().getChat().getId());
        message.setText(answer);
        return new SendMessageMethod(message);
    }

    private String solveAnswer(String text){
        random.setSeed(text.hashCode());
        int sum = yesRatio + noRatio + unknownRatio;
        int rand = random.nextInt(sum);
        if (rand < yesRatio) return yesAnswer;
        if (rand > sum - noRatio) return noAnswer;
        return unknownAnswer;
    }

    public void setAddress(String address) {
        pattern = Pattern.compile(address.toLowerCase() + ",.*?");
    }

    public void setYesRatio(byte yesRatio) {
        Assert.isTrue(yesRatio >= 0, "probability can not be negative");
        this.yesRatio = yesRatio;
    }

    public void setNoRatio(byte noRatio) {
        Assert.isTrue(noRatio >= 0, "probability can not be negative");
        this.noRatio = noRatio;
    }

    public void setUnknownRatio(byte unknownRatio) {
        Assert.isTrue(unknownRatio >= 0, "probability can not be negative");
        this.unknownRatio = unknownRatio;
    }

    public void setYesAnswer(String yesAnswer) {
        this.yesAnswer = yesAnswer;
    }

    public void setNoAnswer(String noAnswer) {
        this.noAnswer = noAnswer;
    }

    public void setUnknownAnswer(String unknownAnswer) {
        this.unknownAnswer = unknownAnswer;
    }
}
