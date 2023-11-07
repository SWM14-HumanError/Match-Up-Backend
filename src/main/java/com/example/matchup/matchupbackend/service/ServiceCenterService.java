package com.example.matchup.matchupbackend.service;

import com.example.matchup.matchupbackend.dto.request.servicecenter.OneOnOneInquiryRequest;
import com.example.matchup.matchupbackend.dto.response.servicecenter.OneOnOneInquiryResponse;
import com.example.matchup.matchupbackend.entity.ServiceCenter;
import com.example.matchup.matchupbackend.entity.User;
import com.example.matchup.matchupbackend.error.exception.ResourceNotFoundEx.UserNotFoundException;
import com.example.matchup.matchupbackend.global.config.jwt.TokenProvider;
import com.example.matchup.matchupbackend.repository.servicecenter.ServiceCenterRepository;
import com.example.matchup.matchupbackend.repository.user.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@RequiredArgsConstructor
@Service
@Transactional(readOnly = true)
public class ServiceCenterService {

    private final TokenProvider tokenProvider;
    private final UserRepository userRepository;
    private final ServiceCenterRepository serviceCenterRepository;

    @Transactional
    public void postOneOnOneInquiry(String authorizationHeader, OneOnOneInquiryRequest oneOnOneInquiryRequest) {
        Long userId = tokenProvider.getUserId(authorizationHeader, "일대일 문의를 작성하는 과정에서 유효하지 않은 토큰으로 요청했습니다.");
        User user = userRepository.findUserById(userId).orElseThrow(() -> new UserNotFoundException("일대일 문의를 작성하는 과정에서 존재하지 않는 user id로 요청했습니다."));

        ServiceCenter oneOnOneInquiry = ServiceCenter.createOneOnOneInquiry(oneOnOneInquiryRequest, user);
        serviceCenterRepository.save(oneOnOneInquiry);
    }

    /**
     * 1:1 문의 내역 조회
     */
    public OneOnOneInquiryResponse getInquiryResponse(String authorizationHeader, Pageable pageable) {
        Long userId = tokenProvider.getUserId(authorizationHeader, "getInquiryResponse");
        User user = userRepository.findUserById(userId).orElseThrow(() -> new UserNotFoundException("일대일 문의 조회 - 존재하지 않는 user id"));
        user.isAdmin();
        Slice<ServiceCenter> serviceCenterSlice = serviceCenterRepository.joinUserOrderByCreatedTime(pageable);
        return OneOnOneInquiryResponse.from(serviceCenterSlice);
    }
}
