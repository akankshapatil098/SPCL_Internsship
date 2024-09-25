import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Scanner;
import java.util.Random;
import java.sql.ResultSet;

public class Task1_OnlineReservationSystem {

        private static final int min = 1000;
        private static final int max = 9999;

        public static class User {
            private String username;
            private String password;

            Scanner sc = new Scanner(System.in);

            public String getUserName() {
                System.out.println("Enter Username: ");
                username = sc.nextLine();
                return username;
            }

            public String getPassword() {
                System.out.println("Enter Password: ");
                password = sc.nextLine();
                return password;
            }

            // Method to authenticate user credentials
            public boolean authenticateUser(Connection con, String username, String password) {
                boolean isAuthenticated = false;
                String query = "SELECT * FROM users WHERE username = ? AND password = ?";
                try (PreparedStatement ps = con.prepareStatement(query)) {
                    ps.setString(1, username);
                    ps.setString(2, password);
                    ResultSet rs = ps.executeQuery();

                    if (rs.next()) {
                        System.out.println("Login successful!");
                        isAuthenticated = true;
                    } else {
                        System.out.println("Invalid username or password.");
                    }
                } catch (SQLException e) {
                    System.out.println("SQL Exception: " + e.getMessage());
                }
                return isAuthenticated;
            }
        }

        public static class PnrRecord {
            private int pnrNumber;
            private String passengerName;
            private String trainNumber;
            private String classType;
            private String journeyDate;
            private String start;
            private String end;

            Scanner sc = new Scanner(System.in);

            public int getPnrNumber() {
                Random r = new Random();
                pnrNumber = r.nextInt(max + min);
                return pnrNumber;
            }

            public String getPassengerName() {
                System.out.println("Enter Passenger Name: ");
                passengerName = sc.nextLine();
                return passengerName;
            }

            public String getTrainNumber() {
                System.out.println("Enter Train Number: ");
                trainNumber = sc.nextLine();
                return trainNumber;
            }

            public String getClassType() {
                System.out.println("Enter Class Type: ");
                classType = sc.nextLine();
                return classType;
            }

            public String getJourneyDate() {
                System.out.println("Enter Journey Date (YYYY-MM-DD): ");
                journeyDate = sc.nextLine();
                return journeyDate;
            }

            public String getStart() {
                System.out.println("Enter Starting Place: ");
                start = sc.nextLine();
                return start;
            }

            public String getEnd() {
                System.out.println("Enter Destination: ");
                end = sc.nextLine();
                return end;
            }
        }

        public static void main(String[] args) {
            Scanner sc = new Scanner(System.in);
            User obj = new User();
            boolean authenticated = false;
            String username = null; // Store the username of the logged-in user

            try {
                // Load JDBC driver1
                Class.forName("com.mysql.cj.jdbc.Driver");

                // Prompt for database username and password to connect
                String dbUsername = "root"; // Replace with your DB username
                String dbPassword = "Admin@123"; // Replace with your DB password

                try (Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/Akanksha", dbUsername, dbPassword)) {
                    System.out.println("Connected to the database!");

                    // Authenticate user
                    while (!authenticated) {
                        username = obj.getUserName();
                        String password = obj.getPassword();
                        authenticated = obj.authenticateUser(con, username, password);

                        if (!authenticated) {
                            System.out.println("Please try logging in again.");
                        }
                    }

                    boolean p = true;
                    while (p) {
                        String insertQuery = "INSERT INTO passengerDetails (pnrNumber, passengerName, trainNumber, classType, journeyDate, startLocation, endLocation, username) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
                        String deleteQuery = "DELETE FROM passengerDetails WHERE pnrNumber = ?";
                        String showQuery = "SELECT * FROM passengerDetails WHERE username = ?";
                        String showQuery1 = "SELECT * FROM passengerDetails WHERE pnrNumber = ?";

                        System.out.println("Please Enter Your Choice: ");
                        System.out.println("1. Insert Record\n2. Delete Record\n3. Show All Passenger Details\n4. Get Details by PNR Number\n5. Exit");

                        int choice = sc.nextInt();
                        sc.nextLine(); // Consume newline

                        switch (choice) {
                            case 1:
                                PnrRecord p1 = new PnrRecord();
                                int pnrNum = p1.getPnrNumber();
                                String pname = p1.getPassengerName();
                                String tno = p1.getTrainNumber();
                                String classtype = p1.getClassType();
                                String jdate = p1.getJourneyDate();
                                String startPlace = p1.getStart(); // This should match the correct column name in the DB
                                String endPlace = p1.getEnd();     // This should match the correct column name in the DB

                                try (PreparedStatement ps1 = con.prepareStatement(insertQuery)) {
                                    ps1.setInt(1, pnrNum);
                                    ps1.setString(2, pname);
                                    ps1.setString(3, tno);
                                    ps1.setString(4, classtype);
                                    ps1.setString(5, jdate);
                                    ps1.setString(6, startPlace); // Ensure the column name matches
                                    ps1.setString(7, endPlace);   // Ensure the column name matches
                                    ps1.setString(8, username);   // Ensure username is also passed in
                                    int rowsAffected = ps1.executeUpdate();
                                    if (rowsAffected > 0) {
                                        System.out.println("Passenger details added successfully!");
                                    } else {
                                        System.out.println("Failed to add passenger details.");
                                    }
                                } catch (SQLException e) {
                                    System.out.println("SQLException: " + e.getMessage());
                                }

                                break;

                            case 2:
                                System.out.println("Enter PNR Number to delete the record: ");
                                int pnrToDelete = sc.nextInt();

                                try (PreparedStatement ps2 = con.prepareStatement(deleteQuery)) {
                                    ps2.setInt(1, pnrToDelete);
                                    int rowsAffected = ps2.executeUpdate();

                                    if (rowsAffected > 0) {
                                        System.out.println("Passenger details deleted successfully.");
                                    } else {
                                        System.out.println("Failed to delete passenger details.");
                                    }
                                } catch (SQLException e) {
                                    System.out.println("SQLException: " + e.getMessage());
                                }
                                break;

                            case 3:
                                try (PreparedStatement ps3 = con.prepareStatement(showQuery)) {
                                    // Print the username to check if it's correctly captured
                                    System.out.println("Username for query: " + username);

                                    // Set the username in the query to fetch only the user's records
                                    ps3.setString(1, username);
                                    ResultSet rs3 = ps3.executeQuery();

                                    // Check if any records are returned
                                    boolean recordFound = false;

                                    while (rs3.next()) {
                                        recordFound = true;
                                        System.out.println("PNR Number: " + rs3.getInt(1));
                                        System.out.println("Passenger Name: " + rs3.getString(2));
                                        System.out.println("Train Number: " + rs3.getString(3));
                                        System.out.println("Class Type: " + rs3.getString(4));
                                        System.out.println("Journey Date: " + rs3.getString(5));
                                        System.out.println("From: " + rs3.getString(6));
                                        System.out.println("To: " + rs3.getString(7));
                                        System.out.println();
                                    }

                                    if (!recordFound) {
                                        System.out.println("No records found for the user: " + username);
                                    }
                                } catch (SQLException e) {
                                    System.out.println("SQL Exception: " + e.getMessage());
                                }
                                break;

                            case 4:
                                System.out.println("Enter PNR Number: ");
                                int pnrToShow = sc.nextInt();

                                try (PreparedStatement ps4 = con.prepareStatement(showQuery1)) {
                                    ps4.setInt(1, pnrToShow);
                                    ResultSet rs4 = ps4.executeQuery();

                                    if (rs4.next()) {
                                        System.out.println("PNR Number: " + rs4.getInt(1));
                                        System.out.println("Passenger Name: " + rs4.getString(2));
                                        System.out.println("Train Number: " + rs4.getString(3));
                                        System.out.println("Class Type: " + rs4.getString(4));
                                        System.out.println("Journey Date: " + rs4.getString(5));
                                        System.out.println("From: " + rs4.getString(6));
                                        System.out.println("To: " + rs4.getString(7));
                                    } else {
                                        System.out.println("No record found with that PNR number.");
                                    }
                                } catch (SQLException e) {
                                    System.out.println("SQL Exception: " + e.getMessage());
                                }
                                break;

                            case 5:
                                System.out.println("Exiting the program - Thank you!");
                                p = false;
                                break;

                            default:
                                System.out.println("Invalid choice, please try again.");
                        }
                    }

                } catch (SQLException e) {
                    System.out.println("SQL Exception: " + e.getMessage());
                }

            } catch (ClassNotFoundException e) {
                System.out.println("JDBC Driver not found.");
            }
        }
    }
