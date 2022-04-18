package roshambo;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import javafx.application.Application;
import javafx.application.Platform;
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

        new Thread(() -> {
            try {
                // Create server socket
                ServerSocket serverSocket = new ServerSocket(8000);
                Platform.runLater(() -> serverLog.appendText("Server started\n"));

                // Accept connections
                while (true) {
                    // Connect player1
                    Socket player1 = serverSocket.accept();
                    Platform.runLater(() -> serverLog.appendText("Player 1 connected\n"));

                    // Let player1 know they are connected and waiting
                    new DataOutputStream(player1.getOutputStream()).writeInt(1);

                    // Connect player2
                    Socket player2 = serverSocket.accept();
                    Platform.runLater(() -> serverLog.appendText("Player 2 connected\n"));

                    // Let player2 know they are connected and game started
                    new DataOutputStream(player2.getOutputStream()).writeInt(2);

                    // Create new thread to handle both players
                    new Thread(new HandleSession(player1, player2)).start();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();
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
