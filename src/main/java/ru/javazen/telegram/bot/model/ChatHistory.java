package ru.javazen.telegram.bot.model;

import lombok.Data;

import javax.persistence.Id;
import javax.persistence.ManyToOne;
import java.util.Date;

//@Entity
@Data
public class ChatHistory {
    @Id
    private Long historyId;

    @ManyToOne
    private ChatEntity chat;

    @ManyToOne
    private UserEntity user;

    private Date date;

    private String property;

    private String value;
}
