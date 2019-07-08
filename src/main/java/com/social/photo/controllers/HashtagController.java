package com.social.photo.controllers;

import com.social.photo.dtos.HashtagDTO;
import com.social.photo.services.HashtagService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(value = "/hashtags")
@Api(value = "Hashtag Management Service")
public class HashtagController {

    @Autowired
    HashtagService hashtagService;

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "Get all hashtags", response = List.class)
    public List<HashtagDTO> getAllHashtagDTOs() {
        return hashtagService.getAllHashtagDTOs();
    }

    @GetMapping(value = "/{hashtagId}", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "Get hashtag by id", response = HashtagDTO.class)
    public HashtagDTO getHashtag(
            @ApiParam(value = "Id of hashtag", required = true) @PathVariable int hashtagId) {
        return hashtagService.getHashtagDTO(hashtagId);
    }

    @PatchMapping(value = "/{hashtagId}")
    @ApiOperation(value = "Update hashtag description")
    public void updateHashtagDescription(
            @ApiParam(value = "Id of hashtag", required = true) @PathVariable int hashtagId,
            @ApiParam(value = "Updated hashtag description", required = true) @RequestBody String description) {
        hashtagService.updateHashtagDescription(hashtagId, description);
    }


}
