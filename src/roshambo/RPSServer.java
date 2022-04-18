package roshambo;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.Socket;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;
import javafx.stage.Stage;

public class RPSServer extends Application {
    private int connections = 1;

    @Override
    public void start(Stage primaryStage) {
        TextArea serverLog = new TextArea();
        // Create scene and add to stage
        Scene scene = new Scene(new ScrollPane(serverLog), 400, 200);
        primaryStage.setTitle("RPS Server");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    class HandleSession implements Runnable {

        private Socket player1;
        private Socket player2;
        private DataInputStream in1;
        private DataInputStream in2;
        private DataOutputStream out1;
        private DataOutputStream out2;

        public HandleSession(Socket player1, Socket player2) {
            this.player1 = player1;
            this.player2 = player2;
        }

        @Override
        public void run() {
            // TODO Auto-generated method stub

        }

        private boolean hasWon() {
            // TODO Auto-generated method stub
            return false;
        }

    }
}
