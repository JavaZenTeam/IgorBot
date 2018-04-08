package ru.javazen.telegram.bot.model;

import org.telegram.telegrambots.api.objects.Chat;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Objects;
import java.util.stream.Stream;

import static java.util.stream.Collectors.joining;

@Entity
@Table
public class ChatEntity {
    @Id
    private long chatId;

    @Column(length = 32)
    private String username;

    @Column(length = 512)
    private String title;

    public ChatEntity() {
    }

    public ChatEntity(long chatId, String username, String title) {
        this.chatId = chatId;
        this.username = username;
        this.title = title;
    }

    public ChatEntity(Chat chat) {
        this.chatId = chat.getId();
        this.username = chat.getUserName();
        this.title = !chat.isUserChat() ? chat.getTitle() :
                Stream.of(chat.getFirstName(), chat.getLastName()).filter(Objects::nonNull).collect(joining(" "));
    }

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
