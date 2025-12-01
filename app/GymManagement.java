import java.sql.*;
import java.util.Scanner;

public class GymManagement {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        String url = "jdbc:postgresql://localhost:5432/FitnessClubManagementDB";
        String user = "postgres";
        String password = "myPassword";
        Connection conn = null;
        MemberService memberService = null;

        try {
            // Load the PostgreSQL JDBC driver class
            Class.forName("org.postgresql.Driver");
            //connection to the database
            conn = DriverManager.getConnection(url, user, password);
            // Check if connection is successful
            // If connected, start the interactive menu
            // Otherwise, print an error message
            if (conn != null) {
                System.out.println("Successfully conneted to the database");
            } else {
                System.out.println("Failed to connect to the database");
            }

        
            // Create service objects
            memberService = new MemberService(conn);
            TrainerService trainerService = new TrainerService(conn);
            AdminService adminService = new AdminService(conn);

            //Main menu loop
            while (true) {
                System.out.println("\n---Fitness and Health Club System---");
                System.out.println("1. Register as New Member");
                System.out.println("2. Member Services");
                System.out.println("3. Trainer Services");
                System.out.println("4. Admin Login");
                System.out.println("5. Exit");
                System.out.println("Select an option: ");

                int choice = scanner.nextInt();
                scanner.nextLine();

                switch (choice) {
                    case 1:
                        //Register as a new member
                        memberService.registerMemberInput();
                        break;

                    case 2:
                        //Member login
                        System.out.println("Enter your Member ID: ");
                        int memberId = Integer.parseInt(scanner.nextLine().trim());

                        //call member actions submenu
                        runMemberActions(scanner, memberService, memberId);
                        break;

                    case 3:
                        System.out.print("Enter Trainer ID: ");
                        try {
                            int trainerId = Integer.parseInt(scanner.nextLine().trim());
                            //TrainerService provides trainerMenu(int)
                            trainerService.trainerMenu(trainerId);
                        } catch (NumberFormatException e) {
                            System.out.println("Invalid Trainer ID.");
                        }
                        break;
                    
                    case 4:
                        System.out.print("Enter Admin Password: ");
                        String pass = scanner.nextLine();
                        if ("admin123".equals(pass)) {
                            adminService.showAdminMenu();
                        } else {
                            System.out.println("Incorrect admin password.");
                        }
                        break;

                    case 5:
                        System.out.println("Exiting... Goodbye!");
                        scanner.close();
                        return;
                    
                    default:
                        System.out.println("Invalid option, please try again.");
                        break;
                }
            }

        } catch (ClassNotFoundException | SQLException e) {
            System.out.println("Database connection failed: " + e.getMessage()); 
        } finally {
        // safely close resources
            try {
                if (conn != null && !conn.isClosed()) conn.close();
                scanner.close();
            } catch (SQLException e) {
                System.out.println("Error closing connection: " + e.getMessage());
            }
        }
    }

    private static void runMemberActions(Scanner scanner, MemberService memberService, int memberId) {
        while (true) {
            System.out.println("\n--- Member Actions ---");
            System.out.println("1. Update Profile");
            System.out.println("2. Log Health Metric");
            System.out.println("3. Book a Session");
            System.out.println("4. Back");
            System.out.print("Select an option: ");
            int choice = Integer.parseInt(scanner.nextLine().trim()); 


            switch (choice) {
                case 1:
                    memberService.updateProfile(memberId);
                    break;
                case 2:
                    memberService.logHealthMetricInput(scanner, memberId);
                    break;
                case 3:
                    System.out.println("Session ID to book: ");
                    int sessionId = Integer.parseInt(scanner.nextLine().trim());
                    memberService.bookSession(memberId, sessionId);
                    break;
                case 4:
                    return;
                default:
                    System.out.println("Invalid choice, please try again.");
                    break;

            }
        }
    }
}