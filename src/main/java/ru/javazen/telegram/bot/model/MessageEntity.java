package ru.javazen.telegram.bot.model;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table
@Getter
@Setter
@ToString
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

    private int textLength;

    private double score;

    @Column
    private Date date;

    @Column(length = 4096)
    private String fileId;

    private String fileUniqueId;

    @Column(length = 10)
    @Enumerated(EnumType.STRING)
    private FileType fileType;

    @JoinColumn(name="forward_user_id")
    @ManyToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    private UserEntity forwardFrom;
}
