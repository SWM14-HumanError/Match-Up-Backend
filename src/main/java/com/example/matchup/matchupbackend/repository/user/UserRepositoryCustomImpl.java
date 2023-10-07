package com.example.matchup.matchupbackend.repository.user;

import com.example.matchup.matchupbackend.dto.request.user.UserSearchRequest;
import com.example.matchup.matchupbackend.entity.*;
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

@Repository
@RequiredArgsConstructor
public class UserRepositoryCustomImpl implements UserRepositoryCustom {
    private final JPAQueryFactory queryFactory;
    private QUser qUser = user;
    private QUserTag qUserTag = userTag;

    @Override
    public Slice<User> findUserListByUserRequest(UserSearchRequest userSearchRequest, Pageable pageable) {
        List<User> content = queryFactory
                .selectFrom(user)
                .where(
                        searchEq(userSearchRequest.getSearch()),
                        placeEq(userSearchRequest.getPlace()),
                        meetingTypeEq(userSearchRequest.getMeetingType()),
                        positionEq(userSearchRequest.getPosition()),
                        user.profileHider.eq(false)
                )
                .orderBy(orderByTo(userSearchRequest.getOrderBy()))
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

    @Override
    public List<UserTag> findUserTagList() {
        List<UserTag> content = queryFactory
                .selectFrom(userTag)
                .fetch();
        return content;
    }

    private BooleanExpression searchEq(String search) {
        return (search != null) ? user.name.contains(search) : null;
    }

    private BooleanExpression placeEq(String place) {
        return (place != null) ? user.address.contains(place) : null;
    }

    private BooleanExpression meetingTypeEq(MeetingType meetingType) {
        return (meetingType != null) ? user.meetingType.eq(meetingType) : null;
    }

    private BooleanExpression positionEq(String position) {
        return (position != null) ? user.position.eq(position) : null;
    }

    private OrderSpecifier<?> orderByTo(String orderBy) {
        Order order = Order.DESC;
        if ("reviewScore".equals(orderBy)) {
            return new OrderSpecifier<>(order, user.feedbackScore);
        } else if ("likes".equals(orderBy)) {
            return new OrderSpecifier<>(order, user.likes);
        } else return new OrderSpecifier<>(order, user.createTime);
    }
    //정렬 조건을 많이 하면 성능 이슈
}
/*
   public List<UserCardResponse> userAndUserTagToUserCard(List<Tuple> userAndUserTagList) {
        List<UserCardResponse> userCardResponseList = new ArrayList<>();

        userAndUserTagList.stream().forEach(tuple -> {
            User user= tuple.get(0, User.class);
            UserTag userTag= tuple.get(1, UserTag.class);
            userCardResponseList.add(UserCardResponse.builder()
                    .userID(user.getId())
                    .profileImageURL(user.getPictureUrl())
                    .memberLevel(user.getUserLevel())
                    .nickname(user.getName())
                    .position(makePosition(user.getPosition(), user.getPositionLevel()))
                    .score(user.getReviewScore())
                    .like(user.getLikes())
                    .TechStacks(techStackList(userAndUserTagList, user.getId()))
                    .build());
        });
        Map<Long, List<UserCardResponse>> mapList = userCardResponseList.stream().collect(Collectors.groupingBy(UserCardResponse::getUserID));
        return mapList.forEach( (key,value) -> vale);
    }

    public Map<Long,List<UserTag>> techStackList(List<Tuple> userAndUserTagList) {
        Map<Long,List<UserTag>> userTagMap = new HashMap<>();

        for (Tuple userAndTag : userAndUserTagList) {
            User user = userAndTag.get(0,User.class);
            UserTag userTag = userAndTag.get(1, UserTag.class);
            if (user.getId().equals(userTag.getId())) {
                userTagMap.put(user.getId(), userTagMap.get(user.getId()).add(userTag));
            }
        }
        return techStacks;
    }

    public static Position makePosition(String positionName, String positionLevel){
        return new Position(positionName, positionLevel);
    }
 */