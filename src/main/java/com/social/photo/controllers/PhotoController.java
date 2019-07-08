package com.social.photo.controllers;

import com.social.photo.dtos.PhotoDTO;
import com.social.photo.entities.Photo;
import com.social.photo.services.PhotoService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
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
@Api(value = "Photo Management Service")
public class PhotoController {

    @Autowired
    private PhotoService photoService;

    @GetMapping(value = "/hashtag/{hashtagId}", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "Get photos using their hashtag", response = List.class)
    public List<PhotoDTO> getPhotosByHashtag(
            @ApiParam(value = "Id of hashtag", required = true) @PathVariable int hashtagId) {
        return photoService.getPhotosByHashtag(hashtagId);
    }

    @GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "Get photo using id", response = PhotoDTO.class)
    public PhotoDTO getPhoto(
            @ApiParam(value = "Id of hashtag", required = true) @PathVariable int id) {
        return photoService.getPhoto(id);
    }

    @GetMapping(value = "/user/{userId}", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "Get user's photo", response = List.class)
    public List<PhotoDTO> getUsersPhotos(
            @ApiParam(value = "Id of user", required = true) @PathVariable int userId) {
        return photoService.getUsersPhotos(userId);
    }

    @GetMapping(value = "/group/{userId}/{groupId}", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "Get photos posted in the group", response = List.class)
    public List<PhotoDTO> getGroupsPhotos(
            @ApiParam(value = "Id of group", required = true) @PathVariable int groupId,
            @ApiParam(value = "Id of user performing action", required = true) @PathVariable int userId) throws IllegalAccessException {
        return photoService.getGroupsPhotos(userId, groupId);
    }

    @PostMapping(value = "/{userId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @ApiOperation(value = "Upload photo")
    public ResponseEntity<Object> addPhoto(
            @ApiParam(value = "Id of user", required = true) @PathVariable int userId,
            @ApiParam(value = "Photo to upload", required = true) @RequestParam MultipartFile file,
            @ApiParam(value = "Name of photo") @RequestParam String photoName,
            @ApiParam(value = "Name of hashtag") @RequestParam String hashtag) throws HttpMediaTypeNotSupportedException {
        return new ResponseEntity<>(photoService.addPhoto(file, photoName, hashtag, userId) ? HttpStatus.OK : HttpStatus.BAD_REQUEST);
    }

    @PostMapping(value = "/{userId}/group/{groupId}")
    @ApiOperation(value = "Upload photo to group")
    public ResponseEntity<Object> addPhotoToGroup(
            @ApiParam(value = "Id of user", required = true) @PathVariable int userId,
            @ApiParam(value = "Id of group", required = true) @PathVariable int groupId,
            @ApiParam(value = "Photo to upload", required = true) @RequestParam MultipartFile file,
            @ApiParam(value = "Name of photo") @RequestParam String photoName,
            @ApiParam(value = "Name of hashtag") @RequestParam String hashtag) throws HttpMediaTypeNotSupportedException {
        return new ResponseEntity<>(photoService.addPhotoToGroup(file, photoName, hashtag, userId, groupId) ? HttpStatus.OK : HttpStatus.FORBIDDEN);
    }

    @DeleteMapping(value = "/{userId}/{photoId}")
    @ApiOperation(value = "Delete photo")
    public ResponseEntity<Object> deletePhoto(
            @ApiParam(value = "Id of user performing the action", required = true) @PathVariable int userId,
            @ApiParam(value = "Id of photo") @PathVariable int photoId) throws IllegalAccessException {
        return new ResponseEntity<>(photoService.deletePhoto(userId, photoId) ? HttpStatus.ACCEPTED : HttpStatus.FORBIDDEN);
    }

    @PatchMapping(value = "/{id}/{hashtagName}")
    @ApiOperation(value = "Add hashtag to photo")
    public void addHashtagToPhoto(
            @ApiParam(value = "Id of photo", required = true) @PathVariable int id,
            @ApiParam(value = "Name of hashtag to add to photo ", required = true) @PathVariable String hashtagName) {
        photoService.addHashtagToPhoto(id, hashtagName);
    }

}
