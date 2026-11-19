package br.com.agendanails.security;

import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Service
public class JwtService {

    private final SecretKey chave;
    private final long expiracaoMinutos;

    public JwtService(@Value("${app.jwt.secret}") String secret,
                      @Value("${app.jwt.expiracao-minutos}") long expiracaoMinutos) {
        this.chave = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
        this.expiracaoMinutos = expiracaoMinutos;
    }

    public String gerar(String email) {
        Date agora = new Date();
        Date fim = new Date(agora.getTime() + expiracaoMinutos * 60 * 1000);
        return Jwts.builder()
                .subject(email)
                .issuedAt(agora)
                .expiration(fim)
                .signWith(chave)
                .compact();
    }

    public boolean valido(String token) {
        try {
            Jwts.parser().verifyWith(chave).build().parseSignedClaims(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }

    public String extrairEmail(String token) {
        return Jwts.parser().verifyWith(chave).build()
                .parseSignedClaims(token).getPayload().getSubject();
    }
}