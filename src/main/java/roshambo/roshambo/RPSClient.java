package roshambo.roshambo;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Timer;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;

public class RPSClient extends Application {

    //Core variables
    Choice myChoice;
    Choice opponentChoice;

    //IO variables
    DataInputStream fromServer;
    DataOutputStream toServer;
    String host = "localhost";

    //GUI variables
    Timer timer;
    Label message = new Label();

    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(RPSClient.class.getResource("hello-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 320, 240);
        stage.setTitle("Hello!");
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