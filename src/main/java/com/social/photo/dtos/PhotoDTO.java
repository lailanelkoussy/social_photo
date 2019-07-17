package com.social.photo.dtos;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;

@Data
@NoArgsConstructor
public class PhotoDTO {

    int id;

    @NotNull
    private String name;

    private String photoPath;

    private String hashtagName;

    @NotNull
    private int userId;

    private int groupId;

    @JsonIgnore
    public void setId(int id){
        this.id = id;
    }

    @JsonProperty
    public int getId(){
        return id;
    }
}
