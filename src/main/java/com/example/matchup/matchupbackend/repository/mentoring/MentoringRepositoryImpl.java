package com.example.matchup.matchupbackend.repository.mentoring;

import com.example.matchup.matchupbackend.dto.request.mentoring.MentoringSearchParam;
import com.example.matchup.matchupbackend.dto.request.mentoring.SearchType;
import com.example.matchup.matchupbackend.entity.Mentoring;
import com.example.matchup.matchupbackend.global.RoleType;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;

import java.util.List;

import static com.example.matchup.matchupbackend.entity.QMentoring.mentoring;
import static com.example.matchup.matchupbackend.entity.QMentoringTag.mentoringTag;

@Slf4j
@RequiredArgsConstructor
public class MentoringRepositoryImpl implements MentoringRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    @Override
    public Slice<Mentoring> findMentoringByMentoringSearchParam(MentoringSearchParam param, Pageable pageable) {
        List<Mentoring> mentorings = queryFactory
                .selectFrom(mentoring).distinct()
                .leftJoin(mentoring.mentoringTags, mentoringTag)
                .where(
                        mentoring.isDeleted.eq(false),
                        searchTitleContentOrWriterEq(param),
                        stackEq(param.getStack()),
                        roleTypeEq(param.getRoleType())
                )
                .orderBy(mentoring.id.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize() + 1)
                .fetch();

        boolean hasNext = false;
        if (mentorings.size() > pageable.getPageSize()) {
            hasNext = true;
            mentorings.remove(pageable.getPageSize());
        }

        return new SliceImpl<>(mentorings, pageable, hasNext);
    }

    private BooleanExpression roleTypeEq(RoleType roleType) {
        return roleType != null ? mentoring.roleType.eq(roleType) : null;
    }

    private BooleanExpression stackEq(String stack) {
        return stack != null ? mentoringTag.tagName.eq(stack) : null;
    }

    private BooleanExpression searchTitleContentOrWriterEq(MentoringSearchParam param) {
        if (param.getSearchType() == SearchType.WRITER) {
            return param.getSearchValue() != null ? mentoring.mentor.nickname.contains(param.getSearchValue()) : null;
        }
        else if (param.getSearchType() == SearchType.TITLE_AND_CONTENT) {
            return param.getSearchValue() != null ? mentoring.title.contains(param.getSearchValue()).or(mentoring.content.contains(param.getSearchValue())) : null;
        } return null;
    }
}
