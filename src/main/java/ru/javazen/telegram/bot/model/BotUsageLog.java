package ru.javazen.telegram.bot.model;

import javax.persistence.*;

@Table
@Entity
public class BotUsageLog {
    @EmbeddedId
    @AttributeOverrides({
            @AttributeOverride(name = "chatId", column = @Column(name = "target_chat_id")),
            @AttributeOverride(name = "messageId", column = @Column(name = "target_message_id")),
    })
    private MessagePK target;

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "chatId", column = @Column(name = "source_chat_id")),
            @AttributeOverride(name = "messageId", column = @Column(name = "source_message_id")),
    })
    private MessagePK source;

    @Column
    private String moduleName;

    @Column(length = 4096)
    private String text;

    public MessagePK getTarget() {
        return target;
    }

    public void setTarget(MessagePK target) {
        this.target = target;
    }

    public MessagePK getSource() {
        return source;
    }

    public void setSource(MessagePK source) {
        this.source = source;
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
