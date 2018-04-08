package ru.javazen.telegram.bot.model;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;

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
    public String toString() {
        return "ChatConfig{" +
                "chatConfigPK=" + chatConfigPK +
                ", value='" + value + '\'' +
                '}';
    }
}
