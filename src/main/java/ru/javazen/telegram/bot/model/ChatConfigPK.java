package ru.javazen.telegram.bot.model;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import java.io.Serializable;
import java.util.Objects;

@Embeddable
public class ChatConfigPK implements Serializable {

    @Column
    private long chatId;

    @Column
    private String key;

    public ChatConfigPK() {
    }

    public ChatConfigPK(long chatId, String key) {
        this.chatId = chatId;
        this.key = key;
    }

    public long getChatId() {
        return chatId;
    }

    public void setChatId(long chatId) {
        this.chatId = chatId;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ChatConfigPK that = (ChatConfigPK) o;

        return chatId == that.chatId && Objects.equals(key, that.key);

    }

    @Override
    public int hashCode() {
        int result = (int) (chatId ^ (chatId >>> 32));
        result = 31 * result + key.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return "ChatConfigPK{" +
                "chatId=" + chatId +
                ", key='" + key + '\'' +
                '}';
    }
}
