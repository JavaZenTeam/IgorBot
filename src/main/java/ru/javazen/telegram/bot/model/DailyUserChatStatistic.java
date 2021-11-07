package ru.javazen.telegram.bot.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.MapsId;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class DailyUserChatStatistic {
    @EmbeddedId
    private DailyUserChatPK pk;

    @MapsId("chatId")
    @JoinColumn(name = "chat_id")
    @ManyToOne
    private ChatEntity chat;

    @MapsId("userId")
    @JoinColumn(name = "user_id")
    @ManyToOne
    private UserEntity user;

    private Long count;

    private Long textLength;

    private Double score;
}
