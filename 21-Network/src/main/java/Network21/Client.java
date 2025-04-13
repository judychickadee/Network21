
package Network21;

import java.io.*;
import java.net.Socket;
import javax.swing.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class Client extends javax.swing.JFrame {

    private Socket socket;
    private BufferedReader in;
    private PrintWriter out;

    private String username;
    
    
    public Client() {
        initComponents();
        

                UsernamePanel.setVisible(true);
                ConnectionRoom.setVisible(false);
                WaitingRoom.setVisible(false);
                RoundPanel.setVisible(false);
                LeaderBoardPanel.setVisible(false);

                ConnectButton.addActionListener(evt -> connectToServer());
                JoinButton.addActionListener(evt -> joinWaitingRoom()); // Action for joinButton in ConnectedRoom

                HitButton.addActionListener(evt -> hit()); 
                PassButton.addActionListener(evt -> pass());
                LeaveGameButton.addActionListener(evt -> LeaveGame());
                NewGameButton.addActionListener(evt -> LeaveGame());

                // Add a window listener to handle the close operation
                this.addWindowListener(new WindowAdapter() {
                        @Override
                        public void windowClosing(WindowEvent e) {
                                sendExitMessage();
                                System.exit(0); // Close the application
                        }
                });
    }
    
    private void sendExitMessage() {
                if (out != null) {
                        out.println("exit"); // Send exit message to the server
                }
        }

        public void connectToServer() {
                username = UsernameField.getText().trim();

                if (username.isEmpty()) {
                        javax.swing.JOptionPane.showMessageDialog(this, "Username cannot be empty!");
                        return;
                }

                try {
                        socket = new Socket("localhost", 2121);
                        out = new PrintWriter(socket.getOutputStream(), true); // Auto flush

                        out.println(username);
                        new Thread(new Listener(socket, this)).start();

                        // Update UI
                        UsernamePanel.setVisible(false);
                        ConnectionRoom.setVisible(true);

                        ConnectedPlayers.setCaretPosition(ConnectedPlayers.getDocument().getLength());
                } catch (Exception e) {
                        e.printStackTrace();
                }
        }
        
        public void joinWaitingRoom() {

                out.println("join"); // Send "join" message to the server

                System.out.println("Switching to WaitingPlayers panel...");
                ConnectionRoom.setVisible(false);
                WaitingRoom.setVisible(true);
                System.out.println("WaitingPlayers panel visible: " + WaitingRoom.isVisible());

                WaitingPlayers.setCaretPosition(WaitingPlayers.getDocument().getLength());
        }

        public void hit(){
            out.println("Hit");
            HitButton.setEnabled(false);
            PassButton.setEnabled(false);
            System.out.println("hit button clicked");
        }
        public void pass(){
            out.println("Pass");
            HitButton.setEnabled(false);
            PassButton.setEnabled(false);
            System.out.println("Pass button clicked");
        }
        
        public void LeaveGame(){
            out.println("leave game");
            RoundPanel.setVisible(false);
            ConnectionRoom.setVisible(true);
            NewGameButton.setVisible(false);
            WinnersPanel.setVisible(false);
            
        }

        public void handleMessage(String message) {
                SwingUtilities.invokeLater(() -> {
                        System.out.println("Received message: " + message);

                        // Handle message for "Waiting Players:"
                        if (message.startsWith("Waiting Players:")) {
                                WaitingRoom.setVisible(true); // Make sure the panel is visible now
                                WaitingPlayers.setText(""); // Clear the previous list

                                // Extract the part after "Waiting Players:\n"
                                String playerList = message.substring("Waiting Players: ".length());
                                playerList = playerList.replace(" ", "\n");

                                WaitingPlayers.setText(playerList); // Update the text area with the player names
                                WaitingPlayers.setCaretPosition(WaitingPlayers.getDocument().getLength());
                        } else if (message.startsWith("00:")) {
                                Timer.setText("");
                                Timer.setText(message);
                        } else if (message.startsWith("Cannot join game at this time.")) {
                                javax.swing.JOptionPane.showMessageDialog(this, message);
                                ConnectionRoom.setVisible(true);
                                WaitingRoom.setVisible(false);
                        }
                        // Handle other types of messages (e.g., connected players, etc.)
                        else if (message.startsWith("Connected Players:")) {
                                ConnectedPlayers.setText("");
                                String displayMessage = message.substring("Connected Players: ".length());
                                displayMessage = displayMessage.replace(" ", "\n");
                                //ConnectedPlayers.append(displayMessage);
                                ConnectedPlayers.setText(displayMessage);
                                ConnectedPlayers.setCaretPosition(ConnectedPlayers.getDocument().getLength());
                                ConnectionRoom.revalidate();
                        } else if(message.startsWith("Round ")){
                            HitButton.setEnabled(true);
                            PassButton.setEnabled(true);
                            RoundNumberLabel.setText("");
                            RoundNumberLabel.setText(message);
                            WaitingRoom.setVisible(false);
                            RoundPanel.setVisible(true);
                            
                        } else if(message.startsWith("Points")){
                            String score = message.substring("Points".length());
                            ScoreLabel.setText("");
                            ScoreLabel.setText(score); 
                        } else if(message.startsWith("Game done.")){
                            RoundPanel.setVisible(false);
                            LeaderBoardPanel.setVisible(true);
                        } else if(message.startsWith("Round00:")){
                            String timer = message.substring("Round".length());
                            RoundTimerLabel.setText("");
                            RoundTimerLabel.setText(timer);
                        
                        } else if (message.startsWith("Score: ")) {
                            String score = message.substring("Score: ".length());
                            score = score.replace(" ", "\n");
                            CurrentScoresText.setText("");
                            CurrentScoresText.setText(score);
                            WinnersTextArea.setText("");
                            WinnersTextArea.setText(score);
                            NewGameButton.setVisible(true);
                            
                        }else {
                                ConnectedPlayers.append(message + "\n");
                        }
                });
        }


    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        LeaderBoardPanel = new javax.swing.JPanel();
        LeaderBoardLabel = new javax.swing.JLabel();
        WinnersPanel = new javax.swing.JScrollPane();
        WinnersTextArea = new javax.swing.JTextArea();
        NewGameButton = new javax.swing.JButton();
        RoundPanel = new javax.swing.JPanel();
        RoundNumberLabel = new javax.swing.JLabel();
        BackOfCardLabel = new javax.swing.JLabel();
        PassButton = new javax.swing.JButton();
        HitButton = new javax.swing.JButton();
        RoundDivider = new javax.swing.JSeparator();
        CurrentScoreLabel = new javax.swing.JLabel();
        ScoreLabel = new javax.swing.JLabel();
        CurrentScoreBoardLabel = new javax.swing.JLabel();
        CurrentScoreboardPane = new javax.swing.JScrollPane();
        CurrentScoresText = new javax.swing.JTextArea();
        RoundTimerLabel = new javax.swing.JLabel();
        LeaveGameButton = new javax.swing.JButton();
        UsernamePanel = new javax.swing.JPanel();
        GameName = new javax.swing.JLabel();
        UsernamePrompt = new javax.swing.JLabel();
        UsernameField = new javax.swing.JTextField();
        ConnectButton = new javax.swing.JButton();
        WaitingRoom = new javax.swing.JPanel();
        WaitingRoomLabel = new javax.swing.JLabel();
        Timer = new javax.swing.JLabel();
        WaitingRoomPane = new javax.swing.JScrollPane();
        WaitingPlayers = new javax.swing.JTextArea();
        ConnectionRoom = new javax.swing.JPanel();
        ConnectedRoomLabel = new javax.swing.JLabel();
        ConnectedPlayerPane = new javax.swing.JScrollPane();
        ConnectedPlayers = new javax.swing.JTextArea();
        JoinButton = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("21");
        setResizable(false);

        LeaderBoardLabel.setFont(new java.awt.Font("Helvetica Neue", 0, 18)); // NOI18N
        LeaderBoardLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        LeaderBoardLabel.setText("Winners");

        WinnersTextArea.setColumns(20);
        WinnersTextArea.setRows(5);
        WinnersPanel.setViewportView(WinnersTextArea);

        NewGameButton.setText("New Game");

        javax.swing.GroupLayout LeaderBoardPanelLayout = new javax.swing.GroupLayout(LeaderBoardPanel);
        LeaderBoardPanel.setLayout(LeaderBoardPanelLayout);
        LeaderBoardPanelLayout.setHorizontalGroup(
            LeaderBoardPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(LeaderBoardPanelLayout.createSequentialGroup()
                .addGroup(LeaderBoardPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(LeaderBoardPanelLayout.createSequentialGroup()
                        .addGap(310, 310, 310)
                        .addComponent(LeaderBoardLabel))
                    .addGroup(LeaderBoardPanelLayout.createSequentialGroup()
                        .addGap(172, 172, 172)
                        .addComponent(WinnersPanel, javax.swing.GroupLayout.PREFERRED_SIZE, 354, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(LeaderBoardPanelLayout.createSequentialGroup()
                        .addGap(300, 300, 300)
                        .addComponent(NewGameButton, javax.swing.GroupLayout.PREFERRED_SIZE, 103, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(177, Short.MAX_VALUE))
        );
        LeaderBoardPanelLayout.setVerticalGroup(
            LeaderBoardPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(LeaderBoardPanelLayout.createSequentialGroup()
                .addGap(117, 117, 117)
                .addComponent(LeaderBoardLabel)
                .addGap(34, 34, 34)
                .addComponent(WinnersPanel, javax.swing.GroupLayout.PREFERRED_SIZE, 277, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(36, 36, 36)
                .addComponent(NewGameButton, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(143, Short.MAX_VALUE))
        );

        RoundNumberLabel.setFont(new java.awt.Font("Helvetica Neue", 0, 18)); // NOI18N
        RoundNumberLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        RoundNumberLabel.setText("Round");

        BackOfCardLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        BackOfCardLabel.setText("Card");
        BackOfCardLabel.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        PassButton.setFont(new java.awt.Font("Helvetica Neue", 0, 14)); // NOI18N
        PassButton.setText("Pass");
        PassButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                PassButtonActionPerformed(evt);
            }
        });

        HitButton.setFont(new java.awt.Font("Helvetica Neue", 0, 14)); // NOI18N
        HitButton.setText("Hit");

        RoundDivider.setOrientation(javax.swing.SwingConstants.VERTICAL);

        CurrentScoreLabel.setFont(new java.awt.Font("Helvetica Neue", 0, 18)); // NOI18N
        CurrentScoreLabel.setText("Your Score:");

        ScoreLabel.setFont(new java.awt.Font("Helvetica Neue", 0, 18)); // NOI18N
        ScoreLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        ScoreLabel.setText("0");
        ScoreLabel.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        CurrentScoreBoardLabel.setFont(new java.awt.Font("Helvetica Neue", 0, 18)); // NOI18N
        CurrentScoreBoardLabel.setText("Scoreboard:");

        CurrentScoresText.setColumns(20);
        CurrentScoresText.setRows(5);
        CurrentScoreboardPane.setViewportView(CurrentScoresText);

        RoundTimerLabel.setToolTipText("");

        LeaveGameButton.setText("Leave Game");

        javax.swing.GroupLayout RoundPanelLayout = new javax.swing.GroupLayout(RoundPanel);
        RoundPanel.setLayout(RoundPanelLayout);
        RoundPanelLayout.setHorizontalGroup(
            RoundPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(RoundPanelLayout.createSequentialGroup()
                .addGroup(RoundPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(RoundPanelLayout.createSequentialGroup()
                        .addGap(94, 94, 94)
                        .addComponent(BackOfCardLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 207, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(RoundPanelLayout.createSequentialGroup()
                        .addGap(59, 59, 59)
                        .addGroup(RoundPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(RoundNumberLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 256, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(RoundPanelLayout.createSequentialGroup()
                                .addComponent(PassButton, javax.swing.GroupLayout.PREFERRED_SIZE, 133, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addComponent(HitButton, javax.swing.GroupLayout.PREFERRED_SIZE, 131, javax.swing.GroupLayout.PREFERRED_SIZE))))
                    .addGroup(RoundPanelLayout.createSequentialGroup()
                        .addGap(165, 165, 165)
                        .addComponent(RoundTimerLabel))
                    .addGroup(RoundPanelLayout.createSequentialGroup()
                        .addGap(141, 141, 141)
                        .addComponent(LeaveGameButton, javax.swing.GroupLayout.PREFERRED_SIZE, 118, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(100, 100, 100)
                .addComponent(RoundDivider, javax.swing.GroupLayout.PREFERRED_SIZE, 16, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(RoundPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(CurrentScoreLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 146, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(ScoreLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 228, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(CurrentScoreBoardLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 167, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(CurrentScoreboardPane, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        RoundPanelLayout.setVerticalGroup(
            RoundPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(RoundPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(RoundDivider)
                .addContainerGap())
            .addGroup(RoundPanelLayout.createSequentialGroup()
                .addGap(25, 25, 25)
                .addComponent(CurrentScoreLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(ScoreLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 66, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(31, 31, 31)
                .addComponent(CurrentScoreBoardLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 44, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(CurrentScoreboardPane, javax.swing.GroupLayout.PREFERRED_SIZE, 361, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(80, Short.MAX_VALUE))
            .addGroup(RoundPanelLayout.createSequentialGroup()
                .addGap(29, 29, 29)
                .addComponent(RoundNumberLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 75, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(RoundTimerLabel)
                .addGap(24, 24, 24)
                .addComponent(BackOfCardLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 284, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(61, 61, 61)
                .addGroup(RoundPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(PassButton, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(HitButton, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(LeaveGameButton, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(48, 48, 48))
        );

        GameName.setFont(new java.awt.Font("Helvetica Neue", 2, 36)); // NOI18N
        GameName.setText("21");

        UsernamePrompt.setText("Please enter your username:");

        ConnectButton.setText("Connect");

        javax.swing.GroupLayout UsernamePanelLayout = new javax.swing.GroupLayout(UsernamePanel);
        UsernamePanel.setLayout(UsernamePanelLayout);
        UsernamePanelLayout.setHorizontalGroup(
            UsernamePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(UsernamePanelLayout.createSequentialGroup()
                .addGroup(UsernamePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(UsernamePanelLayout.createSequentialGroup()
                        .addGap(240, 240, 240)
                        .addComponent(UsernamePrompt))
                    .addGroup(UsernamePanelLayout.createSequentialGroup()
                        .addGap(213, 213, 213)
                        .addComponent(UsernameField, javax.swing.GroupLayout.PREFERRED_SIZE, 235, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(UsernamePanelLayout.createSequentialGroup()
                        .addGap(305, 305, 305)
                        .addComponent(GameName, javax.swing.GroupLayout.PREFERRED_SIZE, 48, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(UsernamePanelLayout.createSequentialGroup()
                        .addGap(292, 292, 292)
                        .addComponent(ConnectButton)))
                .addContainerGap(234, Short.MAX_VALUE))
        );
        UsernamePanelLayout.setVerticalGroup(
            UsernamePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(UsernamePanelLayout.createSequentialGroup()
                .addGap(113, 113, 113)
                .addComponent(GameName, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(34, 34, 34)
                .addComponent(UsernamePrompt, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(UsernameField, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(56, 56, 56)
                .addComponent(ConnectButton)
                .addContainerGap(337, Short.MAX_VALUE))
        );

        WaitingRoomLabel.setFont(new java.awt.Font("Helvetica Neue", 0, 36)); // NOI18N
        WaitingRoomLabel.setText("Waiting Room");

        WaitingPlayers.setEditable(false);
        WaitingPlayers.setColumns(20);
        WaitingPlayers.setFont(new java.awt.Font("Helvetica Neue", 0, 18)); // NOI18N
        WaitingPlayers.setRows(5);
        WaitingRoomPane.setViewportView(WaitingPlayers);

        javax.swing.GroupLayout WaitingRoomLayout = new javax.swing.GroupLayout(WaitingRoom);
        WaitingRoom.setLayout(WaitingRoomLayout);
        WaitingRoomLayout.setHorizontalGroup(
            WaitingRoomLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(WaitingRoomLayout.createSequentialGroup()
                .addContainerGap(174, Short.MAX_VALUE)
                .addGroup(WaitingRoomLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, WaitingRoomLayout.createSequentialGroup()
                        .addComponent(WaitingRoomLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 242, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(236, 236, 236))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, WaitingRoomLayout.createSequentialGroup()
                        .addComponent(WaitingRoomPane, javax.swing.GroupLayout.PREFERRED_SIZE, 351, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(180, 180, 180))))
            .addGroup(WaitingRoomLayout.createSequentialGroup()
                .addGap(311, 311, 311)
                .addComponent(Timer, javax.swing.GroupLayout.PREFERRED_SIZE, 70, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, Short.MAX_VALUE))
        );
        WaitingRoomLayout.setVerticalGroup(
            WaitingRoomLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(WaitingRoomLayout.createSequentialGroup()
                .addGap(102, 102, 102)
                .addComponent(WaitingRoomLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 52, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(Timer, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(WaitingRoomPane, javax.swing.GroupLayout.PREFERRED_SIZE, 192, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(247, Short.MAX_VALUE))
        );

        ConnectedRoomLabel.setFont(new java.awt.Font("Helvetica Neue", 0, 36)); // NOI18N
        ConnectedRoomLabel.setText("Connected Room");

        ConnectedPlayers.setEditable(false);
        ConnectedPlayers.setColumns(20);
        ConnectedPlayers.setFont(new java.awt.Font("Helvetica Neue", 0, 18)); // NOI18N
        ConnectedPlayers.setRows(5);
        ConnectedPlayerPane.setViewportView(ConnectedPlayers);

        JoinButton.setFont(new java.awt.Font("Helvetica Neue", 0, 14)); // NOI18N
        JoinButton.setText("Join");

        javax.swing.GroupLayout ConnectionRoomLayout = new javax.swing.GroupLayout(ConnectionRoom);
        ConnectionRoom.setLayout(ConnectionRoomLayout);
        ConnectionRoomLayout.setHorizontalGroup(
            ConnectionRoomLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(ConnectionRoomLayout.createSequentialGroup()
                .addContainerGap(168, Short.MAX_VALUE)
                .addGroup(ConnectionRoomLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, ConnectionRoomLayout.createSequentialGroup()
                        .addComponent(ConnectedRoomLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 294, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(197, 197, 197))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, ConnectionRoomLayout.createSequentialGroup()
                        .addComponent(ConnectedPlayerPane, javax.swing.GroupLayout.PREFERRED_SIZE, 377, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(160, 160, 160))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, ConnectionRoomLayout.createSequentialGroup()
                        .addComponent(JoinButton, javax.swing.GroupLayout.PREFERRED_SIZE, 90, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(300, 300, 300))))
        );
        ConnectionRoomLayout.setVerticalGroup(
            ConnectionRoomLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(ConnectionRoomLayout.createSequentialGroup()
                .addGap(119, 119, 119)
                .addComponent(ConnectedRoomLabel)
                .addGap(36, 36, 36)
                .addComponent(ConnectedPlayerPane, javax.swing.GroupLayout.PREFERRED_SIZE, 214, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(34, 34, 34)
                .addComponent(JoinButton, javax.swing.GroupLayout.PREFERRED_SIZE, 31, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(167, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 715, Short.MAX_VALUE)
            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(layout.createSequentialGroup()
                    .addContainerGap()
                    .addComponent(LeaderBoardPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addContainerGap()))
            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(layout.createSequentialGroup()
                    .addContainerGap()
                    .addComponent(RoundPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addContainerGap()))
            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(layout.createSequentialGroup()
                    .addGap(0, 0, Short.MAX_VALUE)
                    .addComponent(UsernamePanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGap(0, 0, Short.MAX_VALUE)))
            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(layout.createSequentialGroup()
                    .addGap(0, 0, Short.MAX_VALUE)
                    .addComponent(WaitingRoom, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGap(0, 0, Short.MAX_VALUE)))
            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(layout.createSequentialGroup()
                    .addGap(0, 0, Short.MAX_VALUE)
                    .addComponent(ConnectionRoom, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGap(0, 0, Short.MAX_VALUE)))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 677, Short.MAX_VALUE)
            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(layout.createSequentialGroup()
                    .addContainerGap()
                    .addComponent(LeaderBoardPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addContainerGap()))
            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(layout.createSequentialGroup()
                    .addContainerGap()
                    .addComponent(RoundPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addContainerGap()))
            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(layout.createSequentialGroup()
                    .addGap(0, 0, Short.MAX_VALUE)
                    .addComponent(UsernamePanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGap(0, 0, Short.MAX_VALUE)))
            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(layout.createSequentialGroup()
                    .addGap(0, 0, Short.MAX_VALUE)
                    .addComponent(WaitingRoom, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGap(0, 0, Short.MAX_VALUE)))
            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(layout.createSequentialGroup()
                    .addGap(0, 0, Short.MAX_VALUE)
                    .addComponent(ConnectionRoom, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGap(0, 0, Short.MAX_VALUE)))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void PassButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_PassButtonActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_PassButtonActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(Client.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(Client.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(Client.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(Client.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new Client().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel BackOfCardLabel;
    private javax.swing.JButton ConnectButton;
    private javax.swing.JScrollPane ConnectedPlayerPane;
    private javax.swing.JTextArea ConnectedPlayers;
    private javax.swing.JLabel ConnectedRoomLabel;
    private javax.swing.JPanel ConnectionRoom;
    private javax.swing.JLabel CurrentScoreBoardLabel;
    private javax.swing.JLabel CurrentScoreLabel;
    private javax.swing.JScrollPane CurrentScoreboardPane;
    private javax.swing.JTextArea CurrentScoresText;
    private javax.swing.JLabel GameName;
    private javax.swing.JButton HitButton;
    private javax.swing.JButton JoinButton;
    private javax.swing.JLabel LeaderBoardLabel;
    private javax.swing.JPanel LeaderBoardPanel;
    private javax.swing.JButton LeaveGameButton;
    private javax.swing.JButton NewGameButton;
    private javax.swing.JButton PassButton;
    private javax.swing.JSeparator RoundDivider;
    private javax.swing.JLabel RoundNumberLabel;
    private javax.swing.JPanel RoundPanel;
    private javax.swing.JLabel RoundTimerLabel;
    private javax.swing.JLabel ScoreLabel;
    private javax.swing.JLabel Timer;
    private javax.swing.JTextField UsernameField;
    private javax.swing.JPanel UsernamePanel;
    private javax.swing.JLabel UsernamePrompt;
    private javax.swing.JTextArea WaitingPlayers;
    private javax.swing.JPanel WaitingRoom;
    private javax.swing.JLabel WaitingRoomLabel;
    private javax.swing.JScrollPane WaitingRoomPane;
    private javax.swing.JScrollPane WinnersPanel;
    private javax.swing.JTextArea WinnersTextArea;
    // End of variables declaration//GEN-END:variables
}
