package com.cronos.cronosystem.service;

import com.cronos.cronosystem.model.Usuario;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;

@Service
public class TokenService {

    @Value("${api.security.token.secret}")
    private String secret;

    @Value("${api.security.token.expiracao-horas}")
    private Long expiracaoHoras;

    public String gerarToken(Usuario usuario) {
        return Jwts.builder()
                .setIssuer("cronosystem")
                .setSubject(usuario.getEmail())
                .setIssuedAt(new Date())
                .setExpiration(Date.from(Instant.now().plus(expiracaoHoras, ChronoUnit.HOURS)))
                .signWith(chave(), SignatureAlgorithm.HS256)
                .compact();
    }

    public String getSubject(String token) {
        try {
            return Jwts.parserBuilder()
                    .setSigningKey(chave())
                    .requireIssuer("cronosystem")
                    .build()
                    .parseClaimsJws(token)
                    .getBody()
                    .getSubject();
        } catch (JwtException | IllegalArgumentException e) {
            return null;
        }
    }

    private Key chave() {
        return Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    }
}
