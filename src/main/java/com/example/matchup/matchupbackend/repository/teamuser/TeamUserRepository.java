package com.example.matchup.matchupbackend.repository.teamuser;

import com.example.matchup.matchupbackend.entity.TeamUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface TeamUserRepository extends JpaRepository<TeamUser, Long> {
    @Query(value = "SELECT * FROM team_user WHERE team_id=:teamId"
            , nativeQuery = true)
    List<TeamUser> findAllByTeamID(@Param("teamId") Long teamId);
    @Query("select teamuser from TeamUser teamuser where teamuser.user.id=:userId and teamuser.team.id=:teamId")
    List<TeamUser> isUserRecruitDuplicated(@Param("userId") Long userId, @Param("teamId") Long teamId);
}