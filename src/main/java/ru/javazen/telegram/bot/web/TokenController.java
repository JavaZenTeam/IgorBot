package ru.javazen.telegram.bot.web;

import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseStatus;
import ru.javazen.telegram.bot.security.authentication.AuthenticationToken;
import ru.javazen.telegram.bot.security.authentication.service.AuthenticationTokenService;

@Controller
@AllArgsConstructor
public class TokenController {
    private final AuthenticationTokenService authenticationTokenService;

    @GetMapping("/stats/{token}")
    public String redirectToChat(@PathVariable("token") String token) {
        AuthenticationToken authenticationToken = authenticationTokenService.findByToken(token);
        if (authenticationToken == null) {
            throw new TokenNotFoundException();
        }
        return "redirect:/chat/" + authenticationToken.getChatId() + "/";
    }

    @ResponseStatus(HttpStatus.FORBIDDEN)
    private static class TokenNotFoundException extends RuntimeException {

    }
}
