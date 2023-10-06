package com.example.matchup.matchupbackend.service;

import com.example.matchup.matchupbackend.dto.request.team.TeamCreateRequest;
import com.example.matchup.matchupbackend.dto.request.teamuser.AcceptFormRequest;
import com.example.matchup.matchupbackend.dto.response.user.SuggestInviteMyTeamRequest;
import com.example.matchup.matchupbackend.entity.*;
import com.example.matchup.matchupbackend.error.exception.ResourceNotFoundEx.TeamNotFoundException;
import com.example.matchup.matchupbackend.error.exception.ResourceNotFoundEx.UserNotFoundException;
import com.example.matchup.matchupbackend.error.exception.ResourceNotPermitEx.ResourceNotPermitException;
import com.example.matchup.matchupbackend.global.config.jwt.TokenProvider;
import com.example.matchup.matchupbackend.repository.alert.AlertRepository;
import com.example.matchup.matchupbackend.repository.team.TeamRepository;
import com.example.matchup.matchupbackend.repository.user.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static com.example.matchup.matchupbackend.error.ErrorCode.NOT_PERMITTED;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class AlertCreateService {
    private final AlertRepository alertRepository;
    private final TokenProvider tokenProvider;
    private final UserRepository userRepository;
    private final TeamRepository teamRepository;

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
                .redirectUrl("/team/" + teamID)
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
                .redirectUrl("/team/" + teamID)
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
                .redirectUrl("/")
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
    public void saveTeamUserRecruitAlert(User leader, User volunteer, Team team, Long recruitID) {
        // 팀장에게 보낼 지원 알림
        Alert toLeader = Alert.builder()
                .title(team.getType() == 0L ? "프로젝트 지원" : "스터디 지원")
                .content(volunteer.getName() + " 님이 " + team.getTitle() + " 에 함께 하고 싶어 합니다.")
                .redirectUrl("/team/" + team.getId() + "#팀원")
                .alertType(team.getType() == 0L ? AlertType.PROJECT : AlertType.STUDY)
                .build();
        toLeader.setUser(leader);
        alertRepository.save(toLeader);

        // 지원자에게 보낼 지원 알림
        Alert toVolunteer = Alert.builder()
                .title(team.getType() == 0L ? "프로젝트 지원" : "스터디 지원")
                .content(team.getTitle() + " - 지원하였습니다.")
                .redirectUrl("/team/" + team.getId() + "#팀원")
                .alertType(team.getType() == 0L ? AlertType.PROJECT : AlertType.STUDY)
                .build();
        toVolunteer.setUser(volunteer);
        alertRepository.save(toVolunteer);
    }

    /**
     * 유저가 팀원으로 수락 되었을때 보내는 알림 저장
     * @param sendTo
     * @param volunteer
     * @param acceptForm
     */
    public void saveUserAcceptedToTeamAlert(List<User> sendTo, TeamUser volunteer, AcceptFormRequest acceptForm) {
        Team team = volunteer.getTeam();
        User user = volunteer.getUser();

        // 지원자에게 보낼 알림
        Alert toVolunteer = Alert.builder()
                .title("팀원 수락")
                .content("축하드립니다! " + team.getTitle() + " - " + acceptForm.getRole() + "로 함께 하게 되었습니다.")
                .redirectUrl("/team/" + team.getId())
                .alertType(team.getType() == 0L ? AlertType.PROJECT : AlertType.STUDY)
                .build();
        toVolunteer.setUser(user);
        alertRepository.save(toVolunteer);

        // 기존 유저에게 보낼 알림
        Alert toTeamUser = Alert.builder()
                .title("팀원 수락")
                .content(volunteer.getUser().getName() + " 님이 " + team.getTitle() + " - " + acceptForm.getRole() + "로 함께 합니다.")
                .redirectUrl("/team/" + team.getId())
                .alertType(team.getType() == 0L ? AlertType.PROJECT : AlertType.STUDY)
                .build();
        sendAlertToUsers(sendTo, toTeamUser);
    }

    /**
     * 유저가 팀원으로 거절 되었을때 알림 생성
     * @param leader
     * @param volunteer
     */
    public void saveUserRefusedToTeamAlert(TeamUser leader, User volunteer, Long refuseID) {
        Team team = leader.getTeam();

        // 팀장에게 보낼 지원 알림
        Alert toLeader = Alert.builder()
                .title("팀원 거절")
                .content(volunteer.getName() + " 님에게 팀원 거절 메세지를 보냈습니다.")
                .redirectUrl("?modal=denyContents&refuseID=" + refuseID)
                .alertType(team.getType() == 0L ? AlertType.PROJECT : AlertType.STUDY)
                .build();
        toLeader.setUser(leader.getUser());
        alertRepository.save(toLeader);

        // 지원자에게 보낼 지원 알림
        Alert toVolunteer = Alert.builder()
                .title("팀원 거절")
                .content(team.getTitle() + " - 지원이 거절 되었습니다. (클릭하여 거절 사유 보기)")
                .redirectUrl("?modal=denyContents&refuseID=" + refuseID)
                .alertType(team.getType() == 0L ? AlertType.PROJECT : AlertType.STUDY)
                .build();
        toVolunteer.setUser(volunteer);
        alertRepository.save(toVolunteer);
    }

    /**
     * 팀장이 유저를 강퇴하였을때 알림 생성
     * @param kickedUser
     */
    public void saveUserKickedToTeamAlert(TeamUser kickedUser) {
        Team team = kickedUser.getTeam();

        // 강퇴 유저에게 보낼 알림
        Alert toKickedUser = Alert.builder()
                .title("강퇴 알림")
                .content(team.getTitle() + " - 팀장에 의해서 강퇴되었습니다.")
                .redirectUrl("/방출사유URL") //todo 방출 사유 url
                .alertType(team.getType() == 0L ? AlertType.PROJECT : AlertType.STUDY)
                .build();
        toKickedUser.setUser(kickedUser.getUser());
        alertRepository.save(toKickedUser);
    }

    /**
     * 피드백을 보냈을때 알림 생성
     * @param giver
     * @param receiver
     * @param team
     */
    public void saveFeedbackAlert(User giver, User receiver, Team team) {
        // 피드백 보낸 사람 알림
        Alert toGiver = Alert.builder()
                .title("피드백이 발송되었습니다")
                .content(team.getTitle() + " - " + receiver.getName() + "님에게 피드백을 전달했습니다.") // 휴먼에러 - 준혁님에게 피드백을 전달했습니다
                .redirectUrl("")
                .alertType(team.getType() == 0L ? AlertType.PROJECT : AlertType.STUDY)
                .build();
        toGiver.setUser(giver);
        alertRepository.save(toGiver);

        // 피드백 받은 사람 알림
        Alert toReceiver = Alert.builder()
                .title("피드백이 도착하였습니다")
                .content(team.getTitle() + " - 피드백이 도착했습니다.")
                .redirectUrl("/mypage/profile#상호 평가")
                .alertType(team.getType() == 0L ? AlertType.PROJECT : AlertType.STUDY)
                .build();
        toReceiver.setUser(receiver);
        alertRepository.save(toReceiver);
    }

    /**
     * 피드에 댓글을 작성했을때 알림 생성
     * @param feed
     * @param commenter
     * @param comment
     */
    public void saveCommentCreateAlert(Feed feed, User commenter, Comment comment) {
        Alert alert = Alert.builder()
                .title(commenter.getName() + " 님이 댓글을 작성했습니다.")
                .content(comment.getContent())
                .redirectUrl("/feed/" + feed.getId())
                .alertType(AlertType.FEED)
                .build();
        alert.setUser(feed.getUser());
        alertRepository.save(alert);
    }

    /**
     * 피드에 좋아요를 눌렀을때 알림 생성
     * @param liker
     * @param feed
     * @param likes
     */
    public void saveFeedLikeAlert(User liker, Feed feed, Integer likes) { //todo 유저 많아지면 알림을 한번에 몰아서 보내는 방법도 생각해야 함
        Alert alert = Alert.builder()
                .title(liker.getNickname() + " 님이 " + feed.getTitle() + " 에 좋아요를 눌렀습니다.")
                .content("누적 좋아요 갯수 : " + likes)
                .redirectUrl("/feed/" + feed.getId())
                .alertType(AlertType.FEED)
                .build();
        alert.setUser(feed.getUser());
        alertRepository.save(alert);
    }

    /**
     * 팀에 좋아요를 눌렀을때 알림 생성
     * @param liker
     * @param team
     * @param likes
     */
    public void saveTeamLikeAlert(User liker, Team team, Integer likes) { //todo 유저 많아지면 알림을 한번에 몰아서 보내는 방법도 생각해야 함
        Alert alert = Alert.builder()
                .title(liker.getNickname() + " 님이 " + team.getTitle() + " 에 좋아요를 눌렀습니다.")
                .content("누적 좋아요 갯수 : " + likes)
                .redirectUrl("/team/" + team.getId())
                .alertType(team.getType() == 0L ? AlertType.PROJECT : AlertType.STUDY)
                .build();
        sendAlertToTeamUsers(team.getTeamUserList(), alert);
    }

    /**
     * 유저에 좋아요를 눌렀을때 알림 생성
     */
    public void saveUserLikeAlert(User liker, User receiver, Long likes) {
        Alert alert = Alert.builder()
                .title(liker.getNickname() + " 님이 " + receiver.getNickname() + " 님에게 좋아요를 눌렀습니다.")
                .content("누적 좋아요 갯수 : " + likes)
                .redirectUrl("/mypage/profile")
                .alertType(AlertType.ETC)
                .user(receiver)
                .build();
        alertRepository.save(alert);
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

    /**
     * 여러명의 "팀원"에게 "같은 알림"을 보내는 메서드
     * @param sendTo
     * @param alert
     */
    public void sendAlertToTeamUsers(List<TeamUser> sendTo, Alert alert) {
        for (TeamUser teamUser : sendTo) {
            Alert newAlert = Alert.from(alert);
            newAlert.setUser(teamUser.getUser());
            alertRepository.save(newAlert);
        }
    }

    /**
     * 내 프로젝트에 초대하기에 지원할 때 필요한 검증과 대상자 알림에 등록합니다.
     */
    @Transactional
    public void postInviteMyTeam(String authorizationHeader, SuggestInviteMyTeamRequest request) {
        Long userId = tokenProvider.getUserId(authorizationHeader, "내 프로젝트에 초대하기를 지원하고 있습니다.");
        User suggester = userRepository.findUserById(userId).orElseThrow(() -> new UserNotFoundException("내 프로젝트에 초대하기를 지원하는 과정에서 존재하지 않는 제안 유저 id를 요청했습니다."));
        User receiver = userRepository.findUserById(request.getReceiverId()).orElseThrow(() -> new UserNotFoundException("내 프로젝트에 초대하기를 지원하는 과정에서 존재하지 않는 제안 유저 id를 요청했습니다."));
        Team team = teamRepository.findTeamById(request.getTeamId()).orElseThrow(() -> new TeamNotFoundException("내 프로젝트에 초대하기를 지원하는 과정에서 존재하지 않는 팀 id로 요청했습니다."));

        isProperSuggestMyTeam(team, suggester, receiver);

        alertRepository.save(Alert.builder()
                .title(team.getType().equals(0L) ? "프로젝트에 초대받았습니다." : "스터디에 초대받았습니다.")
                .redirectUrl("/team/" + request.getTeamId())
                .content(request.getContent())
                .alertType(team.getType().equals(0L) ? AlertType.PROJECT : AlertType.STUDY)
                .user(receiver)
                .build());
    }

    private void isProperSuggestMyTeam(Team team, User suggester, User receiver) {
        if (!team.getIsDeleted().equals(0L)) {
            throw new ResourceNotPermitException(NOT_PERMITTED, "내 프로젝트에 초대하기를 지원하면서 삭제된 팀으로 요청했습니다.");
        }

        if (team.getTeamUserList().stream().noneMatch(teamUser -> teamUser.getUser().equals(suggester) && teamUser.getApprove())) {
            throw new ResourceNotPermitException(NOT_PERMITTED, "내 프로젝트에 초대하기를 지원하면서 초대할 수 없는 팀원이 요청했습니다.");
        }

        if (team.getTeamUserList().stream().anyMatch(teamUser -> teamUser.getUser().equals(receiver))) {
            throw new ResourceNotPermitException(NOT_PERMITTED, "내 프로젝트에 초대하기를 지원하면서 이미 합류된 팀원이나 자기 자신에게 초대를 보냈습니다.");
        }
    }
}
