package ru.javazen.telegram.bot.model;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.io.Serializable;

@Embeddable
public class MessagePK implements Serializable {

    @Column
    private long chatId;

    @Column
    private int messageId;

    public MessagePK() {
    }

    public MessagePK(long chatId, int messageId) {
        this.chatId = chatId;
        this.messageId = messageId;
    }

    public long getChatId() {
        return chatId;
    }

    public void setChatId(long chatId) {
        this.chatId = chatId;
    }

    public int getMessageId() {
        return messageId;
    }

    public void setMessageId(int messageId) {
        this.messageId = messageId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        MessagePK that = (MessagePK) o;

        return chatId == that.chatId && messageId == that.messageId;
    }

    @Override
    public int hashCode() {
        int result = (int) (chatId ^ (chatId >>> 32));
        result = 31 * result + messageId;
        return result;
    }
}
