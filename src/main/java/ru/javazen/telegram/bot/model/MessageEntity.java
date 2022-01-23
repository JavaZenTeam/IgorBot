package ru.javazen.telegram.bot.model;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;
import java.util.*;

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

    @JoinColumn(name = "user_id")
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

    @JoinColumn(name = "forward_user_id")
    @ManyToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    private UserEntity forwardFrom;

    @Enumerated(EnumType.STRING)
    private EventType eventType;

    @JoinTable(
            name = "message_entity_member",
            joinColumns = {
                    @JoinColumn(name = "chat_id"),
                    @JoinColumn(name = "message_id")
            },
            inverseJoinColumns = @JoinColumn(name = "user_id")
    )
    @ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    private Set<UserEntity> members;

    public void setMember(UserEntity member) {
        setMembers(Collections.singleton(member));
    }

    public UserEntity getMember() {
        return members.stream().findFirst().orElse(null);
    }

    public String getSmallFileId() {
        return Optional.ofNullable(getFileId())
                .map(s -> s.split(","))
                .map(Arrays::asList)
                .flatMap(list -> list.stream().findFirst())
                .orElse(null);
    }
}
