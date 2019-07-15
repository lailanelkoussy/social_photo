package com.social.photo.dtos;

import lombok.Data;

@Data
public class GroupDTO { //todo grouping
    int id;

    String name;

    String description;

    int creatorId;

    boolean active;

}
