import java.sql.*;
import java.util.Scanner;

public class TrainerService {
    private Connection conn;

    public TrainerService(Connection conn) {
        this.conn = conn;
    }

    public void setAvailability(int trainerId, Date date, Time start_time, Time end_time) {
        try {
            //check for overlapping availability
            String checkOverlap = "SELECT * FROM Trainer_availability " + 
                                  "WHERE trainer_id = ? AND availability_date = ? " + 
                                  "AND NOT (end_time <= ? OR start_time >= ?)";
            
            PreparedStatement psCheck = conn.prepareStatement(checkOverlap);
            psCheck.setInt(1, trainerId);
            psCheck.setDate(2, date);
            psCheck.setTime(3, start_time);
            psCheck.setTime(4, end_time);
            ResultSet rs = psCheck.executeQuery();

            if (rs.next()) {
                System.out.println("Error: Overlapping availability exists. ");
                return;
            }

            //Insert availability
            String sql = "INSERT INTO Trainer_Availability (trainer_id, availability_date, start_time, end_time)" +
                         "VALUES (?, ?, ?, ?)";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, trainerId);
            ps.setDate(2, date);
            ps.setTime(3, start_time);
            ps.setTime(4, end_time);

            ps.executeUpdate();
            System.out.println("Availability set successfully");
        } catch (SQLException e) {
            System.out.println("Error setting availability: " + e.getMessage());
        }
    }

    public void viewSchedule(int trainerId) {
        try {
            String sql = "SELECT s.session_id, s.name, s.start_time, s.duration, r.name AS room_name " +
                         "FROM Session s  " +
                         "JOIN Room r ON s.room_id = r.room_id " +
                         "WHERE s.trainer_id = ? " +
                         "ORDER BY s.start_time";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, trainerId);
            ResultSet rs = ps.executeQuery();

            System.out.println("Your upcoming sessions: ");
            while (rs.next()) {
                int sessionId = rs.getInt("session_id");
                String name = rs.getString("name");
                Timestamp startTime = rs.getTimestamp("start_time");
                String duration = rs.getString("duration");
                String roomName = rs.getString("room_name");

                System.out.printf("Session ID: %d, Name: %s, Start: %s, Duration %s, Room: %s%n",
                                    sessionId, name, startTime, duration, roomName);
            } 
        } catch(SQLException e) {
                System.out.println("Error retrieving schedule: " + e.getMessage());
        }
    }

    public void trainerMenu(int trainerId) {
        Scanner sc = new Scanner(System.in);
        while(true) {
            System.out.println("\nTrainer Menu:");
            System.out.println("1. Set Availability");
            System.out.println("2. View Schedule");
            System.out.println("3. Exit");
            System.out.println("Choose an option: ");
            int choice = sc.nextInt();
            sc.nextLine();

                switch(choice) {
                    case 1:
                        Date date = null;
                        Time startTime = null;
                        Time endTime = null;

                        // Get valid date
                        while (date == null) {
                            System.out.print("Enter date (YYYY-MM-DD): ");
                            try {
                                date = Date.valueOf(sc.nextLine().trim());
                            } catch (IllegalArgumentException e) {
                                System.out.println("Invalid date format. Try again.");
                            }
                        }

                        // Get valid start time
                        while (startTime == null) {
                            System.out.print("Enter start time (HH:MM:SS): ");
                            try {
                                startTime = Time.valueOf(sc.nextLine().trim());
                            } catch (IllegalArgumentException e) {
                                System.out.println("Invalid time format. Try again.");
                            }
                        }

                        // Get valid end time
                        while (endTime == null) {
                            System.out.print("Enter end time (HH:MM:SS): ");
                            try {
                                endTime = Time.valueOf(sc.nextLine().trim());
                            } catch (IllegalArgumentException e) {
                                System.out.println("Invalid time format. Try again.");
                            }
                        }

                        // Set availability
                        setAvailability(trainerId, date, startTime, endTime);
                        break;

                    case 2:
                        viewSchedule(trainerId);
                        break;
                    case 3:
                        System.out.println("Exiting Trainer Menu.");
                        return;
                    default:
                        System.out.println("Invalid choice.");
                }
        }
    }
}