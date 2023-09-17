package com.example.matchup.matchupbackend.service;

import com.example.matchup.matchupbackend.dto.request.team.TeamCreateRequest;
import com.example.matchup.matchupbackend.dto.request.teamuser.AcceptFormRequest;
import com.example.matchup.matchupbackend.entity.*;
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
                .content(teamCreateRequest.getName() + " - 생성되었습니다.")
                .redirectUrl(teamCreateRequest.getType().getTeamType() == 0L ? "/project/" + teamID : "/study/" + teamID)
                .alertType(teamCreateRequest.getType().getTeamType() == 0L ? AlertType.PROJECT : AlertType.STUDY)
                .build();
        alert.setUser(sendTo);
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
                .content(teamCreateRequest.getName() + " - 정보가 업데이트 되었습니다.")
                .redirectUrl(teamCreateRequest.getType().getTeamType() == 0L ? "/project/"+ teamID : "/study/" + teamID)
                .alertType(teamCreateRequest.getType().getTeamType() == 0L ? AlertType.PROJECT : AlertType.STUDY)
                .build();
        sendAlertToUsers(sendTo, alert);
    }

    /**
     * 팀 삭제 알림 저장
     * @param sendTo
     * @param team
     */
    public void saveTeamDeleteAlert(List<User> sendTo, Team team) {
        Alert alert = Alert.builder()
                .title(team.getType() == 0L ? "프로젝트 삭제" : "스터디 삭제")
                .content(team.getTitle() + " - 팀장에 의해서 삭제되었습니다.")
                .redirectUrl("/") // 삭제된 팀이라고 알림
                .alertType(team.getType() == 0L ? AlertType.PROJECT : AlertType.STUDY)
                .build();
        sendAlertToUsers(sendTo, alert);
    }

    /**
     * 유저가 팀에 지원하는 알림 저장
     * @param leader
     * @param volunteer
     * @param team
     */
    public void saveTeamUserRecruitAlert(User leader, User volunteer, Team team) {
        // 팀장에게 보낼 지원 알림
        Alert toLeader = Alert.builder()
                .title(team.getType() == 0L ? "프로젝트 지원" : "스터디 지원")
                .content(volunteer.getName() + " 님이 " + team.getTitle() + " 에 함께 하고 싶어 합니다.")
                .redirectUrl("/유저 지원서 모달창") //todo 지원서 모달창 url
                .alertType(team.getType() == 0L ? AlertType.PROJECT : AlertType.STUDY)
                .build();
        toLeader.setUser(leader);
        alertRepository.save(toLeader);

        // 지원자에게 보낼 지원 알림
        Alert toVolunteer = Alert.builder()
                .title(team.getType() == 0L ? "프로젝트 지원" : "스터디 지원")
                .content(team.getTitle() + " - 지원하였습니다.")
                .redirectUrl("/유저 지원서 모달창") //todo 지원서 모달창 url
                .alertType(team.getType() == 0L ? AlertType.PROJECT : AlertType.STUDY)
                .build();
        toVolunteer.setUser(volunteer);
        alertRepository.save(toVolunteer);
    }

    /**
     * 유저가 팀원으로 수락 되었을때 보내는 알림 저장
     */
    public void saveUserAcceptToTeamAlert(List<User> sendTo, TeamUser volunteer, AcceptFormRequest acceptForm) {
        Team team = volunteer.getTeam();

        // 지원자에게 보낼 알림
        Alert toVolunteer = Alert.builder()
                .title("팀원 수락")
                .content("축하드립니다! " + team.getTitle() + " - " + acceptForm.getRole() + "로 함께 하게 되었습니다")
                .redirectUrl(team.getType() == 0L ? "/project/" + team.getId() : "/study/" + team.getId())
                .alertType(team.getType() == 0L ? AlertType.PROJECT : AlertType.STUDY)
                .build();
        toVolunteer.setUser(volunteer.getUser());
        alertRepository.save(toVolunteer);

        // 기존 유저에게 보낼 알림
        Alert toTeamUser = Alert.builder()
                .title("팀원 수락")
                .content(volunteer.getUser().getName() + " 님이 " + team.getTitle() + " - " + acceptForm.getRole() + "로 함께 합니다.")
                .redirectUrl(team.getType() == 0L ? "/project/" + team.getId() : "/study/" + team.getId())
                .alertType(team.getType() == 0L ? AlertType.PROJECT : AlertType.STUDY)
                .build();
        sendAlertToUsers(sendTo, toTeamUser);
    }

    /**
     * 여러명의 유저에게 "같은 알림"을 보내는 메서드
     * @param sendTo
     * @param alert
     */
    public void sendAlertToUsers(List<User> sendTo, Alert alert) {
        for (User user : sendTo) {
            Alert newAlert = Alert.from(alert);
            newAlert.setUser(user);
            alertRepository.save(newAlert);
        }
    }
}
