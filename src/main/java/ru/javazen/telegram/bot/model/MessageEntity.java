package ru.javazen.telegram.bot.model;

import org.telegram.telegrambots.api.objects.Message;
import ru.javazen.telegram.bot.util.MessageHelper;

import javax.persistence.*;
import java.util.Date;
import java.util.Objects;

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
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        MessageEntity message = (MessageEntity) o;

        return Objects.equals(messagePK, message.messagePK)
                && Objects.equals(user, message.user)
                && Objects.equals(text, message.text)
                && Objects.equals(date, message.date);

    }

    @Override
    public int hashCode() {
        int result = messagePK != null ? messagePK.hashCode() : 0;
        result = 31 * result + (user != null ? user.hashCode() : 0);
        result = 31 * result + (text != null ? text.hashCode() : 0);
        result = 31 * result + (date != null ? date.hashCode() : 0);
        return result;
    }
}
