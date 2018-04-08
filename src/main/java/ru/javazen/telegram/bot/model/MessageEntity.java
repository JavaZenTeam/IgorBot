package ru.javazen.telegram.bot.model;

import org.telegram.telegrambots.api.objects.Message;
import ru.javazen.telegram.bot.util.MessageHelper;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table
public class MessageEntity {
    @EmbeddedId
    private MessagePK messagePK;

    @MapsId("chatId")
    @JoinColumn(name = "chat_id")
    @ManyToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    private ChatEntity chat;

    @JoinColumn(name="user_id")
    @ManyToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    private UserEntity user;

    @Column(length = 4096)
    private String text;

    @Column
    private Date date;

    public MessageEntity() {
    }

    public MessageEntity(Message message) {
        this.messagePK = new MessagePK(message.getChatId(), message.getMessageId());
        this.chat = new ChatEntity(message.getChat());
        this.user = new UserEntity(message.getFrom());
        this.text = MessageHelper.getActualText(message);
        this.date = new Date(1000L * message.getDate());
    }

    public MessagePK getMessagePK() {
        return messagePK;
    }

    public void setMessagePK(MessagePK messagePK) {
        this.messagePK = messagePK;
    }

    public ChatEntity getChat() {
        return chat;
    }

    public void setChat(ChatEntity chat) {
        this.chat = chat;
    }

    public UserEntity getUser() {
        return user;
    }

    public void setUser(UserEntity user) {
        this.user = user;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    @Override
    public String toString() {
        return "MessageEntity{" +
                "messagePK=" + messagePK +
                ", chat=" + chat +
                ", user=" + user +
                ", text='" + text + '\'' +
                ", date=" + date +
                '}';
    }
}
