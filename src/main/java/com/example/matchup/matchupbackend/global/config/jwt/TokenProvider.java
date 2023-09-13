package com.example.matchup.matchupbackend.global.config.jwt;

import com.example.matchup.matchupbackend.entity.User;
import com.example.matchup.matchupbackend.error.exception.AuthorizeException;
import com.example.matchup.matchupbackend.error.exception.ExpiredTokenException;
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
                .claim("unknown",  user.getIsUnknown())
                .signWith(SignatureAlgorithm.HS256, jwtProperties.getSecretKey())
                .compact();
    }

    public TokenStatus validToken(String authorizationHeader) {
        String token = getAccessToken(authorizationHeader);

        try {
            Jwts.parser()
                    .setSigningKey(jwtProperties.getSecretKey())
                    .parseClaimsJws(token);
            return TokenStatus.VALID;
        }
        catch (ExpiredJwtException ex) {
            return TokenStatus.EXPIRED;
        }
        catch (Exception e) {
//            log.info("token is not valid: {}", e.getMessage());
            return TokenStatus.INVALID_OTHER;
        }
    }

    public boolean validTokenInFilter(String authorizationHeader) {
        String token = getAccessToken(authorizationHeader);

        try {
            Jwts.parser()
                    .setSigningKey(jwtProperties.getSecretKey())
                    .parseClaimsJws(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public Authentication getAuthentication(String authorizationHeader) {
        String token = getAccessToken(authorizationHeader);

        Claims claims = getClaims(token);
        Set<SimpleGrantedAuthority> authorities =
                Collections.singleton(new SimpleGrantedAuthority("ROLE_USER"));

        return new UsernamePasswordAuthenticationToken(
                new org.springframework.security.core.userdetails.User(claims.getSubject(), "", authorities),
                token,
                authorities);
    }

    public Long getUserId(String authorizationHeader, String callBack) {
        String token = getAccessToken(authorizationHeader);

        if (token == null) return null;
        if (validToken(token) == TokenStatus.INVALID_OTHER) {
            throw new AuthorizeException(callBack);
        } else if (validToken(token) == TokenStatus.EXPIRED) {
            throw new ExpiredTokenException();
        }

        Claims claims = getClaims(token);
        return claims.get("id", Long.class);
    }

    private Claims getClaims(String authorizationHeader) {
        String token = getAccessToken(authorizationHeader);

        return Jwts.parser()
                .setSigningKey(jwtProperties.getSecretKey())
                .parseClaimsJws(token)
                .getBody();
    }

    public Boolean getUnknown(String authorizationHeader, String callBack) {
        String token = getAccessToken(authorizationHeader);

        if (token == null) return null;
        if (validToken(token) == TokenStatus.INVALID_OTHER) throw new AuthorizeException(callBack);

        Claims claims = getClaims(token);
        return claims.get("unknown", Boolean.class);
    }

    /**
     * Authorization in Header에서 Bearer을 제거하여 토큰만 추출
     * @param authorizationHeader: Bearer token
     */
    private String getAccessToken(String authorizationHeader) {
        if (authorizationHeader != null && authorizationHeader.startsWith(TOKEN_PRIFIX)) {
            return authorizationHeader.substring(TOKEN_PRIFIX.length());
        }
        return authorizationHeader;
    }
}
