package com.example.matchup.matchupbackend.config;

import com.example.matchup.matchupbackend.chat.StompHandler;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Slf4j
@Configuration
@EnableWebSocketMessageBroker
@RequiredArgsConstructor
public class WebSocketBrokerConfig implements WebSocketMessageBrokerConfigurer {
    private final StompHandler stompHandler;
    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        //메세지 받을때 (메세지 구독 요청 URL)
        registry.enableSimpleBroker("/sub-queue", "/sub-topic"); // 이러한 prefix(API 경로 맨 앞)가 붙은 채로 메시지가 프론트 -> 백 송신 되었을때 심플 브로커가 메세지를 바로 처리(queue는 개인 채팅, topic은 그룹 채팅)
        //메세지 보낼때 (메세지 송신 요청 URL)
        registry.setApplicationDestinationPrefixes("/pub"); // 메세지가 핸들러(컨트롤러와 비슷)로 가공이 필요한 경우
    }

    /**
     * 클라이언트에서 웹소캣 연결
     */
    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/ws-stomp") // 클라이언트에서 웹소캣 연결할때 var sock = new SockJS("/ws-stomp"); 이런식으로 연결
                .setAllowedOriginPatterns("*")
                .withSockJS();
    }

    @Override
    public void configureClientInboundChannel(ChannelRegistration registration) {
        registration.interceptors(stompHandler);
    }
}
