package com.example.matchup.matchupbackend.repository.team;

import com.example.matchup.matchupbackend.dto.*;
import com.example.matchup.matchupbackend.entity.Team;
import com.example.matchup.matchupbackend.entity.TeamMentoring;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;

import java.util.List;

public interface TeamRepositoryCustom {
    Slice<Team> findTeamSliceByTeamRequest(TeamSearchRequest teamSearchRequest, Pageable pageable);
    TeamDetailResponse findTeamInfoByTeamId(Long teamID);
    MeetingSpot findMeetingSpotByTeamId(Long teamID);
    List<TeamMentoring> findTeamMentoringListByTeamId(Long teamID);
}
