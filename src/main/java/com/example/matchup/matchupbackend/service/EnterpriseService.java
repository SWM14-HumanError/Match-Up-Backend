package com.example.matchup.matchupbackend.service;

import com.example.matchup.matchupbackend.dto.request.enterprise.EnterpriseStateRequest;
import com.example.matchup.matchupbackend.dto.request.enterprise.EnterpriseVerifyFormRequest;
import com.example.matchup.matchupbackend.dto.response.enterprise.EnterpriseApply;
import com.example.matchup.matchupbackend.dto.response.enterprise.SliceEnterpriseApply;
import com.example.matchup.matchupbackend.entity.EnterpriseVerify;
import com.example.matchup.matchupbackend.entity.Role;
import com.example.matchup.matchupbackend.entity.User;
import com.example.matchup.matchupbackend.error.exception.ResourceNotFoundEx.UserNotFoundException;
import com.example.matchup.matchupbackend.error.exception.ResourceNotPermitEx.AdminOnlyPermitException;
import com.example.matchup.matchupbackend.repository.EnterpriseVerifyRepository;
import com.example.matchup.matchupbackend.repository.user.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class EnterpriseService {

    private final UserRepository userRepository;
    private final EnterpriseVerifyRepository enterpriseVerifyRepository;
    private final AlertCreateService alertCreateService;

    /**
     * 기업 인증 신청
     */
    @Transactional
    public User applyVerifyEnterprise(Long userId, EnterpriseVerifyFormRequest verifyFormRequest) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("userId: " + userId + "에 해당하는 사용자가 없습니다."));
        enterpriseVerifyRepository.save(EnterpriseVerify.from(verifyFormRequest, user));
        return user;
    }

    /**
     * 기업 인증 신청 목록 조회
     */
    public SliceEnterpriseApply showEnterpriseVerifyList(Long adminId, Pageable pageable) {
        if (!isAdminUser(adminId)) {
            throw new AdminOnlyPermitException("기업 인증 신청 목록 조회는 Admin 만 가능 합니다.");
        }
        Slice<EnterpriseVerify> enterpriseApplySlice = enterpriseVerifyRepository.findAll(pageable);
        List<EnterpriseApply> enterpriseApplyList = enterpriseApplySlice.getContent().stream().map(EnterpriseApply::from).toList();
        return new SliceEnterpriseApply(enterpriseApplyList, enterpriseApplySlice.getSize(), enterpriseApplySlice.hasNext());
    }

    /**
     * 기업 인증 상태 변경
     */
    @Transactional
    public User changeEnterpriseVerifyState(Long adminId, EnterpriseStateRequest enterpriseStateRequest) {
        if (!isAdminUser(adminId)) {
            throw new AdminOnlyPermitException("기업 인증 상태 변경은 Admin 만 가능 합니다.");
        }
        EnterpriseVerify enterpriseVerify = enterpriseVerifyRepository.findByIdJoinUser(enterpriseStateRequest.getEnterpriseApplyId())
                .orElseThrow(() -> new UserNotFoundException("enterpriseApplyId: " + enterpriseStateRequest.getEnterpriseApplyId() + "에 해당하는 기업 인증 신청이 없습니다."));
        enterpriseVerify.changeState(enterpriseStateRequest.getIsAccepted());
        User user = enterpriseVerify.getUser();
        if (enterpriseStateRequest.getIsAccepted()) {
            user.changeRoleToUser();
            alertCreateService.enterpriseVerifyAlert(user, false);
        } else {
            user.changeRoleToEnterprise();
            alertCreateService.enterpriseVerifyAlert(user, true);
        }
        return user;
    }

    private boolean isAdminUser(Long adminId) {
        User admin = userRepository.findById(adminId)
                .orElseThrow(() -> new UserNotFoundException("adminId: " + adminId + "에 해당하는 사용자가 없습니다."));
        return admin.getRole().equals(Role.ADMIN);
    }
}
