package com.social.photo.dtos;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class HashTagDTO {

    int id;

    String name;

    String description;

    @JsonIgnore
    public void setId(int id){
        this.id = id;
    }

    @JsonProperty
    public int getId(){
        return id;
    }
}
