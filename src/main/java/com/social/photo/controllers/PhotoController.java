package com.social.photo.controllers;

import com.social.photo.dtos.PhotoDTO;
import com.social.photo.dtos.PhotoPatchDTO;
import com.social.photo.services.PhotoService;
import io.swagger.annotations.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.InvalidClassException;
import java.util.List;

@RestController
@RequestMapping(value = "/photos")
@Api(value = "Photo Management Service")
public class PhotoController {

    @Autowired
    private PhotoService photoService;

    @ApiOperation(value = "Get photos using their hashtag", response = List.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successfully retrieved list"),
            @ApiResponse(code = 404, message = "The resource you were trying to reach is not found")})
    @GetMapping(value = "/all/hashtag/{hashtagId}")
    public List<PhotoDTO> getPhotosByHashtag(
            @ApiParam(value = "Id of hashtag", required = true) @PathVariable int hashtagId) {
        return photoService.getPhotosByHashtag(hashtagId);
    }

    @ApiOperation(value = "Get a user's homepage", response = List.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successfully retrieved list"),
            @ApiResponse(code = 404, message = "The resource you were trying to reach is not found"),})
    @GetMapping(value = "/user/{userId}/home/{pageNumber}/{pageSize}")
    public List<PhotoDTO> getHomepage(@PathVariable int userId, @PathVariable int pageNumber, @PathVariable int pageSize) {
        return photoService.getHomePage(userId, pageNumber, pageSize);
    }

    @ApiOperation(value = "Get photo using id", response = PhotoDTO.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successfully retrieved object"),
            @ApiResponse(code = 404, message = "The resource you were trying to reach is not found")})
    @GetMapping(value = "/{id}")
    public PhotoDTO getPhoto(
            @ApiParam(value = "Id of photo", required = true) @PathVariable int id) {
        return photoService.getPhoto(id);
    }


    @ApiOperation(value = "Get user's photo", response = List.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successfully retrieved list"),
            @ApiResponse(code = 404, message = "The resource you were trying to reach is not found")})
    @GetMapping(value = "/all/user/{userId}")
    public List<PhotoDTO> getUsersPhotos(
            @ApiParam(value = "Id of user", required = true) @PathVariable int userId) {
        return photoService.getUsersPhotos(userId);
    }

    @ApiOperation(value = "Get photos posted in a group", response = List.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successfully retrieved list"),
            @ApiResponse(code = 403, message = "Not authorized to perform this action"),
            @ApiResponse(code = 404, message = "The resource you were trying to reach is not found")})
    @GetMapping(value = "/all/group/{userId}/{groupId}")
    public List<PhotoDTO> getGroupsPhotos(
            @ApiParam(value = "Id of group", required = true) @PathVariable int groupId,
            @ApiParam(value = "Id of user performing action", required = true) @PathVariable int userId) throws IllegalAccessException {
        return photoService.getGroupsPhotos(userId, groupId);
    }

    @ApiOperation(value = "Upload photo")
    @ApiResponses(value = {
            @ApiResponse(code = 202, message = "Successfully uploaded object"),
            @ApiResponse(code = 404, message = "The resource you were trying to reach is not found")})
    @PostMapping(value = "/{userId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public void addPhoto(
            @ApiParam(value = "Id of user", required = true) @PathVariable int userId,
            @ApiParam(value = "Photo to upload", required = true) @RequestParam MultipartFile file,
            @ApiParam(value = "Name of photo") @RequestParam String photoName,
            @ApiParam(value = "Name of hashtag") @RequestParam String hashtag) throws HttpMediaTypeNotSupportedException {
        photoService.addPhoto(file, photoName, hashtag, userId);
    }

    @ApiOperation(value = "Upload photo to group")
    @ApiResponses(value = {
            @ApiResponse(code = 202, message = "Successfully uploaded object"),
            @ApiResponse(code = 403, message = "Not authorized to perform this action"),
            @ApiResponse(code = 404, message = "The resource you were trying to reach is not found")})
    @PostMapping(value = "/{userId}/group/{groupId}")
    public void addPhotoToGroup(
            @ApiParam(value = "Id of user", required = true) @PathVariable int userId,
            @ApiParam(value = "Id of group", required = true) @PathVariable int groupId,
            @ApiParam(value = "Photo to upload", required = true) @RequestParam MultipartFile file,
            @ApiParam(value = "Name of photo") @RequestParam String photoName,
            @ApiParam(value = "Name of hashtag") @RequestParam String hashtag) throws HttpMediaTypeNotSupportedException, IllegalAccessException {
        photoService.addPhotoToGroup(file, photoName, hashtag, userId, groupId);
    }

    @ApiOperation(value = "Delete photo")
    @ApiResponses(value = {
            @ApiResponse(code = 202, message = "Successfully deleted object"),
            @ApiResponse(code = 404, message = "The resource you were trying to reach is not found")})
    @DeleteMapping(value = "/{photoId}/user/{userId}")
    public void deletePhoto(
            @ApiParam(value = "Id of user performing the action", required = true) @PathVariable int userId,
            @ApiParam(value = "Id of photo") @PathVariable int photoId) throws IllegalAccessException {
        photoService.deletePhoto(userId, photoId);
    }

    @ApiOperation(value = "Adding a hashtag to photo")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successfully updated object"),
            @ApiResponse(code = 404, message = "The resource you were trying to reach is not found"),
            @ApiResponse(code = 406, message = "Invalid object")})
    @PatchMapping(value = "/{id}")
    public void addHashtagToPhoto(
            @ApiParam(value = "Id of photo", required = true) @PathVariable int id,
            @ApiParam(value = "Name of hashtag to add to photo ", required = true) @RequestBody PhotoPatchDTO photo) throws InvalidClassException {
        photoService.addHashTagToPhoto(id, photo);
    }

}
