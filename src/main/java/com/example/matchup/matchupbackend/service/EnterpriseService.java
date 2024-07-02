package com.example.matchup.matchupbackend.service;

import com.example.matchup.matchupbackend.dto.request.enterprise.EnterpriseVerifyFormRequest;
import com.example.matchup.matchupbackend.entity.EnterpriseVerify;
import com.example.matchup.matchupbackend.entity.User;
import com.example.matchup.matchupbackend.repository.EnterpriseVerifyRepository;
import com.example.matchup.matchupbackend.repository.user.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class EnterpriseService {

    private final UserRepository userRepository;
    private final EnterpriseVerifyRepository enterpriseVerifyRepository;
    private final FileService fileService;

    /**
     * 기업 인증 신청
     */
    @Transactional
    public User applyVerifyEnterprise(Long userId, EnterpriseVerifyFormRequest verifyFormRequest) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("userId: " + userId + "에 해당하는 사용자가 없습니다."));
        enterpriseVerifyRepository.save(EnterpriseVerify.from(verifyFormRequest, user));
        return user;
    }
}
