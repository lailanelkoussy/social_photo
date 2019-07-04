package com.social.photo.controllers;

import com.social.photo.dtos.PhotoDTO;
import com.social.photo.services.PhotoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping(value = "/photos")
public class PhotoController {

    @Autowired
    private PhotoService photoService;

    @GetMapping(value = "/hashtag/{hashtagName}")
    public List<PhotoDTO> getPhotosByHashtag(@PathVariable String hashtagName){
        return photoService.getPhotosByHashtag(hashtagName);
    }

    @GetMapping(value = "/{id}")
    public PhotoDTO getPhoto(@PathVariable int id){
        return photoService.getPhoto(id);
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Object> addPhoto(@RequestParam MultipartFile file,@RequestParam String photoName, @RequestParam String hashtag) {
        return new ResponseEntity<>(photoService.addPhoto(file, photoName, hashtag) ? HttpStatus.OK : HttpStatus.BAD_REQUEST);
    }

    @DeleteMapping(value = "/{id}")
    public void deletePhoto(@PathVariable int id) {
        photoService.deletePhoto(id);
    }

    @PatchMapping(value = "/{id}/{hashtagName}")
    public void addHashtagToPhoto(@PathVariable int id, @PathVariable String hashtagName) {
        photoService.addHashtagToPhoto(id, hashtagName);
    }

}
