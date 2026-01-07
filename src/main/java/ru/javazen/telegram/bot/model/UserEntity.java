package ru.javazen.telegram.bot.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import java.math.BigInteger;
import java.util.Optional;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserEntity implements IdSupplier, LabelSupplier {
    @Id
    private Long userId;

    @Column
    private String firstName;

    @Column
    private String lastName;

    @Column(length = 32)
    private String username;

    private String languageCode;

    @Override
    public String getLabel() {
        return Optional.ofNullable(getFirstName())
                .map(firstName -> Optional.ofNullable(getLastName())
                        .map(lastName -> firstName + " " + lastName)
                        .orElse(firstName))
                .orElse(getLastName());
    }

    @Override
    public Long getId() {
        return getUserId();
    }

    public UserEntity(BigInteger userId, String firstName, String lastName, String username) {
        this(userId.longValue(), firstName, lastName, username, null);
    }
}
