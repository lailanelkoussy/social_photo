CREATE TABLE hashtag
(
    hashtag_id  int(11)      NOT NULL AUTO_INCREMENT,--this should be named as 'id' only
    name        varchar(255) NOT NULL,
    description varchar(510) DEFAULT NULL,

    PRIMARY KEY (hashtag_id)
);


