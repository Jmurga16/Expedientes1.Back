package com.gestionexpedientes.security.jwt;

import com.gestionexpedientes.security.service.UserPrincipal;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class JwtProvider {

    private static final Logger logger = LoggerFactory.getLogger(JwtProvider.class);

    private final String secret;
    private final int expiration;

    public JwtProvider(@Value("${jwt.secret}") String secret,
                       @Value("${jwt.expiration}") int expiration) {
        this.secret = secret;
        this.expiration = expiration;
    }

    public String generateToken(Authentication authentication) {
        UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
        return Jwts.builder()
                .signWith(getKey(secret))
                .subject(userPrincipal.getUsername())
                .issuedAt(new Date())
                .expiration(new Date(new Date().getTime() + expiration * 1000L))
                .claim("email", userPrincipal.getEmail())
                .claim("roles", getRoles(userPrincipal))
                .compact();
    }

    public String getUsernameFromToken(String token) {
        return Jwts.parser().verifyWith(getKey(secret)).build().parseSignedClaims(token).getPayload().getSubject();
    }

    public boolean validateToken(String token) {
        try {
            Jwts.parser().verifyWith(getKey(secret)).build().parseSignedClaims(token);
            return true;
        } catch (ExpiredJwtException e) {
            logger.error("expired token");
        } catch (UnsupportedJwtException e) {
            logger.error("unsupported token");
        } catch (MalformedJwtException e) {
            logger.error("malformed token");
        } catch (SignatureException e) {
            logger.error("bad signature");
        } catch (IllegalArgumentException e) {
            logger.error("empty or invalid token");
        } catch (Exception e) {
            logger.error("fail token");
        }
        return false;
    }

    private List<String> getRoles(UserPrincipal principal) {
        return principal.getAuthorities()
                .stream().map(GrantedAuthority::getAuthority).collect(Collectors.toList());
    }

    private SecretKey getKey(String secret){
        byte [] secretBytes = Decoders.BASE64URL.decode(secret);
        return Keys.hmacShaKeyFor(secretBytes);
    }
}
