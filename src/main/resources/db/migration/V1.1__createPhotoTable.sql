CREATE TABLE photo
(
    photo_id   int(11)      NOT NULL AUTO_INCREMENT,
    name       varchar(255) NOT NULL,
    photo_path varchar(510) NOT NULL,
    time_stamp datetime(6),
    hashtag_id int(11),

    PRIMARY KEY (photo_id),
    FOREIGN KEY (hashtag_id) REFERENCES hashtag (hashtag_id)
);