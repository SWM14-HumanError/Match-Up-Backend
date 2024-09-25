package com.example.matchup.matchupbackend.repository;

import com.example.matchup.matchupbackend.entity.User;
import com.example.matchup.matchupbackend.entity.UserTag;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserTagRepository extends JpaRepository<UserTag, Long> {
    List<UserTag> findAllByUser(User user);

    void deleteAllByUser(User user);
}
