INSERT INTO users (is_auth, review_score, total_reviews, create_by, create_time, likes, update_by, update_time, user_birthday, user_id, address, certificateurl, exp_year, expertize, meeting_type, picture_url, position, position_level, role, user_email, user_level, user_name)
VALUES
    (true, 4.5, 10, 101, '2023-08-17 10:00:00', 50, 201, '2023-08-17 15:30:00', '1990-01-15', DEFAULT, '123 Main St', 'https://example.com/cert1', '5', 'Software Engineering', 'ONLINE', 'https://example.com/pic1', 'Senior Developer', '1', 'MENTOR', 'user1@example.com', '3', 'John Doe'),
    (false, 3.2, 5, 102, '2023-08-17 12:30:00', 20, 202, '2023-08-17 16:45:00', '1985-07-25', DEFAULT, '456 Elm St', 'https://example.com/cert2', '8', 'Data Science', 'OFFLINE', 'https://example.com/pic2', 'Data Scientist', '2', 'MENTOR', 'user2@example.com', '2', 'Jane Smith'),
    (true, 4.8, 15, 103, '2023-08-17 09:15:00', 100, 203, '2023-08-17 14:15:00', '1982-03-10', DEFAULT, '789 Oak St', 'https://example.com/cert3', '12', 'Marketing Strategy', 'FREE', 'https://example.com/pic3', 'Marketing Manager', '3', 'MENTOR', 'user3@example.com', '1', 'Michael Johnson'),
    (true, 4.8, 15, 103, '2023-08-17 09:15:00', 100, 203, '2023-08-17 14:15:00', '1982-03-10', DEFAULT, '789 Oak St', 'https://example.com/cert3', '12', 'Marketing Strategy', 'FREE', 'https://example.com/pic3', 'Marketing Manager', '4', 'MENTOR', 'jujemu30@gmail.com', '4', 'jujemu');

INSERT
    INTO team
        (team_id, team_title, description, type, detail_type, thumbnail_Url, content_like, On_Offline, city, detail_spot, recruit_finish, is_deleted, leader_id)
    VALUES
        (1, 'Team 1', 'Description 1', 1, 'Detail Type 1', 'thumbnail1.jpg', 10, 'Offline', 'City 1', 'Spot 1', '2023-08-31', 0, 101),
        (2, 'Team 2', 'Description 2', 2, 'Detail Type 2', 'thumbnail2.jpg', 5, 'Online', 'City 2', 'Spot 2', '2023-09-15', 0, 102),
        (3, 'Team 3', 'Description 3', 1, 'Detail Type 1', 'thumbnail3.jpg', 20, 'Offline', 'City 3', 'Spot 3', '2023-08-20', 0, 103),
        (4, 'Team 4', 'Description 4', 3, 'Detail Type 3', 'thumbnail4.jpg', 8, 'Online', 'City 4', 'Spot 4', '2023-09-05', 0, 104);

