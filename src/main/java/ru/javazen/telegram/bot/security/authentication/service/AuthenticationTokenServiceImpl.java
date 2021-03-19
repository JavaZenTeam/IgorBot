package ru.javazen.telegram.bot.security.authentication.service;

import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.stereotype.Service;
import ru.javazen.telegram.bot.security.authentication.AuthenticationToken;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class AuthenticationTokenServiceImpl implements AuthenticationTokenService {

    private static final int RANDOM_LENGTH = 8;

    private final Map<String, AuthenticationToken> authenticationTokens = new ConcurrentHashMap<>();

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
    public AuthenticationToken generateToken(String path) {
        String token = RandomStringUtils.randomAlphanumeric(RANDOM_LENGTH);
        AuthenticationToken authenticationToken = new AuthenticationToken(token, path);
        authenticationTokens.put(token, authenticationToken);

        return authenticationToken;
    }
}
