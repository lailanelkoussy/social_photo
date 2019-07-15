package com.social.photo.controllers;

import com.social.photo.dtos.HashtagDTO;
import com.social.photo.services.HashtagService;
import io.swagger.annotations.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;

@RestController
@RequestMapping(value = "/hashtags")
@Api(value = "Hashtag Management Service")
public class HashtagController {

    @Autowired
    HashtagService hashtagService;

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "Get all hashtags", response = List.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successfully retrieved list"),
            @ApiResponse(code = 404, message = "The resource you were trying to reach is not found")})
    public List<HashtagDTO> getAllHashtagDTOs() {
        return hashtagService.getAllHashtagDTOs();
    }

    @GetMapping(value = "/{hashtagId}", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "Get hashtag by id", response = HashtagDTO.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successfully retrieved object"),
            @ApiResponse(code = 404, message = "The resource you were trying to reach is not found")})
    public HashtagDTO getHashtag(
            @ApiParam(value = "Id of hashtag", required = true) @PathVariable int hashtagId) {
        return hashtagService.getHashtagDTO(hashtagId);
    }

    @GetMapping(value = "/group/{groupId}")//todo hashtag/all/....
    @ApiOperation(value = "Get a group's associated hashtags")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successfully retrieved list"),
            @ApiResponse(code = 404, message = "The resource you were trying to reach is not found")})
    public Set<String> getGroupsHashtags(
            @ApiParam(value = "Id of group", required = true) @PathVariable int groupId) {
        return hashtagService.getGroupsTags(groupId);
    }

    @PatchMapping(value = "/{hashtagId}")
    @ApiOperation(value = "Update hashtag description")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successfully updated object"),
            @ApiResponse(code = 404, message = "The resource you were trying to reach is not found")})
    public void updateHashtagDescription(
            @ApiParam(value = "Id of hashtag", required = true) @PathVariable int hashtagId,
            @ApiParam(value = "Hashtag object with new description", required = true) @RequestBody HashtagDTO hashtagDTO) {
        hashtagService.updateHashtagDescription(hashtagId, hashtagDTO);
    }


}
