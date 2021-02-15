package ru.javazen.telegram.bot.util;

import lombok.AllArgsConstructor;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@AllArgsConstructor(staticName = "getInstance")
public class WordSplitter implements Function<String, List<String>> {
    private static final Pattern SPLIT_PATTERN = Pattern.compile("[,.?!():;\"']*(^|\\s+|$)[,.?!():;\"']*", Pattern.UNICODE_CHARACTER_CLASS);

    @Override
    public List<String> apply(String s) {
        return Optional.ofNullable(s)
                .map(string -> SPLIT_PATTERN.splitAsStream(string)
                        .filter(token -> !token.isEmpty())
                        .collect(Collectors.toList()))
                .orElse(Collections.emptyList());
    }
}
