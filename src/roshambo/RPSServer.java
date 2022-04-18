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

        private int player1Choice;
        private int player2Choice;

        public HandleSession(Socket player1, Socket player2) {
            this.player1 = player1;
            this.player2 = player2;
        }

        @Override
        public void run() {
            try {
                // Create input streams and output streams
                in1 = new DataInputStream(player1.getInputStream());
                in2 = new DataInputStream(player2.getInputStream());
                out1 = new DataOutputStream(player1.getOutputStream());
                out2 = new DataOutputStream(player2.getOutputStream());

                // Get player1's choice
                player1Choice = in1.readInt();
            } catch (IOException e) {
                e.printStackTrace();
            }

        }

        private void sendChoice(DataOutputStream out, int choice) {
            try {
                out.writeInt(choice);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }


        private boolean hasWon() {
            if (player1Choice == player2Choice) {
                return false;
            } else if (player1Choice == 0 && player2Choice == 2) {
                return true;
            } else if (player1Choice == 1 && player2Choice == 0) {
                return true;
            } else if (player1Choice == 2 && player2Choice == 1) {
                return true;
            } else {
                return false;
            }
        }

    }
}
