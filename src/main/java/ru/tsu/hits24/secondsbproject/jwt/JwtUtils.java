package ru.tsu.hits24.secondsbproject.jwt;

import io.jsonwebtoken.Claims;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ru.tsu.hits24.secondsbproject.jpa.entity.Role;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class JwtUtils {

    public static JwtAuthentication generate(Claims claims) {
        final JwtAuthentication jwtInfoToken = new JwtAuthentication();
        jwtInfoToken.setRoles(getRoles(claims));
        jwtInfoToken.setFullName(claims.get("fullName", String.class));
        jwtInfoToken.setEmail(claims.get("email", String.class));
        jwtInfoToken.setUsername(claims.getSubject());
        return jwtInfoToken;
    }

    private static Set<String> getRoles(Claims claims) {
        final List<String> roles = claims.get("roles", List.class);

        return roles.stream()
                .collect(Collectors.toSet());
    }

}