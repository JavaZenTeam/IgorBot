package ru.javazen.telegram.bot.web;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import ru.javazen.telegram.bot.security.authentication.AuthenticationToken;
import ru.javazen.telegram.bot.security.authentication.service.AuthenticationTokenService;

@Controller
public class TokenController {
    private AuthenticationTokenService authenticationTokenService;

    public TokenController(AuthenticationTokenService authenticationTokenService) {
        this.authenticationTokenService = authenticationTokenService;
    }

    @GetMapping("/stats/{token}")
    public String redirectToChat(@PathVariable("token") String token) {
        AuthenticationToken authenticationToken = authenticationTokenService.findByToken(token);
        return "redirect:/chat/" + authenticationToken.getChatId() + "/";
    }
}
