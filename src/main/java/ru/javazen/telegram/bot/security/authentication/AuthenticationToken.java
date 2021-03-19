package ru.javazen.telegram.bot.security.authentication;

import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class AuthenticationToken {

    private String token;
    private String path;
}