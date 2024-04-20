CREATE SEQUENCE IF NOT EXISTS categories_seq START WITH 1 INCREMENT BY 50;

CREATE SEQUENCE IF NOT EXISTS messages_seq START WITH 1 INCREMENT BY 50;

CREATE SEQUENCE IF NOT EXISTS roles_seq START WITH 1 INCREMENT BY 50;

CREATE SEQUENCE IF NOT EXISTS topics_seq START WITH 1 INCREMENT BY 50;

CREATE SEQUENCE IF NOT EXISTS users_seq START WITH 1 INCREMENT BY 50;

CREATE TABLE categories
(
    id                 BIGINT       NOT NULL,
    creation_time      TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    modification_time  TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    creator_id         BIGINT       NOT NULL,
    name               VARCHAR(255) NOT NULL,
    parent_category_id BIGINT,
    CONSTRAINT pk_categories PRIMARY KEY (id)
);

CREATE TABLE category_moderators
(
    category_id BIGINT NOT NULL,
    user_id     BIGINT NOT NULL
);

CREATE TABLE messages
(
    id                BIGINT       NOT NULL,
    creation_time     TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    modification_time TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    creator_id        BIGINT       NOT NULL,
    content           VARCHAR(255) NOT NULL,
    parent_topic_id   BIGINT,
    CONSTRAINT pk_messages PRIMARY KEY (id)
);

CREATE TABLE roles
(
    id   BIGINT NOT NULL,
    name VARCHAR(255),
    CONSTRAINT pk_roles PRIMARY KEY (id)
);

CREATE TABLE topics
(
    id                 BIGINT       NOT NULL,
    creation_time      TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    modification_time  TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    creator_id         BIGINT       NOT NULL,
    name               VARCHAR(255) NOT NULL,
    is_archived        BOOLEAN      NOT NULL,
    parent_category_id BIGINT,
    CONSTRAINT pk_topics PRIMARY KEY (id)
);

CREATE TABLE users
(
    id           BIGINT       NOT NULL,
    full_name    VARCHAR(255),
    email        VARCHAR(255) NOT NULL,
    username     VARCHAR(255) NOT NULL,
    password     VARCHAR(255),
    phone_number VARCHAR(255),
    is_banned    BOOLEAN,
    is_enabled   BOOLEAN,
    CONSTRAINT pk_users PRIMARY KEY (id)
);

CREATE TABLE users_roles
(
    user_entity_id BIGINT NOT NULL,
    roles_id       BIGINT NOT NULL
);

ALTER TABLE users
    ADD CONSTRAINT uc_users_email UNIQUE (email);

ALTER TABLE users
    ADD CONSTRAINT uc_users_username UNIQUE (username);

ALTER TABLE categories
    ADD CONSTRAINT FK_CATEGORIES_ON_CREATOR FOREIGN KEY (creator_id) REFERENCES users (id);

ALTER TABLE categories
    ADD CONSTRAINT FK_CATEGORIES_ON_PARENT_CATEGORY FOREIGN KEY (parent_category_id) REFERENCES categories (id);

ALTER TABLE messages
    ADD CONSTRAINT FK_MESSAGES_ON_CREATOR FOREIGN KEY (creator_id) REFERENCES users (id);

ALTER TABLE messages
    ADD CONSTRAINT FK_MESSAGES_ON_PARENT_TOPIC FOREIGN KEY (parent_topic_id) REFERENCES topics (id);

ALTER TABLE topics
    ADD CONSTRAINT FK_TOPICS_ON_CREATOR FOREIGN KEY (creator_id) REFERENCES users (id);

ALTER TABLE topics
    ADD CONSTRAINT FK_TOPICS_ON_PARENT_CATEGORY FOREIGN KEY (parent_category_id) REFERENCES categories (id);

ALTER TABLE category_moderators
    ADD CONSTRAINT fk_catmod_on_category_entity FOREIGN KEY (category_id) REFERENCES categories (id);

ALTER TABLE category_moderators
    ADD CONSTRAINT fk_catmod_on_user_entity FOREIGN KEY (user_id) REFERENCES users (id);

ALTER TABLE users_roles
    ADD CONSTRAINT fk_userol_on_role_entity FOREIGN KEY (roles_id) REFERENCES roles (id);

ALTER TABLE users_roles
    ADD CONSTRAINT fk_userol_on_user_entity FOREIGN KEY (user_entity_id) REFERENCES users (id);