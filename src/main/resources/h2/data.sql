INSERT INTO team (content_like, create_by, create_time, post_id, type, update_by, update_time, recruit_finish, team_content, team_title, thumbnail_url)
VALUES
    (10, 1, '2023-07-22 02:37:13', 3, 1, 1, '2023-07-22 02:37:13', '2023-08-22', 'team1_content', 'team1_title', 'thumbnail1'),
    (15, 2, '2023-07-22 02:37:13', 4, 2, 2, '2023-07-22 02:37:13', '2023-09-15', 'team2_content', 'team2_title', 'thumbnail2')

INSERT INTO post (user_id, content, title)
VALUES
    (1, '첫 번째 게시물 내용입니다.', '첫 번째 게시물 제목'),
    (2, '두 번째 게시물 내용입니다.', '두 번째 게시물 제목'),
    (1, '세 번째 게시물 내용입니다.', '세 번째 게시물 제목'),
    (3, '네 번째 게시물 내용입니다.', '네 번째 게시물 제목')

INSERT INTO user (is_auth, create_by, create_time, update_by, update_time, user_birthday, address, certificateurl, exp_year, expertize, html_content, meeting_type, picture_url, role, user_email, user_name)
VALUES
    (true, 1, '2023-07-22 12:00:00', 1, '2023-07-22 12:00:00', '1990-01-01 00:00:00', '서울시 강남구', 'https://example.com/certificate', '5', 'Java, Spring', 'HTML content', 'ONLINE', 'https://example.com/picture', 'USER', 'user1@example.com', 'User 1'),
    (false, 2, '2023-07-22 13:00:00', 2, '2023-07-22 13:00:00', '1985-05-15 00:00:00', '인천시 남구', 'https://example.com/certificate2', '7', 'Python, Django', 'HTML content 2', 'OFFLINE', 'https://example.com/picture2', 'MENTOR', 'user2@example.com', 'User 2'),
    (true, 3, '2023-07-22 14:00:00', 3, '2023-07-22 14:00:00', '1995-09-30 00:00:00', '대전시 서구', 'https://example.com/certificate3', '3', 'React, Node.js', 'HTML content 3', 'FREE', 'https://example.com/picture3', 'ADMIN', 'admin@example.com', 'Admin')


INSERT INTO user (user_name, picture_url, user_level, address, user_email, position, position_level, likes)
VALUES
    ('John Doe', 'http://example.com/johndoe.jpg', 'Level 3', '123 Main St', 'johndoe@example.com', 'Software Engineer', 'Senior', 100),
    ('Alice Smith', 'http://example.com/alicesmith.jpg', 'Level 2', '456 Elm St', 'alicesmith@example.com', 'Database Administrator', 'Intermediate', 50),
    ('Mike Brown', 'http://example.com/mikebrown.jpg', 'Level 2', '789 Oak St', 'mikebrown@example.com', 'Frontend Developer', 'Junior', 200);

INSERT INTO team_user (role, approve, team_id, user_id)
VALUES
    ('Developer', true, 1, 1),
    ('Designer', false, 1, 2),
    ('Tester', true, 2, 3);