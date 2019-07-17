package com.social.photo.proxies;

import com.social.photo.dtos.UserDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@FeignClient(name = "UserService", url = "http://localhost:8081")
@RequestMapping(value = "/users")
public interface UserServiceProxy {

    @GetMapping(value = "/{id}")
    UserDTO getUser(@PathVariable int id);

    @GetMapping(value = "/{id}/following")
    List<UserDTO> getUserFollowing(@PathVariable int id);
}
