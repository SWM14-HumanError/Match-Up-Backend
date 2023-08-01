package com.example.matchup.matchupbackend.controller;

import com.example.matchup.matchupbackend.dto.*;
import com.example.matchup.matchupbackend.dto.mentoring.MentoringCardResponse;
import com.example.matchup.matchupbackend.service.TeamService;
import com.example.matchup.matchupbackend.service.TeamUserService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
@Slf4j
public class TeamController {
    private final TeamService teamService;
    private final TeamUserService teamUserService;

    @GetMapping("/list/team")
    @ResponseStatus(HttpStatus.OK)
    @Operation(description = "메인 페이지 API")
    public SliceTeamResponse showTeams(TeamSearchRequest teamSearchRequest, Pageable pageable) {
        return teamService.searchSliceTeamList(teamSearchRequest, pageable);
    }

    @PostMapping("/team")
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(description = "팀 생성 및 저장")
    public Long makeTeam(@RequestBody TeamCreateRequest teamCreateRequest) {
        return teamService.makeNewTeam(teamCreateRequest);
    }

    //팀 내용 업데이트
    @PutMapping("/team/{teamID}")
    @Operation(description = "팀 정보 수정") //인증 정보 추가돼서 팀장만 삭제할수 있도록 함
    public ResponseEntity<String> updateTeam(@PathVariable Long teamID, @RequestBody TeamCreateRequest teamCreateRequest) {
        if (teamService.isUpdatable(teamID, teamCreateRequest) == false) //업데이트 불가능
        {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("기존 팀원 인원수 보다 높게 인원수를 설정하세요");
        }
        teamService.updateTeam(teamID, teamCreateRequest);
        return ResponseEntity.ok("업데이트 완료");
    }

    @DeleteMapping("/team/{teamID}")
    @Operation(description = "팀 삭제") //인증 정보 추가돼서 팀장만 삭제할수 있도록 함
    public ResponseEntity<String> deleteTeam(@PathVariable Long teamID) {
        teamService.deleteTeam(teamID);
        return ResponseEntity.ok("팀 삭제 완료");
    }

/*


    @GetMapping("/team/{teamID}")
    @Operation(description = "팀 세부 정보 불러오기") //인증
    public ResponseEntity<TeamDetailResponse> showTeamDetail(@PathVariable Long teamID)
    {
        TeamDetailResponse teamDetailResponse = teamService.showTeamDetail(teamID);
//        if(teamDetailResponse == null)
//        {
//            return null;  //여기 부분 어떻게 할지
//        }
        return ResponseEntity.ok(teamDetailResponse);
    }
 */

    @GetMapping("/team/{teamID}/info")
    @ResponseStatus(HttpStatus.OK)
    @Operation(description = "팀 상세페이지의 제목,상세설명 API")
    public TeamDetailResponse showTeamInfo(@PathVariable Long teamID) {
        return teamService.getTeamInfo(teamID);
    }

    @GetMapping("/team/{teamID}/member")
    @ResponseStatus(HttpStatus.OK)
    @Operation(description = "팀 상세페이지의 유저 API")
    public List<TeamUserCardResponse> showTeamUsers(@PathVariable Long teamID) {
        return teamUserService.getTeamUserCard(teamID);
    }

    @GetMapping("/team/{teamID}/spot")
    @ResponseStatus(HttpStatus.OK)
    @Operation(description = "팀 상세페이지의 모임 장소 API")
    public MeetingSpot showTeamMeetingSpot(@PathVariable Long teamID) {
        return teamService.getTeamMeetingSpot(teamID);
    }

    @GetMapping("/team/{teamID}/mentoring")
    @ResponseStatus(HttpStatus.OK)
    @Operation(description = "팀 상세페이지의 멘토링 API")
    public List<MentoringCardResponse> showTeamMentoringList(@PathVariable Long teamID) {
        return teamService.getTeamMentoringCardList(teamID);
    }

    @GetMapping("/team/{teamID}/stacks")
    @ResponseStatus(HttpStatus.OK)
    @Operation(description = "팀 상세페이지의 개발 스택 API")
    public List<String> showTeamTagList(@PathVariable Long teamID) {
        return teamService.getTeamTagStringList(teamID);
    }

}
