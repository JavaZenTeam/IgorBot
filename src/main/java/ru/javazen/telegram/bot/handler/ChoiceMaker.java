package ru.javazen.telegram.bot.handler;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Required;
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
    private static final BiFunction<Update, String, String> DUMP_PROCESSOR = (udp, str) -> str;

    @Autowired
    private TelegramBotService botService;

    private Pattern pattern;
    private String splitPattern;
    private Comparator<String> comparator = Comparator.naturalOrder();
    private List<String> options;
    private BiFunction<Update, String, String> processorsChain = DUMP_PROCESSOR;

    public void setPreprocessors(List<BiFunction<Update, String, String>> preprocessors) {
        this.processorsChain = preprocessors
                .stream()
                .reduce((p1, p2) ->
                    (upd, str) -> p1.apply(upd,p2.apply(upd, str)))
                .orElse(DUMP_PROCESSOR);
    }

    @Required
    public void setPattern(String pattern) {
        this.pattern = Pattern.compile(pattern, Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE | Pattern.DOTALL);
    }

    @Required
    public void setSplitPattern(String splitPattern) {
        this.splitPattern = splitPattern;
    }

    public void setComparator(Comparator<String> comparator) {
        this.comparator = comparator;
    }

    public Comparator<String> getComparator() {
        return comparator;
    }

    @Override
    public boolean handle(Update update) {
        String text = MessageHelper.getActualText(update.getMessage());
        if (text == null) return false;

        Matcher matcher = pattern.matcher(text);
        if (!matcher.matches()) return false;

        String choice = parseParamsAndMakeChoice(update, matcher);
        if (choice == null) return false;

        botService.sendMessage(MessageHelper.answer(update.getMessage(), choice));
        return true;
    }

    private synchronized String parseParamsAndMakeChoice(Update update, Matcher matcher) {
        parseParameters(update, matcher);
        return makeChoice(update);
    }

    private String makeChoice(Update update) {
        if (options == null || options.isEmpty()) return null;

        //KEY = processed option, VALUE = original option
        Map<String, String> processedOptions = options.stream()
                .collect(Collectors.toMap(
                        o -> process(o, update),
                        o -> o,
                        (o1, o2) -> o1
                ));

        String choice = Collections.max(processedOptions.keySet(), getComparator());
        return processedOptions.get(choice);
    }

    String process(String string, Update update){
        return processorsChain.apply(update, string);
    }

    void parseParameters(Update update, Matcher matcher) {
        String optionsGroup = matcher.group(OPTIONS_GROUP_NAME);
        if (optionsGroup == null) return;
        options = Arrays.asList(optionsGroup.split(splitPattern));
    }
}

