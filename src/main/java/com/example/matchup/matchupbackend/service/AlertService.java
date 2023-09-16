package com.example.matchup.matchupbackend.service;

import com.example.matchup.matchupbackend.dto.request.team.TeamCreateRequest;
import com.example.matchup.matchupbackend.entity.Alert;
import com.example.matchup.matchupbackend.entity.AlertType;
import com.example.matchup.matchupbackend.entity.User;
import com.example.matchup.matchupbackend.repository.AlertRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class AlertService {
    private final AlertRepository alertRepository;

    /**
     * 팀 생성 알림 저장
     * @param teamID
     * @param sendTo
     * @param teamCreateRequest
     */
    public void saveTeamCreateAlert(Long teamID, User sendTo, TeamCreateRequest teamCreateRequest) {
        Alert alert = Alert.builder()
                .title(teamCreateRequest.getType().getTeamType() == 0L ? "프로젝트 생성" : "스터디 생성")
                .content(teamCreateRequest.getName() + " 팀이 생성되었습니다.")
                .redirectUrl(teamCreateRequest.getType().getTeamType() == 0L ? "/project/" : "/study/" + teamID)
                .alertType(teamCreateRequest.getType().getTeamType() == 0L ? AlertType.PROJECT : AlertType.STUDY)
                .user(sendTo)
                .build();
        alertRepository.save(alert);
    }

    /**
     * 팀 업데이트 알림 저장
     * @param teamID
     * @param sendTo
     * @param teamCreateRequest
     */
    public void saveTeamUpdateAlert(Long teamID, List<User> sendTo, TeamCreateRequest teamCreateRequest) {
        Alert alert = Alert.builder()
                .title(teamCreateRequest.getType().getTeamType() == 0L ? "프로젝트 업데이트" : "스터디 업데이트")
                .content(teamCreateRequest.getName() + " 팀의 정보가 업데이트 되었습니다.")
                .redirectUrl(teamCreateRequest.getType().getTeamType() == 0L ? "/project/" : "/study/" + teamID)
                .alertType(teamCreateRequest.getType().getTeamType() == 0L ? AlertType.PROJECT : AlertType.STUDY)
                .build();
        sendAlertToUsers(sendTo, alert);
    }



    /**
     * 여러명의 유저에게 알림을 보냄
     * @param sendTo
     * @param alert
     */
    public void sendAlertToUsers(List<User> sendTo, Alert alert) {
        for (User user : sendTo) {
            alert.setUser(user);
            alertRepository.save(alert);
        }
    }
}
