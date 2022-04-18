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

/**
 * Roshambo game server.
 * 
 * A server that listens for connections from clients and plays a game of
 * Roshambo
 * 
 * @author Frank Lin
 * @version 1.0
 * @see RPSClient
 */
public class RPSServer extends Application {
    private int connections = 1;

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

    /** Construct the GUI and start the game server
     * 
     * @param primaryStage the stage to show the GUI on
     */
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
                    new DataOutputStream(player1.getOutputStream()).writeInt(PLAYER1);

                    // Connect player2
                    Socket player2 = serverSocket.accept();
                    Platform.runLater(() -> serverLog.appendText("Player 2 connected\n"));

                    // Let player2 know they are connected and game started
                    new DataOutputStream(player2.getOutputStream()).writeInt(PLAYER2);

                    // Create new thread to handle both players
                    new Thread(new HandleSession(player1, player2)).start();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();
    }

    /** Handle a session between two players */
    class HandleSession implements Runnable {

        private Socket player1;
        private Socket player2;
        private DataInputStream in1;
        private DataInputStream in2;
        private DataOutputStream out1;
        private DataOutputStream out2;

        private int player1Choice;
        private int player2Choice;

        /** Constructor */
        public HandleSession(Socket player1, Socket player2) {
            this.player1 = player1;
            this.player2 = player2;
        }

        /** Run the session */
        @Override
        public void run() {
            try {
                // Create input streams and output streams
                in1 = new DataInputStream(player1.getInputStream());
                in2 = new DataInputStream(player2.getInputStream());
                out1 = new DataOutputStream(player1.getOutputStream());
                out2 = new DataOutputStream(player2.getOutputStream());

                // Let player1 know game started
                out1.writeInt(PLAYER1);

                // Game loop
                while (true) {
                    // Receive player1 choice
                    player1Choice = in1.readInt();
                    // Receive player2 choice
                    player2Choice = in2.readInt();

                    if (player1Choice == player2Choice) {
                        // Send draw message
                        out1.writeInt(DRAW);
                        out2.writeInt(DRAW);
                        sendChoice(out2, player1Choice);
                        sendChoice(out1, player2Choice);
                        break;
                    } else if (player1Choice == 0 && player2Choice == 2) {
                        out1.writeInt(PLAYER1_WON);
                        out2.writeInt(PLAYER1_WON);
                        sendChoice(out2, player1Choice);
                        sendChoice(out1, player2Choice);
                        break;
                    } else if (player1Choice == 1 && player2Choice == 0) {
                        out1.writeInt(PLAYER1_WON);
                        out2.writeInt(PLAYER1_WON);
                        sendChoice(out2, player1Choice);
                        sendChoice(out1, player2Choice);
                        break;
                    } else if (player1Choice == 2 && player2Choice == 1) {
                        out1.writeInt(PLAYER1_WON);
                        out2.writeInt(PLAYER1_WON);
                        sendChoice(out2, player1Choice);
                        sendChoice(out1, player2Choice);
                        break;
                    } else {
                        out1.writeInt(PLAYER2_WON);
                        out2.writeInt(PLAYER2_WON);
                        sendChoice(out2, player1Choice);
                        sendChoice(out1, player2Choice);
                        break;
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

        }

        /** Send choice to player */
        private void sendChoice(DataOutputStream out, int choice) {
            try {
                out.writeInt(choice);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
