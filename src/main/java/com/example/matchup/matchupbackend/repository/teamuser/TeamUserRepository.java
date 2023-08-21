package com.example.matchup.matchupbackend.repository.teamuser;

import com.example.matchup.matchupbackend.entity.TeamUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface TeamUserRepository extends JpaRepository<TeamUser, Long> {
    @Query(value = "SELECT * FROM team_user WHERE team_id=:teamId"
            , nativeQuery = true)
    List<TeamUser> findAllByTeamID(@Param("teamId") Long teamId);

    @Query("select teamuser from TeamUser teamuser where teamuser.user.id=:userId and teamuser.team.id=:teamId")
    List<TeamUser> isUserRecruitDuplicated(@Param("userId") Long userId, @Param("teamId") Long teamId);

    @Query("select teamuser from TeamUser teamuser where teamuser.team.id=:teamId and teamuser.user.id=:userId")
    TeamUser findTeamUserByTeamIdAndUserId(@Param("teamId") Long teamId, @Param("userId") Long userId);

    @Modifying
    @Query("UPDATE TeamUser teamuser SET teamuser.count = teamuser.count + 1 WHERE teamuser.team.id =:teamId AND teamuser.role =:role")
    void updateTeamUserStatusByAcceptUser(@Param("teamId") Long teamId, @Param("role") String role);
}