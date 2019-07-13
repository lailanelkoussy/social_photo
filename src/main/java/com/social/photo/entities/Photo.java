package com.social.photo.entities;

import lombok.Data;

import javax.persistence.*;
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

    @ManyToOne
    @JoinColumn(name = "hashtag_id")
    private Hashtag hashtag;

    @Column(name = "time_stamp")
    private Calendar timeStamp = Calendar.getInstance();

    public String getSystemName() {
        return name + " " + timeStamp.getTime() + ".jpg";
    } //todo spaces can cause a problem later, prefer using '_' or '-' in the name instead

}
