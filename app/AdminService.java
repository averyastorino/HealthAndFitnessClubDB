import java.sql.*;
import java.util.Scanner;

public class AdminService {
    private Connection conn;
    private Scanner scanner;

    public AdminService(Connection conn) {
        this.conn = conn;
        this.scanner = new Scanner(System.in);
    }

    //Room management
    public void showAdminMenu() {
        while (true) {
            System.out.println("\nAdmin Menu");
            System.out.println("1. Create Session");
            System.out.println("2. Assign Room to Session (Room Booking)");
            System.out.println("3. View All Rooms");
            System.out.println("4. Delete Session");
            System.out.println("5. Back to Main Menu");
            System.out.println("Select an option");
            int choice = scanner.nextInt();
            scanner.nextLine();

            switch (choice) {
                case 1:
                    createSession();
                    break;
                case 2:
                    assignRoomToSession();
                    break;
                case 3:
                    viewAllRooms();
                    break;
                case 4:
                    deleteSession();
                    break;
                case 5:
                    return;
                default:
                    System.out.println("Invalid choice. Please try again.");
            }
        }
    }

    private void createSession() {
        try {
            System.out.print("Session Name: ");
            String name = scanner.nextLine();

            System.out.print("Start Time (YYYY-MM-DD HH:MM:SS)");
            String startTimeStr = scanner.nextLine();
            Timestamp startTime = Timestamp.valueOf(startTimeStr);

            System.out.print("Duration (HH:MM:SS): ");
            String durationStr = scanner.nextLine();
            String[] hms = durationStr.split(":");
            int hours = Integer.parseInt(hms[0]);
            int minutes = Integer.parseInt(hms[1]);
            int seconds = Integer.parseInt(hms[2]);
            
            System.out.print("Capacity: ");
            int capacity = scanner.nextInt();
            scanner.nextLine();

            System.out.print("Trainer ID: ");
            int trainerId = scanner.nextInt();
            scanner.nextLine();

            System.out.print("Room ID: ");
            int roomId = scanner.nextInt();
            scanner.nextLine();

            String sql = "INSERT INTO Session (name, start_time, duration, capacity, trainer_id, room_id )" +
                         "VALUES (?, ?, ?::interval, ?, ?, ?)";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, name);
            ps.setTimestamp(2, startTime);
            ps.setString(3, durationStr);
            ps.setInt(4, capacity);
            ps.setInt(5, trainerId);
            ps.setInt(6, roomId);

            ps.executeUpdate();
            System.out.println("Session create successfully!");
        } catch (SQLException e) {
            System.out.println("Error creating session: " + e.getMessage());
        } catch(IllegalArgumentException e) {
            System.out.println("Invalid date/time format.");
        }
    }

    private void assignRoomToSession() {
        try {
            System.out.print("Session ID to assign room: ");
            int sessionId = scanner.nextInt();
            scanner.nextLine();

            System.out.print("Room ID to assign: ");
            int roomId = scanner.nextInt();
            scanner.nextLine();
            
            //Check for room conflicts
            String conflictSQL = "SELECT 1 FROM Session " +
                                 "WHERE room_id = ? AND start_time < (SELECT start_time + duration::interval FROM Session WHERE session_id = ?)" +
                                 "AND start_time + duration::interval > (SELECT start_time FROM Session WHERE session_id = ?)";
            PreparedStatement conflictCheck = conn.prepareStatement(conflictSQL);
            conflictCheck.setInt(1, roomId);
            conflictCheck.setInt(2, sessionId);
            conflictCheck.setInt(3, sessionId); //check if I need both
            ResultSet rs = conflictCheck.executeQuery();

            if (rs.next()) {
                System.out.println("Error: Room is already booked during this time.");
                return;
            }

            //update session room
            String updateSQL = "UPDATE Session SET room_id = ? WHERE session_id = ?";
            PreparedStatement update = conn.prepareStatement(updateSQL);
            update.setInt(1, roomId);
            update.setInt(2, sessionId);
            update.executeUpdate();

            System.out.println("Room assigned successfully!");

        } catch (SQLException e) {
            System.out.println("Error assigning room: " + e.getMessage());
        }
    }

    private void viewAllRooms() {
        try {
            String sql = "SELECT * FROM Room ORDER BY room_id";
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(sql);

            System.out.println("Rooms");
            while (rs.next()) {
                System.out.printf("ID: %d, Name: %s, Capacity: %d%n", 
                        rs.getInt("room_id"), 
                        rs.getString("name"),
                        rs.getInt("capacity"));
            }
        
        } catch (SQLException e) {
            System.out.println("Error retrieving rooms: " +e.getMessage());
        }
    }

    private void deleteSession() {
        try {
            System.out.print("Session ID to delete: ");
            int sessionId = scanner.nextInt();
            scanner.nextLine();

            String sql = "DELETE FROM Session WHERE session_id = ?";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, sessionId);
            int rows = ps.executeUpdate();

            if (rows > 0) {
                System.out.println("Session deleted successfully!");
            } else {
                System.out.println("No session found with that ID.");
            }
        } catch (SQLException e) {
            System.out.println("Error deleting session: " + e.getMessage());
        }
    }

}