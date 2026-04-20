INSERT INTO departments (name) VALUES ('컴퓨터 소프트웨어학과');

INSERT INTO Users
    (id, email, nickname, password, role, dept_id, is_verified, create_at)
VALUES
    ('admin', 'admin@admin.com', '관리자', 'admin123', 'ADMIN', 1, true, CURRENT_TIMESTAMP);

-- (이메일)테이블 추가
CREATE TABLE IF NOT EXISTS email_verification (
    id         BIGINT AUTO_INCREMENT PRIMARY KEY,
    email      VARCHAR(255) NOT NULL,
    code       VARCHAR(6)   NOT NULL,
    is_verified BOOLEAN     NOT NULL DEFAULT FALSE,
    expired_at  TIMESTAMP   NOT NULL,
    created_at  TIMESTAMP   NOT NULL DEFAULT CURRENT_TIMESTAMP
    );