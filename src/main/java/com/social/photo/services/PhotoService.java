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
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.multipart.MultipartFile;

import javax.persistence.EntityNotFoundException;
import java.io.InvalidClassException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


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
    //todo: you shouldn't use an explicit one like this that points to your project, it should be read from the yml settings file
    private final String photoFilePath ="/Users/macbookpro/Documents/University/Internship/Social Media/photo/src/main/resources/images";

    public boolean addPhoto(MultipartFile photoFile, String photoName, String hashtag, int userId) throws HttpMediaTypeNotSupportedException {

        Photo photo = new Photo();
        if (!photoName.equals(""))
            photo.setName(photoName);

        if (!moveAndRenamePhoto(photoFile, photo.getSystemName())) {
            throw (new HttpMediaTypeNotSupportedException("Unable to save file to internal directory"));
        }
        photo = setPhotoInfo(photo, photoFilePath, photo.getSystemName(), userId);
        if (!hashtag.equals(""))
            photo.setHashtag(hashtagService.addHashtag(hashtag));
        log.info("Saving image...");
        photoRepository.save(photo);
        return true;
    }

    public boolean addPhotoToGroup(MultipartFile photoFile, String photoName, String hashtag, int userId, int groupId) throws HttpMediaTypeNotSupportedException {
        Photo photo = new Photo();
        if (!photoName.equals(""))
            photo.setName(photoName);

        if (userIsInGroup(userId, groupId)) {
            if (!moveAndRenamePhoto(photoFile, photo.getSystemName()))
                throw (new HttpMediaTypeNotSupportedException("Unable to save file to internal directory"));
            photo = setPhotoInfo(photo, photoFilePath, photo.getName(), userId);
            photo.setGroupId(groupId);
            if (!hashtag.equals(""))
                photo.setHashtag(hashtagService.addHashtag(hashtag));
            log.info("Saving image...");
            photoRepository.save(photo);
            return true;
        } else return false;
    }

    private boolean moveAndRenamePhoto(MultipartFile photoFile, String newName) {

        try {
            Files.copy(photoFile.getInputStream(), Paths.get(photoFilePath + "/" + newName),
                    StandardCopyOption.REPLACE_EXISTING);

        } catch (java.io.IOException e) {
            log.error("Unable to save file to internal directory.");
            return false;
        }

        return true;
    }


        //todo well, this is not a good idea, you are doing too much operations
        //todo you can just set the hash tag to a photo and save it, instead of loading all the photos and updating the list

    public void addHashtagToPhoto(int photoId, PhotoDTO newPhoto) throws InvalidClassException {

        Optional<Photo> photoOptional = photoRepository.findById(photoId);

        if (photoOptional.isPresent()) {
            Photo photo = photoOptional.get();

            if (!newPhoto.getHashtagName().equals(photo.getHashtag().getName()) && !newPhoto.getHashtagName().equals(null)) {
                if (!hashtagService.hashtagExists(newPhoto.getHashtagName())) {
                    hashtagService.addHashtag(newPhoto.getHashtagName());
                }
                Hashtag oldHashtag = hashtagService.getHashtagByName(photo.getHashtag().getName());
                Hashtag newHashtag = hashtagService.getHashtagByName(newPhoto.getHashtagName());

                List<Photo> photos = newHashtag.getPhotos();
                photo.setHashtag(newHashtag);
                photos.add(photo);
                photoRepository.save(photo);
                newHashtag.setPhotos(photos);
                hashtagService.updateHashtag(newHashtag);
                hashtagService.updateHashtag(oldHashtag);

            } else {
                throw (new InvalidClassException("Invalid hashtag object"));
            }
        }else{
            throw (new EntityNotFoundException("Could not retrieve photo object"));
        }
    }

    public void updatePhotos(List<Photo> photos) {
        photoRepository.saveAll(photos);
    }

    public boolean deletePhoto(int photo_id, int userId) throws IllegalAccessException {//todo should be photoId
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

    public List<PhotoDTO> getPhotosByHashtag(int hashtagId) {
        List<Photo> photos = photoRepository.findAllByHashtagId(hashtagId);
        return toPhotoDTOs(photos);
    }

    public PhotoDTO getPhoto(int id) {
        Photo photo = (photoRepository.findById(id)).get();
        PhotoDTO photoDTO = new PhotoDTO();

        modelMapper.map(photo, photoDTO);
        return photoDTO;
    }

    public List<PhotoDTO> getGroupsPhotos(int userId, int groupId) throws IllegalAccessException {
        if (!userIsInGroup(userId, groupId))
            throw (new IllegalAccessException("Not authorized to perform this action"));

        return getGroupsPhotos(groupId);

    }

    public List<PhotoDTO> getGroupsPhotos(int groupId){
        return toPhotoDTOs(photoRepository.findAllByGroupId(groupId));
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

        List<GroupDTO> groups = groupServiceProxy.getUserGroups(userId);
        for (GroupDTO group : groups) {
            if (group.getId() == groupId)
                return true;
        }
        return false;


    }

    private List<PhotoDTO> toPhotoDTOs(List<Photo> photos) {
        List<PhotoDTO> photoDTOS = new ArrayList<>();

        for (Photo photo : photos) {
            PhotoDTO photoDTO = new PhotoDTO();
            modelMapper.map(photo, photoDTO);
            photoDTOS.add(photoDTO);
        }
        return photoDTOS;

    }

}
