package ru.javazen.telegram.bot.model;

import javax.persistence.*;

@Table
@Entity
public class BotUsageLog {
    @EmbeddedId
    @AttributeOverrides({
            @AttributeOverride(name = "chatId", column = @Column(name = "chat_id")),
            @AttributeOverride(name = "messageId", column = @Column(name = "target_message_id")),
    })
    private MessagePK messagePK;

    @Column(name = "source_message_id")
    private Integer sourceMessageId;

    @Column
    private String moduleName;

    @Column(length = 4096)
    private String text;

    public MessagePK getMessagePK() {
        return messagePK;
    }

    public void setMessagePK(MessagePK messagePK) {
        this.messagePK = messagePK;
    }

    public Integer getSourceMessageId() {
        return sourceMessageId;
    }

    public void setSourceMessageId(Integer sourceMessageId) {
        this.sourceMessageId = sourceMessageId;
    }

    public String getModuleName() {
        return moduleName;
    }

    public void setModuleName(String moduleName) {
        this.moduleName = moduleName;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
}
