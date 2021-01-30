package ru.javazen.telegram.bot.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ChatEntity {
    @Id
    private long chatId;

    @Column(length = 32)
    private String username;

    @Column(length = 512)
    private String title;

}
