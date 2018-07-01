package ru.javazen.telegram.bot.security.authentication.service;

import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.stereotype.Repository;
import ru.javazen.telegram.bot.security.authentication.AuthenticationToken;

import java.util.HashMap;
import java.util.Map;

@Repository
public class AuthenticationTokenServiceImpl implements AuthenticationTokenService {

    private static final int RANDOM_LENGTH = 8;

    private Map<String, AuthenticationToken> authenticationTokens = new HashMap<>();

    @Override
    public AuthenticationToken findByToken(String token) {
        return authenticationTokens.get(token);
    }

    @Override
    public AuthenticationToken save(AuthenticationToken authenticationToken) {
        return authenticationTokens.put(authenticationToken.getToken(), authenticationToken);
    }

    @Override
    public AuthenticationToken delete(String token) {
        return authenticationTokens.remove(token);
    }

    @Override
    public AuthenticationToken generateToken(Long chatId) {
        String token = RandomStringUtils.randomAlphanumeric(RANDOM_LENGTH);
        AuthenticationToken authenticationToken = new AuthenticationToken(token, chatId);
        authenticationTokens.put(token, authenticationToken);

        return authenticationToken;
    }
}
