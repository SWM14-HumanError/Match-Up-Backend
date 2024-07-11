package com.example.matchup.matchupbackend.repository.userposition;

import com.example.matchup.matchupbackend.entity.User;
import com.example.matchup.matchupbackend.entity.UserPosition;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserPositionRepository extends JpaRepository<UserPosition, Long> {
    List<UserPosition> findByUserId(Long userId);

    void deleteAllByUser(User user);

    List<UserPosition> findAllByUser(User user);

    @Query("select up from UserPosition up join fetch up.user where up.user in :userList")
    List<UserPosition> findAllJoinUserBy(@Param(value = "userList") List<User> userList);

    @Query("select up from UserPosition up join fetch up.user where up.user in :userList")
    List<UserPosition> findAllJoinUserByUserList(@Param("userList") List<User> userList);
}
