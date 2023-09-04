package com.example.matchup.matchupbackend.repository.feed;

import com.example.matchup.matchupbackend.entity.Feed;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FeedRepository extends JpaRepository<Feed, Long>{

//    @Query(value = "select f from Feed f where f.user.name like %:search%")
//    Slice<Feed> findFeedSliceBySearchRequest(@Param("search") String search,
//                                             @Param("searchType") String searchType,
//                                             Pageable pageable);
}
