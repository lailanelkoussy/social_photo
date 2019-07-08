package com.social.photo.services;

import com.social.photo.dtos.HashtagDTO;
import com.social.photo.entities.Hashtag;
import com.social.photo.entities.Photo;
import com.social.photo.repos.HashtagRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

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

        } else return hashtagRepository.getByName(hashtagName).get();

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

    public void deleteHashtag(int id) {
        Optional<Hashtag> hashtagOptional = hashtagRepository.findById(id);
        if (!hashtagOptional.isPresent())
            throw (new EntityNotFoundException("Could not retrieve hashtag"));
        Hashtag hashtag = hashtagOptional.get();
        List<Photo> photos = new ArrayList<>();
        for (Photo photo : hashtag.getPhotos()) {
            photo.setHashtag(null);
            photos.add(photo);
        }
        hashtagRepository.deleteById(id);
        photoService.updatePhotos(photos);

    }

    public Hashtag getHashtag(int id) {

        Optional<Hashtag> hashtagOptional = hashtagRepository.findById(id);
        if (hashtagOptional.isPresent())
            return hashtagOptional.get();
        else throw (new EntityNotFoundException("Could not retrieve hashtag"));

    }

    public Hashtag getHashtagByName(String hashtagName) {
        Optional<Hashtag> hashtagOptional = hashtagRepository.getByName(hashtagName);
        if (hashtagOptional.isPresent())
            return hashtagOptional.get();
        else throw (new EntityNotFoundException("Could not retrieve hashtag"));

    }

    public boolean hashtagExists(String hashtagName) {
        return (hashtagRepository.countByName(hashtagName) != 0);
    }

    public void updateHashtag(Hashtag hashtag) {
        if (hashtag.getPhotos().size() != 0)
            hashtagRepository.save(hashtag);
        else hashtagRepository.delete(hashtag);
    }

    public void updateHashtagDescription(int id, String description) {
        Hashtag hashtag = getHashtag(id);
        hashtag.setDescription(description);
        hashtagRepository.save(hashtag);
    }

    public HashtagDTO getHashtagDTO(int id) {
        Hashtag hashtag = getHashtag(id);
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
