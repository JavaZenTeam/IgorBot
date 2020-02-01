package ru.javazen.telegram.bot.model;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;

@Entity
@Table
@Getter
@Setter
@ToString
public class UserVoiceMessage {

    @EmbeddedId
    private VoiceMessagePK messagePK;

    @MapsId("userId")
    @JoinColumn(name = "user_id")
    @ManyToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    private UserEntity user;

    @MapsId("messageId")
    @JoinColumn(name = "message_id")
    @ManyToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    private VoiceMessageEntity message;

    private long count;
}
