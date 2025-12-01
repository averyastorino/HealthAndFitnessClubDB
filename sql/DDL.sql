-- MEMBER TABLE
CREATE TABLE Member (
    member_id SERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    email VARCHAR(100) UNIQUE NOT NULL,
    weight_goal NUMERIC,
    date_of_birth DATE,
    gender VARCHAR(10)
);

-- MEMBER PHONE TABLE (handles multiple phone numbers)
CREATE TABLE Member_Phone (
    member_id INT NOT NULL,
    phone_number VARCHAR(20) NOT NULL,
    PRIMARY KEY (member_id, phone_number),
    FOREIGN KEY (member_id) REFERENCES Member(member_id) ON DELETE CASCADE
);

-- HEALTH METRIC TABLE
CREATE TABLE Health_Metric (
    metric_id SERIAL PRIMARY KEY,
    member_id INT NOT NULL,
    metric_date DATE NOT NULL,
    weight NUMERIC,
    body_fat_percentage NUMERIC,
    heart_rate INT,
    FOREIGN KEY (member_id) REFERENCES Member(member_id) ON DELETE CASCADE
);

-- TRAINER TABLE
CREATE TABLE Trainer (
    trainer_id SERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    email VARCHAR(100) UNIQUE NOT NULL,
    phone VARCHAR(20)
);

-- ROOM TABLE
CREATE TABLE Room (
    room_id SERIAL PRIMARY KEY,
    name VARCHAR(50) NOT NULL,
    capacity INT NOT NULL
);

-- SESSION TABLE
CREATE TABLE Session (
    session_id SERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    start_time TIMESTAMP NOT NULL,
    duration INTERVAL NOT NULL,
    capacity INT NOT NULL,
    trainer_id INT NOT NULL,
    room_id INT NOT NULL,
    FOREIGN KEY (trainer_id) REFERENCES Trainer(trainer_id),
    FOREIGN KEY (room_id) REFERENCES Room(room_id)
);

-- BOOKING TABLE (Relationship)
CREATE TABLE Booking (
    booking_id SERIAL PRIMARY KEY,
    member_id INT NOT NULL,
    session_id INT NOT NULL,
    booking_time TIMESTAMP NOT NULL,
    status VARCHAR(20),
    FOREIGN KEY (member_id) REFERENCES Member(member_id),
    FOREIGN KEY (session_id) REFERENCES Session(session_id)
);

-- TRAINER AVAILABILITY TABLE
CREATE TABLE Trainer_Availability (
    availability_id SERIAL PRIMARY KEY,
    trainer_id INT NOT NULL,
    availability_date DATE NOT NULL,
    start_time TIME NOT NULL,
    end_time TIME NOT NULL,
    FOREIGN KEY (trainer_id) REFERENCES Trainer(trainer_id)
);

-- View: Shows the member's latest health metrics, weight goal, and upcoming sessions
CREATE OR REPLACE VIEW LatestHealthMetrics AS
SELECT
    m.member_id,
    m.name,
    hm.metric_date,
    hm.weight,
    hm.body_fat_percentage,
    hm.heart_rate
FROM
    Member m
JOIN
    Health_Metric hm ON m.member_id = hm.member_id
WHERE
    hm.metric_date = (
        SELECT MAX(hm2.metric_date)
        FROM Health_Metric hm2
        WHERE hm2.member_id = m.member_id
    );

-- Trigger function to prevent a member from double-booking overlapping sessions
CREATE OR REPLACE FUNCTION prevent_booking_overlap()
RETURNS TRIGGER AS $$
DECLARE
    new_start_time TIMESTAMP;
BEGIN
    -- Get the start time of the session the user is trying to book
    SELECT start_time INTO new_start_time
    FROM Session
    WHERE session_id = NEW.session_id;

    -- Check for overlapping sessions already booked
    IF EXISTS (
        SELECT 1
        FROM Booking b
        JOIN Session s ON s.session_id = b.session_id
        WHERE b.member_id = NEW.member_id
          AND s.start_time < new_start_time + INTERVAL '1 hour'
          AND s.start_time + INTERVAL '1 hour' > new_start_time
    ) THEN
        RAISE EXCEPTION 'Booking conflict: Member already has a session at this time';
    END IF;

    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

-- Attach trigger to Booking table
CREATE TRIGGER trg_prevent_booking_overlap
BEFORE INSERT OR UPDATE ON Booking
FOR EACH ROW
EXECUTE FUNCTION prevent_booking_overlap();

-- Index on email to speed up queries for login/profile updates
CREATE INDEX idx_member_email ON Member(email);