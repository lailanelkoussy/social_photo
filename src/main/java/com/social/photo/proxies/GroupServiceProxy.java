package com.social.photo.proxies;

import com.social.photo.dtos.GroupDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@FeignClient(name = "GroupService", url = "http://localhost:8083") //todo please update that to match the other service controller
@RequestMapping(value = "/groups")
public interface GroupServiceProxy {

    @GetMapping(value = "/user/{id}/", produces = MediaType.APPLICATION_JSON_VALUE)
    List<GroupDTO> getUserGroups(@PathVariable int id);

    @GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    GroupDTO getGroup(@PathVariable int id);
}
