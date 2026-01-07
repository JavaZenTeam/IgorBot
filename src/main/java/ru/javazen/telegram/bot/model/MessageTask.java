package ru.javazen.telegram.bot.model;

import jakarta.persistence.*;

@Entity
@Table(name = "MESSAGE_TASK")
public class MessageTask {

    @Id
    @Column(name="ID")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "TIME_OF_COMPLETION")
    private Long timeOfCompletion;

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

    @Column(name = "REPEAT_INTERVAL")
    private String repeatInterval;

    @Column(name = "REPEAT_COUNT")
    private Integer repeatCount;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getTimeOfCompletion() {
        return timeOfCompletion;
    }

    public void setTimeOfCompletion(Long timeOfCompletion) {
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

    public String getRepeatInterval() {
        return repeatInterval;
    }

    public void setRepeatInterval(String repeatInterval) {
        this.repeatInterval = repeatInterval;
    }

    public Integer getRepeatCount() {
        return repeatCount;
    }

    public void setRepeatCount(Integer repeatCount) {
        this.repeatCount = repeatCount;
    }

    @Override
    public String toString() {
        return "MessageTask{" +
                "id=" + id +
                ", timeOfCompletion=" + timeOfCompletion +
                ", scheduledText='" + scheduledText + '\'' +
                ", repeatInterval='" + repeatInterval + '\'' +
                ", repeatCount=" + repeatCount +
                ", replyMessageId=" + replyMessageId +
                ", isCompleted=" + isCompleted +
                ", userId=" + userId +
                ", chatId=" + chatId +
                ", messageId=" + messageId +
                ", botName='" + botName + '\'' +
                '}';
    }
}
