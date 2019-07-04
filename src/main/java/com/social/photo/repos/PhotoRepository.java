package com.social.photo.repos;

import com.social.photo.entities.Photo;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PhotoRepository extends JpaRepository<Photo,Integer> {

    List<Photo> findAllByHashtagId(int id);
}
