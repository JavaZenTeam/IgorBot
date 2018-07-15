package ru.javazen.telegram.bot.handler;

import org.springframework.beans.factory.annotation.Required;
import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.objects.Message;
import org.telegram.telegrambots.bots.AbsSender;
import org.telegram.telegrambots.exceptions.TelegramApiException;
import ru.javazen.telegram.bot.handler.base.TextMessageHandler;

import java.util.*;
import java.util.function.BiFunction;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class ChoiceMaker implements TextMessageHandler {
    private static final String OPTIONS_GROUP_NAME = "options";

    private Pattern pattern;
    private String splitPattern;
    private Comparator<String> comparator;
    private List<String> options;
    private List<BiFunction<Message, String, String>> preprocessors;

    public void setPreprocessors(List<BiFunction<Message, String, String>> preprocessors) {
        this.preprocessors = preprocessors;
    }

    @Required
    public void setPattern(String pattern) {
        this.pattern = Pattern.compile(pattern, Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE | Pattern.DOTALL);
    }

    @Required
    public void setSplitPattern(String splitPattern) {
        this.splitPattern = splitPattern;
    }

    @Required
    public void setComparator(Comparator<String> comparator) {
        this.comparator = comparator;
    }

    public Comparator<String> getComparator() {
        return comparator;
    }

    @Override
    public boolean handle(Message message, String text, AbsSender sender) throws TelegramApiException {
        Matcher matcher = pattern.matcher(text);
        if (!matcher.matches()) return false;

        parseParameters(message, matcher);
        String choice = makeChoice(message);

        if (choice == null) return false;

        sender.execute(new SendMessage(message.getChatId(), choice));
        return true;
    }

    private String makeChoice(Message message) {
        if (options == null || options.isEmpty()) return null;

        if (preprocessors == null || preprocessors.isEmpty()){
            return Collections.max(options, getComparator());
        }

        Map<String, String> processedOptions = options.stream()
                .collect(Collectors.toMap(
                        o -> process(o, message),
                        o -> o,
                        (o1, o2) -> o1
                ));

        String choice = Collections.max(processedOptions.keySet(), getComparator());
        return processedOptions.get(choice);
    }

    protected String process(String string, Message message) {
        for (BiFunction<Message, String, String> preprocessor : preprocessors) {
            string = preprocessor.apply(message, string);
        }
        return string;
    }

    protected void parseParameters(Message message, Matcher matcher) {
        String optionsGroup = matcher.group(OPTIONS_GROUP_NAME);
        if (optionsGroup == null) return;
        options = Arrays.asList(optionsGroup.split(splitPattern));
    }
}

