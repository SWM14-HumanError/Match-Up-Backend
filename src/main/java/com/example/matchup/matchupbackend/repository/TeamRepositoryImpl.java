package com.example.matchup.matchupbackend.repository;

import com.example.matchup.matchupbackend.dto.TeamSearchRequest;
import com.example.matchup.matchupbackend.dto.TeamSearchResponse;
import com.example.matchup.matchupbackend.entity.QTeam;
import com.example.matchup.matchupbackend.entity.Team;
import com.querydsl.core.types.Predicate;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;

import java.util.List;

import static com.example.matchup.matchupbackend.entity.QTeam.*;

@RequiredArgsConstructor
public class TeamRepositoryImpl implements TeamRepositoryCustom{
    private final JPAQueryFactory queryFactory;
    private QTeam qTeam = team;

    @Override
    public Slice<TeamSearchResponse> findTeamSliceByTeamRequest(TeamSearchRequest teamSearchRequest, Pageable pageable) {
        List<TeamSearchResponse> content = queryFactory
                .select(Projections.bean(TeamSearchResponse.class,
                        team.id,
                        team.title,
                        team.content.as("description"),
                        team.like,
                        team.thumbnailUrl
                ))
                .from(team)
                .where(typeEq(teamSearchRequest.getType()),
                        categoryEq(teamSearchRequest.getCategory()),
                        searchEq(teamSearchRequest.getSearch()))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize() + 1)
                .fetch();

        boolean hasNext = false;
        if(content.size()>pageable.getPageSize())
        {
            hasNext=true;
            content.remove(pageable.getPageSize());
        }


        return new SliceImpl<>(content, pageable, hasNext);
    }

    private Predicate searchEq(String search) {
        return (search != null) ? team.title.contains(search) : null;
    }
// tag 중 하나라도 있으면 가져옴
    private Predicate categoryEq(String category) {
        if(category != null){ //태그 안에 카테고리가 있는지 찾아야함
            return team.tags.any().name.eq(category); // any하면 리스트를 통째로 들고 온다
        }
        else return null;
    }

    private Predicate typeEq(Long type) {
        return (type != null) ? team.type.eq(type) : null;
    }

}