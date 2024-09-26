package com.example.matchup.matchupbackend.repository;

import com.example.matchup.matchupbackend.entity.ServiceCenter;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface ServiceCenterRepository extends JpaRepository<ServiceCenter, Long>{
    @Query("select sc from ServiceCenter sc join fetch sc.user order by sc.createTime desc")
    Slice<ServiceCenter> joinUserOrderByCreatedTime(Pageable pageable);
}
