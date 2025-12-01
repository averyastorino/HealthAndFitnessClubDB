-- INSERT SAMPLE MEMBERS
INSERT INTO Member (name, email, weight_goal, date_of_birth, gender)
VALUES 
('Alice Johnson', 'alice@example.com', 60, '1990-05-12', 'Female'),
('Bob Smith', 'bob@example.com', 75, '1985-03-22', 'Male'),
('Charlie Lee', 'charlie@example.com', 68, '1992-11-05', 'Male');

-- MEMBER PHONES
INSERT INTO Member_Phone (member_id, phone_number)
VALUES
(1, '613-555-1234'),
(1, '343-555-5678'),
(2, '613-555-8765'),
(3, '226-555-4321');

-- INSERT SAMPLE TRAINERS
INSERT INTO Trainer (name, email, phone)
VALUES
('Diana Prince', 'diana@example.com', '613-555-111'),
('Edward Norton', 'edward@example.com', '343-555-2222');

-- INSERT SAMPLE ROOMS
INSERT INTO Room (name, capacity)
VALUES
('Studio A', 20),
('Studio B', 15);

-- INSERT SAMPLE SESSIONS
INSERT INTO Session (name, start_time, duration, capacity, trainer_id, room_id)
VALUES
('Yoga Class', '2025-11-20 09:00', '1 hour', 15, 1, 1),
('HIIT Training', '2025-11-20 10:30', '45 minutes', 10, 2, 2);

-- INSERT SAMPLE BOOKINGS
INSERT INTO Booking (member_id, session_id, booking_time, status)
VALUES
(1, 1, '2025-11-19 14:00', 'Confirmed'),
(2, 2, '2025-11-19 15:00', 'Confirmed'),
(3, 1, '2025-11-19 16:00', 'Cancelled');

-- INSERT SAMPLE HEALTH METRICS
INSERT INTO Health_Metric (member_id, metric_date, weight, body_fat_percentage, heart_rate)
VALUES
(1, '2025-11-01', 62, 22.5, 72),
(1, '2025-11-15', 61, 21.8, 70),
(2, '2025-11-10', 78, 25.0, 75),
(3, '2025-11-12', 67, 19.5, 68);

-- INSERT TRAINER AVAILABILITY
INSERT INTO Trainer_Availability (trainer_id, availability_date, start_time, end_time)
VALUES
(1, '2025-11-20', '08:00', '12:00'),
(2, '2025-11-20', '10:00', '14:00');
