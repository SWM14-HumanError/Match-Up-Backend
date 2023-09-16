package com.example.matchup.matchupbackend.repository.teamuser;

import com.example.matchup.matchupbackend.entity.TeamUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface TeamUserRepository extends JpaRepository<TeamUser, Long> {
    @Query("select teamuser from TeamUser teamuser JOIN FETCH User user ON user.id=teamuser.user.id WHERE teamuser.team.id=:teamId")
    List<TeamUser> findAllTeamUserByTeamID(@Param("teamId") Long teamId); //todo N+1 문제 해결

    @Query("select teamuser from TeamUser teamuser JOIN FETCH User user ON user.id=teamuser.user.id WHERE teamuser.team.id=:teamId and teamuser.approve=true")
    List<TeamUser> findAcceptedTeamUserByTeamID(@Param("teamId") Long teamId);

    @Query("select teamuser from TeamUser teamuser where teamuser.user.id=:userId and teamuser.team.id=:teamId")
    List<TeamUser> isUserRecruitDuplicated(@Param("userId") Long userId, @Param("teamId") Long teamId);

    @Query("select teamuser from TeamUser teamuser where teamuser.team.id=:teamId and teamuser.user.id=:userId")
    Optional<TeamUser> findTeamUserByTeamIdAndUserId(@Param("teamId") Long teamId, @Param("userId") Long userId);

    @Modifying(clearAutomatically = true)
    @Query("UPDATE TeamUser teamuser SET teamuser.count = teamuser.count + 1 WHERE teamuser.team.id =:teamId AND teamuser.role =:role")
    void updateTeamUserStatusByAcceptUser(@Param("teamId") Long teamId, @Param("role") String role);

    @Modifying(clearAutomatically = true)
    @Query("UPDATE TeamUser teamuser SET teamuser.count = teamuser.count - 1 WHERE teamuser.team.id =:teamId AND teamuser.role =:role")
    void updateTeamUserStatusByKickedUser(@Param("teamId") Long teamId, @Param("role") String role);

    @Modifying(clearAutomatically = true)
    @Query("DELETE from TeamUser teamuser where teamuser.team.id=:teamId AND teamuser.user.id=:userId")
    void deleteTeamUserByTeamIdAndUserId(@Param("teamId") Long teamId, @Param("userId") Long userId);
}