package ru.javazen.telegram.bot.security.authentication;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.FilterConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class AuthenticationTokenFilter implements Filter {

    @Override
    public void init(FilterConfig filterConfig) throws ServletException { }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {

        String uri = ((HttpServletRequest) servletRequest).getRequestURI();

        Matcher matcher = Pattern.compile("/stats/(.*)").matcher(uri);
        if (matcher.matches()) {
            String token = matcher.group(1);
            SecurityContext context = SecurityContextHolder.getContext();
            Collection<? extends GrantedAuthority> grantedAuthorities = Collections.emptyList();

            if (context.getAuthentication() != null) {
                grantedAuthorities = context.getAuthentication().getAuthorities();
            }
            Authentication auth = new TokenAuthentication(token, grantedAuthorities);
            SecurityContextHolder.getContext().setAuthentication(auth);

        }

        filterChain.doFilter(servletRequest, servletResponse);
    }

    @Override
    public void destroy() { }

    class TokenAuthentication implements Authentication {
        private String token;
        Collection<? extends GrantedAuthority> authorities;

        private TokenAuthentication(String token, Collection<? extends GrantedAuthority> authorities) {
            this.token = token;
            this.authorities = authorities;
        }
        @Override
        public Collection<? extends GrantedAuthority> getAuthorities() {
            return authorities;
        }
        @Override
        public Object getCredentials() {
            return token;
        }
        @Override
        public Object getDetails() {
            return null;
        }
        @Override
        public Object getPrincipal() {
            return null;
        }
        @Override
        public boolean isAuthenticated() {
            return false;
        }
        @Override
        public void setAuthenticated(boolean isAuthenticated) throws IllegalArgumentException {
        }
        @Override
        public String getName() {
            return token;
        }
    }
}
