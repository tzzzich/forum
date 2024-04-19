package ru.tsu.hits24.secondsbproject.jpa.entity;

import jakarta.persistence.Entity;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;

@RequiredArgsConstructor
public enum Role implements GrantedAuthority {

    ADMIN("ADMIN"),
    MODERATOR("MODERATOR"),
    USER("USER");

    private final String value;

    @Override
    public String getAuthority() {
        return value;
    }

}
