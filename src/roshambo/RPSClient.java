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

public class RPSClient extends Application {

    // Status codes
    public static int PLAYER1 = 1; // Indicate player 1
    public static int PLAYER2 = 2; // Indicate player 2
    public static int PLAYER1_WON = 3; // Indicate player 1 won
    public static int PLAYER2_WON = 4; // Indicate player 2 won
    public static int DRAW = 5; // Indicate a draw

    // Core variables
    Choice myChoice;
    Choice opponentChoice;
    String choices[] = { "Rock", "Paper", "Scissors" };
    boolean isActive = true;

    // IO variables
    DataInputStream fromServer;
    DataOutputStream toServer;
    String host = "localhost";

    // GUI variables
    Label message = new Label();
    StackPane opponentStack;
    Text opponentText;
    Label playerLabel = new Label();
    

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

    private void handleButtonClick() {
        // Send choice to server
        try {
            sendChoice();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

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

    private void sendChoice() throws IOException {
        toServer.writeInt(myChoice.getChoice());
    }

    private void receiveChoice() throws IOException {
        opponentChoice = new Choice(fromServer.readInt());
        Platform.runLater(() -> opponentText.setText(choices[opponentChoice.getChoice()]));
    }

    private void receiveFromServer() throws IOException{
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

    public static void main(String[] args) {
        launch();
    }

    class Choice extends Rectangle {
        private int choice;

        public Choice(int choice) {
            super(100, 60, Color.TRANSPARENT);
            this.setStroke(Color.BLACK);
            this.choice = choice;
            this.setOnMouseClicked(e -> handleMouseClicked());
        }

        public int getChoice() {
            return choice;
        }

        public void setChoice(int choice) {
            this.choice = choice;
        }

        protected void outline(int x) {
            if (x == 0) {
                this.setStroke(Color.BLACK);
            } else {
                this.setStroke(Color.RED);
            }
        }

        private void handleMouseClicked() {
            if (myChoice != null) {
                myChoice.outline(0);
            }
            myChoice = this;
            outline(1);
        }
    }
}