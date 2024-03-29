package com.social.photo.repos;

import com.social.photo.entities.Photo;
import org.springframework.data.jpa.repository.JpaRepository;

import org.springframework.data.domain.Pageable;

import javax.transaction.Transactional;
import java.util.List;

public interface PhotoRepository extends JpaRepository<Photo,Integer> {

    List<Photo> findAllByHashtagId(int id);

    List<Photo> findAllByUserId(int userId);

    List<Photo> findAllByGroupId(int groupId);

    List<Photo> findByUserIdInOrderByTimeStampDesc(List<Integer> userIds, Pageable pageable);


    @Transactional
    void deleteAllByGroupId(int groupId);
    @Transactional
    void deleteAllByUserIdAndGroupId(int userId, int groupId);
}
