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
public class UserEntity {
    @Id
    private Integer userId;

    @Column
    private String firstName;

    @Column
    private String lastName;

    @Column(length = 32)
    private String username;

    public String getLabel() {
        return Optional.ofNullable(getFirstName())
                .map(firstName -> Optional.ofNullable(getLastName())
                        .map(lastName -> firstName + " " + lastName)
                        .orElse(firstName))
                .orElse(getLastName());
    }
}
