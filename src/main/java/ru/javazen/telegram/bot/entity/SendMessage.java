package ru.javazen.telegram.bot.entity;

import org.codehaus.jackson.annotate.JsonProperty;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class SendMessage {

    public SendMessage() { };

    public SendMessage(long chatId, String text) {
        this.chatId = chatId;
        this.text = text;
    }

    @JsonProperty("chat_id")
    private long chatId;

    @JsonProperty("text")
    private String text;

    public long getChatId() {
        return chatId;
    }

    public void setChatId(long chatId) {
        this.chatId = chatId;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
}
