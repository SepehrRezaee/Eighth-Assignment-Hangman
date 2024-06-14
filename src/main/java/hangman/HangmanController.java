package hangman;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;

import java.sql.SQLException;
import java.util.List;
import java.util.Random;

public class HangmanController {
    @FXML
    private TextField usernameField;
    @FXML
    private PasswordField passwordField;
    @FXML
    private TextField nameField;
    @FXML
    private Button loginButton;
    @FXML
    private Button signupButton;
    @FXML
    private Button startGameButton;
    @FXML
    private Button viewGamesButton;
    @FXML
    private Button viewLeaderboardButton;
    @FXML
    private Label messageLabel;
    @FXML
    private GridPane hangmanPane;

    private String username;
    private DatabaseManager dbManager = new DatabaseManager();
    private String currentWord;
    private int wrongGuesses;
    private long startTime;

    @FXML
    private void initialize() {
        try {
            dbManager.createTables();
        } catch (SQLException e) {
            messageLabel.setText("Error initializing database: " + e.getMessage());
        }
    }

    @FXML
    private void handleLogin(ActionEvent event) {
        try {
            String username = usernameField.getText();
            String password = passwordField.getText();
            if (dbManager.validateUser(username, password)) {
                this.username = username;
                messageLabel.setText("Login successful!");
            } else {
                messageLabel.setText("Invalid username or password.");
            }
        } catch (SQLException e) {
            messageLabel.setText("Error logging in: " + e.getMessage());
        }
    }

    @FXML
    private void handleSignup(ActionEvent event) {
        try {
            String name = nameField.getText();
            String username = usernameField.getText();
            String password = passwordField.getText();
            if (dbManager.addUser(name, username, password)) {
                messageLabel.setText("Signup successful!");
            } else {
                messageLabel.setText("Signup failed. Username may already exist.");
            }
        } catch (SQLException e) {
            messageLabel.setText("Error signing up: " + e.getMessage());
        }
    }

    @FXML
    private void handleStartGame(ActionEvent event) {
        startNewGame();
    }

    @FXML
    private void handleViewGames(ActionEvent event) {
        try {
            List<String> gameDetails = dbManager.getGameDetails(username);
            messageLabel.setText(String.join("\n", gameDetails));
        } catch (SQLException e) {
            messageLabel.setText("Error retrieving game details: " + e.getMessage());
        }
    }

    @FXML
    private void handleViewLeaderboard(ActionEvent event) {
        try {
            List<String> leaderboard = dbManager.getLeaderboard();
            messageLabel.setText(String.join("\n", leaderboard));
        } catch (SQLException e) {
            messageLabel.setText("Error retrieving leaderboard: " + e.getMessage());
        }
    }

    private void startNewGame() {
        // For simplicity, using a fixed word here. In actual implementation, use the API.
        String[] words = {"JAVA", "PROGRAMMING", "HANGMAN"};
        currentWord = words[new Random().nextInt(words.length)];
        wrongGuesses = 0;
        startTime = System.currentTimeMillis();
        // Reset hangman UI
        resetHangmanUI();
        messageLabel.setText("New game started. Good luck!");
    }

    private void resetHangmanUI() {
        // Reset the UI components for the hangman game
        hangmanPane.getChildren().clear();
    }

    // Other methods for handling game logic, such as checking guesses, updating the UI, etc.
}
