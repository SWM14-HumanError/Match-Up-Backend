package com.example.matchup.matchupbackend.repository.user;

import com.example.matchup.matchupbackend.dto.Position;
import com.example.matchup.matchupbackend.dto.user.TechStack;
import com.example.matchup.matchupbackend.dto.user.UserCardResponse;
import com.example.matchup.matchupbackend.dto.user.UserSearchRequest;
import com.example.matchup.matchupbackend.entity.QUser;
import com.example.matchup.matchupbackend.entity.QUserTag;
import com.querydsl.core.types.*;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;
import org.springframework.stereotype.Repository;

import java.util.List;

import static com.example.matchup.matchupbackend.entity.QUser.*;
import static com.example.matchup.matchupbackend.entity.QUserTag.*;
import static com.querydsl.core.group.GroupBy.groupBy;
import static com.querydsl.core.types.Projections.list;

@Repository
@RequiredArgsConstructor
public class UserRepositoryImpl implements UserRepositoryCustom {
    private final JPAQueryFactory queryFactory;
    private QUser qUser = user;
    private QUserTag qUserTag = userTag;

    @Override
    public Slice<UserCardResponse> findUserCardListByUserRequest(UserSearchRequest userSearchRequest, Pageable pageable) {
        List<UserCardResponse> content = queryFactory
                .select(Projections.bean(UserCardResponse.class,
                        user.id.as("userID"),
                        user.pictureUrl.as("profileImageURL"),
                        user.userLevel.as("memberLevel"),
                        user.name.as("nickname"),
                        Projections.bean(Position.class, user.position.as("positionName"),
                                user.positionLevel.as("positionLevel")).as("position"),
                        user.reviewScore.as("score"),
                        user.likes.as("like"),
                        list(Projections.bean(TechStack.class,
                                userTag.id.as("tagID"),
                                userTag.tagName.as("tagName")).as("TechStacks"))
                ))
                .from(user)
                .leftJoin(userTag).fetchJoin().on(user.id.eq(userTag.user.id))
                .where(
                        searchEq(userSearchRequest.getSearch()),
                        placeEq(userSearchRequest.getPlace()),
                        stackEq(userSearchRequest.getTechStack()),
                        positionEq(userSearchRequest.getPosition())
                )
                .orderBy(orderByEq(userSearchRequest.getOrderBy()))
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


    //order by에도 BooleanExpression 쓰면 될듯
    private BooleanExpression searchEq(String search) {
        return (search != null) ? user.name.contains(search) : null;
    }

    private BooleanExpression placeEq(String place) {
        return (place != null) ? user.address.eq(place) : null;
    }

    private BooleanExpression stackEq(String stack) {
        if (stack != null) { //태그 안에 카테고리가 있는지 찾아야함
            return user.userTagList.any().tagName.eq(stack); // any하면 리스트를 통째로 들고 온다
        } //error 확인!
        else return null;
    }
    private BooleanExpression positionEq(String position) {
        return (position != null) ? user.position.eq(position) : null;
    }

    private OrderSpecifier<?> orderByEq(Long orderBy) {
        if (orderBy == 0) {
            return new OrderSpecifier<>(Order.DESC, user.reviewScore);
        } else if (orderBy == 1) {
            return new OrderSpecifier<>(Order.DESC, user.likes);
        } else return new OrderSpecifier<>(Order.DESC, user.createTime);
    }
}


