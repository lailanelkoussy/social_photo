package com.social.photo.repos;

import com.social.photo.entities.Hashtag;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface HashtagRepository extends JpaRepository<Hashtag, Integer> {

    long countByName(String name);

    void deleteByName(String name);

    Optional<Hashtag> getByName(String name);
}
