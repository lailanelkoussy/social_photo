package com.social.photo.services;

import com.social.photo.dtos.GroupDTO;
import com.social.photo.dtos.PhotoDTO;
import com.social.photo.dtos.PhotoPatchDTO;
import com.social.photo.dtos.UserDTO;
import com.social.photo.entities.HashTag;
import com.social.photo.entities.Photo;
import com.social.photo.proxies.GroupServiceProxy;
import com.social.photo.proxies.UserServiceProxy;
import com.social.photo.repos.PhotoRepository;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FilenameUtils;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.multipart.MultipartFile;

import javax.persistence.EntityNotFoundException;
import java.io.File;
import java.io.InvalidClassException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


@Service
@Slf4j
@Getter
@Setter
@ConfigurationProperties(prefix = "photo")
public class PhotoService {

    @Autowired
    PhotoRepository photoRepository;

    @Autowired
    HashTagService hashtagService;

    @Autowired
    GroupServiceProxy groupServiceProxy;

    @Autowired
    UserServiceProxy userServiceProxy;

    ModelMapper modelMapper = new ModelMapper();

    private String filePath;

    public void addPhoto(MultipartFile photoFile, String photoName, String hashtag, int userId) throws HttpMediaTypeNotSupportedException {

        Photo photo = new Photo();
        if (!photoName.equals(""))
            photo.setName(photoName);

        if (!moveAndRenamePhoto(photoFile, photo.getSystemName(FilenameUtils.getExtension(photoFile.getOriginalFilename())))) {
            throw (new HttpMediaTypeNotSupportedException("Unable to save file to internal directory"));
        }
        photo = setPhotoInfo(photo, filePath, userId);
        if (!hashtag.equals(""))
            photo.setHashtag(hashtagService.addHashTag(hashtag));
        log.info("Saving image...");
        photoRepository.save(photo);
    }

    public void addPhotoToGroup(MultipartFile photoFile, String photoName, String hashtag, int userId, int groupId) throws HttpMediaTypeNotSupportedException, IllegalAccessException {
        try {
            groupServiceProxy.getGroup(groupId);
        } catch (Exception e) {
            throw new EntityNotFoundException("Group not found");
        }

        Photo photo = new Photo();
        if (!photoName.equals(""))
            photo.setName(photoName);

        try {
            GroupDTO group = groupServiceProxy.getGroup(groupId);
        } catch (Exception e) {
            throw (new EntityNotFoundException("Could not find group"));
        }

        if (userIsInGroup(userId, groupId)) {
            if (!moveAndRenamePhoto(photoFile, photo.getSystemName(FilenameUtils.getExtension(photoFile.getOriginalFilename()))))
                throw (new HttpMediaTypeNotSupportedException("Unable to save file to internal directory"));

            photo = setPhotoInfo(photo, filePath, userId);
            photo.setGroupId(groupId);
            if (!hashtag.equals(""))
                photo.setHashtag(hashtagService.addHashTag(hashtag));
            log.info("Saving image...");
            photoRepository.save(photo);
        } else throw new IllegalAccessException("Not authorized to perform this action");
    }

    private boolean moveAndRenamePhoto(MultipartFile photoFile, String newName) {

        try {
            Files.copy(photoFile.getInputStream(), Paths.get(filePath + "/" + newName),
                    StandardCopyOption.REPLACE_EXISTING);
            log.info("Saving file to internal directory...");

        } catch (java.io.IOException e) {
            log.error("Unable to save file to internal directory.");
            return false;
        }

        return true;
    }

    public void addHashTagToPhoto(int photoId, PhotoPatchDTO newPhoto) throws InvalidClassException {

        Optional<Photo> photoOptional = photoRepository.findById(photoId);

        if (photoOptional.isPresent()) {
            Photo photo = photoOptional.get();

            if (!newPhoto.getHashtagName().equals(photo.getHashtag().getName()) && !newPhoto.getHashtagName().equals(null)) {
                if (!hashtagService.hashTagExists(newPhoto.getHashtagName())) {
                    hashtagService.addHashTag(newPhoto.getHashtagName());
                }
                HashTag oldHashtag = hashtagService.getHashTagByName(photo.getHashtag().getName());
                HashTag newHashtag = hashtagService.getHashTagByName(newPhoto.getHashtagName());

                photo.setHashtag(newHashtag);
                log.info("Adding hashtag to photo...");
                photoRepository.save(photo);
                hashtagService.updateHashTag(oldHashtag);
                log.info("Updating hashtags in database...");

            } else {
                throw (new InvalidClassException("Invalid hashtag object"));
            }
        } else {
            throw (new EntityNotFoundException("Could not retrieve photo object"));
        }
    }

    public void deletePhoto(int userId, int photoId) throws IllegalAccessException {
        Optional<Photo> photoOptional = photoRepository.findById(photoId);
        if (photoOptional.isPresent()) {
            Photo photo = (photoOptional).get();
            HashTag hashtag = photo.getHashtag();
            log.info("Checking for user credentials...");
            if (isOwnerOrGroupCreator(photo, userId)) {
                log.info("User is allowed to delete photo...");
                List<Photo> photos = hashtag.getPhotos();
                photos.remove(photo);
                hashtag.setPhotos(photos);
                photoRepository.deleteById(photoId);
                hashtagService.updateHashTag(hashtag);
                File file = new File(photo.getPhotoPath() + photo.getName());
                if (file.delete())
                    log.info("Deleted photo successfully");

            } else {
                throw (new IllegalAccessException("Not authorized to perform this action"));
            }
        } else throw (new EntityNotFoundException("Could not find photo"));
    }

    public List<PhotoDTO> getPhotosByHashtag(int hashtagId) {
        log.info("Searching for photos");
        try {
            hashtagService.getHashTagDTO(hashtagId);
        } catch (Exception e) {
            throw new EntityNotFoundException("Hashtag not found");
        }
        List<Photo> photos = photoRepository.findAllByHashtagId(hashtagId);
        return toPhotoDTOs(photos);
    }

    public PhotoDTO getPhoto(int id) {
        Optional<Photo> photoOptional = photoRepository.findById(id);
        log.info("Looking for photo");

        if (photoOptional.isPresent()) {
            log.info("Photo found");
            Photo photo = (photoOptional).get();
            PhotoDTO photoDTO = new PhotoDTO();
            modelMapper.map(photo, photoDTO);
            return photoDTO;
        } else throw (new EntityNotFoundException("Could not find photo"));

    }

    public List<PhotoDTO> getGroupsPhotos(int userId, int groupId) throws IllegalAccessException {

        try {
            groupServiceProxy.getGroup(groupId);
        } catch (Exception e) {
            throw (new EntityNotFoundException("Could not retrieve group"));
        }
        if (!userIsInGroup(userId, groupId)) {
            log.error("Not authorized to perform this action");
            throw (new IllegalAccessException("Not authorized to perform this action"));
        }

        log.info("Retrieving photos... ");
        return getGroupsPhotos(groupId);
    }

    List<PhotoDTO> getGroupsPhotos(int groupId) {

        return toPhotoDTOs(photoRepository.findAllByGroupId(groupId));
    }

    public List<PhotoDTO> getUsersPhotos(int userId) {
        try {
            userServiceProxy.getUser(userId);
        } catch (Exception e) {
            throw new EntityNotFoundException("Could not find user");
        }
        List<PhotoDTO> photoDTOS = new ArrayList<>();
        log.info("Retrieving photos...");
        List<Photo> photos = photoRepository.findAllByUserId(userId);

        for (Photo photo : photos) {
            PhotoDTO photoDTO = new PhotoDTO();
            modelMapper.map(photo, photoDTO);
            photoDTOS.add(photoDTO);
        }
        return photoDTOS;
    }

    private Photo setPhotoInfo(Photo photo, String photoFilePath, int userId) {
        log.info("Setting photo information");
        photo.setPhotoPath(photoFilePath);
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

    public List<PhotoDTO> getHomePage(int userId, int pageNumber, int pageSize) {
        List<UserDTO> userAndFollowing = new ArrayList<>();
        try {
            userAndFollowing.add(userServiceProxy.getUser(userId));
            userAndFollowing.addAll(userServiceProxy.getUserFollowing(userId));
        } catch (Exception e) {
            throw new EntityNotFoundException("Could not find user");
        }

        List<Integer> userIds = new ArrayList<>();
        for (UserDTO user : userAndFollowing) {
            userIds.add(user.getUserId());
        }
        Pageable pageable = PageRequest.of(pageNumber, pageSize);
        List<Photo> photos = photoRepository.findByUserIdInOrderByTimeStampDesc(userIds, pageable);

        for (Photo photo : photos) {
            if (photo.getGroupId() != 0 && !userIsInGroup(userId, photo.getGroupId()))
                photos.remove(photo);
        }

        List<PhotoDTO> photoDTOS = toPhotoDTOs(photos);
//        for (GroupDTO group : groupServiceProxy.getUserGroups(userId)) {
//            photoDTOS.addAll(getGroupsPhotos(group.getId()));
//        }

        return photoDTOS;
    }
}
