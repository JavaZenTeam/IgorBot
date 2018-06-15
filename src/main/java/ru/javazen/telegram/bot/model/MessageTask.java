package ru.javazen.telegram.bot.model;

import javax.persistence.*;
import java.time.OffsetDateTime;

@Entity
@Table(name = "MESSAGE_TASK")
public class MessageTask {

    @Id
    @Column(name="ID")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "TIME_OF_COMPLETION")
    private OffsetDateTime timeOfCompletion;

    @Column(name = "SCHEDULED_TEXT")
    private String scheduledText;

    @Column(name = "REPLY_MESSAGE_ID")
    private Long replyMessageId;

    @Column(name = "IS_COMPLETED")
    private Boolean isCompleted;

    @Column(name = "USER_ID")
    private Long userId;

    @Column(name = "CHAT_ID")
    private Long chatId;

    @Column(name = "MESSAGE_ID")
    private Long messageId;

    @Column(name = "BOT_NAME")
    private String botName;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public OffsetDateTime getTimeOfCompletion() {
        return timeOfCompletion;
    }

    public void setTimeOfCompletion(OffsetDateTime timeOfCompletion) {
        this.timeOfCompletion = timeOfCompletion;
    }

    public String getScheduledText() {
        return scheduledText;
    }

    public void setScheduledText(String scheduledText) {
        this.scheduledText = scheduledText;
    }

    public Long getReplyMessageId() {
        return replyMessageId;
    }

    public void setReplyMessageId(Long replyMessageId) {
        this.replyMessageId = replyMessageId;
    }

    public Boolean getCompleted() {
        return isCompleted;
    }

    public void setCompleted(Boolean completed) {
        isCompleted = completed;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Long getChatId() {
        return chatId;
    }

    public void setChatId(Long chatId) {
        this.chatId = chatId;
    }

    public Long getMessageId() {
        return messageId;
    }

    public void setMessageId(Long messageId) {
        this.messageId = messageId;
    }

    public String getBotName() {
        return botName;
    }

    public void setBotName(String botName) {
        this.botName = botName;
    }

    @Override
    public String toString() {
        return "MessageTask{" +
                "id=" + id +
                ", timeOfCompletion=" + timeOfCompletion +
                ", scheduledText='" + scheduledText + '\'' +
                ", replyMessageId=" + replyMessageId +
                ", isCompleted=" + isCompleted +
                ", userId=" + userId +
                ", chatId=" + chatId +
                ", messageId=" + messageId +
                ", botName='" + botName + '\'' +
                '}';
    }
}
