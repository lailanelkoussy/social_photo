CREATE TABLE hashtag
(
    hash_tag_id  int(11)      NOT NULL AUTO_INCREMENT,
    name        varchar(255) NOT NULL,
    description varchar(510) DEFAULT NULL,

    PRIMARY KEY (hash_tag_id)
);


