package com.social.photo.services;

import com.social.photo.dtos.PhotoDTO;
import com.social.photo.entities.Hashtag;
import com.social.photo.entities.Photo;
import com.social.photo.repos.PhotoRepository;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Files;
import java.nio.file.Path;
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

    ModelMapper modelMapper = new ModelMapper();

    private final String photoFilePath ="/Users/macbookpro/Documents/University/Internship/Social Media/photo/src/main/resources/images";

    public boolean addPhoto(MultipartFile photoFile, String photoName, String hashtag) {

        Photo photo = new Photo();
        photo.setName(photoName);

        if (!moveAndRenamePhoto(photoFile, photo.getName(), photo.getSystemName()))
            return false;
        photo.setPhotoPath(photoFilePath);
        photo.setName(photo.getSystemName());
        log.info("Saving image...");
        photo.setHashtag(hashtagService.addHashtag(hashtag));
        photoRepository.save(photo);
        return true;

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

    public void deletePhoto(int photo_id) {
        Photo photo = (photoRepository.findById(photo_id)).get();
        Hashtag hashtag = photo.getHashtag();

        List<Photo> photos = hashtag.getPhotos();
        photos.remove(photo);
        hashtag.setPhotos(photos);
        photoRepository.deleteById(photo_id);
        hashtagService.updateHashtag(hashtag);

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


}
