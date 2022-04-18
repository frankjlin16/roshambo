package roshambo;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javafx.stage.Stage;

/**
 * Roshambo game client.
 *
 * A client that connects to a server and plays a game of Roshambo
 * 
 * @author Frank Lin
 * @version 1.0
 * @see RPSServer
 */
public class RPSClient extends Application {

    /** Status indicating player 1 */
    public static int PLAYER1 = 1; // Indicate player 1
    /** Status indicating player 2 */
    public static int PLAYER2 = 2; // Indicate player 2
    /** Status indicating player 1 won */
    public static int PLAYER1_WON = 3; // Indicate player 1 won
    /** Status indicating player 2 won */
    public static int PLAYER2_WON = 4; // Indicate player 2 won
    /** Status indicating a draw */
    public static int DRAW = 5; // Indicate a draw

    // Core variables
    private Choice myChoice;
    private Choice opponentChoice;
    private String choices[] = { "Rock", "Paper", "Scissors" };
    private boolean isActive = true;

    // IO variables
    private DataInputStream fromServer;
    private DataOutputStream toServer;
    private String host = "localhost";

    // GUI variables
    private Label message = new Label();
    private StackPane opponentStack;
    private Text opponentText;
    private Label playerLabel = new Label();

    /** Construct the GUI and start the game client 
     * 
     * @param stage the stage to display the client
     * @throws IOException if the client cannot connect to the server
     *
    */
    @Override
    public void start(Stage stage) throws IOException {

        // Create border pane
        GridPane bottomGrid = new GridPane();

        // Create the bottom choice buttons
        for (int i = 0; i < 3; i++) {
            StackPane stack = new StackPane();
            Text text = new Text(choices[i]);
            Choice choice = new Choice(i);
            stack.getChildren().addAll(text, choice);
            GridPane.setMargin(stack, new Insets(10));
            bottomGrid.add(stack, i, 1);
        }

        // Align grids center
        bottomGrid.setAlignment(Pos.CENTER);

        // Create border pane
        BorderPane border = new BorderPane();
        border.setBottom(bottomGrid);
        BorderPane.setMargin(bottomGrid, new Insets(10));
        BorderPane.setAlignment(bottomGrid, Pos.CENTER);

        // Add oppenent rectangle
        opponentStack = new StackPane();
        opponentText = new Text("- - -");
        opponentChoice = new Choice(0);
        opponentStack.getChildren().addAll(opponentText, opponentChoice);
        border.setTop(opponentStack);
        BorderPane.setMargin(opponentStack, new Insets(20));
        BorderPane.setAlignment(opponentStack, Pos.CENTER);

        // Create the message in the center
        border.setCenter(message);

        // Add player label
        border.setLeft(playerLabel);

        // Add button to side
        Button confirmButton = new Button("Confirm");
        confirmButton.setOnAction(e -> handleButtonClick());
        BorderPane.setAlignment(confirmButton, Pos.BOTTOM_LEFT);
        BorderPane.setMargin(confirmButton, new Insets(0, 5, 0, 0));
        border.setRight(confirmButton);

        Scene scene = new Scene(border, 600, 400);
        stage.setTitle("RSP Client");
        stage.setScene(scene);
        stage.show();

        // Connect to server
        connectToServer();
    }

    /** Handles the button click */
    private void handleButtonClick() {
        // Send choice to server
        try {
            sendChoice();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /** Connect to server */
    private void connectToServer() {
        try {
            Socket socket = new Socket(host, 8000);
            fromServer = new DataInputStream(socket.getInputStream());
            toServer = new DataOutputStream(socket.getOutputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Start thread to listen for server messages
        new Thread(() -> {
            try {
                int status = fromServer.readInt();
                if (status == PLAYER1) {
                    Platform.runLater(() -> playerLabel.setText("Player 1"));
                    Platform.runLater(() -> message.setText("Waiting for opponent..."));
                    fromServer.readInt(); // Read opponent choice
                    Platform.runLater(() -> message.setText("Opponent connected, game started!"));
                } else if (status == PLAYER2) {
                    Platform.runLater(() -> playerLabel.setText("Player 2"));
                    Platform.runLater(() -> message.setText("Connected, game started!"));
                }

                // Game loop
                while (isActive) {
                    receiveFromServer();
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }

    /** Send player choice to server */
    private void sendChoice() throws IOException {
        toServer.writeInt(myChoice.getChoice());
    }

    /** Receive opponent choice from server */
    private void receiveChoice() throws IOException {
        opponentChoice = new Choice(fromServer.readInt());
        Platform.runLater(() -> opponentText.setText(choices[opponentChoice.getChoice()]));
    }

    /** Receive status from server */
    private void receiveFromServer() throws IOException {
        // Receive game status
        int status = fromServer.readInt();

        receiveChoice();

        if (status == PLAYER1_WON) {
            Platform.runLater(() -> message.setText("Player 1 won!"));
            isActive = false;
        } else if (status == PLAYER2_WON) {
            Platform.runLater(() -> message.setText("Player 2 won!"));
            isActive = false;
        } else if (status == DRAW) {
            Platform.runLater(() -> message.setText("Draw!"));
            isActive = false;
        }

    }

    /** Main method
     * @param args command line arguments
     */
    public static void main(String[] args) {
        launch();
    }

    /** Choice class: describing the choices */
    class Choice extends Rectangle {
        private int choice;

        /** Constructor */
        public Choice(int choice) {
            super(100, 60, Color.TRANSPARENT);
            this.setStroke(Color.BLACK);
            this.choice = choice;
            this.setOnMouseClicked(e -> handleMouseClicked());
        }

        /** Get choice */
        public int getChoice() {
            return choice;
        }

        /** Set choice */
        public void setChoice(int choice) {
            this.choice = choice;
        }

        /** Outline the choice when selected */
        protected void outline(int x) {
            if (x == 0) {
                this.setStroke(Color.BLACK);
            } else {
                this.setStroke(Color.RED);
            }
        }

        /** Handle mouse click */
        private void handleMouseClicked() {
            if (myChoice != null) {
                myChoice.outline(0);
            }
            myChoice = this;
            outline(1);
        }
    }
}