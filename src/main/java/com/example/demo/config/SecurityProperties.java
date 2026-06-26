package com.example.demo.config;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "app.security")
public class SecurityProperties {

    private String passwordPepper;
    private Jwt jwt = new Jwt();
    private Users users = new Users();

    public String getPasswordPepper() {
        return passwordPepper;
    }

    public void setPasswordPepper(String passwordPepper) {
        this.passwordPepper = passwordPepper;
    }

    public Jwt getJwt() {
        return jwt;
    }

    public void setJwt(Jwt jwt) {
        this.jwt = jwt;
    }

    public Users getUsers() {
        return users;
    }

    public void setUsers(Users users) {
        this.users = users;
    }

    public static class Jwt {

        private String secret;
        private Duration accessTokenTtl = Duration.ofMinutes(30);

        public String getSecret() {
            return secret;
        }

        public void setSecret(String secret) {
            this.secret = secret;
        }

        public Duration getAccessTokenTtl() {
            return accessTokenTtl;
        }

        public void setAccessTokenTtl(Duration accessTokenTtl) {
            this.accessTokenTtl = accessTokenTtl;
        }
    }

    public static class Users {

        private StaticUser user = new StaticUser();
        private StaticUser reader = new StaticUser();

        public StaticUser getUser() {
            return user;
        }

        public void setUser(StaticUser user) {
            this.user = user;
        }

        public StaticUser getReader() {
            return reader;
        }

        public void setReader(StaticUser reader) {
            this.reader = reader;
        }
    }

    public static class StaticUser {

        private String username;
        private String passwordHash;
        private List<String> roles = new ArrayList<>();
        private List<String> authorities = new ArrayList<>();

        public String getUsername() {
            return username;
        }

        public void setUsername(String username) {
            this.username = username;
        }

        public String getPasswordHash() {
            return passwordHash;
        }

        public void setPasswordHash(String passwordHash) {
            this.passwordHash = passwordHash;
        }

        public List<String> getRoles() {
            return roles;
        }

        public void setRoles(List<String> roles) {
            this.roles = roles;
        }

        public List<String> getAuthorities() {
            return authorities;
        }

        public void setAuthorities(List<String> authorities) {
            this.authorities = authorities;
        }
    }
}
