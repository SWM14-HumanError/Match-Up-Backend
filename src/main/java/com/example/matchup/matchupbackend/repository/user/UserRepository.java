package com.example.matchup.matchupbackend.repository.user;

import com.example.matchup.matchupbackend.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long>, UserRepositoryCustom {

    Optional<User> findByEmail(String email);
}
