package ru.javazen.telegram.bot.entity.response;

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

    @JsonProperty("reply_to_message_id")
    private Long replyMessageId;

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

    public Long getReplyMessageId() {
        return replyMessageId;
    }

    public void setReplyMessageId(Long replyMessageId) {
        this.replyMessageId = replyMessageId;
    }
}
