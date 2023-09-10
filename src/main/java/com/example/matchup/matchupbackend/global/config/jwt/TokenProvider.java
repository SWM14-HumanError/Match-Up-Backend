package com.example.matchup.matchupbackend.global.config.jwt;

import com.example.matchup.matchupbackend.entity.User;
import com.example.matchup.matchupbackend.error.exception.AuthorizeException;
import com.example.matchup.matchupbackend.global.config.oauth.dto.OAuth2LoginUrl;
import io.jsonwebtoken.*;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Collections;
import java.util.Date;
import java.util.Set;

@Slf4j
@RequiredArgsConstructor
@Getter
@Service
public class TokenProvider {

    private final JwtProperties jwtProperties;
    private final OAuth2LoginUrl oAuth2LoginUrl;

    public final static String HEADER_AUTHORIZATION = "Authorization";
    public final static String TOKEN_PRIFIX = "Bearer ";

    public String generateToken(User user, Duration expiredAt) {

        Date now = new Date();
        return makeToken(new Date(now.getTime() + expiredAt.toMillis()), user);
    }

    private String makeToken(Date expiry, User user) {

        return Jwts.builder()
                .setHeaderParam(Header.TYPE, Header.JWT_TYPE)
                .setIssuer(jwtProperties.getIssuer())
                .setIssuedAt(new Date())
                .setExpiration(expiry)
                .setSubject(user.getEmail())
                .claim("id", user.getId())
                .signWith(SignatureAlgorithm.HS256, jwtProperties.getSecretKey())
                .compact();
    }

    public boolean validToken(String token) {

        try {
            // 토큰의 유효성 검증
            Jwts.parser()
                    .setSigningKey(jwtProperties.getSecretKey())
                    .parseClaimsJws(token);
            return true;
//        } catch (ExpiredJwtException e) {
//            log.info("Expired JWT: " + e.getMessage(), e);
//            return false;
//        } catch (UnsupportedJwtException e) {
//            log.info("Unsupported JWT: " + e.getMessage(), e);
//            return false;
//        } catch (MalformedJwtException e) {
//            log.info("Malformed JWT: " + e.getMessage(), e);
//            return false;
//        } catch (SignatureException e) {
//            log.info("JWT Signature Error: " + e.getMessage(), e);
//            return false;
//        } catch (IllegalArgumentException e) {
//            log.info("Invalid JWT argument: " + e.getMessage(), e);
//            return false;
//        } catch (Exception e) {
//            log.info("Unhandled Exception: " + e.getMessage(), e);
//            return false;
        }
        catch (Exception e) {
            return false;
        }
    }

    public Authentication getAuthentication(String token) {

        Claims claims = getClaims(token);
        Set<SimpleGrantedAuthority> authorities =
                Collections.singleton(new SimpleGrantedAuthority("ROLE_USER"));

        return new UsernamePasswordAuthenticationToken(
                new org.springframework.security.core.userdetails.User(claims.getSubject(), "", authorities),
                token,
                authorities);
    }

    public Long getUserId(String token, String callBack) {
        if (token == null) return null;
        if (!validToken(token)) throw new AuthorizeException(callBack);

        Claims claims = getClaims(token);
        return claims.get("id", Long.class);
    }

    private Claims getClaims(String token) {

        return Jwts.parser()
                .setSigningKey(jwtProperties.getSecretKey())
                .parseClaimsJws(token)
                .getBody();
    }

//    public String getAccessToken(String authorizationHeader) {
//
//        if (authorizationHeader != null && authorizationHeader.startsWith(TOKEN_PRIFIX)) {
//            return authorizationHeader.substring(TOKEN_PRIFIX.length());
//        }
//        return null;
//    }
}
