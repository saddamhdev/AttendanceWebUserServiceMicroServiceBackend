package com.example.userservice.security;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.security.SignatureException;
import java.time.Instant;
import java.util.Base64;
import java.util.Date;
import java.util.List;

@Component  // Ensure this annotation is present!
public class JwtGenerator {
   public static String secretKey = "Yn2kjibddFAWtnPJ2AFlL8WXmohJMCvigQggaEypa5Ejvxcvbjxc";
<<<<<<< HEAD
    public  String generateToken(String username){
=======
    public  String generateToken(String username, List<String> role){
>>>>>>> e578cacc7894235eed0f181de9721cbe75df770f

        Instant now = Instant.now(); // Current time
        Instant expirationTime = now.plusSeconds(3600); // Token expires in 1 hour (3600 sec)
        String jws = Jwts.builder()
                .setIssuer("saddamnvn")
                .setSubject(username)
                .claim("name", username)
<<<<<<< HEAD
=======
                .claim("role",  role)  // ✅ Prefix role with "ROLE_"
>>>>>>> e578cacc7894235eed0f181de9721cbe75df770f
                .setIssuedAt(Date.from(now))  // Issued at current time
                .setExpiration(Date.from(expirationTime)) // Expiration set dynamically
                .signWith(
                        SignatureAlgorithm.HS256,
                        Base64.getDecoder().decode(secretKey) // Decoding Base64 properly
                )
                .compact();
       // System.out.println("Generated JWT: " + jws);
        return  jws;
    }
    /*public static void main(String[] args) {
      // Base64-encoded secret key

        String jws = Jwts.builder()
                .setIssuer("Stormpath")
                .setSubject("msilverman")
                .claim("name", "Micah Silverman")
                .claim("scope", "admins")
                .setIssuedAt(Date.from(Instant.ofEpochSecond(1466796822L)))  // Issued At
                .setExpiration(Date.from(Instant.ofEpochSecond(4622470422L))) // Expiration
                .signWith(
                        SignatureAlgorithm.HS256,
                        Base64.getDecoder().decode(secretKey) // Decoding Base64 properly
                )
                .compact();

        System.out.println("Generated JWT: " + jws);
        String username = extractUsername(jws);
        System.out.println("Extracted Username: " + username);

        Date expirationDate = extractExpiration(jws);
        System.out.println("Token Expiration Date: " + expirationDate);

        if (isTokenValid(jws)) {
            System.out.println("✅ Token is VALID!");
        } else {
            System.out.println("❌ Token is INVALID or EXPIRED!");
        }
    }*/

<<<<<<< HEAD
    public  String generateRefreshToken(String username) {
=======
    public  String generateRefreshToken(String username,List<String> role) {
>>>>>>> e578cacc7894235eed0f181de9721cbe75df770f
        Instant now = Instant.now();
        Instant expirationTime = now.plusSeconds(86400 * 2); // 7 days validity
       // Instant expirationTime = now.plusSeconds(120); // 7 days validity

        return Jwts.builder()
                .setIssuer("saddamnvn")
                .setSubject(username)
                .claim("type", "refresh") // To differentiate from access tokens
<<<<<<< HEAD
=======
                .claim("role",  role)  // ✅ Prefix role with "ROLE_"
>>>>>>> e578cacc7894235eed0f181de9721cbe75df770f
                .setIssuedAt(Date.from(now))
                .setExpiration(Date.from(expirationTime))
                .signWith(SignatureAlgorithm.HS256, Base64.getDecoder().decode(secretKey))
                .compact();
    }
    public boolean validateToken(String token, UserDetails userDetails) {
        try {
            Claims claims = Jwts.parser()
                    .setSigningKey(Base64.getDecoder().decode(secretKey))
                    .parseClaimsJws(token)
                    .getBody();

            String username = claims.getSubject();
            Date expiration = claims.getExpiration();

           // System.out.println("Extracted Username: " + username);
           // System.out.println("Token Expiration Date: " + expiration);

            return (username.equals(userDetails.getUsername()) && expiration.after(new Date()));
        } catch (Exception e) {
           // System.out.println("❌ Invalid or malformed token: " + e.getMessage());
        }
        return false;
    }
    public static Date extractExpiration(String token) {
        Claims claims = Jwts.parser()
                .setSigningKey(Base64.getDecoder().decode(secretKey))
                .parseClaimsJws(token)
                .getBody();

        return claims.getExpiration(); // Extracts expiration time
    }

    public static Claims extractClaims(String token) {
        return Jwts.parser()
                .setSigningKey(Base64.getDecoder().decode(secretKey))
                .parseClaimsJws(token)
                .getBody();
    }

    public static String extractUsername(String token) {
        Claims claims = Jwts.parser()
                .setSigningKey(Base64.getDecoder().decode(secretKey))
                .parseClaimsJws(token)
                .getBody();

        return claims.getSubject(); // Subject is the username
    }

    public static boolean isTokenValid(String token) {
        try {
            Claims claims = Jwts.parser()
                    .setSigningKey(Base64.getDecoder().decode(secretKey))
                    .parseClaimsJws(token)
                    .getBody();

            Date expirationDate = claims.getExpiration();
          //  System.out.println("Token Expiration Date: " + expirationDate);

            return expirationDate.before(new Date()); // Check if the token is expired
        } catch (Exception e) {
          //  System.out.println("Invalid or malformed token: " + e.getMessage());
            return false;
        }
    }
}
