package ru.javazen.telegram.bot.entity.response;

import org.codehaus.jackson.annotate.JsonProperty;

public class ForwardMessage {

    @JsonProperty("chat_id")
    private String chatId;

    @JsonProperty("from_chat_id")
    private String fromChatId;

    @JsonProperty("message_id")
    private long messageId;

    public String getChatId() {
        return chatId;
    }

    public void setChatId(String chatId) {
        this.chatId = chatId;
    }

    public String getFromChatId() {
        return fromChatId;
    }

    public void setFromChatId(String fromChatId) {
        this.fromChatId = fromChatId;
    }

    public long getMessageId() {
        return messageId;
    }

    public void setMessageId(long messageId) {
        this.messageId = messageId;
    }
}
