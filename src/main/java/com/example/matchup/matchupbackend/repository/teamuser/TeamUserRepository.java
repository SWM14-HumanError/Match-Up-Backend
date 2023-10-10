package com.example.matchup.matchupbackend.repository.teamuser;

import com.example.matchup.matchupbackend.entity.TeamUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface TeamUserRepository extends JpaRepository<TeamUser, Long> {
    @Query("select teamuser from TeamUser teamuser " +
            "left join fetch teamuser.user " +
            "WHERE teamuser.team.id=:teamId")
    List<TeamUser> findTeamUserJoinUser(@Param("teamId") Long teamId);

    @Query("select teamuser from TeamUser teamuser LEFT JOIN FETCH teamuser.user WHERE teamuser.team.id=:teamId and teamuser.approve=true")
    List<TeamUser> findAcceptedTeamUserByTeamID(@Param("teamId") Long teamId);

    @Query("select teamuser from TeamUser teamuser where teamuser.user.id=:userId and teamuser.team.id=:teamId")
    List<TeamUser> isUserRecruitDuplicated(@Param("userId") Long userId, @Param("teamId") Long teamId);

    @Query("select teamuser from TeamUser teamuser " +
            "join fetch teamuser.team " +
            "join fetch teamuser.user " +
            "where teamuser.team.id=:teamId and teamuser.user.id=:userId")
    Optional<TeamUser> findTeamUserJoinTeamAndUser(@Param("teamId") Long teamId, @Param("userId") Long userId);

    @Modifying(clearAutomatically = true)
    @Query("UPDATE TeamUser teamuser SET teamuser.count = teamuser.count + 1 WHERE teamuser.team.id =:teamId AND teamuser.role =:role")
    void updateTeamUserStatusByAcceptUser(@Param("teamId") Long teamId, @Param("role") String role);

    @Modifying(clearAutomatically = true)
    @Query("UPDATE TeamUser teamuser SET teamuser.count = teamuser.count - 1 WHERE teamuser.team.id =:teamId AND teamuser.role =:role")
    void updateTeamUserStatusByKickedUser(@Param("teamId") Long teamId, @Param("role") String role);

    @Modifying(clearAutomatically = true)
    @Query("DELETE from TeamUser teamuser where teamuser.team.id=:teamId AND teamuser.user.id=:userId")
    void deleteTeamUserByTeamIdAndUserId(@Param("teamId") Long teamId, @Param("userId") Long userId);

    @Query("select teamuser from TeamUser teamuser where teamuser.team.id=:teamId AND teamuser.user.id=:userId1 OR teamuser.user.id=:userId2")
    List<TeamUser> findTwoUserByTeamIdAndUserIds(@Param("teamId") Long teamId, @Param("userId1") Long userId1, @Param("userId2")Long userId2);
    @Query("select teamuser from TeamUser teamuser " +
            "left join fetch teamuser.user " +
            "WHERE teamuser.team.id=:teamId and teamuser.approve=true")
    List<TeamUser> findAcceptedTeamUserJoinUser(@Param("teamId") Long teamId);

    @Query("select teamuser from TeamUser teamuser " +
            "left join fetch teamuser.user " +
            "left join fetch teamuser.teamRecruit " +
            "WHERE teamuser.team.id=:teamId")
    List<TeamUser> findTeamUserJoinUserAndRecruit(@Param("teamId") Long teamId);

    boolean existsByTeamIdAndUserIdAndApproveTrue(Long teamId, Long userId);

}