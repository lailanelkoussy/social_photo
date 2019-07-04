package com.social.photo.controllers;

import com.social.photo.dtos.HashtagDTO;
import com.social.photo.services.HashtagService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(value = "/hashtags")
public class HashtagController {

    @Autowired
    HashtagService hashtagService;

    @GetMapping
    public List<HashtagDTO> getAllHashtagDTOs(){
        return hashtagService.getAllHashtagDTOs();
    }

    @GetMapping(value = "/{hashtagName}")
    public HashtagDTO getHashtag(@PathVariable String hashtagName){
        return hashtagService.getHashtagDTO(hashtagName);
    }

    @PatchMapping(value = "/{hashtagName}")
    public void updateHashtagDescription(@PathVariable String hashtagName, @RequestBody String description){
        hashtagService.updateHashtagDescription(hashtagName, description);
    }


}
