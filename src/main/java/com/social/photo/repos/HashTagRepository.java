package com.social.photo.repos;

import com.social.photo.entities.HashTag;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface HashTagRepository extends JpaRepository<HashTag, Integer> {

    long countByName(String name);

    Optional<HashTag> getByName(String name);
}
