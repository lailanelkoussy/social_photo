package com.social.photo.proxies;

import com.social.photo.dtos.GroupDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@FeignClient(name = "GroupService", url = "http://localhost:8083")
@RequestMapping(value = "/groups")
public interface GroupServiceProxy {

    @GetMapping(value = "/user/{id}/")
    List<GroupDTO> getUserGroups(@PathVariable int id);

    @GetMapping(value = "/{id}")
    GroupDTO getGroup(@PathVariable int id);
}
