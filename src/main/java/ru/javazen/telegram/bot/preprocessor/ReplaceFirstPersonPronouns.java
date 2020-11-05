package ru.javazen.telegram.bot.preprocessor;

import org.telegram.telegrambots.meta.api.objects.Message;

import java.util.List;
import java.util.function.BiFunction;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class ReplaceFirstPersonPronouns implements BiFunction<Message, String, String> {
    private List<Pattern> pronounPatterns;

    public ReplaceFirstPersonPronouns(List<String> pronouns) {
        pronounPatterns = pronouns.stream()
                .map(s -> "\\b" + s + "\\b")
                .map(s -> Pattern.compile(s, Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE))
                .collect(Collectors.toList());
    }

    @Override
    public String apply(Message message, String text) {
        String userId = Long.toString(message.getFrom().getId());
        for (Pattern pattern : pronounPatterns) {
            text = pattern.matcher(text).replaceAll(userId);
        }
        return text;
    }
}
