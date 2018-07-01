package ru.javazen.telegram.bot.security.authentication;

import com.google.common.collect.Lists;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationToken;
import org.springframework.stereotype.Service;
import ru.javazen.telegram.bot.security.authentication.service.AuthenticationTokenService;

import java.util.Collection;

@Service
public class TokenAuthenticationProvider implements AuthenticationProvider {

    private final AuthenticationTokenService authenticationTokenService;

    @Autowired
    public TokenAuthenticationProvider(AuthenticationTokenService authenticationTokenService) {
        this.authenticationTokenService = authenticationTokenService;
    }

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {

        String token = authentication.getCredentials().toString();
        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
        AuthenticationToken authenticationToken = authenticationTokenService.findByToken(token);

        if (authenticationToken != null) {
            Collection<GrantedAuthority> grantedAuthorities =
                    Lists.newArrayList(new SimpleGrantedAuthority(authenticationToken.getChatId().toString()));

            for (GrantedAuthority authority : authorities) {
                if (grantedAuthorities.stream().noneMatch(a -> a.getAuthority().equals(authority.getAuthority()))) {
                    grantedAuthorities.add(authority);
                }
            }

            authentication = new PreAuthenticatedAuthenticationToken(authenticationToken, token,
                    grantedAuthorities);

            authentication.setAuthenticated(true);
        } else {
            authentication = new PreAuthenticatedAuthenticationToken(this, token,
                    authentication.getAuthorities());
        }

        return authentication;
    }

    @Override
    public boolean supports(Class<?> aClass) {
        return AuthenticationTokenFilter.TokenAuthentication.class.equals(aClass);
    }
}
