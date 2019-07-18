package com.social.photo.services;

import com.social.photo.dtos.HashTagDTO;
import com.social.photo.dtos.PhotoDTO;
import com.social.photo.entities.HashTag;
import com.social.photo.proxies.GroupServiceProxy;
import com.social.photo.repos.HashTagRepository;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;
import java.util.*;

@Slf4j
@Service
public class HashTagService {

    @Autowired
    private HashTagRepository hashtagRepository;

    @Autowired
    private PhotoService photoService;

    @Autowired
    private GroupServiceProxy groupServiceProxy;

    private ModelMapper modelMapper = new ModelMapper();

    HashTag addHashTag(String hashTagName) {

        if (!hashTagExists(hashTagName)) {
            HashTag hashtag = new HashTag();
            hashtag.setName(hashTagName);
            log.info("Creating new hash tag");
            return hashtagRepository.save(hashtag);

        } else return hashtagRepository.getByName(hashTagName).get();
    }

    private HashTag getHashTag(int id) {

        log.info("Retrieving hash tag");
        Optional<HashTag> hashtagOptional = hashtagRepository.findById(id);
        if (hashtagOptional.isPresent()) {
            log.info("HashTag found");
            return hashtagOptional.get();
        } else {
            log.error("Could not retrieve hash tag");
            throw (new EntityNotFoundException("Could not retrieve hash tag"));
        }
    }

    HashTag getHashTagByName(String hashTagName) {

        Optional<HashTag> hashTagOptional = hashtagRepository.getByName(hashTagName);
        if (hashTagOptional.isPresent())
            return hashTagOptional.get();
        else throw (new EntityNotFoundException("Could not retrieve hash tag"));
    }

    boolean hashTagExists(String hashTagName) {
        return (hashtagRepository.countByName(hashTagName) != 0);
    }

    void updateHashTag(HashTag hashtag) {
        if (hashtag.getPhotos().size() != 0)
            hashtagRepository.save(hashtag);
        else {
            log.info("Deleting hash tag...");
            hashtagRepository.delete(hashtag);
        }
    }

    public void updateHashTagDescription(int id, HashTagDTO hashtagDTO) {
        HashTag hashtag = getHashTag(id);
        if ((hashtagDTO.getDescription() != null) && !hashtagDTO.getDescription().equals(hashtag.getDescription())) {
            log.info("Updating hash tag description");
            hashtag.setDescription(hashtagDTO.getDescription());
            hashtagRepository.save(hashtag);
        }
    }

    public HashTagDTO getHashTagDTO(int id) {
        HashTag hashtag = getHashTag(id);
        HashTagDTO hashtagDTO = new HashTagDTO();

        modelMapper.map(hashtag, hashtagDTO);
        return hashtagDTO;
    }

    public List<HashTagDTO> getAllHashTagDTOs() {

        log.info("Retrieving all hashTags");
        List<HashTag> hashTags = hashtagRepository.findAll();
        List<HashTagDTO> hashTagDTOS = new ArrayList<>();

        for (HashTag hashtag : hashTags)
            hashTagDTOS.add(toHashTagDTO(hashtag));

        return hashTagDTOS;
    }

    public Set<String> getGroupsTags(int groupId) {
        log.info("Retrieving hash tags");

        try {
            groupServiceProxy.getGroup(groupId);
        } catch (Exception e) {
            throw new EntityNotFoundException("Could not find group");
        }
        List<PhotoDTO> photoDTOS = photoService.getGroupsPhotos(groupId);
        Set<String> hashTagNames = new HashSet<>();

        for (PhotoDTO photoDTO : photoDTOS) {
            hashTagNames.add(photoDTO.getHashtagName());
        }
        return hashTagNames;
    }

    private HashTagDTO toHashTagDTO(HashTag hashtag) {
        HashTagDTO hashtagDTO = new HashTagDTO();
        modelMapper.map(hashtag, hashtagDTO);
        return hashtagDTO;
    }
}
