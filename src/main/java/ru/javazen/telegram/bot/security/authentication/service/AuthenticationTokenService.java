package ru.javazen.telegram.bot.security.authentication.service;

import ru.javazen.telegram.bot.security.authentication.AuthenticationToken;

public interface AuthenticationTokenService {

    AuthenticationToken findByToken(String token);

    AuthenticationToken save(AuthenticationToken authenticationToken);

    AuthenticationToken delete(String token);

    AuthenticationToken generateToken(String path);

}