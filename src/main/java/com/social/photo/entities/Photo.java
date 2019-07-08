package com.social.photo.entities;

import lombok.Data;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.Calendar;

@Entity
@Data
@Table(name = "photo")
public class Photo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "photo_id")
    private int id;

    private String name;

    @Column(name = "photo_path")
    private String photoPath;

    @Column(name = "user_id")
    private int userId;

    @Column(name = "group_id")
    private int groupId;

    @ManyToOne
    @JoinColumn(name = "hashtag_id")
    private Hashtag hashtag;

    @Column(name = "time_stamp")
    private LocalDateTime timeStamp = LocalDateTime.now();

    public String getSystemName() {
        return name + " " + timeStamp + ".jpg";
    }

}
