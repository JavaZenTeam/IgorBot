package ru.javazen.telegram.bot.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table
public class ChatEntity {
    @Id
    private long chatId;

    @Column(length = 32)
    private String username;

    @Column(length = 512)
    private String title;

    public long getChatId() {
        return chatId;
    }

    public void setChatId(long chatId) {
        this.chatId = chatId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    @Override
    public String toString() {
        return "ChatEntity{" +
                "chatId=" + chatId +
                ", username='" + username + '\'' +
                ", title='" + title + '\'' +
                '}';
    }
}
