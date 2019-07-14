package com.social.photo.services;

import com.social.photo.dtos.HashtagDTO;
import com.social.photo.dtos.PhotoDTO;
import com.social.photo.entities.Hashtag;
import com.social.photo.entities.Photo;
import com.social.photo.repos.HashtagRepository;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;
import java.util.*;

@Slf4j
@Service
public class HashtagService {

    @Autowired
    HashtagRepository hashtagRepository;

    @Autowired
    PhotoService photoService;

    ModelMapper modelMapper = new ModelMapper();

    public Hashtag addHashtag(String hashtagName) {

        if (!hashtagExists(hashtagName)) {
            Hashtag hashtag = new Hashtag();
            hashtag.setName(hashtagName);
            log.info("Creating new hashtag");
            return hashtagRepository.save(hashtag);

        } else return hashtagRepository.getByName(hashtagName).get();

    }

    public Hashtag getHashtag(int id) {

        log.info("Retrieving hashtag");
        Optional<Hashtag> hashtagOptional = hashtagRepository.findById(id);
        if (hashtagOptional.isPresent()) {
            log.info("Hashtag found");
            return hashtagOptional.get();
        } else {
            log.error("Could not retrieve hashtag");
            throw (new EntityNotFoundException("Could not retrieve hashtag"));

        }
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
        else {
            log.info("Deleting hashtag");
            hashtagRepository.delete(hashtag);
        }
    }

    public void updateHashtagDescription(int id, HashtagDTO hashtagDTO) {
        Hashtag hashtag = getHashtag(id);
        if (!hashtagDTO.getDescription().equals(null) && !hashtagDTO.getDescription().equals(hashtag.getDescription())) {
            log.info("Updating hashtag description");
            hashtag.setDescription(hashtagDTO.getDescription());
            hashtagRepository.save(hashtag);
        }
    }

    public HashtagDTO getHashtagDTO(int id) {
        Hashtag hashtag = getHashtag(id);
        HashtagDTO hashtagDTO = new HashtagDTO();

        modelMapper.map(hashtag, hashtagDTO);
        return hashtagDTO;
    }

    public List<HashtagDTO> getAllHashtagDTOs() {
        log.info("Retrieving all hashtags");
        List<Hashtag> hashtags = hashtagRepository.findAll();
        List<HashtagDTO> hashtagDTOS = new ArrayList<>();

        for (Hashtag hashtag : hashtags) {

            hashtagDTOS.add(toHashtagDTO(hashtag));
        }

        return hashtagDTOS;
    }

    public Set<String> getGroupsTags(int groupId) {
        log.info("Retrieving hashtags");
        List<PhotoDTO> photoDTOS = photoService.getGroupsPhotos(groupId);
        Set<String> hashtagNames = new HashSet<>();

        for (PhotoDTO photoDTO : photoDTOS) {
            hashtagNames.add(photoDTO.getHashtagName());
        }
        return hashtagNames;

    }

    private HashtagDTO toHashtagDTO(Hashtag hashtag) {
        HashtagDTO hashtagDTO = new HashtagDTO();
        modelMapper.map(hashtag, hashtagDTO);
        return hashtagDTO;
    }




}
