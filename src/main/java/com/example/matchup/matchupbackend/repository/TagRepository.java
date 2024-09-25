package com.example.matchup.matchupbackend.repository;

import com.example.matchup.matchupbackend.entity.Tag;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TagRepository extends JpaRepository<Tag, Long> {
    Tag findByName(String tagName);

    boolean existsByName(String tagName);

}
