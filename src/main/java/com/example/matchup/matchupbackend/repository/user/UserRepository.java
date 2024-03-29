package com.example.matchup.matchupbackend.repository.user;

import com.example.matchup.matchupbackend.entity.User;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long>, UserRepositoryCustom {
    @Query("SELECT user from User user where user.email=:userEmail")
    Optional<User> findByEmail(@Param("userEmail") String email);
    @Query("SELECT user from User user where user.refreshToken=:token")
    Optional<User> findByRefreshToken(@Param("token") String refreshToken);
    @Query("SELECT user from User user")
    List<User> findAllUser();
    @Query("SELECT user from User user where user.id=:userID")
    Optional<User> findUserById(@Param("userID") Long userID);

    Optional<User> findUserByNicknameAndIdNot(String nickname, Long userId);

    Optional<User> findUserByNickname(String nickname);

    Slice<User> findAllByIdIn(List<Long> userId, Pageable pageable);

    @Query("SELECT user.nickname from User user where user.id=:leaderID")
    String findNicknameById(@Param("leaderID") Long leaderID);
}
