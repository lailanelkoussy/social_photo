CREATE TABLE photo
(
    photo_id   int(11)      NOT NULL AUTO_INCREMENT, -- todo only id
    name       varchar(255) NOT NULL, --todo a name for the photo?? isn't that a bit strange ?
    photo_path varchar(510) NOT NULL,
    time_stamp datetime(6),
    hashtag_id int(11), --todo this should be hash_tag_id, this is a typo

    PRIMARY KEY (photo_id),
    FOREIGN KEY (hashtag_id) REFERENCES hashtag (hashtag_id)
);