package ru.javazen.telegram.bot.model;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.util.Objects;

@Entity
@Table
public class ChatConfig {
    @EmbeddedId
    private ChatConfigPK chatConfigPK;

    @Column
    private String value;

    public ChatConfig() {
    }

    public ChatConfig(long chatId, String key, String value) {
        this.chatConfigPK = new ChatConfigPK(chatId, key);
        this.value = value;
    }

    public ChatConfig(ChatConfigPK chatConfigPK, String value) {
        this.chatConfigPK = chatConfigPK;
        this.value = value;
    }

    public ChatConfigPK getChatConfigPK() {
        return chatConfigPK;
    }

    public void setChatConfigPK(ChatConfigPK chatConfigPK) {
        this.chatConfigPK = chatConfigPK;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ChatConfig that = (ChatConfig) o;

        return Objects.equals(chatConfigPK, that.chatConfigPK)
                && Objects.equals(value, that.value);

    }

    @Override
    public int hashCode() {
        int result = chatConfigPK != null ? chatConfigPK.hashCode() : 0;
        result = 31 * result + (value != null ? value.hashCode() : 0);
        return result;
    }
}
