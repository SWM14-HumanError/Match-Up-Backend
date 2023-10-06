package com.example.matchup.matchupbackend.service;

import com.example.matchup.matchupbackend.entity.Likes;
import com.example.matchup.matchupbackend.entity.User;
import com.example.matchup.matchupbackend.error.exception.ResourceNotFoundEx.LikeNotFoundException;
import com.example.matchup.matchupbackend.error.exception.ResourceNotFoundEx.UserNotFoundException;
import com.example.matchup.matchupbackend.repository.LikeRepository;
import com.example.matchup.matchupbackend.repository.user.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class LikeService {

    private final LikeRepository likeRepository;
    private final UserRepository userRepository;

    /**
     * 유저에게 좋아요를 주는 로직
     */
    @Transactional
    public void saveLikeToUser(Long likeGiverId, Long likeReceiverId) {
        User likeGiver = userRepository.findById(likeGiverId).orElseThrow(() -> {
            throw new UserNotFoundException("좋아요를 준 유저를 찾을 수 없습니다.");
        });
        User likeReceiver = userRepository.findById(likeReceiverId).orElseThrow(() -> {
            throw new UserNotFoundException("좋아요를 준 유저를 찾을 수 없습니다.");
        });
        Likes likesToUser = Likes.builder()
                .user(likeGiver)
                .likeReceiver(likeReceiver)
                .build();
        likeRepository.save(likesToUser);
    }

    @Transactional
    public void deleteLikeToUser(Long likeGiverId, Long likeReceiverId) {
        Likes likes = likeRepository.findByUserIdAndLikeReceiverId(likeGiverId, likeReceiverId)
                .orElseThrow(() -> {
                    throw new LikeNotFoundException("좋아요 받은 사람: " + likeReceiverId, likeGiverId);
                });
        likeRepository.delete(likes);
    }
}
