package ru.tsu.hits24.secondsbproject;

import lombok.Data;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import ru.tsu.hits24.secondsbproject.jpa.entity.Role;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

@Data
public class JwtAuthentication implements Authentication {

    private boolean authenticated;
    private String username;
    private String fullName;
    private String email;
    private Set<String> roles;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        Set<Role> result = new HashSet<Role>();
        for (String strRole: roles){
            result.add(strRole == "ADMIN"?Role.ADMIN:strRole == "MODERATOR"?Role.MODERATOR:Role.USER);
        }
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
    public String getName() { return fullName; }

}