package ru.javazen.telegram.bot.model;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Table
@Entity
@Getter
@Setter
public class BotUsageLog {
    @EmbeddedId
    @AttributeOverrides({
            @AttributeOverride(name = "chatId", column = @Column(name = "target_chat_id")),
            @AttributeOverride(name = "messageId", column = @Column(name = "target_message_id")),
    })
    private MessagePK target;

    @ManyToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @AttributeOverrides({
            @AttributeOverride(name = "chatId", column = @Column(name = "source_chat_id")),
            @AttributeOverride(name = "messageId", column = @Column(name = "source_message_id")),
    })
    private MessageEntity source;

    @Column
    private String moduleName;

    @Column(length = 4096)
    private String text;
}
