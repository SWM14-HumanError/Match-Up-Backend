package com.example.matchup.matchupbackend.repository;

import com.example.matchup.matchupbackend.entity.EnterpriseVerify;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface EnterpriseVerifyRepository extends JpaRepository<EnterpriseVerify, Long> {
    @Query("select ev from EnterpriseVerify ev join fetch ev.user where ev.id = :enterpriseApplyId")
    Optional<EnterpriseVerify> findByIdJoinUser(@Param("enterpriseApplyId") Long enterpriseApplyId);
}
