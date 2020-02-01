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
public class VoiceMessageEntity {

    @Id
    @Column(length = 4096)
    private String text;

    @Column(length = 4096)
    private String fileId;
}
