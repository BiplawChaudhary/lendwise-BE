package com.lendwise.iam.utils;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.security.SignatureException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

/**
 * Example usage of JwtUtil
 */
@Service
public class JwtUsageExample {

    @Autowired
    private JwtUtil jwtUtil;

    /**
     * Example 1: Generate token without claims
     */
    public void generateSimpleToken() {
        String username = "john.doe@example.com";
        String token = jwtUtil.generateToken(username);
        System.out.println("Generated Token: " + token);
    }

    /**
     * Example 2: Generate token with custom claims
     */
    public void generateTokenWithClaims() {
        String username = "john.doe@example.com";
        
        Map<String, Object> claims = new HashMap<>();
        claims.put("role", "ADMIN");
        claims.put("userId", 12345);
        claims.put("department", "Engineering");
        
        String token = jwtUtil.generateToken(claims, username);
        System.out.println("Generated Token with Claims: " + token);
    }

    /**
     * Example 3: Validate token with UserDetails
     */
    public void validateTokenWithUserDetails(String token, UserDetails userDetails) {
        try {
            boolean isValid = jwtUtil.validateToken(token, userDetails);
            if (isValid) {
                System.out.println("Token is valid!");
            } else {
                System.out.println("Token is invalid!");
            }
        } catch (ExpiredJwtException e) {
            System.err.println("Token expired: " + e.getMessage());
        } catch (MalformedJwtException e) {
            System.err.println("Malformed token: " + e.getMessage());
        } catch (SignatureException e) {
            System.err.println("Invalid signature: " + e.getMessage());
        }
    }

    /**
     * Example 4: Validate token without UserDetails
     */
    public void validateTokenSimple(String token) {
        try {
            boolean isValid = jwtUtil.validateToken(token);
            if (isValid) {
                System.out.println("Token is valid!");
            }
        } catch (ExpiredJwtException e) {
            System.err.println("Token expired: " + e.getMessage());
        } catch (Exception e) {
            System.err.println("Token validation failed: " + e.getMessage());
        }
    }

    /**
     * Example 5: Extract information from token
     */
    public void extractTokenInformation(String token) {
        try {
            String username = jwtUtil.extractUsername(token);
            System.out.println("Username: " + username);
            
            var expiration = jwtUtil.extractExpiration(token);
            System.out.println("Expires at: " + expiration);
            
            var issuedAt = jwtUtil.extractIssuedAt(token);
            System.out.println("Issued at: " + issuedAt);
            
            boolean isExpired = jwtUtil.isTokenExpired(token);
            System.out.println("Is expired: " + isExpired);
            
            long remainingTime = jwtUtil.getTokenRemainingTime(token);
            System.out.println("Remaining time (ms): " + remainingTime);
            
            // Extract custom claims
            var claims = jwtUtil.extractAllClaims(token);
            System.out.println("Role: " + claims.get("role"));
            System.out.println("User ID: " + claims.get("userId"));
            
        } catch (Exception e) {
            System.err.println("Error extracting token info: " + e.getMessage());
        }
    }

    /**
     * Example 6: Refresh token
     */
    public void refreshExistingToken(String oldToken) {
        try {
            String newToken = jwtUtil.refreshToken(oldToken);
            System.out.println("Refreshed Token: " + newToken);
        } catch (Exception e) {
            System.err.println("Error refreshing token: " + e.getMessage());
        }
    }

    /**
     * Example 7: Complete authentication flow
     */
    public String authenticateUser(String username, String password, UserDetails userDetails) {
        // Assume password validation happens here
        
        // Generate token with user roles and permissions
        Map<String, Object> claims = new HashMap<>();
        claims.put("authorities", userDetails.getAuthorities());
        
        return jwtUtil.generateToken(claims, username);
    }
}