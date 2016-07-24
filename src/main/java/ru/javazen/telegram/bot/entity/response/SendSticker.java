package ru.javazen.telegram.bot.entity.response;

import org.codehaus.jackson.annotate.JsonProperty;
import org.codehaus.jackson.map.annotate.JsonSerialize;

@JsonSerialize(include= JsonSerialize.Inclusion.NON_NULL)
public class SendSticker {
    public SendSticker() {
    }

    public SendSticker(String sticker, long chatId) {
        this.sticker = sticker;
        this.chatId = chatId;
    }

    @JsonProperty("chat_id")
    long chatId;

    @JsonProperty("sticker")
    String sticker;

    @JsonProperty("disable_notification")
    Boolean disableNotification;

    @JsonProperty("reply_to_message_id")
    Long replyMessageId;

    public long getChatId() {
        return chatId;
    }

    public void setChatId(long chatId) {
        this.chatId = chatId;
    }

    public String getSticker() {
        return sticker;
    }

    public void setSticker(String sticker) {
        this.sticker = sticker;
    }

    public Boolean getDisableNotification() {
        return disableNotification;
    }

    public void setDisableNotification(Boolean disableNotification) {
        this.disableNotification = disableNotification;
    }

    public Long getReplyMessageId() {
        return replyMessageId;
    }

    public void setReplyMessageId(Long replyMessageId) {
        this.replyMessageId = replyMessageId;
    }
}
