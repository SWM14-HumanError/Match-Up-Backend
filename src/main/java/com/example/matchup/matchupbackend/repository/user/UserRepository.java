package com.example.matchup.matchupbackend.repository.user;

import com.example.matchup.matchupbackend.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long>, UserRepositoryCustom {
    @Query("SELECT user from User user where user.email=:userEmail")
    Optional<User> findByEmail(@Param("userEmail") String email);
    @Query("SELECT user from User user where user.refreshToken=:token")
    Optional<User> findByRefreshToken(@Param("token") String token);
    @Query("SELECT user from User user")
    List<User> findAllUser();
    @Query("SELECT user from User user where user.id=:userID")
    Optional<User> findUserById(@Param("userID") Long userID);
}
