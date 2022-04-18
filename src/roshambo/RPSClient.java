package roshambo;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;

public class RPSClient extends Application {

    // Core variables
    Choice myChoice;
    Choice opponentChoice;

    // IO variables
    DataInputStream fromServer;
    DataOutputStream toServer;
    String host = "localhost";

    // GUI variables
    Label message = new Label();

    @Override
    public void start(Stage stage) throws IOException {

        // Create border pane
        GridPane bottomGrid = new GridPane();
        GridPane topGrid = new GridPane();

        // Create the bottom choice buttons
        for (int i = 0; i < 3; i++) {
            Choice choice = new Choice(i);
            GridPane.setMargin(choice, new Insets(10));
            bottomGrid.add(choice, i, 1);
        }

        // Create the top choice buttons
        for (int i = 0; i < 3; i++) {
            Choice choice = new Choice(i);
            GridPane.setMargin(choice, new Insets(10));
            topGrid.add(choice, i, 0);
        }

        // Align grids center
        bottomGrid.setAlignment(Pos.CENTER);
        topGrid.setAlignment(Pos.CENTER);

        // Create border pane
        BorderPane border = new BorderPane();
        border.setBottom(bottomGrid);
        border.setTop(topGrid);
        BorderPane.setMargin(bottomGrid, new Insets(10));
        BorderPane.setMargin(topGrid, new Insets(10));
        BorderPane.setAlignment(bottomGrid, Pos.CENTER);
        BorderPane.setAlignment(topGrid, Pos.CENTER);

        // Create the message in the center
        message.setText("Waiting for opponent...");
        border.setCenter(message);

        // Add button to side
        Button confirmButton = new Button("Confirm");
        BorderPane.setAlignment(confirmButton, Pos.BOTTOM_LEFT);
        BorderPane.setMargin(confirmButton, new Insets(0, 5, 0, 0));
        border.setRight(confirmButton);

        Scene scene = new Scene(border, 600, 400);
        stage.setTitle("RSP Client");
        stage.setScene(scene);
        stage.show();
    }

    private void connectToServer() {

    }

    private void sendChoice() {

    }

    private void receiveChoice() {

    }

    private void receiveFromServer() {

    }

    public static void main(String[] args) {
        launch();
    }

    class Choice extends Rectangle {
        private int choice;

        public Choice(int choice) {
            super(100, 60, Color.RED);
            this.choice = choice;
            this.setOnMouseClicked(e -> handleMouseClicked());
        }

        public int getChoice() {
            return choice;
        }

        public void setChoice(int choice) {
            this.choice = choice;
        }

        protected void outline() {
            // TODO Auto-generated method stub
        }

        private void handleMouseClicked() {
            // TODO Auto-generated method stub
        }
    }
}