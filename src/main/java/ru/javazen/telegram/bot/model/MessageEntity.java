package ru.javazen.telegram.bot.model;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.util.Date;
import java.util.Objects;

@Entity
@Table
public class MessageEntity {
    @EmbeddedId
    private MessagePK messagePK;

    @Column
    private Integer userId;

    @Column(length = 4096)
    private String text;

    @Column
    private Date date;

    public MessagePK getMessagePK() {
        return messagePK;
    }

    public void setMessagePK(MessagePK messagePK) {
        this.messagePK = messagePK;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
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
                && Objects.equals(userId, message.userId)
                && Objects.equals(text, message.text)
                && Objects.equals(date, message.date);

    }

    @Override
    public int hashCode() {
        int result = messagePK != null ? messagePK.hashCode() : 0;
        result = 31 * result + (userId != null ? userId.hashCode() : 0);
        result = 31 * result + (text != null ? text.hashCode() : 0);
        result = 31 * result + (date != null ? date.hashCode() : 0);
        return result;
    }
}
