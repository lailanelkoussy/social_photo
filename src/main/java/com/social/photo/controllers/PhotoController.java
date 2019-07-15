package com.social.photo.controllers;

import com.social.photo.dtos.PhotoDTO;
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
    //todo can we put the annotations regarding the documentation above the GET - POST etc ??

    @GetMapping(value = "/hashtag/{hashtagId}", produces = MediaType.APPLICATION_JSON_VALUE)//todo can this be /photos/all/hashtag/... ??
    @ApiOperation(value = "Get photos using their hashtag", response = List.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successfully retrieved list"),
            @ApiResponse(code = 404, message = "The resource you were trying to reach is not found")})
    public List<PhotoDTO> getPhotosByHashtag(
            @ApiParam(value = "Id of hashtag", required = true) @PathVariable int hashtagId) {
        return photoService.getPhotosByHashtag(hashtagId);
    }

    @GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "Get photo using id", response = PhotoDTO.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successfully retrieved object"),
            @ApiResponse(code = 404, message = "The resource you were trying to reach is not found")})
    public PhotoDTO getPhoto(
            @ApiParam(value = "Id of photo", required = true) @PathVariable int id) {
        return photoService.getPhoto(id);
    }

    //todo should be photos/all/user/....
    @GetMapping(value = "/user/{userId}", produces = MediaType.APPLICATION_JSON_VALUE) //todo, please check the default values, JSON is already the default value, you are repeating yourself
    @ApiOperation(value = "Get user's photo", response = List.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successfully retrieved list"),
            @ApiResponse(code = 404, message = "The resource you were trying to reach is not found")})
    public List<PhotoDTO> getUsersPhotos(
            @ApiParam(value = "Id of user", required = true) @PathVariable int userId) {
        return photoService.getUsersPhotos(userId);
    }

    @GetMapping(value = "/group/{userId}/{groupId}", produces = MediaType.APPLICATION_JSON_VALUE)//todo this is not the right endpoint that your should use, what do you think?
    @ApiOperation(value = "Get photos posted in a group", response = List.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successfully retrieved list"),
            @ApiResponse(code = 404, message = "The resource you were trying to reach is not found"),
            @ApiResponse(code = 500, message = "Could not connect to Group Service")})
    public List<PhotoDTO> getGroupsPhotos(
            @ApiParam(value = "Id of group", required = true) @PathVariable int groupId,
            @ApiParam(value = "Id of user performing action", required = true) @PathVariable int userId) throws IllegalAccessException {
        return photoService.getGroupsPhotos(userId, groupId);
    }

    @PostMapping(value = "/{userId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @ApiOperation(value = "Upload photo")
    @ApiResponses(value = {
            @ApiResponse(code = 202, message = "Successfully uploaded object"),
            @ApiResponse(code = 404, message = "The resource you were trying to reach is not found")})
    public ResponseEntity<Object> addPhoto(
            @ApiParam(value = "Id of user", required = true) @PathVariable int userId,
            @ApiParam(value = "Photo to upload", required = true) @RequestParam MultipartFile file,
            @ApiParam(value = "Name of photo") @RequestParam String photoName,
            @ApiParam(value = "Name of hashtag") @RequestParam String hashtag) throws HttpMediaTypeNotSupportedException {
        return new ResponseEntity<>(photoService.addPhoto(file, photoName, hashtag, userId) ? HttpStatus.OK : HttpStatus.BAD_REQUEST);
    }

    @PostMapping(value = "/{userId}/group/{groupId}")
    @ApiOperation(value = "Upload photo to group")
    @ApiResponses(value = {
            @ApiResponse(code = 202, message = "Successfully uploaded object"),
            @ApiResponse(code = 404, message = "The resource you were trying to reach is not found"),
            @ApiResponse(code = 500, message = "Could not connect to Group Service")}) //todo this should never happen, the user should never be presented with suck an error !, try to select another one
    public ResponseEntity<Object> addPhotoToGroup(
            @ApiParam(value = "Id of user", required = true) @PathVariable int userId,
            @ApiParam(value = "Id of group", required = true) @PathVariable int groupId,
            @ApiParam(value = "Photo to upload", required = true) @RequestParam MultipartFile file,
            @ApiParam(value = "Name of photo") @RequestParam String photoName,
            @ApiParam(value = "Name of hashtag") @RequestParam String hashtag) throws HttpMediaTypeNotSupportedException {
        return new ResponseEntity<>(photoService.addPhotoToGroup(file, photoName, hashtag, userId, groupId) ? HttpStatus.OK : HttpStatus.FORBIDDEN);
    }

    @DeleteMapping(value = "/{userId}/{photoId}")//todo this is not the right endpoint
    @ApiOperation(value = "Delete photo")
    @ApiResponses(value = {
            @ApiResponse(code = 202, message = "Successfully deleted object"),
            @ApiResponse(code = 404, message = "The resource you were trying to reach is not found"),
            @ApiResponse(code = 500, message = "Could not connect to Group Service")})
    public ResponseEntity<Object> deletePhoto(
            @ApiParam(value = "Id of user performing the action", required = true) @PathVariable int userId,
            @ApiParam(value = "Id of photo") @PathVariable int photoId) throws IllegalAccessException {
        return new ResponseEntity<>(photoService.deletePhoto(userId, photoId) ? HttpStatus.ACCEPTED : HttpStatus.FORBIDDEN);
    }

    @PatchMapping(value = "/{id}")
    @ApiOperation(value = "Add hashtag to photo")//todo can we make that more generic?
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successfully updated object"),
            @ApiResponse(code = 404, message = "The resource you were trying to reach is not found"),
            @ApiResponse(code = 406, message = "Invalid object")})
    public void addHashtagToPhoto(
            @ApiParam(value = "Id of photo", required = true) @PathVariable int id,
            @ApiParam(value = "Name of hashtag to add to photo ", required = true) @RequestBody PhotoDTO photo) throws InvalidClassException {
        photoService.addHashtagToPhoto(id, photo);
    }
}
