package com.example.matchup.matchupbackend.global.config;

import com.example.matchup.matchupbackend.global.config.jwt.TokenProvider;
import com.example.matchup.matchupbackend.global.config.jwt.refreshtoken.RefreshTokenRepository;
import com.example.matchup.matchupbackend.global.config.oauth.OAuth2AuthorizationRequestBasedOnCookieRepository;
import com.example.matchup.matchupbackend.global.config.oauth.handler.OAuth2SuccessHandler;
import com.example.matchup.matchupbackend.global.config.oauth.service.OAuth2UserCustomService;
import com.example.matchup.matchupbackend.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import static org.springframework.boot.autoconfigure.security.servlet.PathRequest.toH2Console;

@RequiredArgsConstructor
@EnableWebSecurity
@Configuration
public class WebOAuthSecurityConfig {

    private final OAuth2UserCustomService oAuth2UserCustomService;
    private final TokenProvider tokenProvider;
    private final RefreshTokenRepository refreshTokenRepository;
    private final UserService userService;

    @Value("${oaut-login.logout-success-url}")
    private String logoutSuccessUrl;

    @Bean
    public WebSecurityCustomizer configure() {
        return (web) -> web.ignoring()
                .requestMatchers(toH2Console())
                .requestMatchers("/img/**", "/css/**", "/js/**");
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        // 폼로그인 + csrf 비활성화
        http.csrf((csrf) -> csrf.disable());

        // 세션 비활성화
        http.sessionManagement((sessionManagement) ->
                sessionManagement.sessionCreationPolicy(SessionCreationPolicy.STATELESS));

        // http request의 header에서 authorization(토큰을 저장한 header field) 값을 추출 후 bearer과 분리
        http.addFilterBefore(tokenAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class);

        http.authorizeHttpRequests((authorizeHttpRequests) ->
                        authorizeHttpRequests
                                .requestMatchers(HttpMethod.GET, "/**").permitAll()
                                .anyRequest().authenticated());

        http.oauth2Login((oauth2Login) ->
                oauth2Login
                            .loginPage("/login")
                            .authorizationEndpoint((authorizationEndpoint) ->
                                    authorizationEndpoint
                                            .baseUri("/oauth2/authorize")
                                            .authorizationRequestRepository(oAuth2AuthorizationRequestBasedOnCookieRepository()))
                            .successHandler(oAuth2SuccessHandler())
                            .userInfoEndpoint((userInfoEndpoint) ->
                                    userInfoEndpoint
                                            .userService(oAuth2UserCustomService)));

        http.logout((logout) ->
                logout
                        .logoutUrl("/logout")
                        .logoutSuccessUrl(logoutSuccessUrl));

        http.exceptionHandling((exceptionHandling) ->
                exceptionHandling.defaultAuthenticationEntryPointFor(
                                new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED),
                                new AntPathRequestMatcher("/api/**"))
                        .accessDeniedPage("/articles"));

        return http.build();
    }

    @Bean
    public OAuth2SuccessHandler oAuth2SuccessHandler() {

        return new OAuth2SuccessHandler(
                tokenProvider,
                refreshTokenRepository,
                oAuth2AuthorizationRequestBasedOnCookieRepository(),
                userService);
    }

    @Bean
    public OAuth2AuthorizationRequestBasedOnCookieRepository oAuth2AuthorizationRequestBasedOnCookieRepository() {

        return new OAuth2AuthorizationRequestBasedOnCookieRepository();
    }

    @Bean
    public TokenAuthenticationFilter tokenAuthenticationFilter() {

        return new TokenAuthenticationFilter(tokenProvider);
    }

    @Bean
    public BCryptPasswordEncoder bCryptPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }
}