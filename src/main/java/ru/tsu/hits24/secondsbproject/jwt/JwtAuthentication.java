package ru.tsu.hits24.secondsbproject.jwt;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import ru.tsu.hits24.secondsbproject.jpa.entity.Role;

import java.util.*;

@Data
@Slf4j
public class JwtAuthentication implements Authentication {

    private boolean authenticated;
    private String username;
    private String fullName;
    private Long id;
    private String email;
    private Set<String> roles;

    public Collection<? extends GrantedAuthority> getAuthorities() {
        Set<Role> result = new HashSet<>();
        for (String role : roles) {
                switch (role) {
                    case "ADMIN":
                        result.add(Role.ADMIN);
                        break;
                    case "MODERATOR":
                        result.add(Role.MODERATOR);
                        break;
                    default:
                        result.add(Role.USER);
                }
        }
        log.info("authorities: {}", result);
        return result;
    }


    @Override
    public Object getCredentials() { return null; }

    @Override
    public Object getDetails() { return null; }

    @Override
    public Object getPrincipal() { return username; }

    @Override
    public boolean isAuthenticated() { return authenticated; }

    @Override
    public void setAuthenticated(boolean isAuthenticated) throws IllegalArgumentException {
        this.authenticated = isAuthenticated;
    }

    @Override
    public String getName() { return username; }

}