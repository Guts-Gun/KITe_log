CREATE TABLE address_email
(
    id                 BIGINT AUTO_INCREMENT NOT NULL,
    reg_dt             VARCHAR(255)          NULL,
    mod_dt             VARCHAR(255)          NULL,
    fk_user_address_id BIGINT                NULL,
    email              VARCHAR(255)          NULL,
    is_deleted         BIT(1)                NULL,
    reg_id             VARCHAR(20)           NOT NULL,
    mod_id             VARCHAR(20)           NULL,
    CONSTRAINT pk_addressemail PRIMARY KEY (id)
);

CREATE TABLE address_group
(
    id                 BIGINT AUTO_INCREMENT NOT NULL,
    reg_dt             VARCHAR(255)          NULL,
    mod_dt             VARCHAR(255)          NULL,
    fk_user_address_id BIGINT                NULL,
    fk_user_group_id   BIGINT                NULL,
    is_deleted         BIT(1)                NULL,
    reg_id             VARCHAR(20)           NOT NULL,
    mod_id             VARCHAR(20)           NULL,
    CONSTRAINT pk_addressgroup PRIMARY KEY (id)
);

CREATE TABLE address_phone
(
    id                 BIGINT AUTO_INCREMENT NOT NULL,
    reg_dt             VARCHAR(255)          NULL,
    mod_dt             VARCHAR(255)          NULL,
    fk_user_address_id BIGINT                NULL,
    phone              VARCHAR(255)          NULL,
    is_deleted         BIT(1)                NULL,
    reg_id             VARCHAR(20)           NOT NULL,
    mod_id             VARCHAR(20)           NULL,
    CONSTRAINT pk_addressphone PRIMARY KEY (id)
);

CREATE TABLE broker
(
    id           BIGINT AUTO_INCREMENT NOT NULL,
    reg_dt       VARCHAR(255)          NULL,
    mod_dt       VARCHAR(255)          NULL,
    name         VARCHAR(255)          NULL,
    ip           VARCHAR(255)          NULL,
    color        VARCHAR(255)          NULL,
    sending_type INT                   NULL,
    price        FLOAT                 NULL,
    latency      FLOAT                 NULL,
    failure_rate FLOAT                 NULL,
    is_deleted   BIT(1)                NULL,
    CONSTRAINT pk_broker PRIMARY KEY (id)
);

CREATE TABLE message_template
(
    id         BIGINT       NOT NULL,
    reg_dt     VARCHAR(255) NULL,
    mod_dt     VARCHAR(255) NULL,
    fk_user_id VARCHAR(255) NULL,
    title      VARCHAR(255) NULL,
    content    VARCHAR(255) NULL,
    is_deleted BIT(1)       NULL,
    reg_id     VARCHAR(20)  NOT NULL,
    mod_id     VARCHAR(20)  NULL,
    CONSTRAINT pk_message_template PRIMARY KEY (id)
);

CREATE TABLE result_sending
(
    id                BIGINT AUTO_INCREMENT NOT NULL,
    reg_dt            VARCHAR(255)          NULL,
    mod_dt            VARCHAR(255)          NULL,
    fk_user_id        VARCHAR(255)          NULL,
    fk_sending_id     BIGINT                NULL,
    sending_type      INT                   NULL,
    sending_rule_type INT                   NULL,
    sending_status    INT                   NULL,
    success           BIT(1)                NULL,
    total_message     BIGINT                NULL,
    failed_message    BIGINT                NULL,
    avg_latency       FLOAT                 NULL,
    input_time        BIGINT                NULL,
    schedule_time     BIGINT                NULL,
    start_time        BIGINT                NULL,
    complete_time     BIGINT                NULL,
    log_time          BIGINT                NULL,
    is_deleted        BIT(1)                NULL,
    CONSTRAINT pk_resultsending PRIMARY KEY (id)
);

CREATE TABLE result_tx
(
    id                   BIGINT AUTO_INCREMENT NOT NULL,
    reg_dt               VARCHAR(255)          NULL,
    mod_dt               VARCHAR(255)          NULL,
    fk_user_id           VARCHAR(255)          NULL,
    fk_result_sending_id BIGINT                NULL,
    fk_tx_id             BIGINT                NULL,
    fk_broker_id         BIGINT                NULL,
    sending_type         INT                   NULL,
    sender               VARCHAR(255)          NULL,
    receiver             VARCHAR(255)          NULL,
    status               INT                   NULL,
    fail_reason          INT                   NULL,
    title                VARCHAR(255)          NULL,
    media_link           VARCHAR(255)          NULL,
    content              VARCHAR(255)          NULL,
    input_time           BIGINT                NULL,
    schedule_time        BIGINT                NULL,
    start_time           BIGINT                NULL,
    log_time             BIGINT                NULL,
    is_deleted           BIT(1)                NULL,
    CONSTRAINT pk_resulttx PRIMARY KEY (id)
);

CREATE TABLE result_tx_failure
(
    id                   BIGINT       NOT NULL,
    reg_dt               VARCHAR(255) NULL,
    mod_dt               VARCHAR(255) NULL,
    fk_user_id           VARCHAR(255) NULL,
    fk_result_sending_id BIGINT       NULL,
    fk_tx_id             BIGINT       NULL,
    fk_broker_id         BIGINT       NULL,
    sending_type         INT          NULL,
    fail_reason          INT          NULL,
    title                VARCHAR(255) NULL,
    media_link           VARCHAR(255) NULL,
    content              VARCHAR(255) NULL,
    is_deleted           BIT(1)       NULL,
    CONSTRAINT pk_resulttxfailure PRIMARY KEY (id)
);

CREATE TABLE result_tx_transfer
(
    id              BIGINT AUTO_INCREMENT NOT NULL,
    reg_dt          VARCHAR(255)          NULL,
    mod_dt          VARCHAR(255)          NULL,
    fk_result_tx_id BIGINT                NULL,
    fk_broker_id    BIGINT                NULL,
    sending_type    INT                   NULL,
    sender          VARCHAR(255)          NULL,
    receiver        VARCHAR(255)          NULL,
    success         BIT(1)                NULL,
    fail_reason     INT                   NULL,
    send_time       BIGINT                NULL,
    complete_time   BIGINT                NULL,
    log_time        BIGINT                NULL,
    is_deleted      BIT(1)                NULL,
    CONSTRAINT pk_result_tx_transfer PRIMARY KEY (id)
);

CREATE TABLE send_replace
(
    fk_tx_id BIGINT AUTO_INCREMENT NOT NULL,
    reg_dt   VARCHAR(255)          NULL,
    mod_dt   VARCHAR(255)          NULL,
    receiver VARCHAR(255)          NULL,
    sender   VARCHAR(255)          NULL,
    reg_id   VARCHAR(20)           NOT NULL,
    mod_id   VARCHAR(20)           NULL,
    CONSTRAINT pk_send_replace PRIMARY KEY (fk_tx_id)
);

CREATE TABLE sending
(
    id                BIGINT AUTO_INCREMENT NOT NULL,
    reg_dt            VARCHAR(255)          NULL,
    mod_dt            VARCHAR(255)          NULL,
    fk_user_id        VARCHAR(255)          NULL,
    sending_rule_type VARCHAR(255)          NULL,
    sending_type      VARCHAR(255)          NULL,
    replace_yn        VARCHAR(255)          NULL,
    total_message     BIGINT                NULL,
    input_time        BIGINT                NULL,
    schedule_time     BIGINT                NULL,
    title             VARCHAR(255)          NULL,
    media_link        VARCHAR(255)          NULL,
    content           VARCHAR(255)          NULL,
    sender            VARCHAR(255)          NULL,
    is_deleted        BIT(1)                NULL,
    reg_id            VARCHAR(20)           NOT NULL,
    mod_id            VARCHAR(20)           NULL,
    CONSTRAINT pk_sending PRIMARY KEY (id)
);

CREATE TABLE sending_block
(
    id         BIGINT AUTO_INCREMENT NOT NULL,
    reg_dt     VARCHAR(255)          NULL,
    mod_dt     VARCHAR(255)          NULL,
    sender     VARCHAR(255)          NULL,
    receiver   VARCHAR(255)          NULL,
    block_time BIGINT                NULL,
    is_deleted BIT(1)                NULL,
    reg_id     VARCHAR(20)           NOT NULL,
    mod_id     VARCHAR(20)           NULL,
    CONSTRAINT pk_sendingblock PRIMARY KEY (id)
);

CREATE TABLE sending_email
(
    id            BIGINT AUTO_INCREMENT NOT NULL,
    reg_dt        VARCHAR(255)          NULL,
    mod_dt        VARCHAR(255)          NULL,
    fk_sending_id BIGINT                NULL,
    sender        VARCHAR(255)          NULL,
    receiver      VARCHAR(255)          NULL,
    name          VARCHAR(255)          NULL,
    var1          VARCHAR(255)          NULL,
    var2          VARCHAR(255)          NULL,
    var3          VARCHAR(255)          NULL,
    is_deleted    BIT(1)                NULL,
    reg_id        VARCHAR(20)           NOT NULL,
    mod_id        VARCHAR(20)           NULL,
    CONSTRAINT pk_sendingemail PRIMARY KEY (id)
);

CREATE TABLE sending_msg
(
    id            BIGINT AUTO_INCREMENT NOT NULL,
    reg_dt        VARCHAR(255)          NULL,
    mod_dt        VARCHAR(255)          NULL,
    fk_sending_id BIGINT                NULL,
    sender        VARCHAR(255)          NULL,
    receiver      VARCHAR(255)          NULL,
    name          VARCHAR(255)          NULL,
    var1          VARCHAR(255)          NULL,
    var2          VARCHAR(255)          NULL,
    var3          VARCHAR(255)          NULL,
    is_deleted    BIT(1)                NULL,
    reg_id        VARCHAR(20)           NOT NULL,
    mod_id        VARCHAR(20)           NULL,
    CONSTRAINT pk_sending_msg PRIMARY KEY (id)
);

CREATE TABLE sending_rule
(
    id            BIGINT AUTO_INCREMENT NOT NULL,
    reg_dt        VARCHAR(255)          NULL,
    mod_dt        VARCHAR(255)          NULL,
    fk_user_id    VARCHAR(255)          NULL,
    fk_sending_id BIGINT                NULL,
    fk_broker_id  BIGINT                NULL,
    weight        BIGINT                NULL,
    is_deleted    BIT(1)                NULL,
    CONSTRAINT pk_sending_rule PRIMARY KEY (id)
);

CREATE TABLE sending_schedule
(
    id            BIGINT AUTO_INCREMENT NOT NULL,
    reg_dt        VARCHAR(255)          NULL,
    mod_dt        VARCHAR(255)          NULL,
    fk_sending_id BIGINT                NULL,
    time          BIGINT                NULL,
    is_deleted    BIT(1)                NULL,
    CONSTRAINT pk_sending_schedule PRIMARY KEY (id)
);

CREATE TABLE user
(
    id         VARCHAR(255) NOT NULL,
    reg_dt     VARCHAR(255) NULL,
    mod_dt     VARCHAR(255) NULL,
    name       VARCHAR(255) NULL,
    email      VARCHAR(255) NULL,
    is_deleted BIT(1)       NULL,
    CONSTRAINT pk_user PRIMARY KEY (id)
);

CREATE TABLE user_address
(
    id         BIGINT AUTO_INCREMENT NOT NULL,
    reg_dt     VARCHAR(255)          NULL,
    mod_dt     VARCHAR(255)          NULL,
    fk_user_id VARCHAR(255)          NULL,
    name       VARCHAR(255)          NULL,
    is_deleted BIT(1)                NULL,
    reg_id     VARCHAR(20)           NOT NULL,
    mod_id     VARCHAR(20)           NULL,
    CONSTRAINT pk_user_address PRIMARY KEY (id)
);

CREATE TABLE user_email
(
    id         BIGINT AUTO_INCREMENT NOT NULL,
    reg_dt     VARCHAR(255)          NULL,
    mod_dt     VARCHAR(255)          NULL,
    fk_user_id VARCHAR(255)          NULL,
    name       VARCHAR(255)          NULL,
    email      VARCHAR(255)          NULL,
    is_deleted BIT(1)                NULL,
    reg_id     VARCHAR(20)           NOT NULL,
    mod_id     VARCHAR(20)           NULL,
    CONSTRAINT pk_user_email PRIMARY KEY (id)
);

CREATE TABLE user_group
(
    id            BIGINT AUTO_INCREMENT NOT NULL,
    reg_dt        VARCHAR(255)          NULL,
    mod_dt        VARCHAR(255)          NULL,
    fk_user_id    VARCHAR(255)          NULL,
    group_name    VARCHAR(255)          NULL,
    `description` VARCHAR(255)          NULL,
    is_deleted    BIT(1)                NULL,
    reg_id        VARCHAR(20)           NOT NULL,
    mod_id        VARCHAR(20)           NULL,
    CONSTRAINT pk_user_group PRIMARY KEY (id)
);

CREATE TABLE user_phone
(
    id         BIGINT AUTO_INCREMENT NOT NULL,
    reg_dt     VARCHAR(255)          NULL,
    mod_dt     VARCHAR(255)          NULL,
    fk_user_id VARCHAR(255)          NULL,
    name       VARCHAR(255)          NULL,
    phone      VARCHAR(255)          NULL,
    is_deleted BIT(1)                NULL,
    reg_id     VARCHAR(20)           NOT NULL,
    mod_id     VARCHAR(20)           NULL,
    CONSTRAINT pk_user_phone PRIMARY KEY (id)
);

CREATE TABLE user_sending_rule
(
    id           BIGINT AUTO_INCREMENT NOT NULL,
    reg_dt       VARCHAR(255)          NULL,
    mod_dt       VARCHAR(255)          NULL,
    fk_user_id   VARCHAR(255)          NULL,
    fk_broker_id BIGINT                NULL,
    weight       BIGINT                NULL,
    reg_id       VARCHAR(20)           NOT NULL,
    mod_id       VARCHAR(20)           NULL,
    is_deleted   BIT(1)                NULL,
    CONSTRAINT pk_user_sending_rule PRIMARY KEY (id)
);

CREATE INDEX idx_address_email_user_address_id ON address_email (fk_user_address_id);

CREATE INDEX idx_address_group_user_group_id ON address_group (fk_user_group_id);

CREATE INDEX idx_address_phone_user_address_id ON address_phone (fk_user_address_id);

CREATE INDEX idx_result_sending_sending_id ON result_sending (fk_sending_id);

CREATE INDEX idx_result_sending_user_id ON result_sending (fk_user_id);

CREATE INDEX idx_result_tx_failure_result_sending_id ON result_tx_failure (fk_result_sending_id);

CREATE INDEX idx_result_tx_failure_tx_id ON result_tx_failure (fk_tx_id);

CREATE INDEX idx_result_tx_failure_user_id ON result_tx_failure (fk_user_id);

CREATE INDEX idx_result_tx_result_sending_id ON result_tx (fk_result_sending_id);

CREATE INDEX idx_result_tx_transfer_result_tx_id ON result_tx_transfer (fk_result_tx_id);

CREATE INDEX idx_result_tx_tx_id ON result_tx (fk_tx_id);

CREATE INDEX idx_result_tx_user_id ON result_tx (fk_user_id);

CREATE INDEX idx_sending_block_receiver ON sending_block (receiver);

CREATE INDEX idx_sending_block_sender ON sending_block (sender);

CREATE INDEX idx_sending_email_sending_id ON sending_email (fk_sending_id);

CREATE INDEX idx_sending_msg_sending_id ON sending_msg (fk_sending_id);

CREATE INDEX idx_sending_rule_sending_id ON sending_rule (fk_sending_id);

CREATE INDEX idx_sending_rule_user_id ON sending_rule (fk_user_id);

CREATE INDEX idx_sending_schedule_time ON sending_schedule (time);

CREATE INDEX idx_sending_user_id ON sending (fk_user_id);

CREATE INDEX idx_user_address_user_id ON user_address (fk_user_id);

CREATE INDEX idx_user_email_user_id ON user_email (fk_user_id);

CREATE INDEX idx_user_group_user_id ON user_group (fk_user_id);

CREATE INDEX idx_user_phone_user_id ON user_phone (fk_user_id);

CREATE INDEX idx_user_sending_rule_user_id ON user_sending_rule (fk_user_id);