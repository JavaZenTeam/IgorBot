package ru.javazen.telegram.bot.model;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.CascadeType;
import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.MapsId;
import javax.persistence.Table;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

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

    @Column(precision = 8, scale = 7)
    private BigDecimal tone;

    private int textLength;

    private double score;

    @Column
    private Date date;

    @Column(length = 4096)
    private String fileId;

    @Column(length = 10)
    @Enumerated(EnumType.STRING)
    private FileType fileType;

    @JoinColumn(name="forward_user_id")
    @ManyToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    private UserEntity forwardFrom;

    @ElementCollection
    @Column(name = "word")
    @CollectionTable(joinColumns = {
            @JoinColumn(name = "chat_id"),
            @JoinColumn(name = "message_id"),
    })
    private List<String> words;
}
