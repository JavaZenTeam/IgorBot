package ru.javazen.telegram.bot.security.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import ru.javazen.telegram.bot.security.authentication.AuthenticationTokenFilter;
import ru.javazen.telegram.bot.security.authentication.TokenAuthenticationProvider;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true)
public class WebSecurityConfiguration {

    private final TokenAuthenticationProvider tokenAuthenticationProvider;

    public WebSecurityConfiguration(TokenAuthenticationProvider tokenAuthenticationProvider) {
        this.tokenAuthenticationProvider = tokenAuthenticationProvider;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.authenticationProvider(tokenAuthenticationProvider)
            .addFilterAfter(new AuthenticationTokenFilter(), BasicAuthenticationFilter.class);
        return http.build();
    }
}
