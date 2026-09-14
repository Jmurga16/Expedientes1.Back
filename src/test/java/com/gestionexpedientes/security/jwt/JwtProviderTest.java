package com.gestionexpedientes.security.jwt;

import com.gestionexpedientes.security.service.UserPrincipal;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class JwtProviderTest {

    private static final String SECRETO = "c2VjcmV0by1kZS1wcnVlYmEtY29uLTI1Ni1iaXRzLXBhcmEtaG1hYy1zaGEtMjU2";
    private static final String OTRO_SECRETO = "b3Ryby1zZWNyZXRvLWRlLXBydWViYS1jb24tMjU2LWJpdHMtcGFyYS1obWFj";

    private final JwtProvider jwtProvider = new JwtProvider(SECRETO, 3600);

    private Authentication autenticacionDe(String usuario) {
        UserPrincipal principal = new UserPrincipal(1, null, usuario, "ana@demo.test", "hash",
                List.of(new SimpleGrantedAuthority("ROLE_ADMIN")));
        return new UsernamePasswordAuthenticationToken(principal, null, principal.getAuthorities());
    }

    @Test
    @DisplayName("el token que genera es el que despues valida y del que lee el usuario")
    void generaYLeeElToken() {
        String token = jwtProvider.generateToken(autenticacionDe("ana"));

        assertThat(jwtProvider.validateToken(token)).isTrue();
        assertThat(jwtProvider.getUsernameFromToken(token)).isEqualTo("ana");
    }

    @Test
    @DisplayName("un token firmado con otro secreto no valida")
    void rechazaOtraFirma() {
        String token = new JwtProvider(OTRO_SECRETO, 3600).generateToken(autenticacionDe("ana"));

        assertThat(jwtProvider.validateToken(token)).isFalse();
    }

    @Test
    @DisplayName("un token vencido no valida")
    void rechazaTokenVencido() {
        String token = new JwtProvider(SECRETO, -60).generateToken(autenticacionDe("ana"));

        assertThat(jwtProvider.validateToken(token)).isFalse();
    }

    @Test
    @DisplayName("un texto que no es un token no valida y no rompe")
    void rechazaTextoInvalido() {
        assertThat(jwtProvider.validateToken("no-es-un-jwt")).isFalse();
        assertThat(jwtProvider.validateToken("")).isFalse();
    }
}
