CREATE TABLE photo
(
    photo_id   int(11)      NOT NULL AUTO_INCREMENT,
    user_id    int(11)      NOT NULL,
    group_id   int(11),
    name       varchar(255) NOT NULL,
    photo_path varchar(510) NOT NULL,
    time_stamp datetime(6),
    hash_tag_id int(11),

    PRIMARY KEY (photo_id),
    FOREIGN KEY (hash_tag_id) REFERENCES hashtag (hash_tag_id)
);