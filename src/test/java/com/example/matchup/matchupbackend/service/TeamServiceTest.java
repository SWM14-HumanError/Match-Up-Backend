package com.example.matchup.matchupbackend.service;

import com.example.matchup.matchupbackend.dto.Member;
import com.example.matchup.matchupbackend.dto.request.team.TeamCreateRequest;
import com.example.matchup.matchupbackend.dto.response.team.MeetingSpotResponse;
import com.example.matchup.matchupbackend.dto.response.team.TeamTypeResponse;
import com.example.matchup.matchupbackend.repository.team.TeamRepository;
import com.example.matchup.matchupbackend.repository.user.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

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
    @DisplayName("로그인 한 유저가 새로운 팀을 생성한다.")
    void makeNewTeam(){
        //given
        Long leaderId = 1L;
        TeamCreateRequest teamCreateRequest = createTeamCreateRequest();

        //when
        Long teamID = teamService.makeNewTeam(leaderId, teamCreateRequest);

        //then
        assertThat(teamID).isEqualTo(1L);
    }

    private TeamCreateRequest createTeamCreateRequest() {
        return TeamCreateRequest.builder()
                .imageBase64(null)
                .imageName("imageName")
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