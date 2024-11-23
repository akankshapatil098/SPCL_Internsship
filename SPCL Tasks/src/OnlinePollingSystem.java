import java.sql.*;
import java.util.*;

public class OnlinePollingSystem {
    private static final String URL = "jdbc:mysql://localhost:3306/OnlinePollingSystem";
    private static final String USER = "root";
    private static final String PASSWORD = "Admin@123";

    public static void main(String[] args) {
        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD)) {
            Scanner scanner = new Scanner(System.in);

            while (true) {
                System.out.println("\n--- Online Polling System ---");
                System.out.println("1. Create Poll");
                System.out.println("2. Vote in a Poll");
                System.out.println("3. View Poll Results");
                System.out.println("4. Exit");
                System.out.print("Choose an option: ");
                int choice = scanner.nextInt();
                scanner.nextLine(); // Consume newline

                switch (choice) {
                    case 1:
                        createPoll(conn, scanner);
                        break;
                    case 2:
                        voteInPoll(conn, scanner);
                        break;
                    case 3:
                        viewPollResults(conn, scanner);
                        break;
                    case 4:
                        System.out.println("Thank you for using the Online Polling System!");
                        return;
                    default:
                        System.out.println("Invalid choice. Please try again.");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static void createPoll(Connection conn, Scanner scanner) throws SQLException {
        System.out.print("Enter the poll question: ");
        String question = scanner.nextLine();

        // Insert poll into Polls table
        String insertPollSQL = "INSERT INTO Polls (Question) VALUES (?)";
        try (PreparedStatement stmt = conn.prepareStatement(insertPollSQL, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, question);
            stmt.executeUpdate();

            ResultSet rs = stmt.getGeneratedKeys();
            if (rs.next()) {
                int pollID = rs.getInt(1);
                System.out.println("Poll created successfully! Poll ID: " + pollID);

                System.out.println("Enter poll options (type 'done' to finish):");
                while (true) {
                    String option = scanner.nextLine();
                    if (option.equalsIgnoreCase("done")) break;

                    // Insert options into Options table
                    String insertOptionSQL = "INSERT INTO Options (PollID, OptionText) VALUES (?, ?)";
                    try (PreparedStatement optionStmt = conn.prepareStatement(insertOptionSQL)) {
                        optionStmt.setInt(1, pollID);
                        optionStmt.setString(2, option);
                        optionStmt.executeUpdate();
                    }
                }
                System.out.println("Poll options added successfully!");
            }
        }
    }

    private static void voteInPoll(Connection conn, Scanner scanner) throws SQLException {
        System.out.print("Enter the Poll ID you want to vote in: ");
        int pollID = scanner.nextInt();
        scanner.nextLine(); // Consume newline

        // Retrieve the poll question
        String getQuestionSQL = "SELECT Question FROM Polls WHERE PollID = ?";
        try (PreparedStatement stmt = conn.prepareStatement(getQuestionSQL)) {
            stmt.setInt(1, pollID);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                String pollQuestion = rs.getString("Question");
                System.out.println("\nPoll Question: " + pollQuestion);  // Display the poll question
            } else {
                System.out.println("Poll ID not found.");
                return;
            }
        }

        // Retrieve poll options
        String getOptionsSQL = "SELECT OptionID, OptionText FROM Options WHERE PollID = ?";
        try (PreparedStatement stmt = conn.prepareStatement(getOptionsSQL)) {
            stmt.setInt(1, pollID);
            ResultSet rs = stmt.executeQuery();

            System.out.println("Options for Poll ID " + pollID + ":");
            Map<Integer, Integer> optionMap = new HashMap<>();
            while (rs.next()) {
                int optionID = rs.getInt("OptionID");
                String optionText = rs.getString("OptionText");
                optionMap.put(optionMap.size() + 1, optionID);
                System.out.println(optionMap.size() + ". " + optionText);
            }

            // Ensure options exist for voting
            if (optionMap.isEmpty()) {
                System.out.println("No options available for this poll.");
                return;
            }

            // User voting process
            System.out.print("Enter your User ID: ");
            String userID = scanner.nextLine();

            System.out.print("Enter the option number you want to vote for: ");
            int optionNumber = scanner.nextInt();
            scanner.nextLine(); // Consume newline

            if (!optionMap.containsKey(optionNumber)) {
                System.out.println("Invalid option number. Please try again.");
                return;
            }

            int optionID = optionMap.get(optionNumber);

            // Insert vote into Votes table and update vote count
            conn.setAutoCommit(false);
            try {
                String checkVoteSQL = "SELECT * FROM Votes WHERE PollID = ? AND UserID = ?";
                try (PreparedStatement checkStmt = conn.prepareStatement(checkVoteSQL)) {
                    checkStmt.setInt(1, pollID);
                    checkStmt.setString(2, userID);
                    ResultSet voteCheck = checkStmt.executeQuery();

                    if (voteCheck.next()) {
                        System.out.println("You have already voted in this poll.");
                        return;
                    }
                }

                String insertVoteSQL = "INSERT INTO Votes (PollID, UserID) VALUES (?, ?)";
                try (PreparedStatement voteStmt = conn.prepareStatement(insertVoteSQL)) {
                    voteStmt.setInt(1, pollID);
                    voteStmt.setString(2, userID);
                    voteStmt.executeUpdate();
                }

                String updateVoteCountSQL = "UPDATE Options SET VoteCount = VoteCount + 1 WHERE OptionID = ?";
                try (PreparedStatement updateStmt = conn.prepareStatement(updateVoteCountSQL)) {
                    updateStmt.setInt(1, optionID);
                    updateStmt.executeUpdate();
                }

                conn.commit();
                System.out.println("Vote cast successfully!");
            } catch (SQLException e) {
                conn.rollback();
                throw e;
            } finally {
                conn.setAutoCommit(true);
            }
        }
    }


    private static void viewPollResults(Connection conn, Scanner scanner) throws SQLException {
        System.out.print("Enter the Poll ID to view results: ");
        int pollID = scanner.nextInt();
        scanner.nextLine(); // Consume newline

        // Retrieve the poll question
        String getQuestionSQL = "SELECT Question FROM Polls WHERE PollID = ?";
        try (PreparedStatement stmt = conn.prepareStatement(getQuestionSQL)) {
            stmt.setInt(1, pollID);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                String pollQuestion = rs.getString("Question");
                System.out.println("\nResults for Poll ID " + pollID + ":");
                System.out.println("Poll Question: " + pollQuestion);  // Display the question
            } else {
                System.out.println("Poll ID not found.");
                return;
            }
        }

        // Retrieve poll options and vote counts
        String getResultsSQL = "SELECT OptionText, VoteCount FROM Options WHERE PollID = ?";
        try (PreparedStatement stmt = conn.prepareStatement(getResultsSQL)) {
            stmt.setInt(1, pollID);
            ResultSet rs = stmt.executeQuery();

            int totalVotes = 0;
            List<String> results = new ArrayList<>();
            while (rs.next()) {
                String option = rs.getString("OptionText");
                int voteCount = rs.getInt("VoteCount");
                totalVotes += voteCount;
                results.add(option + " - Votes: " + voteCount);
            }

            for (String result : results) {
                System.out.println(result);
            }
            System.out.println("Total Votes: " + totalVotes);
        }
    }
}
