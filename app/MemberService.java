import java.sql.*;
import java.util.Scanner;

public class MemberService {
    private Connection conn;

    public MemberService(Connection conn) {
        this.conn = conn;
    }

    public void registerMember(String name, String email, String phone, double weightGoal, Date dateOfBirth, String gender) {
        try {
            //insert into Member table
            String memberSql = "INSERT INTO Member (name, email, weight_goal, date_of_birth, gender) VALUES (?, ?, ?, ?, ?) RETURNING member_id";
            PreparedStatement ps = conn.prepareStatement(memberSql);
            ps.setString(1, name);
            ps.setString(2, email);
            ps.setDouble(3, weightGoal);
            ps.setDate(4, dateOfBirth);
            ps.setString(5, gender);

            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                int memberID = rs.getInt("member_id");
                System.out.println("Member registered successfully with ID: " + memberID);

                //insert phone number
                String phoneSql = "INSERT INTO Member_Phone (member_id, phone_number) VALUES (?, ?)";
                PreparedStatement psPhone = conn.prepareStatement(phoneSql);
                psPhone.setInt(1, memberID);
                psPhone.setString(2, phone);
                psPhone.executeUpdate();
            }
        } catch(SQLException e) {
            System.out.println("Error registering member: " + e.getMessage());

        }
    }

    public void registerMemberInput() {
        Scanner scanner = new Scanner(System.in);

        try {
            System.out.print("Name: ");
            String name = scanner.nextLine();

            System.out.print("Email: ");
            String email = scanner.nextLine();

            System.out.print("Phone Number: ");
            String phone = scanner.nextLine();

            System.out.print("Weight Goal (number only): ");
            String wg = scanner.nextLine().trim();
            Double weightGoal = wg.isEmpty() ? null : Double.parseDouble(wg);

            System.out.print("Date of Birth (YYYY-MM-DD or blank): ");
            String dobStr = scanner.nextLine().trim();
            Date dob = dobStr.isEmpty() ? null : Date.valueOf(dobStr);

            System.out.print("Gender: ");
            String gender = scanner.nextLine();

            // Call existing DB insert method
            registerMember(name, email, phone, weightGoal, dob, gender);

        } catch (Exception e) {
            System.out.println("Error registering member: " + e.getMessage());
        }
    }

    public void updateProfile(int memberID) {
        Scanner scanner = new Scanner(System.in);
        boolean keepRunning = true;

        while (keepRunning) {
            System.out.println("\n--- Profile Management ---");
            System.out.println("1. Update Name");
            System.out.println("2. Update Email");
            System.out.println("3. Update Weight Goal");
            System.out.println("4. Update Date of Birth");
            System.out.println("5. Update Gender");
            System.out.println("6. Add Phone Number");
            System.out.println("7. Delete Phone Number");
            System.out.println("8. Exit Profile Management");
            System.out.print("Choose an option: ");

            int choice = Integer.parseInt(scanner.nextLine());

            try {
                switch (choice) {
                    case 1: //update name
                        System.out.print("Enter new name: ");
                        String newName = scanner.nextLine();
                        updateMemberField(memberID, "name", newName);
                        break;
                    case 2: //update email
                        System.out.print("Enter new email: ");
                        String newEmail = scanner.nextLine();
                        updateMemberField(memberID, "email", newEmail);
                        break;
                    case 3: // Update Weight Goal
                        System.out.print("Enter new weight goal (number only): ");
                        double newGoal = Double.parseDouble(scanner.nextLine());
                        updateMemberField(memberID, "weight_goal", newGoal);
                        break;
                    case 4: // Update Date of Birth
                        System.out.print("Enter new date of birth (YYYY-MM-DD): ");
                        String newDOB = scanner.nextLine();
                        updateMemberField(memberID, "date_of_birth", Date.valueOf(newDOB));
                        break;
                    case 5: // Update Gender
                        System.out.print("Enter new gender: ");
                        String newGender = scanner.nextLine();
                        updateMemberField(memberID, "gender", newGender);
                        break;
                    case 6: // Add Phone Number
                        System.out.print("Enter phone number to add: ");
                        String phoneToAdd = scanner.nextLine();
                        addPhoneNumber(memberID, phoneToAdd);
                        break;
                    case 7: // Delete Phone Number
                        System.out.print("Enter phone number to delete: ");
                        String phoneToDelete = scanner.nextLine();
                        deletePhoneNumber(memberID, phoneToDelete);
                        break;
                    case 8:
                        keepRunning = false;
                        break;
                    default:
                        System.out.println("Invalid choice.");
                }
            } catch (SQLException e) {
                System.out.println("Database error: " + e.getMessage());
            }
        }
    }
    
    private void updateMemberField(int memberID, String field, Object value) throws SQLException {
        String sql = "UPDATE Member SET " + field + " = ? WHERE member_id = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setObject(1, value);
            pstmt.setInt(2, memberID);
            int rows = pstmt.executeUpdate();
            if (rows > 0) System.out.println(field + " updated successfully.");
            else System.out.println("Update failed.");
        }
    }

    private void addPhoneNumber(int memberID, String phone) throws SQLException {
        String sql = "INSERT INTO Member_Phone(member_id, phone_number) VALUES(?, ?)";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, memberID);
            pstmt.setString(2, phone);
            pstmt.executeUpdate();
            System.out.println("Phone number added successfully.");
        }
    }

    private void deletePhoneNumber(int memberID, String phone) throws SQLException {
        String sql = "DELETE FROM Member_Phone WHERE member_id = ? AND phone_number = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, memberID);
            pstmt.setString(2, phone);
            int rows = pstmt.executeUpdate();
            if (rows > 0) System.out.println("Phone number deleted successfully.");
            else System.out.println("Phone number not found.");
        }
    }

    public void logHealthMetric(int memberID,  Date metricDate, double weight, double bodyFat, int heartRate) {
        try {
            String sql = "INSERT INTO Health_Metric (member_id, metric_date, weight, body_fat_percentage, heart_rate) VALUES (?, ?, ?, ?, ?)";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, memberID);
            ps.setDate(2, metricDate);
            ps.setDouble(3, weight);
            ps.setDouble(4, bodyFat);
            ps.setInt(5, heartRate);
            ps.executeUpdate();
            System.out.println("Health metric logged successfully!");
        } catch(SQLException e) {
            System.out.println("Error logging health metric: " + e.getMessage());
        }
    }

    public void logHealthMetricInput(Scanner scanner, int memberID) {
        try {
            System.out.print("Date (YYYY-MM-DD): ");
            Date metricDate = Date.valueOf(scanner.nextLine().trim());

            System.out.print("Weight: ");
            double weight = Double.parseDouble(scanner.nextLine().trim());

            System.out.print("Body Fat %: ");
            double bodyFat = Double.parseDouble(scanner.nextLine().trim());

            System.out.print("Heart Rate (int): ");
            int heartRate = Integer.parseInt(scanner.nextLine().trim());

            logHealthMetric(memberID, metricDate, weight, bodyFat, heartRate);
        } catch (IllegalArgumentException e) {
            System.out.println("Invalid input format: " + e.getMessage());
        }
    }


    public void bookSession(int memberID, int sessionId) {
        try {
            //get session start time
            String getTimeSql = "SELECT start_time FROM Session WHERE session_id = ?";
            PreparedStatement ps1 = conn.prepareStatement(getTimeSql);
            ps1.setInt(1, sessionId);
            ResultSet rs = ps1.executeQuery();

            if (!rs.next()) {
                System.out.println("Session not found.");
                return;
            }

            Timestamp sessionStartTime = rs.getTimestamp("start_time");

            String sql = "INSERT INTO Booking (member_id, session_id, booking_time, status) VALUES (?, ?, ?, 'Booked')";
            PreparedStatement ps2 = conn.prepareStatement(sql);
            ps2.setInt(1, memberID);
            ps2.setInt(2, sessionId);
            ps2.setTimestamp(3, sessionStartTime);

            ps2.executeUpdate();
            System.out.println("Session booked successfully!");

        } catch (SQLException e) {
            System.out.println("Error booking session: " + e.getMessage());
        }
    }
}