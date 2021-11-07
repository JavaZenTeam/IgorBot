package ru.javazen.telegram.bot.model;

import lombok.Data;

import javax.persistence.Embeddable;
import java.io.Serializable;
import java.time.LocalDate;

@Embeddable
@Data
public class DailyUserChatPK implements Serializable {
    private Integer userId;
    private Long chatId;
    private LocalDate date;
}
