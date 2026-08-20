package com.cronos.cronosystem.service;

import com.cronos.cronosystem.model.Usuario;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Service
public class JwtService {

    @Value("${security.jwt.secret}")
    private String secret;

    @Value("${security.jwt.expiration-ms}")
    private long expirationMs;

    public String gerarToken(Usuario usuario) {
        SecretKey chave = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));

        return Jwts.builder()
                .setSubject(usuario.getEmail())
                .claim("id", usuario.getId())
                .claim("nome", usuario.getNome())
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + expirationMs))
                .signWith(chave, SignatureAlgorithm.HS256)
                .compact();
    }
}
