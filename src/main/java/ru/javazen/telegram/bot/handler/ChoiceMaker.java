package ru.javazen.telegram.bot.handler;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Required;
import ru.javazen.telegram.bot.Bot;
import ru.javazen.telegram.bot.entity.request.Update;
import ru.javazen.telegram.bot.service.MessageHelper;
import ru.javazen.telegram.bot.service.TelegramBotService;

import java.util.*;
import java.util.function.BiFunction;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class ChoiceMaker implements UpdateHandler{
    private static final String OPTIONS_GROUP_NAME = "options";

    @Autowired
    private TelegramBotService botService;

    private Pattern pattern;
    private String splitPattern;
    private Comparator<String> comparator;
    private List<String> options;
    private List<BiFunction<Update, String, String>> preprocessors;

    public void setPreprocessors(List<BiFunction<Update, String, String>> preprocessors) {
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
    public boolean handle(Update update) {
        String text = update.getMessage().getText();
        if (text == null) return false;

        Matcher matcher = pattern.matcher(text);
        if (!matcher.matches()) return false;

        parseParameters(update, matcher);
        String choice = makeChoice(update);

        if (choice == null) return false;

        botService.sendMessage(MessageHelper.answer(update.getMessage(), choice));
        return true;
    }

    private String makeChoice(Update update) {
        if (options == null || options.isEmpty()) return null;

        if (preprocessors == null || preprocessors.isEmpty()){
            return Collections.max(options, getComparator());
        }

        Map<String, String> processedOptions = options.stream()
                .collect(Collectors.toMap(
                        o -> process(o, update),
                        o -> o,
                        (o1, o2) -> o1
                ));

        String choice = Collections.max(processedOptions.keySet(), getComparator());
        return processedOptions.get(choice);
    }

    protected String process(String string, Update update){
        for (BiFunction<Update, String, String> preprocessor : preprocessors) {
            string = preprocessor.apply(update, string);
        }
        return string;
    }

    protected void parseParameters(Update update, Matcher matcher) {
        String optionsGroup = matcher.group(OPTIONS_GROUP_NAME);
        if (optionsGroup == null) return;
        options = Arrays.asList(optionsGroup.split(splitPattern));
    }
}

