package com.social.photo.services;

import com.social.photo.dtos.GroupDTO;
import com.social.photo.dtos.PhotoDTO;
import com.social.photo.entities.Hashtag;
import com.social.photo.entities.Photo;
import com.social.photo.proxies.GroupServiceProxy;
import com.social.photo.repos.PhotoRepository;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;


@Service
@Slf4j
public class PhotoService {

    @Autowired
    PhotoRepository photoRepository;

    @Autowired
    HashtagService hashtagService;

    @Autowired
    GroupServiceProxy groupServiceProxy;

    ModelMapper modelMapper = new ModelMapper();

    private final String photoFilePath = "/Users/macbookpro/Documents/University/Internship/Social Media/photo/src/main/resources/images";

    public boolean addPhoto(MultipartFile photoFile, String photoName, String hashtag, int userId) throws HttpMediaTypeNotSupportedException {

        Photo photo = new Photo();
        photo.setName(photoName);

        if (!moveAndRenamePhoto(photoFile, photo.getName(), photo.getSystemName())) {
            throw (new HttpMediaTypeNotSupportedException("Unable to save file to internal directory"));
        }
        photo = setPhotoInfo(photo, photoFilePath, photo.getSystemName(), userId);
        photo.setHashtag(hashtagService.addHashtag(hashtag));
        log.info("Saving image...");
        photoRepository.save(photo);
        return true;
    }

    public boolean addPhotoToGroup(MultipartFile photoFile, String photoName, String hashtag, int userId, int groupId) throws HttpMediaTypeNotSupportedException {
        Photo photo = new Photo();
        photo.setName(photoName);

        if (userIsInGroup(userId, groupId)) {
            if (!moveAndRenamePhoto(photoFile, photo.getName(), photo.getSystemName()))
                throw (new HttpMediaTypeNotSupportedException("Unable to save file to internal directory"));
            photo = setPhotoInfo(photo, photoFilePath, photo.getName(), userId);
            photo.setGroupId(groupId);
            photo.setHashtag(hashtagService.addHashtag(hashtag));
            log.info("Saving image...");
            photoRepository.save(photo);
            return true;
        } else return false;
    }

    private boolean moveAndRenamePhoto(MultipartFile photoFile, String oldName, String newName) {

        try {
            Files.copy(photoFile.getInputStream(), Paths.get(photoFilePath + "/" + newName),
                    StandardCopyOption.REPLACE_EXISTING);

        } catch (java.io.IOException e) {
            log.error("Unable to save file to internal directory.");
            return false;
        }

        return true;
    }

    public void addHashtagToPhoto(int photo_id, String hashtagName) {

        Photo photo = photoRepository.findById(photo_id).get();

        if (!hashtagService.hashtagExists(hashtagName)) {
            hashtagService.addHashtag(hashtagName);
        }
        Hashtag hashtag = hashtagService.getHashtag(hashtagName);
        List<Photo> photos = hashtag.getPhotos();
        photo.setHashtag(hashtag);
        photos.add(photo);
        photoRepository.save(photo);
        hashtag.setPhotos(photos);
        hashtagService.updateHashtag(hashtag);


    }

    public void updatePhotos(List<Photo> photos) {
        photoRepository.saveAll(photos);
    }

    public boolean deletePhoto(int photo_id, int userId) throws IllegalAccessException {
        Photo photo = (photoRepository.findById(photo_id)).get();
        Hashtag hashtag = photo.getHashtag();

        if (isOwnerOrGroupCreator(photo, userId)) {
            List<Photo> photos = hashtag.getPhotos();
            photos.remove(photo);
            hashtag.setPhotos(photos);
            photoRepository.deleteById(photo_id);
            hashtagService.updateHashtag(hashtag);
            return true;
        } else throw (new IllegalAccessException("Not authorized to perform this action"));
    }

    public List<PhotoDTO> getPhotosByHashtag(String hashtagName) {
        List<Photo> photos = photoRepository.findAllByHashtagId(hashtagService.getHashtag(hashtagName).getId());
        List<PhotoDTO> photoDTOS = new ArrayList<>();


        for (Photo photo : photos) {
            PhotoDTO photoDTO = new PhotoDTO();
            modelMapper.map(photo, photoDTO);
            photoDTOS.add(photoDTO);
        }

        return photoDTOS;

    }

    public PhotoDTO getPhoto(int id) {
        Photo photo = (photoRepository.findById(id)).get();
        PhotoDTO photoDTO = new PhotoDTO();

        modelMapper.map(photo, photoDTO);
        return photoDTO;

    }

    public List<PhotoDTO> getUsersPhotos(int userId) {
        List<PhotoDTO> photoDTOS = new ArrayList<>();
        List<Photo> photos = photoRepository.findAllByUserId(userId);

        for (Photo photo : photos) {
            PhotoDTO photoDTO = new PhotoDTO();
            modelMapper.map(photo, photoDTO);
            photoDTOS.add(photoDTO);
        }
        return photoDTOS;
    }

    public Photo setPhotoInfo(Photo photo, String photoFilePath, String name, int userId) {
        photo.setPhotoPath(photoFilePath);
        photo.setName(photo.getSystemName());
        photo.setUserId(userId);
        return photo;
    }

    private boolean isOwnerOrGroupCreator(Photo photo, int userId) {

        if (photo.getGroupId() != 0) {
            GroupDTO group = groupServiceProxy.getGroup(photo.getGroupId());
            return (photo.getUserId() == userId || group.getCreatorId() == userId);
        } else return (photo.getUserId() == userId);

    }

    private boolean userIsInGroup(int userId, int groupId) {

        List<Integer> groupIds = groupServiceProxy.getUserGroupIds(userId);

        for (int id : groupIds) {
            if (id == groupId)
                return true;
        }
        return false;


    }

}
