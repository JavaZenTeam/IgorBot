package ru.javazen.telegram.bot.model;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
@Data
public class DictionaryWord {
    @Id
    private String word;

    private Long count;
}
