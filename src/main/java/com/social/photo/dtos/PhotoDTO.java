package com.social.photo.dtos;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;

@Data
@NoArgsConstructor
public class PhotoDTO {

    @NotNull
    private String name;

    private String photoPath;

    private String hashtagName;

    @NotNull
    private int userId;

    private int groupId;
}
