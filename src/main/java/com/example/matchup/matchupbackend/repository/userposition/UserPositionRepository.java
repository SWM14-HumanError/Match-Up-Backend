package com.example.matchup.matchupbackend.repository.userposition;

import com.example.matchup.matchupbackend.entity.User;
import com.example.matchup.matchupbackend.entity.UserPosition;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserPositionRepository extends JpaRepository<UserPosition, Long> {
    List<UserPosition> findByUserId(Long userId);

    void deleteAllByUser(User user);
}
