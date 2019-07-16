package com.social.photo.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

import javax.persistence.*;
import java.util.List;

@Entity
@Data
@Table(name = "hashtag")
public class HashTag {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "hash_tag_id")
    int id;

    @Column(nullable = false, unique = true)
    String name;

    @JsonIgnore
    @OneToMany(mappedBy = "hashtag")
    List<Photo> photos;

    String description;
}
