// Copy
package Net21.network;

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

    static Timer roomTimer = null;
    static boolean gameStarted = false;

    static Deck deck;

    // Scheduler 
    private static final ScheduledExecutorService executor = Executors.newScheduledThreadPool(1);
    private static Thread gameThread = new Thread();

    public static void main(String[] args) throws IOException {
        // Set the system property to prevent AWT/Swing from showing a GUI
        System.setProperty("java.awt.headless", "true");
        
        ServerSocket serverSocket = new ServerSocket(2121);
        System.out.println("Server started on port 2121...");

        try {
            while (true) {
                Socket socket = serverSocket.accept();
                System.out.println("A new client has connected.");

                ClientHandler clientHandler = new ClientHandler(socket);
                Thread thread = new Thread(clientHandler);
                thread.start();
            }
        } catch (IOException e) {
            System.err.println("Server error: " + e.getMessage());
            e.printStackTrace();
        } finally {
            if (serverSocket != null && !serverSocket.isClosed()) {
                serverSocket.close();
            }
        }
    }

    public static void broadcastClients() {
        StringBuilder playerList = new StringBuilder("Connected Players: "); // \n

        for (ClientHandler client : clients) {
            playerList.append(client.getPlayerName()).append(" ");
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
        if(waitingRoom.isEmpty()){
            resetGame();
        }
        if (Server.waitingRoom.size() >= 4 || gameStarted) {
            return "Cannot join game at this time. Try again in a few minutes";
        }
        if (!gameStarted) {
            Server.waitingRoom.add(client);
        }
        System.out.println(client.getPlayerName() + " joined the waiting room.");
        Server.broadcastWaitingRoom();
        if (Server.waitingRoom.size() >= 2 && !gameStarted) {
            if (Server.waitingRoom.size() == 4) {
                for(ClientHandler player: waitingRoom){
                    client.sendMessage("start");
                }
                roomTimer.cancel();
                startGame();
                
            } else {
                if (roomTimer == null) {
                    roomTimer = new Timer();
                    roomTimer.scheduleAtFixedRate(new TimerTask() {
                        private int countdown = 10; 

                        @Override
                        public void run() {
                           
                            if (countdown > 0) {
                                broadcastTimer(countdown);
                                countdown--;
                            } else {
                                broadcastTimer(countdown);
                                startGame();
                                if(roomTimer!=null) 
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

        gameThread = new Thread(() -> {
            try {
                for (int i = 0; i < 5; i++) {
                    startRound(i);
                    CountDownLatch latch = new CountDownLatch(1);
                    boolean isFinalRound = (i == 4);
                    startCountdown(latch, isFinalRound);
                    latch.await(); 

                   
                    if (!gameStarted) {
                        break;  
                    }
                }
                endGame();
            } catch (InterruptedException e) {
                System.out.println("Game thread interrupted");
                Thread.currentThread().interrupt();
            } finally {
                gameStarted = false; 
            }
        });
        gameThread.start();  
    }

    public static synchronized void resetGame() {
        gameStarted = false;  
        if (gameThread != null && gameThread.isAlive()) {
            gameThread.interrupt();  
        }
        gameThread = null;  
        roomTimer = null;  
        
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
        final ScheduledFuture<?>[] timerHolder = new ScheduledFuture<?>[1]; 

        timerHolder[0] = executor.scheduleAtFixedRate(() -> {
            if (countdown[0] > 0) {
                broadcastRoundTimer(countdown[0]);
                countdown[0]--;
            } else {
                
                if (!isFinalRound) {
                    broadcastRoundTimer(countdown[0]);
                }


                timerHolder[0].cancel(false);

                latch.countDown(); 
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
