package com.social.photo.dtos;

import lombok.Data;

@Data
public class GroupDTO {

    int id;
    int creatorId;

    String name;
    String description;

    boolean active;

}
