package ru.javazen.telegram.bot.entity.response;

import org.codehaus.jackson.annotate.JsonProperty;

public enum ParseMode {
    @JsonProperty("HTML") HTML,
    @JsonProperty("Markdown") MARKDOWN
}
