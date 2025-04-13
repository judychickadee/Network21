package Network21;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;
import java.util.concurrent.*;

public class Server {

    static List<ClientHandler> clients = new ArrayList<>();
    static List<ClientHandler> waitingRoom = new ArrayList<>();

    private BufferedReader in;
    private PrintWriter out;
    private static Timer roomTimer = null;
    private static boolean gameStarted = false;

    static Deck deck;
    private static Timer roundTimer;

    // Scheduler 
    private static final ScheduledExecutorService executor = Executors.newScheduledThreadPool(1);

    public static void main(String[] args) throws IOException {

        ServerSocket serverSocket = new ServerSocket(2121);
        System.out.println("Server started...");

        try {
            while (true) {

                Socket socket = serverSocket.accept();

                System.out.println("A new client has connected.");

                ClientHandler clientHandler = new ClientHandler(socket);

                Thread thread = new Thread(clientHandler);
                thread.start();

            }
        } catch (IOException e) {

        }

    }

    public static void broadcastClients() {
        StringBuilder playerList = new StringBuilder("Connected Players: \n");

        for (ClientHandler client : clients) {
            playerList.append(client.getPlayerName()).append("\n");
        }

        for (ClientHandler client : clients) {
            client.sendMessage(playerList.toString());
        }
    }

    public static void broadcastWaitingRoom() {
        StringBuilder playerList = new StringBuilder("Waiting Players: "); //\n

        for (ClientHandler client : waitingRoom) {
            playerList.append(client.getPlayerName()).append(" "); //\n
        }

        System.out.println("Broadcasting waiting room message: " + playerList.toString());
        System.out.println("Number of players in waiting room: " + waitingRoom.size());

        for (ClientHandler client : waitingRoom) {
            client.sendMessage(playerList.toString());
        }
    }

    public static void broadcastScoreList() {
        StringBuilder playerList = new StringBuilder("Score: ");
        List<ClientHandler> losers = new ArrayList<>();
        List<ClientHandler> sortedClients = new ArrayList<>();
        for (ClientHandler client : waitingRoom) {
            if (client.getScore() > 21) {
                losers.add(client);
            } else {
                sortedClients.add(client);
            }
        }
        sortedClients.sort((a, b) -> Integer.compare(b.getScore(), a.getScore()));
        losers.sort((a, b) -> Integer.compare(a.getScore(), b.getScore()));
        for (ClientHandler client : sortedClients) {
            playerList.append(client.getPlayerName()).append("-").append(client.getScore()).append(" ");
        }
        for (ClientHandler client : losers) {
            playerList.append(client.getPlayerName()).append("-").append(client.getScore()).append(" ");
        }
        for (ClientHandler client : clients) {
            client.sendMessage(playerList.toString());
        }
    }

    public static String join(ClientHandler client) {
        if (Server.waitingRoom.size() >= 4 || gameStarted) {
            return "Cannot join game at this time. Try again in a few minutes";
        }
        Server.waitingRoom.add(client);
        System.out.println(client.getPlayerName() + " joined the waiting room.");
        Server.broadcastWaitingRoom();
        if (Server.waitingRoom.size() >= 2 && !gameStarted) {
            if (Server.waitingRoom.size() == 4) {

                startGame();
            } else {
                if (roomTimer == null) {
                    roomTimer = new Timer();
                    roomTimer.scheduleAtFixedRate(new TimerTask() {
                        private int countdown = 30; // Countdown from 30 seconds

                        @Override
                        public void run() {
                            // Send the countdown to all clients in the waiting room
                            if (countdown > 0) {
                                broadcastTimer(countdown);
                                countdown--;
                            } else {
                                broadcastTimer(countdown);
                                startGame();
                                roomTimer.cancel();
                            }
                        }
                    }, 0, 1000);
                }
            }
        }
        return client.getPlayerName() + " joined.";
    }

    public static void declareWinners() {
        broadcastScoreList();
    }

    public static synchronized void startGame() {
        if (gameStarted) {
            System.out.println("already started");
            return;
        }

        gameStarted = true;
        System.out.println("started");
        deck = new Deck();

        new Thread(() -> {
            try {
                for (int i = 0; i < 5; i++) {
                    startRound(i);
                    // Wait for round to complete
                    CountDownLatch latch = new CountDownLatch(1);
                    boolean isFinalRound = (i == 4);
                    startCountdown(latch, isFinalRound);
                    latch.await(); // Wait until countdown completes
                }
                endGame();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }).start();
    }

    private static void startRound(int roundNumber) {
        for (ClientHandler client : waitingRoom) {
            System.out.println(client.getPlayerName() + " Round " + roundNumber);
            client.sendMessage("Round " + (roundNumber + 1));
            broadcastScoreList();
        }
    }

    private static void startCountdown(CountDownLatch latch, boolean isFinalRound) {
        final int[] countdown = {10};
        final ScheduledFuture<?>[] timerHolder = new ScheduledFuture<?>[1]; // Holder for timer

        timerHolder[0] = executor.scheduleAtFixedRate(() -> {
            if (countdown[0] > 0) {
                broadcastRoundTimer(countdown[0]);
                countdown[0]--;
            } else {
                // For final round, don't broadcast 00:00
                if (!isFinalRound) {
                    broadcastRoundTimer(countdown[0]);
                }

                // Cancel the timer task
                timerHolder[0].cancel(false);

                latch.countDown(); // Signal that countdown is complete
            }
        }, 0, 1, TimeUnit.SECONDS);
    }

    public static void broadcastTimer(int countdown) {
        String t;
        if (countdown <= 9) {
            t = "0" + countdown;
        } else {
            t = countdown + "";
        }
        String timeLeft = "00:" + t;
        for (ClientHandler client : waitingRoom) {
            client.sendMessage(timeLeft);
        }
    }

    public static void endGame() {
        declareWinners();
        for (ClientHandler client : waitingRoom) {
            client.setScore(0);
            client.sendMessage("Game done.");
        }
        roomTimer = null;
        gameStarted = false;
    }

    public static void broadcastRoundTimer(int countdown) {
        String t;
        if (countdown <= 9) {
            t = "0" + countdown;
        } else {
            t = countdown + "";
        }
        String timeLeft = "Round00:" + t;
        for (ClientHandler client : waitingRoom) {
            client.sendMessage(timeLeft);
        }
    }

}
