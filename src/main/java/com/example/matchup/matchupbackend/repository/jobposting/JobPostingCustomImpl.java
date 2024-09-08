package com.example.matchup.matchupbackend.repository.jobposting;

import com.example.matchup.matchupbackend.dto.request.jobposting.JobPostingSearchRequest;
import com.example.matchup.matchupbackend.entity.JobPosition;
import com.example.matchup.matchupbackend.entity.JobPosting;
import com.example.matchup.matchupbackend.entity.JobType;
import com.example.matchup.matchupbackend.entity.QJobPosting;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class JobPostingCustomImpl implements JobPostingCustom {
    private final JPAQueryFactory queryFactory;
    private QJobPosting jobPosting = QJobPosting.jobPosting;

    @Override
    public Page<JobPosting> findJobPostingBySearchRequest(JobPostingSearchRequest jobSearchRequest, Pageable pageable) {
        List<JobPosting> jobPostings = queryFactory.selectFrom(jobPosting)
                .where(
                        contentEq(jobSearchRequest.getSearchWord()),
                        jobPositionEq(jobSearchRequest.getJobPosition()),
                        jobTypeEq(jobSearchRequest.getJobType()),
                        hideClosedJobEq(jobSearchRequest.isHideClosedJob())
                )
                .orderBy(jobPosting.createTime.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();
        return new PageImpl<>(jobPostings, pageable, countAll(jobSearchRequest));
    }

    @Override
    public Long countAll(JobPostingSearchRequest jobSearchRequest) {
        return queryFactory.select(jobPosting.count())
                .from(jobPosting)
                .where(
                        contentEq(jobSearchRequest.getSearchWord()),
                        jobPositionEq(jobSearchRequest.getJobPosition()),
                        jobTypeEq(jobSearchRequest.getJobType()),
                        hideClosedJobEq(jobSearchRequest.isHideClosedJob())
                )
                .fetchOne();
    }


    private BooleanExpression contentEq(String searchWord) {
        return searchWord != null ? jobPosting.title.contains(searchWord)
                .or(jobPosting.companyName.contains(searchWord)
                        .or(jobPosting.description.contains(searchWord))) : null;
    }

    private BooleanExpression jobPositionEq(JobPosition jobPosition) {
        return jobPosition != null ? jobPosting.jobPosition.eq(jobPosition) : null;
    }

    private BooleanExpression jobTypeEq(JobType jobType) {
        return jobType != null ? jobPosting.jobType.eq(jobType) : null;
    }

    private BooleanExpression hideClosedJobEq(boolean hideClosedJob) {
        return hideClosedJob ? jobPosting.deadLine.after(LocalDateTime.now()) : null;
    }
}
