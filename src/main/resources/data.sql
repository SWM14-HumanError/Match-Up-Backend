INSERT
    INTO
        users (is_first_login, is_auth, review_score, total_reviews, create_by, create_time, likes, update_by, update_time, user_birthday, user_id, address, certificateurl, exp_year, expertize, meeting_type, picture_url, position, position_level, role, user_email, user_level, user_name, refresh_token)
    VALUES
        (true, true, 4.5, 10, 101, '2023-08-17 10:00:00', 50, 201, '2023-08-17 15:30:00', '1990-01-15', DEFAULT, '123 Main St', 'https://example.com/cert1', '5', 'Software Engineering', 'ONLINE', 'https://velog.velcdn.com/images/jujemu/post/d5c02f31-7d21-4034-a421-b4869fb15b7b/image.png', '백엔드 개발자', '1', 'MENTOR', 'user1@example.com', '3', 'John Doe', 'refresh_token_1'),
        (true, false, 3.2, 5, 102, '2023-08-17 12:30:00', 20, 202, '2023-08-17 16:45:00', '1985-07-25', DEFAULT, '456 Elm St', 'https://example.com/cert2', '8', 'Data Science', 'OFFLINE', 'https://velog.velcdn.com/images/jujemu/post/a8fd1782-c22c-4253-8bf1-ba5f9d7135ba/image.png', '프론트엔드 개발자', '2', 'MENTOR', 'user2@example.com', '2', 'Jane Smith', 'refresh_token_2'),
        (true, true, 4.8, 15, 103, '2023-08-17 09:15:00', 100, 203, '2023-08-17 14:15:00', '1982-03-10', DEFAULT, '789 Oak St', 'https://example.com/cert3', '12', 'Marketing Strategy', 'FREE', 'https://velog.velcdn.com/images/jujemu/post/9b98b118-aad7-4912-9e97-bec36834e895/image.png', 'AI/NLP', '3', 'MENTOR', 'user3@example.com', '1', 'Michael Johnson', 'refresh_token_3'),
        (false, true, 4.8, 15, 103, '2023-08-17 09:15:00', 100, 203, '2023-08-17 14:15:00', '1982-03-10', DEFAULT, '789 Oak St', 'https://example.com/cert3', '12', 'Marketing Strategy', 'FREE', 'https://velog.velcdn.com/images/jujemu/post/eae8fe88-cbba-47fc-83e8-4ed91a9df18d/image.png', '백엔드 개발자', '4', 'MENTOR', 'jujemu30@gmail.com', '4', 'jujemu', 'eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJpc3MiOiJtYXRjaC11cC54eXoiLCJpYXQiOjE2OTQwMjc5MjcsImV4cCI6MTcwODUyNzYwMCwic3ViIjoianVqZW11QG5hdmVyLmNvbSIsImlkIjo1fQ.Mz9OcDdroQ9Xp2vmpmqUVnmpJGEEfkGEJ-OBwVmbIm8'),
        (false, true, 4.8, 15, 103, '2023-08-17 09:15:00', 100, 203, '2023-08-17 14:15:00', '1982-03-10', DEFAULT, '789 Oak St', 'https://example.com/cert3', '12', 'Marketing Strategy', 'FREE', 'https://velog.velcdn.com/images/jujemu/post/eae8fe88-cbba-47fc-83e8-4ed91a9df18d/image.png', '백엔드 개발자', '4', 'MENTOR', 'jujemu@naver.com', '4', 'jujemu', 'eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJpc3MiOiJtYXRjaC11cC54eXoiLCJpYXQiOjE2OTQwMjc5MjcsImV4cCI6MTcwODUyNzYwMCwic3ViIjoianVqZW11QG5hdmVyLmNvbSIsImlkIjo1fQ.Mz9OcDdroQ9Xp2vmpmqUVnmpJGEEfkGEJ-OBwVmbIm8');

INSERT
    INTO
        team (team_id, team_title, description, type, detail_type, thumbnail_url, content_like, On_Offline, city, detail_spot, recruit_finish, is_deleted, leader_id)
    VALUES
        (DEFAULT, 'IT직군 멘토-멘티 찾기', '멘토와 멘티를 매칭하는 앱 프로젝트입니다.', 0, 'Detail Type 1', 'https://velog.velcdn.com/images/jujemu/post/8e058525-b6a1-46ae-bc58-30fb17c068e7/image.png', 10, 'Offline', 'City 1', 'Spot 1', '2023-08-31', 0, 1),
        (DEFAULT, '나만의 단짝 찾기', '나의 단짝은 어딨을까?', 0, 'Detail Type 2', 'https://velog.velcdn.com/images/jujemu/post/791a5c26-4fde-4bbb-8921-35c564833768/image.png', 5, 'Online', 'City 2', 'Spot 2', '2023-09-15', 0, 1),
        (DEFAULT, '부동산 투자 용어 공부', '강남찾아 인생을 찾아', 0, 'Detail Type 1', 'https://velog.velcdn.com/images/jujemu/post/3f9b6044-5dc5-4a88-8c01-4f96d110a01c/image.png', 20, 'Offline', 'City 3', 'Spot 3', '2023-08-20', 0, 2),
        (DEFAULT, '대학교 카풀 서비스', '대학생만을 위한 카풀 서비스 개발!', 0, 'Detail Type 3', 'https://velog.velcdn.com/images/jujemu/post/e17c2fdc-5651-4d2a-8333-93b022209dd0/image.png', 8, 'Online', 'City 4', 'Spot 4', '2023-09-05', 0, 2),
        (DEFAULT, 'CS 공부', '기초 네트워크부터 시작', 1, 'Detail Type 3', 'https://velog.velcdn.com/images/jujemu/post/fffac160-dd1b-4fa2-a866-9292e00d44ad/image.png', 8, 'Online', 'City 4', 'Spot 4', '2023-09-05', 0, 3),
        (DEFAULT, '운영체제 복습반', '공룡책 모임', 1, 'Detail Type 3', 'https://velog.velcdn.com/images/jujemu/post/fbc44b28-deb0-47bc-a46e-5208ec43abfe/image.png', 8, 'Online', 'City 4', 'Spot 4', '2023-09-05', 0, 4),
        (DEFAULT, 'NLP 초보자를 위한 트랜스포머 모임', '논문 읽기 모임', 1, 'Detail Type 3', 'https://velog.velcdn.com/images/jujemu/post/ecdbe8d5-f6c7-4208-8c2b-e3deb46232f0/image.png', 8, 'Online', 'City 4', 'Spot 4', '2023-09-05', 0, 4);

INSERT
    INTO
        mentoring (mentoring_title, mentoring_content, thumbnail_url, likes)
    VALUES
        ('프로그래밍 입문', '프로그래밍의 기본 개념을 배우세요.', 'https://velog.velcdn.com/images/jujemu/post/45143996-efae-4b61-860f-20ff1cc8fdab/image.png', 40),
        ('웹 개발 워크샵', 'HTML, CSS 및 JavaScript를 사용하여 현대적인 웹 애플리케이션을 만드세요.', 'https://velog.velcdn.com/images/jujemu/post/5fb1ec57-f5b0-4b52-887c-aad2518f8a6b/image.png', 23),
        ('데이터 과학 필수', '데이터 분석 및 머신 러닝 기술을 탐색하세요.', 'https://velog.velcdn.com/images/jujemu/post/aabbb272-1f21-4e29-90c5-0add17738bb8/image.png', 120),
        ('그래픽 디자인 기본', '시각 콘텐츠 작성의 기술을 마스터하세요.', 'https://velog.velcdn.com/images/jujemu/post/a5592035-4538-4824-8c24-369909f91ba7/image.png', 5);

INSERT
    INTO
        feed (create_time, feed_title, feed_content, thumbnail_url, user_id)
    VALUES
        ('2023-09-01', '1Spring 의 이여름 휴가 계획', '올해 여름 휴가 계획을 세우고 있습니다. 어디로 가야 할까요?', 'https://velog.velcdn.com/images/jujemu/post/e080fd9c-9c86-485d-b526-31c1a667790f/image.png', 4),
        ('2023-09-01', '2최신 프로그래밍 언어 트렌드', '2023년의 최신 프로그래밍 언어 트렌드는 무엇일까요?', 'https://velog.velcdn.com/images/jujemu/post/871f4b0a-704d-4b80-9578-d8aa9d0c08d2/image.png', 4),
        ('2023-09-01', '3맛집 추천', '요즘 강남에서 핫한 맛집을 소개합니다. 꼭 가보세요!', 'https://velog.velcdn.com/images/jujemu/post/e14c4d17-7b24-4cf2-82be-dfa77433d113/image.png', 4),
        ('2023-09-01', '4취미로 하는 일러스트 공유', '저는 주말마다 일러스트를 그리는 취미가 있습니다. 같이 공유하고 싶어요.', 'https://velog.velcdn.com/images/jujemu/post/a6668a9a-8d63-4a8c-873c-d7af5043f36a/image.png', 4),
        ('2023-09-01', '5독서 모임 참여자 모집', '매주 독서 모임을 개최하고 있습니다. 함께 읽을 동료를 찾습니다. 관심 있으신 분 연락주세요!', 'https://velog.velcdn.com/images/jujemu/post/7f78d17e-a499-4b00-b28f-ddf62117db73/image.png', 4),
        ('2023-09-01', '6여름 휴가 계획', '올해 여름 휴가 계획을 세우고 있습니다. 어디로 가야 할까요?', 'https://velog.velcdn.com/images/jujemu/post/e080fd9c-9c86-485d-b526-31c1a667790f/image.png', 4),
        ('2023-09-01', '7최신 프로그래밍 언어 트렌드', '2023년의 최신 프로그래밍 언어 트렌드는 무엇일까요?', 'https://velog.velcdn.com/images/jujemu/post/871f4b0a-704d-4b80-9578-d8aa9d0c08d2/image.png', 4),
        ('2023-09-01', '8맛집 추천', '요즘 강남에서 핫한 맛집을 소개합니다. 꼭 가보세요!', 'https://velog.velcdn.com/images/jujemu/post/e14c4d17-7b24-4cf2-82be-dfa77433d113/image.png', 4),
        ('2023-09-01', '9취미로 하는 일러스트 공유', '저는 주말마다 일러스트를 그리는 취미가 있습니다. 같이 공유하고 싶어요.', 'https://velog.velcdn.com/images/jujemu/post/a6668a9a-8d63-4a8c-873c-d7af5043f36a/image.png', 4),
        ('2023-09-01', '10여름 휴가 계획', '올해 여름 휴가 계획을 세우고 있습니다. 어디로 가야 할까요?', 'https://velog.velcdn.com/images/jujemu/post/e080fd9c-9c86-485d-b526-31c1a667790f/image.png', 4),
        ('2023-09-01', '11최신 프로그래밍 언어 트렌드', '2023년의 최신 프로그래밍 언어 트렌드는 무엇일까요?', 'https://velog.velcdn.com/images/jujemu/post/871f4b0a-704d-4b80-9578-d8aa9d0c08d2/image.png', 4),
        ('2023-09-01', '12맛집 추천', '요즘 강남에서 핫한 맛집을 소개합니다. 꼭 가보세요!', 'https://velog.velcdn.com/images/jujemu/post/e14c4d17-7b24-4cf2-82be-dfa77433d113/image.png', 4),
        ('2023-09-01', '13취미로 하는 일러스트 공유', '저는 주말마다 일러스트를 그리는 취미가 있습니다. 같이 공유하고 싶어요.', 'https://velog.velcdn.com/images/jujemu/post/a6668a9a-8d63-4a8c-873c-d7af5043f36a/image.png', 4),
        ('2023-09-01', '14여름 휴가 계획', '올해 여름 휴가 계획을 세우고 있습니다. 어디로 가야 할까요?', 'https://velog.velcdn.com/images/jujemu/post/e080fd9c-9c86-485d-b526-31c1a667790f/image.png', 4),
        ('2023-09-01', '15최신 프로그래밍 언어 트렌드', '2023년의 최신 프로그래밍 언어 트렌드는 무엇일까요?', 'https://velog.velcdn.com/images/jujemu/post/871f4b0a-704d-4b80-9578-d8aa9d0c08d2/image.png', 4),
        ('2023-09-01', '16맛집 추천', '요즘 강남에서 핫한 맛집을 소개합니다. 꼭 가보세요!', 'https://velog.velcdn.com/images/jujemu/post/e14c4d17-7b24-4cf2-82be-dfa77433d113/image.png', 4),
        ('2023-09-01', '17취미로 하는 일러스트 공유', '저는 주말마다 일러스트를 그리는 취미가 있습니다. 같이 공유하고 싶어요.', 'https://velog.velcdn.com/images/jujemu/post/a6668a9a-8d63-4a8c-873c-d7af5043f36a/image.png', 4);

INSERT
    INTO
        team_position (role, count, max_count, team_id)
    VALUES
        ('Manager', 0, 1, 1),
        ('Back-end/spring', 1, 2, 1),
        ('Front-end/react', 2, 3, 1),
        ('Manager', 0, 1, 2),
        ('Back-end/Django', 1, 2, 2),
        ('Front-end/react', 3, 3, 2);

-- INSERT
-- INTO
--     team_tag (tag_name, team_id, image_url)
-- VALUES
--     ('Spring', 1, 'https://cdn.jsdelivr.net/gh/devicons/devicon/icons/spring/spring-original.svg');