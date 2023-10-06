package com.example.matchup.matchupbackend.repository.team;

import com.example.matchup.matchupbackend.dto.request.team.TeamSearchRequest;
import com.example.matchup.matchupbackend.dto.response.team.MeetingSpotResponse;
import com.example.matchup.matchupbackend.dto.response.team.TeamDetailResponse;
import com.example.matchup.matchupbackend.entity.*;
import com.example.matchup.matchupbackend.entity.QTeamMentoring;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;
import org.springframework.stereotype.Repository;

import java.util.List;

import static com.example.matchup.matchupbackend.entity.QMentoring.mentoring;
import static com.example.matchup.matchupbackend.entity.QTeamMentoring.*;
import static com.example.matchup.matchupbackend.entity.QTeam.*;
import static com.example.matchup.matchupbackend.entity.QTeamTag.teamTag;
import static com.example.matchup.matchupbackend.entity.QTeamUser.*;

@Repository
@RequiredArgsConstructor
public class TeamRepositoryCustomImpl implements TeamRepositoryCustom {
    private final JPAQueryFactory queryFactory;
    private QTeam qTeam = team;
    private QTeamTag qTeamTag = teamTag;
    private QTeamUser qTeamUser = teamUser;
    private QTeamMentoring qTeamMentoring = teamMentoring;
    private QMentoring qMentoring = mentoring;

    @Override
    public Slice<Team> findTeamSliceByTeamRequest(TeamSearchRequest teamSearchRequest, Pageable pageable) {
        List<Team> content = queryFactory
                .selectFrom(team)
                .where(typeEq(teamSearchRequest.getType()),
                        categoryEq(teamSearchRequest.getCategory()),
                        searchEq(teamSearchRequest.getSearch()),
                        team.isDeleted.eq(0L))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize() + 1)
                .fetch();

        boolean hasNext = false;
        if (content.size() > pageable.getPageSize()) {
            hasNext = true;
            content.remove(pageable.getPageSize());
        }


        return new SliceImpl<>(content, pageable, hasNext);
    }

    private BooleanExpression searchEq(String search) {
        return (search != null) ? team.title.contains(search) : null;
    }

    // tag 중 하나라도 있으면 가져옴
    private BooleanExpression categoryEq(String category) {
        return (category != null) ? team.detailType.eq(category) : null;
    }

    private BooleanExpression typeEq(Long type) {
        return (type != null) ? team.type.eq(type) : null;
    }

    @Override
    public TeamDetailResponse findTeamInfoByTeamId(Long teamID) {
        TeamDetailResponse content = queryFactory
                .select(Projections.bean(TeamDetailResponse.class,
                        team.id.as("teamID"),
                        team.title,
                        team.description,
                        team.leaderID,
                        team.thumbnailUrl,
                        team.meetingTime))
                .from(team)
                .where(team.id.eq(teamID))
                .fetchOne();
        return content;
    }

    @Override
    public MeetingSpotResponse findMeetingSpotByTeamId(Long teamID) {
        MeetingSpotResponse content = queryFactory
                .select(Projections.bean(MeetingSpotResponse.class,
                        team.onOffline,
                        team.city,
                        team.detailSpot
                ))
                .from(team)
                .where(team.id.eq(teamID))
                .fetchOne();
        return content;
    }

    @Override
    public List<TeamMentoring> findTeamMentoringListByTeamId(Long teamID) {
        List<TeamMentoring> content = queryFactory
                .selectFrom(teamMentoring)
                .where(teamMentoring.team.id.eq(teamID))
                .fetch();
        return content;
    }
}

    /*
    @Override
    public TeamDetailResponse findTeamDetailByTeamID(Long teamID) {
        TeamDetailResponse content = queryFactory
                .select(Projections.bean(TeamDetailResponse.class,
                        team.id,
                        team.title,
                        team.description,
                        team.leaderID,
                        //"teamUserCardList"
                        Projections.list(Projections.bean(TeamUserCardResponse.class,
                                teamUser.user.id,
                                teamUser.user.pictureUrl,
                                teamUser.user.userLevel,
                                teamUser.user.name,
                                //"position"
                                Projections.bean(Position.class,
                                        teamUser.user.position,
                                        teamUser.user.positionLevel),
                                //!!!!!!!!!!!!!!!!!teamUser.user.reviewAverage,
                                teamUser.user.likes,
                                //"stacks"
                                Projections.list(
                                        teamUser.user.userTagList.),
                                teamUser.role,
                                teamUser.approve
                                )),
                        Projections.bean(MeetingSpot.class,
                                team.onOffline,
                                team.city,
                                team.detailSpot),
                        Projections.list(Projections.bean(MentoringCardResponse.class,
                                mentoringTeam.mentoring.thumbnailURL,
                                mentoringTeam.mentoring.title,
                                //"position"
                                Projections.bean(Position.class,
                                        mentoringTeam.mentoring.user.position,
                                        mentoringTeam.mentoring.user.positionLevel),
                                mentoringTeam.mentoring.user.pictureUrl,
                                mentoringTeam.mentoring.user.name,
                                //mentoringTeam.mentoring.reviewAverage,
                                mentoringTeam.mentoring.likes
                                )),
                        Projections.list(team.teamTagList)
                ))
                .from(team)
                .leftJoin(teamUser).fetchJoin().on(teamUser.id.eq(teamID))
                .leftJoin(mentoringTeam).fetchJoin().on(mentoringTeam.id.eq(teamID))
                .where(team.id.eq(teamID)) //approve 된 애들만 return
                .fetchOne();
        return content;
    }
     */
