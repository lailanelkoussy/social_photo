package com.social.photo.services;

import com.social.photo.dtos.HashtagDTO;
import com.social.photo.entities.Hashtag;
import com.social.photo.entities.Photo;
import com.social.photo.repos.HashtagRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class HashtagService {

    @Autowired
    HashtagRepository hashtagRepository;

    @Autowired
    PhotoService photoService;

    public Hashtag addHashtag(String hashtagName) {

        if (!hashtagExists(hashtagName)) {
            Hashtag hashtag = new Hashtag();
            hashtag.setName(hashtagName);
            return hashtagRepository.save(hashtag);

        } else return hashtagRepository.getByName(hashtagName);

    }

    public boolean addHashtag(HashtagDTO hashtagDTO) {

        if (!hashtagExists(hashtagDTO.getName())) {
            ModelMapper modelMapper = new ModelMapper();
            Hashtag hashtag = new Hashtag();
            modelMapper.map(hashtag, hashtag);
            hashtagRepository.save(hashtag);
            return true;
        } else return false;
    }

    public void deleteHashtag(String hashtagName) {
        Hashtag hashtag = hashtagRepository.getByName(hashtagName);
        List<Photo> photos = new ArrayList<>();
        for (Photo photo : hashtag.getPhotos()) {
            photo.setHashtag(null);
            photos.add(photo);
        }
        hashtagRepository.deleteByName(hashtagName);
        photoService.updatePhotos(photos);

    }

    public Hashtag getHashtag(String hashtagName) {
        return hashtagRepository.getByName(hashtagName);

    }

    public boolean hashtagExists(String hashtagName) {
        return (hashtagRepository.countByName(hashtagName) != 0);
    }

    public void updateHashtag(Hashtag hashtag) { //todo the name of the function is not descriptive
        if (hashtag.getPhotos().size() != 0)
            hashtagRepository.save(hashtag);
        else hashtagRepository.delete(hashtag);
    }

    public void updateHashtagDescription(String hashtagName, String description) {
        Hashtag hashtag = hashtagRepository.getByName(hashtagName);
        hashtag.setDescription(description);
        hashtagRepository.save(hashtag);
    }

    public HashtagDTO getHashtagDTO(String hashtagName) {//todo you shouldn't name a function here as DTO!
        Hashtag hashtag = getHashtag(hashtagName);
        ModelMapper modelMapper = new ModelMapper();
        HashtagDTO hashtagDTO = new HashtagDTO();

        modelMapper.map(hashtag, hashtagDTO);
        return hashtagDTO;
    }

    public List<HashtagDTO> getAllHashtagDTOs() {
        List<Hashtag> hashtags = hashtagRepository.findAll();
        ModelMapper modelMapper = new ModelMapper();
        List<HashtagDTO> hashtagDTOS = new ArrayList<>();

        for (Hashtag hashtag : hashtags) {
            HashtagDTO hashtagDTO = new HashtagDTO();
            modelMapper.map(hashtag, hashtagDTO);
            hashtagDTOS.add(hashtagDTO);
        }

        return hashtagDTOS;
    }


}
