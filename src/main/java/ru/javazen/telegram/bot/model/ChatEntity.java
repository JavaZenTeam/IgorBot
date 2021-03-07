package ru.javazen.telegram.bot.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import java.util.Optional;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ChatEntity implements IdSupplier, LabelSupplier {
    @Id
    private long chatId;

    @Column(length = 32)
    private String username;

    @Column(length = 512)
    private String title;

    @Override
    public String getLabel() {
        return Optional.ofNullable(getTitle())
                .orElse("Private chat");
    }

    @Override
    public long getId() {
        return getChatId();
    }
}
