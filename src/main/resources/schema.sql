CREATE TABLE users (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    email VARCHAR(150) NOT NULL UNIQUE
);

-- Insert sample users
INSERT INTO users (name, email) VALUES
('Alice Müller', 'alice.muller@example.com'),
('Bob Schmidt', 'bob.schmidt@example.com'),
('Carlos García', 'carlos.garcia@example.com');

CREATE TABLE meeting (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    title VARCHAR(200) NOT NULL,
    description VARCHAR(500),
    start_time TIMESTAMP NOT NULL,
    end_time TIMESTAMP NOT NULL,
    idempotency_key VARCHAR(100)
);

CREATE TABLE time_slot (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    start_time TIMESTAMP NOT NULL,
    end_time TIMESTAMP NOT NULL,
    is_busy BOOLEAN DEFAULT FALSE,
    meeting_id BIGINT,
    idempotency_key VARCHAR(100),
    CONSTRAINT fk_slot_user FOREIGN KEY (user_id) REFERENCES users(id),
    CONSTRAINT fk_slot_meeting FOREIGN KEY (meeting_id) REFERENCES meeting(id),
    CONSTRAINT chk_valid_time CHECK (end_time > start_time)
);

CREATE UNIQUE INDEX uq_idempotency_key ON time_slot (idempotency_key);

-- To avoid overlapping, this index avoids slots with exactly the same range.
CREATE UNIQUE INDEX uq_user_slot_range
ON time_slot (user_id, start_time, end_time);

CREATE TABLE meeting_participant (
    meeting_id BIGINT NOT NULL,
    user_id BIGINT NOT NULL,
    PRIMARY KEY (meeting_id, user_id),
    CONSTRAINT fk_meeting_user FOREIGN KEY (user_id) REFERENCES users(id),
    CONSTRAINT fk_meeting FOREIGN KEY (meeting_id) REFERENCES meeting(id)
);