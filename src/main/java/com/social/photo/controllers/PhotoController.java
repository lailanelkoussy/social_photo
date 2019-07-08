package com.social.photo.controllers;

import com.social.photo.dtos.PhotoDTO;
import com.social.photo.services.PhotoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping(value = "/photos")
public class PhotoController {

    @Autowired
    private PhotoService photoService;

    @GetMapping(value = "/hashtag/{hashtagName}")
    public List<PhotoDTO> getPhotosByHashtag(@PathVariable String hashtagName) {
        return photoService.getPhotosByHashtag(hashtagName);
    }

    @GetMapping(value = "/{id}")
    public PhotoDTO getPhoto(@PathVariable int id) {
        return photoService.getPhoto(id);
    }

    @PostMapping(value = "/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Object> addPhoto(@PathVariable int id, @RequestParam MultipartFile file, @RequestParam String photoName, @RequestParam String hashtag) throws HttpMediaTypeNotSupportedException {
        return new ResponseEntity<>(photoService.addPhoto(file, photoName, hashtag, id) ? HttpStatus.OK : HttpStatus.BAD_REQUEST);
    }

    @PostMapping(value = "/{id}/group/{groupId}")
    public ResponseEntity<Object> addPhoto(@PathVariable int id, @PathVariable int groupId, @RequestParam MultipartFile file, @RequestParam String photoName, @RequestParam String hashtag) throws HttpMediaTypeNotSupportedException {
        return new ResponseEntity<>(photoService.addPhotoToGroup(file, photoName, hashtag, id, groupId) ? HttpStatus.OK : HttpStatus.FORBIDDEN);
    }

    @DeleteMapping(value = "/{userId}/{photoId}")
    public ResponseEntity<Object> deletePhoto(@PathVariable int userId, @PathVariable int photoId) {
        return new ResponseEntity<>(photoService.deletePhoto(userId, photoId)?HttpStatus.ACCEPTED: HttpStatus.FORBIDDEN);
    }

    @PatchMapping(value = "/{id}/{hashtagName}")
    public void addHashtagToPhoto(@PathVariable int id, @PathVariable String hashtagName) {
        photoService.addHashtagToPhoto(id, hashtagName);
    }

}
