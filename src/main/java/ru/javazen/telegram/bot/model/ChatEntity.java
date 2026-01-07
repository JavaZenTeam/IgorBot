package ru.javazen.telegram.bot.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.persistence.*;
import java.math.BigInteger;
import java.util.Optional;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ChatEntity implements IdSupplier, LabelSupplier {
    @Id
    private Long chatId;

    @Column(length = 32)
    private String username;

    @Column(length = 512)
    private String title;

    @Enumerated(EnumType.STRING)
    private ChatType type;

    @Override
    public String getLabel() {
        return Optional.ofNullable(getTitle())
                .orElse("Private chat");
    }

    @Override
    public Long getId() {
        return getChatId();
    }

    public ChatEntity(BigInteger chatId, String username, String title) {
        this(chatId.longValue(), username, title, ChatType.UNKNOWN);
    }
}
