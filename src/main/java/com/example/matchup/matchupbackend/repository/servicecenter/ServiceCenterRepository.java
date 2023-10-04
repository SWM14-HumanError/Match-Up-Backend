package com.example.matchup.matchupbackend.repository.servicecenter;

import com.example.matchup.matchupbackend.entity.ServiceCenter;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ServiceCenterRepository extends JpaRepository<ServiceCenter, Long>{
}
