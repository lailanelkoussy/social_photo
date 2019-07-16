package com.social.photo.controllers;

import com.social.photo.dtos.HashTagDTO;
import com.social.photo.services.HashTagService;
import io.swagger.annotations.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;

@RestController
@RequestMapping(value = "/hashtags")
@Api(value = "HashTag Management Service")
public class HashTagController {

    @Autowired
    HashTagService hashtagService;

    @ApiOperation(value = "Get all hashtags", response = List.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successfully retrieved list"),
            @ApiResponse(code = 404, message = "The resource you were trying to reach is not found")})
    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public List<HashTagDTO> getAllHashtagDTOs() {
        return hashtagService.getAllHashTagDTOs();
    }

    @ApiOperation(value = "Get hashtag by id", response = HashTagDTO.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successfully retrieved object"),
            @ApiResponse(code = 404, message = "The resource you were trying to reach is not found")})
    @GetMapping(value = "/{hashtagId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public HashTagDTO getHashtag(
            @ApiParam(value = "Id of hashtag", required = true) @PathVariable int hashtagId) {
        return hashtagService.getHashTagDTO(hashtagId);
    }

    @ApiOperation(value = "Get a group's associated hash tag")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successfully retrieved list"),
            @ApiResponse(code = 404, message = "The resource you were trying to reach is not found")})
    @GetMapping(value = "/group/{groupId}")
    public Set<String> getGroupsHashtags(
            @ApiParam(value = "Id of group", required = true) @PathVariable int groupId) {
        return hashtagService.getGroupsTags(groupId);
    }

    @ApiOperation(value = "Update hash tag description")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successfully updated object"),
            @ApiResponse(code = 404, message = "The resource you were trying to reach is not found")})
    @PatchMapping(value = "/{hashtagId}")
    public void updateHashtagDescription(
            @ApiParam(value = "Id of hashtag", required = true) @PathVariable int hashtagId,
            @ApiParam(value = "HashTag object with new description", required = true) @RequestBody HashTagDTO hashtagDTO) {
        hashtagService.updateHashTagDescription(hashtagId, hashtagDTO);
    }


}
