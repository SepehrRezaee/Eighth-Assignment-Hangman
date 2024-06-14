package hangman;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class DatabaseManager {
    private static final String URL = "jdbc:postgresql://localhost:5432/hangman";
    private static final String USER = "admin";
    private static final String PASSWORD = "12345";

    private Connection connect() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }

    public void createTables() throws SQLException {
        String createUserTableSQL = "CREATE TABLE IF NOT EXISTS UserInfo ("
                + "Name VARCHAR(100), "
                + "Username VARCHAR(100) PRIMARY KEY, "
                + "Password VARCHAR(100))";

        String createGameTableSQL = "CREATE TABLE IF NOT EXISTS GameInfo ("
                + "GameID SERIAL PRIMARY KEY, "
                + "Username VARCHAR(100), "
                + "Word VARCHAR(100), "
                + "WrongGuesses INTEGER, "
                + "Time INTEGER, "
                + "Win BOOLEAN, "
                + "FOREIGN KEY (Username) REFERENCES UserInfo(Username))";

        try (Connection conn = connect();
             PreparedStatement createUserTable = conn.prepareStatement(createUserTableSQL);
             PreparedStatement createGameTable = conn.prepareStatement(createGameTableSQL)) {

            createUserTable.executeUpdate();
            createGameTable.executeUpdate();
        }
    }

    public boolean addUser(String name, String username, String password) throws SQLException {
        String insertUserSQL = "INSERT INTO UserInfo (Name, Username, Password) VALUES (?, ?, ?)";

        try (Connection conn = connect();
             PreparedStatement preparedStatement = conn.prepareStatement(insertUserSQL)) {
            preparedStatement.setString(1, name);
            preparedStatement.setString(2, username);
            preparedStatement.setString(3, password);

            return preparedStatement.executeUpdate() > 0;
        }
    }

    public boolean validateUser(String username, String password) throws SQLException {
        String queryUserSQL = "SELECT * FROM UserInfo WHERE Username = ? AND Password = ?";

        try (Connection conn = connect();
             PreparedStatement preparedStatement = conn.prepareStatement(queryUserSQL)) {
            preparedStatement.setString(1, username);
            preparedStatement.setString(2, password);

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                return resultSet.next();
            }
        }
    }

    public boolean addGame(String username, String word, int wrongGuesses, int time, boolean win) throws SQLException {
        String insertGameSQL = "INSERT INTO GameInfo (Username, Word, WrongGuesses, Time, Win) VALUES (?, ?, ?, ?, ?)";

        try (Connection conn = connect();
             PreparedStatement preparedStatement = conn.prepareStatement(insertGameSQL)) {
            preparedStatement.setString(1, username);
            preparedStatement.setString(2, word);
            preparedStatement.setInt(3, wrongGuesses);
            preparedStatement.setInt(4, time);
            preparedStatement.setBoolean(5, win);

            return preparedStatement.executeUpdate() > 0;
        }
    }

    public List<String> getGameDetails(String username) throws SQLException {
        String queryGameSQL = "SELECT * FROM GameInfo WHERE Username = ?";
        List<String> gameDetails = new ArrayList<>();

        try (Connection conn = connect();
             PreparedStatement preparedStatement = conn.prepareStatement(queryGameSQL)) {
            preparedStatement.setString(1, username);

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                while (resultSet.next()) {
                    gameDetails.add("GameID: " + resultSet.getInt("GameID") +
                            ", Word: " + resultSet.getString("Word") +
                            ", Wrong Guesses: " + resultSet.getInt("WrongGuesses") +
                            ", Time: " + resultSet.getInt("Time") +
                            ", Win: " + resultSet.getBoolean("Win"));
                }
            }
        }
        return gameDetails;
    }

    public List<String> getLeaderboard() throws SQLException {
        String queryLeaderboardSQL = "SELECT Username, COUNT(*) AS Wins FROM GameInfo WHERE Win = true GROUP BY Username ORDER BY Wins DESC";
        List<String> leaderboard = new ArrayList<>();

        try (Connection conn = connect();
             PreparedStatement preparedStatement = conn.prepareStatement(queryLeaderboardSQL);
             ResultSet resultSet = preparedStatement.executeQuery()) {

            while (resultSet.next()) {
                leaderboard.add("Username: " + resultSet.getString("Username") +
                        ", Wins: " + resultSet.getInt("Wins"));
            }
        }
        return leaderboard;
    }
}
