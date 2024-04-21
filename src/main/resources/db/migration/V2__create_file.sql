CREATE TABLE files
(
    id            VARCHAR(255) NOT NULL,
    creation_time TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    file_name     VARCHAR(255) NOT NULL,
    file_size     BIGINT       NOT NULL,
    creator_id    BIGINT       NOT NULL,
    message_id    BIGINT       NOT NULL,
    CONSTRAINT pk_files PRIMARY KEY (id)
);

ALTER TABLE files
    ADD CONSTRAINT FK_FILES_ON_CREATOR FOREIGN KEY (creator_id) REFERENCES users (id);

ALTER TABLE files
    ADD CONSTRAINT FK_FILES_ON_MESSAGE FOREIGN KEY (message_id) REFERENCES messages (id);