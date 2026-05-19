-- 학과 추가
INSERT INTO departments (name) VALUES ('컴퓨터 소프트웨어학과');
INSERT INTO departments (name) VALUES ('산업경영공학과');
INSERT INTO departments (name) VALUES ('시각디자인학과');
INSERT INTO departments (name) VALUES ('기계자동차학과');
INSERT INTO departments (name) VALUES ('기계 공학과');
INSERT INTO departments (name) VALUES ('멀티 미디어학과');
INSERT INTO departments (name) VALUES ('방송 영상 미디어학과');
INSERT INTO departments (name) VALUES ('실내건축학과');
INSERT INTO departments (name) VALUES ('건축학과');
INSERT INTO departments (name) VALUES ('주얼리 디자인학과');

--사용자 추가
INSERT INTO Users (id, email, nickname, password, role, dept_id, is_verified, created_at) VALUES
    ('admin', 'admin@admin.com', '관리자', 'admin123', 'ADMIN', 1, true, CURRENT_TIMESTAMP);
INSERT INTO Users(id, email, nickname, password, role, dept_id, is_verified, created_at)
VALUES ('user123', 'user123@gsuite.induk.ac.kr', '유저1234', 'user1234', 'STUDENT', 1, true, CURRENT_TIMESTAMP);

-- 전시 추가
INSERT INTO Exhibitions (name, description, thumbnail_image, dept_id, start_date, end_date)
values ('컴퓨터 소프트웨어학과 전시', '컴퓨터 소프트웨어학과 전시입니다.',
        'computerSoftWare.png', 1, '2026-05-11', '2026-08-11');

-- 작품 추가
INSERT INTO Artworks(user_id, exhi_id, title, description, like_count, views, created_at, updated_at)
VALUES(2, 1, '컴퓨터소프트웨어학과 전시', '컴퓨터 소프트가 아니라 컴퓨터 공학이네용', 0,
       0, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);
--작품의 이미지들
INSERT INTO ARTWORK_IMAGES(artwork_id, image_url, sort_order) VALUES
    (1, 'computer_ai1.png', 0),
    (1, 'computer_a2.png', 1),
    (1, 'computer_a3.png', 2);

INSERT INTO COMMENTS(artwork_id, user_id, content, created_at, deleted) VALUES(
    1, 2, '댓글이에용', CURRENT_TIMESTAMP, false);

-- (이메일)테이블 추가
CREATE TABLE IF NOT EXISTS email_verification (
    id         BIGINT AUTO_INCREMENT PRIMARY KEY,
    email      VARCHAR(255) NOT NULL,
    code       VARCHAR(6)   NOT NULL,
    is_verified BOOLEAN     NOT NULL DEFAULT FALSE,
    expired_at  TIMESTAMP   NOT NULL,
    created_at  TIMESTAMP   NOT NULL DEFAULT CURRENT_TIMESTAMP
    );