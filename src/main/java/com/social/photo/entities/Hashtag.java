package com.social.photo.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

import javax.persistence.*;
import java.util.List;

@Entity
@Data
@Table(name = "hashtag")
public class Hashtag {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "hashtag_id")
    int id;

    String name;

    @JsonIgnore
    @OneToMany(mappedBy = "hashtag")
    List<Photo> photos;

    String description;
}
