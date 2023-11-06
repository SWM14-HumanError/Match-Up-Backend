package com.example.matchup.matchupbackend.service;

import com.example.matchup.matchupbackend.dto.Member;
import com.example.matchup.matchupbackend.dto.request.team.TeamCreateRequest;
import com.example.matchup.matchupbackend.dto.request.team.TeamSearchRequest;
import com.example.matchup.matchupbackend.dto.response.team.MeetingSpotResponse;
import com.example.matchup.matchupbackend.dto.response.team.SliceTeamResponse;
import com.example.matchup.matchupbackend.dto.response.team.TeamTypeResponse;
import com.example.matchup.matchupbackend.entity.Team;
import com.example.matchup.matchupbackend.entity.User;
import com.example.matchup.matchupbackend.repository.team.TeamRepository;
import com.example.matchup.matchupbackend.repository.user.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.tuple;

@SpringBootTest
@ActiveProfiles("test")
class TeamServiceTest {
    @Autowired
    private TeamService teamService;

    @Autowired
    private TeamRepository teamRepository;

    @Autowired
    private UserRepository userRepository;


    @Test
    @DisplayName("메인페이지 API에 보여지는 팀 리스트를 조회한다.")
    public void searchSliceTeamResponseList(){
        //given
        TeamSearchRequest teamSearchRequest = TeamSearchRequest.builder()
                .type(0L)
                .category("웹")
                .search("검색어")
                .page(0)
                .size(10)
                .build();
        Pageable pageable = PageRequest.of(teamSearchRequest.getPage(), teamSearchRequest.getSize());
        createDummyTeams();
        createDummyUsers();
        //when
        SliceTeamResponse sliceTeamResponse = teamService.searchSliceTeamResponseList(teamSearchRequest, pageable);

        //then
        assertThat(sliceTeamResponse.getTeamSearchResponseList().size()).isEqualTo(2);
        assertThat(sliceTeamResponse.getTeamSearchResponseList())
                .extracting("id", "title", "description", "thumbnailUrl", "leaderID", "leaderNickname", "leaderLevel")
                .containsExactlyInAnyOrder(
                        tuple(2L, "검색어1", "description", null, 1L, "nickname", null),
                        tuple(3L, "검색어2", "description", null, 1L, "nickname", null)
                );
    }

    @Transactional
    public void createDummyTeams(){
        Team team = Team.of(1L, createTeamCreateRequest1());
        Team team2 = Team.of(1L, createTeamCreateRequest2());
        Team team3 = Team.of(1L, createTeamCreateRequest3());
        teamRepository.saveAll(List.of(team, team2, team3));
    }

    public void createDummyUsers(){
        User user1 = User.builder()
                .nickname("nickname")
                .email("email")
                .build();
        userRepository.saveAll(List.of(user1));
    }

    @Test
    @DisplayName("로그인 한 유저가 새로운 팀을 생성하고 생성된 팀 ID를 반환한다.")
    void makeNewTeam(){
        //given
        Long leaderId = 1L;
        TeamCreateRequest teamCreateRequest = createTeamCreateRequest1();
        createDummyUsers();
        //when
        Long teamID = teamService.makeNewTeam(leaderId, teamCreateRequest);

        //then
        assertThat(teamID).isEqualTo(1L);
    }

    private TeamCreateRequest createTeamCreateRequest1() {
        return TeamCreateRequest.builder()
                .imageBase64(null)
                .imageName(null)
                .name("name")
                .type(getTeamTypeResponse())
                .description("description")
                .meetingSpot(getMeetingSpotResponse())
                .meetingDate("2021-08-01")
                .memberList(List.of(Member.builder()
                        .role("BE")
                        .stacks(List.of("java", "spring"))
                        .maxCount(3L)
                        .build()))
                .build();
    }

    private TeamCreateRequest createTeamCreateRequest2() {
        return TeamCreateRequest.builder()
                .imageBase64(null)
                .imageName(null)
                .name("검색어1")
                .type(getTeamTypeResponse())
                .description("description")
                .meetingSpot(getMeetingSpotResponse())
                .meetingDate("2021-08-01")
                .memberList(List.of(Member.builder()
                        .role("BE")
                        .stacks(List.of("java", "spring"))
                        .maxCount(3L)
                        .build()))
                .build();
    }

    private TeamCreateRequest createTeamCreateRequest3() {
        return TeamCreateRequest.builder()
                .imageBase64(null)
                .imageName(null)
                .name("검색어2")
                .type(getTeamTypeResponse())
                .description("description")
                .meetingSpot(getMeetingSpotResponse())
                .meetingDate("2021-08-01")
                .memberList(List.of(Member.builder()
                        .role("BE")
                        .stacks(List.of("java", "spring"))
                        .maxCount(3L)
                        .build()))
                .build();
    }


    private TeamTypeResponse getTeamTypeResponse(){
        return TeamTypeResponse.builder()
                .teamType(0L)
                .detailType("웹")
                .build();
    }

    private MeetingSpotResponse getMeetingSpotResponse(){
        return MeetingSpotResponse.builder()
                .onOffline("ONLINE")
                .city("서울")
                .detailSpot("강남역")
                .build();
    }

}