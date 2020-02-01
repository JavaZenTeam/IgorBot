package ru.javazen.telegram.bot.model;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.io.Serializable;
import java.util.Objects;

@Embeddable
public class VoiceMessagePK implements Serializable {

    @Column
    private Integer userId;

    @Column
    private String messageId;

    public VoiceMessagePK() {
    }

    public VoiceMessagePK(Integer userId, String messageId) {
        this.userId = userId;
        this.messageId = messageId;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public String getMessageId() {
        return messageId;
    }

    public void setMessageId(String messageId) {
        this.messageId = messageId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        VoiceMessagePK that = (VoiceMessagePK) o;
        return Objects.equals(userId, that.userId) &&
                Objects.equals(messageId, that.messageId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(userId, messageId);
    }

    @Override
    public String toString() {
        return "MessagePK{" +
                "chatId=" + userId +
                ", messageId=" + messageId +
                '}';
    }
}
