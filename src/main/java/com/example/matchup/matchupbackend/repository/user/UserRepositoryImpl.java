package com.example.matchup.matchupbackend.repository.user;

import com.example.matchup.matchupbackend.dto.Position;
import com.example.matchup.matchupbackend.dto.user.TechStack;
import com.example.matchup.matchupbackend.dto.user.UserCardResponse;
import com.example.matchup.matchupbackend.dto.user.UserSearchRequest;
import com.example.matchup.matchupbackend.entity.QUser;
import com.example.matchup.matchupbackend.entity.QUserTag;
import com.querydsl.core.Tuple;
import com.querydsl.core.types.*;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

import static com.example.matchup.matchupbackend.entity.QUser.*;
import static com.example.matchup.matchupbackend.entity.QUserTag.*;

@Repository
@RequiredArgsConstructor
public class UserRepositoryImpl implements UserRepositoryCustom {
    private final JPAQueryFactory queryFactory;
    private QUser qUser = user;
    private QUserTag qUserTag = userTag;

    @Override
    public Slice<UserCardResponse> findUserCardListByUserRequest(UserSearchRequest userSearchRequest, Pageable pageable) {
        List<Tuple> content = queryFactory
                .select(user, userTag)
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
        List<UserCardResponse> value = userAndUserTagTo(content);
        return new SliceImpl<>(value, pageable, hasNext);
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

    public List<UserCardResponse> userAndUserTagTo(List<Tuple> userAndUserTagList) {
        List<UserCardResponse> userCardResponseList = new ArrayList<>();
        userAndUserTagList.stream().forEach(tuple -> {
            userCardResponseList.add(UserCardResponse.builder()
                    .userID(tuple.get(user.id))
                    .profileImageURL(tuple.get(user.pictureUrl))
                    .memberLevel(tuple.get(user.pictureUrl))
                    .nickname(tuple.get(user.name))
                    .position(makePosition(tuple.get(user.position), tuple.get(user.positionLevel)))
                    .score(tuple.get(user.reviewScore))
                    .like(tuple.get(user.likes))
                    .TechStacks(techStackList(userAndUserTagList, tuple.get(user.id)))
                    .build());
        });
        return userCardResponseList;
    }

    public List<TechStack> techStackList(List<Tuple> userAndUserTagList, Long userId) {
        List<TechStack> techStacks = new ArrayList<>();
        for (Tuple userAndTag : userAndUserTagList) {
            if (userAndTag.get(user.id) == userId) {
                techStacks.add(new TechStack(userAndTag.get(userTag.id), userAndTag.get(userTag.tagName)));
            }
        }
        return techStacks;
    }

    public static Position makePosition(String positionName, String positionLevel){
        return new Position(positionName, positionLevel);
    }
}


