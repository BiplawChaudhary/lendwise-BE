package com.lendwise.iam.utils;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

/**
 * JWT Utility class for generating and validating JWT tokens
 * Compatible with Spring Boot 3.5.9 and Java 17
 */
@Component
public class JwtUtil {

    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.expiration:86400000}") // Default 24 hours in milliseconds
    private Long expiration;

    /**
     * Generate JWT token without additional claims
     *
     * @param username the username/subject
     * @return generated JWT token
     */
    public String generateToken(String username) {
        return generateToken(new HashMap<>(), username);
    }

    /**
     * Generate JWT token with custom claims
     *
     * @param claims   additional claims to include in the token
     * @param username the username/subject
     * @return generated JWT token
     */
    public String generateToken(Map<String, Object> claims, String username) {
        return Jwts.builder()
                .claims(claims)
                .subject(username)
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(getSigningKey())
                .compact();
    }

    /**
     * Validate JWT token
     *
     * @param token the JWT token to validate
     * @param userDetails the user details to validate against
     * @return true if token is valid, false otherwise
     * @throws ExpiredJwtException if token is expired
     * @throws MalformedJwtException if token is malformed
     * @throws SignatureException if signature validation fails
     * @throws UnsupportedJwtException if token format is not supported
     */
    public boolean validateToken(String token, UserDetails userDetails) {
        final String username = extractUsername(token);
        return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
    }

    public boolean validateToken(String token, String  userEmail) {
        final String username = extractUsername(token);
        return (username.equals(userEmail) && !isTokenExpired(token));
    }


    /**
     * Validate JWT token without UserDetails
     *
     * @param token the JWT token to validate
     * @return true if token is valid and not expired
     * @throws ExpiredJwtException if token is expired
     * @throws MalformedJwtException if token is malformed
     * @throws SignatureException if signature validation fails
     * @throws UnsupportedJwtException if token format is not supported
     */
    public boolean validateToken(String token) {
        try {
            extractAllClaims(token);
            return !isTokenExpired(token);
        } catch (ExpiredJwtException e) {
            throw new ExpiredJwtException(e.getHeader(), e.getClaims(), "JWT token has expired");
        } catch (MalformedJwtException e) {
            throw new MalformedJwtException("Invalid JWT token format");
        } catch (SignatureException e) {
            throw new SignatureException("JWT signature validation failed");
        } catch (UnsupportedJwtException e) {
            throw new UnsupportedJwtException("JWT token is not supported");
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("JWT claims string is empty");
        }
    }

    /**
     * Extract username from JWT token
     *
     * @param token the JWT token
     * @return username/subject from token
     */
    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    /**
     * Extract expiration date from JWT token
     *
     * @param token the JWT token
     * @return expiration date
     */
    public Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    /**
     * Extract issued at date from JWT token
     *
     * @param token the JWT token
     * @return issued at date
     */
    public Date extractIssuedAt(String token) {
        return extractClaim(token, Claims::getIssuedAt);
    }

    /**
     * Extract a specific claim from JWT token
     *
     * @param token          the JWT token
     * @param claimsResolver function to resolve the claim
     * @param <T>            type of claim
     * @return extracted claim
     */
    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    /**
     * Extract all claims from JWT token
     *
     * @param token the JWT token
     * @return all claims
     */
    public Claims extractAllClaims(String token) {
        return Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    /**
     * Check if token is expired
     *
     * @param token the JWT token
     * @return true if expired, false otherwise
     */
    public boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    /**
     * Get the signing key for JWT
     *
     * @return SecretKey for signing
     */
    private SecretKey getSigningKey() {
        byte[] keyBytes = secret.getBytes(StandardCharsets.UTF_8);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    /**
     * Refresh token by generating a new one with the same claims
     *
     * @param token the existing JWT token
     * @return new refreshed token
     */
    public String refreshToken(String token) {
        final Claims claims = extractAllClaims(token);
        claims.remove(Claims.ISSUED_AT);
        claims.remove(Claims.EXPIRATION);
        return generateToken(new HashMap<>(claims), claims.getSubject());
    }

    /**
     * Get remaining time until token expiration in milliseconds
     *
     * @param token the JWT token
     * @return remaining time in milliseconds
     */
    public long getTokenRemainingTime(String token) {
        Date expiration = extractExpiration(token);
        return expiration.getTime() - System.currentTimeMillis();
    }
}