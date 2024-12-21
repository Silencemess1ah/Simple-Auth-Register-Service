package com.shnordiq.config.security;

import com.nimbusds.jose.jwk.source.ImmutableSecret;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.jwt.JwsHeader;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.security.oauth2.jwt.JwtException;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtEncoder;
import org.springframework.stereotype.Component;

import javax.crypto.spec.SecretKeySpec;
import java.time.Instant;
import java.time.temporal.ChronoUnit;

@Component
public class JwtService {

    private final SecurityProperties securityProperties;
    private final JwtEncoder jwtEncoder;
    private final JwtDecoder jwtDecoder;

    @Autowired
    public JwtService(SecurityProperties securityProperties) {
        var secretKeySpec = new SecretKeySpec(securityProperties.getJwt().getSecretKey().getBytes(),
                securityProperties.getJwt().getKeyCryptoAlgorithm());
        jwtEncoder = new NimbusJwtEncoder(new ImmutableSecret<>(secretKeySpec));
        jwtDecoder = NimbusJwtDecoder.withSecretKey(secretKeySpec).build();
        this.securityProperties = securityProperties;
    }

    public String generateToken(String email, String role) { //todo на енам мб?
        Instant now = Instant.now();
        JwsHeader header = JwsHeader.with(() -> securityProperties.getJwt().getJwtAlgorithm()).build(); //todo возможно header Jose был правильным что-то не то доабвил в зависимости
        JwtClaimsSet claims = JwtClaimsSet.builder()
                .subject(email)
                .issuedAt(now)
                .expiresAt(now.plus(securityProperties.getJwt().getExpirationTime(), ChronoUnit.HOURS))
                .claim("roles", role)
                .build();
        return jwtEncoder.encode(JwtEncoderParameters.from(header, claims)).getTokenValue();
    }

    public String extractEmailLogin(String token) {
        Jwt decodedJwt = jwtDecoder.decode(token);
        return decodedJwt.getSubject();
    }

    public boolean isTokenValid(String token, String email) {
        try {
            String extractEmailLogin = extractEmailLogin(token);
            return extractEmailLogin.equals(email) && !isTokenExpired(token);
        } catch (JwtException e) {
            return false;
        }
    }

    private boolean isTokenExpired(String token) {
        Instant expiration = jwtDecoder.decode(token).getExpiresAt();
        return Instant.now().isAfter(expiration);
    }
}
