INSERT INTO users (is_auth, review_score, total_reviews, create_by, create_time, likes, update_by, update_time, user_birthday, user_id, address, certificateurl, exp_year, expertize, html_content, meeting_type, picture_url, position, position_level, role, user_email, user_level, user_name)
VALUES
    (true, 4.5, 10, 101, '2023-08-17 10:00:00', 50, 201, '2023-08-17 15:30:00', '1990-01-15', DEFAULT, '123 Main St', 'https://example.com/cert1', '5', 'Software Engineering', '<p>Hello, I am an expert in programming.</p>', 'ONLINE', 'https://example.com/pic1', 'Senior Developer', '1', 'MENTOR', 'user1@example.com', '3', 'John Doe'),
    (false, 3.2, 5, 102, '2023-08-17 12:30:00', 20, 202, '2023-08-17 16:45:00', '1985-07-25', DEFAULT, '456 Elm St', 'https://example.com/cert2', '8', 'Data Science', '<p>Welcome! I specialize in data analysis.</p>', 'OFFLINE', 'https://example.com/pic2', 'Data Scientist', '2', 'MENTOR', 'user2@example.com', '2', 'Jane Smith'),
    (true, 4.8, 15, 103, '2023-08-17 09:15:00', 100, 203, '2023-08-17 14:15:00', '1982-03-10', DEFAULT, '789 Oak St', 'https://example.com/cert3', '12', 'Marketing Strategy', '<p>Marketing guru ready to assist you!</p>', 'FREE', 'https://example.com/pic3', 'Marketing Manager', '3', 'MENTOR', 'user3@example.com', '1', 'Michael Johnson'),
    (true, 4.8, 15, 103, '2023-08-17 09:15:00', 100, 203, '2023-08-17 14:15:00', '1982-03-10', DEFAULT, '789 Oak St', 'https://example.com/cert3', '12', 'Marketing Strategy', '<p>Marketing guru ready to assist you!</p>', 'FREE', 'https://example.com/pic3', 'Marketing Manager', '4', 'MENTOR', 'jujemu30@gmail.com', '4', 'jujemu');