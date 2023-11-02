package com.example.matchup.matchupbackend.chat;

import com.example.matchup.matchupbackend.global.config.jwt.TokenProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class StompHandler implements ChannelInterceptor {
    private final TokenProvider tokenProvider;

    /**
     * 메세지가 전송되기 전에 로그인 한 사용자인지 확인
     */
    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(message); // 메세지 헤더에 접근하기 위한 객체
        List<String> authorization = accessor.getNativeHeader("Authorization");
        Long userId = tokenProvider.getUserId(authorization.get(0), "preSend");
        log.info("userId: " + userId + " 가 문자를 전송하였습니다.");
        return message;
    }

    @Override
    public void postSend(Message<?> message, MessageChannel channel, boolean sent) {
        log.info("postSend");
    }
}
