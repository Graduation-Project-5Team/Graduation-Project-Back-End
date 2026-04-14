INSERT INTO users
    (id, email, nickname, password, role, dept_id, is_verified, create_at)
values
    ('admin', 'admin@admin.com', '관리자', 'admin123', 'ADMIN', 1, true, CURRENT_TIMESTAMP);