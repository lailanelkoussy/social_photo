package com.social.photo.proxies;

import com.social.photo.dtos.GroupDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@FeignClient(name = "GroupService", url = "http://localhost:8083")
public interface GroupServiceProxy {

    @GetMapping(value = "/user/{id}/", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<GroupDTO> getUserGroups(@PathVariable int id);

    @GetMapping(value = "/user/{id}/ids")
    public List<Integer> getUserGroupIds(@PathVariable int id);

    @GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public GroupDTO getGroup(@PathVariable int id);
}
