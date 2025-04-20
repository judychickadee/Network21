package Net21.network;

import java.awt.Color;
import java.awt.Dimension;
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
     //   ImageIcon testIcon = new ImageIcon(getClass().getResource("/images/Clubs/1.jpg"));
// JOptionPane.showMessageDialog(this, "Test Image", "Test", JOptionPane.INFORMATION_MESSAGE, testIcon);
        BackOfCardLabel.setPreferredSize(new Dimension(150, 200));
BackOfCardLabel.setMinimumSize(new Dimension(150, 200));
BackOfCardLabel.setSize(new Dimension(150, 200));
RoundPanel.add(BackOfCardLabel, new org.netbeans.lib.awtextra.AbsoluteConstraints(
    370, 190, 150, 200));

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
        WaitingRoomLabel.setVisible(true);
        System.out.println("WaitingPlayers panel visible: " + WaitingRoom.isVisible());

        WaitingPlayers.setCaretPosition(WaitingPlayers.getDocument().getLength());
    }

    public void hit() {
        out.println("Hit");
        HitButton.setEnabled(false);
        PassButton.setEnabled(false);
        System.out.println("hit button clicked");
    }
   private void displayPrivateCard(Card card) {
    System.out.println("Card dimensions: " + 
        card.getImage().getIconWidth() + "x" + 
        card.getImage().getIconHeight());
     System.out.println("RoundPanel visible: " + RoundPanel.isVisible());

   
    BackOfCardLabel.setIcon(card.getImage());
    BackOfCardLabel.revalidate();
    BackOfCardLabel.repaint();
}
    public void pass() {
        out.println("Pass");
        HitButton.setEnabled(false);
        PassButton.setEnabled(false);
        System.out.println("Pass button clicked");
    }

    /*public void LeaveGame(){
            out.println("leave game");
            RoundPanel.setVisible(false);
            ConnectionRoom.setVisible(true);
            NewGameButton.setVisible(false);
            WinnersPanel.setVisible(false);
            LeaderBoardLabel.setVisible(false);
            
        }*/
    public void LeaveGame() {
        out.println("leave game");

        // Hide game-related panels
        RoundPanel.setVisible(false);
        LeaderBoardPanel.setVisible(false);
        WinnersPanel.setVisible(false);

        // Show ConnectionRoom and ALL its components
        ConnectionRoom.setVisible(true);
        ConnectionRoomLabel.setVisible(true);  // Make sure label is visible
        JoinButton.setVisible(true);          // Make sure button is visible
        ConnectedPlayerPane.setVisible(true); // Make sure player list is visible

        // Reset game state UI elements
        NewGameButton.setVisible(false);
        HitButton.setEnabled(true);
        PassButton.setEnabled(true);

        // If using CardLayout, you might need to call revalidate/repaint
        ConnectionRoom.revalidate();
        ConnectionRoom.repaint();
    }

    public void handleMessage(String message) {
        SwingUtilities.invokeLater(() -> {
            System.out.println("Received message: " + message);

            // Handle message for "Waiting Players:"
            if (message.startsWith("Waiting Players:")) {
                WaitingRoom.setVisible(true); // Make sure the panel is visible now
                WaitingRoomLabel.setVisible(true);
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
            } // Handle other types of messages (e.g., connected players, etc.)
            else if (message.startsWith("Connected Players:")) {
                ConnectedPlayers.setText("");
                String displayMessage = message.substring("Connected Players: ".length());
                displayMessage = displayMessage.replace(" ", "\n");
                //ConnectedPlayers.append(displayMessage);
                ConnectedPlayers.setText(displayMessage);
                ConnectedPlayers.setCaretPosition(ConnectedPlayers.getDocument().getLength());
                ConnectionRoom.revalidate();
            } else if (message.startsWith("Round ")) {
                HitButton.setEnabled(true);
                PassButton.setEnabled(true);
                RoundNumberLabel.setText("");
                RoundNumberLabel.setText(message);
                WaitingRoom.setVisible(false);
                RoundPanel.setVisible(true);
                NewGameButton.setVisible(false);

            } else if (message.startsWith("Points")) {
                String score = message.substring("Points".length());
                CurrentScore.setText("");
                CurrentScore.setText(score);
            } else if (message.startsWith("Game done.")) {
                RoundPanel.setVisible(false);
                LeaderBoardPanel.setVisible(true);
            } else if (message.startsWith("Round00:")) {
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
                WinnersTextArea.setVisible(true);

            }
            else if (message.startsWith("Card:")) {
    // Example message format: "Card:Hearts:Ace:1:images/Hearts/Ace"
    String[] parts = message.split(":");
    Card drawnCard = new Card(parts[1], parts[2], Integer.parseInt(parts[3]), parts[4]); 
    displayPrivateCard(drawnCard); } /*else { 
                                ConnectedPlayers.append(message + "\n");
                        }*/
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
        NewGameButton = new javax.swing.JButton();
        WinnersPanel = new javax.swing.JScrollPane();
        WinnersTextArea = new javax.swing.JTextArea();
        LeaderboardBG = new javax.swing.JLabel();
        UsernamePanel = new javax.swing.JPanel();
        GameName = new javax.swing.JLabel();
        UsernamePrompt = new javax.swing.JLabel();
        UsernameField = new javax.swing.JTextField();
        ConnectButton = new javax.swing.JButton();
        UsernameBG = new javax.swing.JLabel();
        ConnectionRoom = new javax.swing.JPanel();
        ConnectionRoomLabel = new javax.swing.JLabel();
        JoinButton = new javax.swing.JButton();
        ConnectedPlayerPane = new javax.swing.JScrollPane();
        ConnectedPlayers = new javax.swing.JTextArea();
        ConnectionRoomBG = new javax.swing.JLabel();
        WaitingRoom = new javax.swing.JPanel();
        WaitingRoomLabel = new javax.swing.JLabel();
        Timer = new javax.swing.JLabel();
        WaitingRoomPane = new javax.swing.JScrollPane();
        WaitingPlayers = new javax.swing.JTextArea();
        WaitingRoomBG = new javax.swing.JLabel();
        RoundPanel = new javax.swing.JPanel();
        RoundNumberLabel = new javax.swing.JLabel();
        RoundTimerLabel = new javax.swing.JLabel();
        BackOfCardLabel = new javax.swing.JLabel();
        PassButton = new javax.swing.JButton();
        HitButton = new javax.swing.JButton();
        CurrentScoreLabel = new javax.swing.JLabel();
        CurrentScore = new javax.swing.JLabel();
        LeaveGameButton = new javax.swing.JButton();
        CurrentScoreBoardLabel = new javax.swing.JLabel();
        CurrentScoreboardPane = new javax.swing.JScrollPane();
        CurrentScoresText = new javax.swing.JTextArea();
        RoundBG = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("21");
        setExtendedState(6);
        setPreferredSize(new java.awt.Dimension(2560, 1600));
        setResizable(false);
        setSize(new java.awt.Dimension(2560, 1600));
        getContentPane().setLayout(new java.awt.CardLayout());

        LeaderBoardPanel.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        LeaderBoardLabel.setFont(new java.awt.Font("Helvetica Neue", 0, 18)); // NOI18N
        LeaderBoardLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        LeaderBoardLabel.setText("Winners");
        LeaderBoardPanel.add(LeaderBoardLabel, new org.netbeans.lib.awtextra.AbsoluteConstraints(640, 260, 110, 30));

        NewGameButton.setText("New Game");
        LeaderBoardPanel.add(NewGameButton, new org.netbeans.lib.awtextra.AbsoluteConstraints(650, 580, 110, 40));

        WinnersTextArea.setEditable(false);
        WinnersTextArea.setColumns(20);
        WinnersTextArea.setRows(5);
        WinnersPanel.setViewportView(WinnersTextArea);

        LeaderBoardPanel.add(WinnersPanel, new org.netbeans.lib.awtextra.AbsoluteConstraints(520, 340, 360, 200));
        LeaderBoardPanel.add(LeaderboardBG, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, -270, 2570, 1620));

        getContentPane().add(LeaderBoardPanel, "card2");

        UsernamePanel.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        GameName.setFont(new java.awt.Font("Helvetica Neue", 0, 24)); // NOI18N
        GameName.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        GameName.setText("21");
        UsernamePanel.add(GameName, new org.netbeans.lib.awtextra.AbsoluteConstraints(720, 220, -1, -1));

        UsernamePrompt.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        UsernamePrompt.setText("Please enter your username:");
        UsernamePanel.add(UsernamePrompt, new org.netbeans.lib.awtextra.AbsoluteConstraints(600, 290, 270, 30));

        UsernameField.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        UsernameField.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                UsernameFieldActionPerformed(evt);
            }
        });
        UsernamePanel.add(UsernameField, new org.netbeans.lib.awtextra.AbsoluteConstraints(580, 340, 300, 30));

        ConnectButton.setText("Connect");
        UsernamePanel.add(ConnectButton, new org.netbeans.lib.awtextra.AbsoluteConstraints(700, 400, -1, -1));
        UsernamePanel.add(UsernameBG, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, -270, 2570, 1610));

        getContentPane().add(UsernamePanel, "card2");

        ConnectionRoom.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        ConnectionRoomLabel.setFont(new java.awt.Font("Helvetica Neue", 0, 18)); // NOI18N
        ConnectionRoomLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        ConnectionRoomLabel.setText("Connected Room");
        ConnectionRoom.add(ConnectionRoomLabel, new org.netbeans.lib.awtextra.AbsoluteConstraints(640, 240, -1, -1));

        JoinButton.setText("Join");
        ConnectionRoom.add(JoinButton, new org.netbeans.lib.awtextra.AbsoluteConstraints(670, 560, 110, 30));

        ConnectedPlayers.setEditable(false);
        ConnectedPlayers.setColumns(20);
        ConnectedPlayers.setRows(5);
        ConnectedPlayerPane.setViewportView(ConnectedPlayers);

        ConnectionRoom.add(ConnectedPlayerPane, new org.netbeans.lib.awtextra.AbsoluteConstraints(520, 310, 400, 210));
        ConnectionRoom.add(ConnectionRoomBG, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, -290, 2570, 1610));

        getContentPane().add(ConnectionRoom, "card2");

        WaitingRoom.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        WaitingRoomLabel.setFont(new java.awt.Font("Helvetica Neue", 0, 18)); // NOI18N
        WaitingRoomLabel.setText("Waiting Room");
        WaitingRoom.add(WaitingRoomLabel, new org.netbeans.lib.awtextra.AbsoluteConstraints(670, 240, -1, -1));

        Timer.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        WaitingRoom.add(Timer, new org.netbeans.lib.awtextra.AbsoluteConstraints(690, 290, 80, 30));

        WaitingPlayers.setEditable(false);
        WaitingPlayers.setColumns(20);
        WaitingPlayers.setRows(5);
        WaitingRoomPane.setViewportView(WaitingPlayers);

        WaitingRoom.add(WaitingRoomPane, new org.netbeans.lib.awtextra.AbsoluteConstraints(550, 360, 370, 210));
        WaitingRoom.add(WaitingRoomBG, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, -270, 2570, 1610));

        getContentPane().add(WaitingRoom, "card2");

        RoundPanel.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        RoundNumberLabel.setFont(new java.awt.Font("Helvetica Neue", 0, 18)); // NOI18N
        RoundNumberLabel.setText("Round");
        RoundPanel.add(RoundNumberLabel, new org.netbeans.lib.awtextra.AbsoluteConstraints(470, 110, -1, 20));

        RoundTimerLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        RoundPanel.add(RoundTimerLabel, new org.netbeans.lib.awtextra.AbsoluteConstraints(450, 150, 100, 30));

        BackOfCardLabel.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        RoundPanel.add(BackOfCardLabel, new org.netbeans.lib.awtextra.AbsoluteConstraints(370, 190, 270, 400));

        PassButton.setText("Pass");
        PassButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                PassButtonActionPerformed(evt);
            }
        });
        RoundPanel.add(PassButton, new org.netbeans.lib.awtextra.AbsoluteConstraints(370, 630, 110, 40));

        HitButton.setText("Hit");
        HitButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                HitButtonActionPerformed(evt);
            }
        });
        RoundPanel.add(HitButton, new org.netbeans.lib.awtextra.AbsoluteConstraints(530, 630, 110, 40));

        CurrentScoreLabel.setFont(new java.awt.Font("Helvetica Neue", 0, 18)); // NOI18N
        CurrentScoreLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        CurrentScoreLabel.setText("Score:");
        RoundPanel.add(CurrentScoreLabel, new org.netbeans.lib.awtextra.AbsoluteConstraints(860, 150, 150, 50));

        CurrentScore.setBackground(new java.awt.Color(255, 255, 255));
        CurrentScore.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        CurrentScore.setText("0");
        CurrentScore.setOpaque(true);
        RoundPanel.add(CurrentScore, new org.netbeans.lib.awtextra.AbsoluteConstraints(880, 220, 120, 30));

        LeaveGameButton.setText("Leave Game");
        LeaveGameButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                LeaveGameButtonActionPerformed(evt);
            }
        });
        RoundPanel.add(LeaveGameButton, new org.netbeans.lib.awtextra.AbsoluteConstraints(450, 690, 110, 40));

        CurrentScoreBoardLabel.setFont(new java.awt.Font("Helvetica Neue", 0, 18)); // NOI18N
        CurrentScoreBoardLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        CurrentScoreBoardLabel.setText("Scoreboard:");
        RoundPanel.add(CurrentScoreBoardLabel, new org.netbeans.lib.awtextra.AbsoluteConstraints(870, 270, 150, 40));

        CurrentScoresText.setEditable(false);
        CurrentScoresText.setColumns(20);
        CurrentScoresText.setRows(5);
        CurrentScoreboardPane.setViewportView(CurrentScoresText);

        RoundPanel.add(CurrentScoreboardPane, new org.netbeans.lib.awtextra.AbsoluteConstraints(820, 340, 260, 280));
        RoundPanel.add(RoundBG, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, -270, 2570, 1620));

        getContentPane().add(RoundPanel, "card2");

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void UsernameFieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_UsernameFieldActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_UsernameFieldActionPerformed

    private void LeaveGameButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_LeaveGameButtonActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_LeaveGameButtonActionPerformed

    private void PassButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_PassButtonActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_PassButtonActionPerformed

    private void HitButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_HitButtonActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_HitButtonActionPerformed

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
    private javax.swing.JPanel ConnectionRoom;
    private javax.swing.JLabel ConnectionRoomBG;
    private javax.swing.JLabel ConnectionRoomLabel;
    private javax.swing.JLabel CurrentScore;
    private javax.swing.JLabel CurrentScoreBoardLabel;
    private javax.swing.JLabel CurrentScoreLabel;
    private javax.swing.JScrollPane CurrentScoreboardPane;
    private javax.swing.JTextArea CurrentScoresText;
    private javax.swing.JLabel GameName;
    private javax.swing.JButton HitButton;
    private javax.swing.JButton JoinButton;
    private javax.swing.JLabel LeaderBoardLabel;
    private javax.swing.JPanel LeaderBoardPanel;
    private javax.swing.JLabel LeaderboardBG;
    private javax.swing.JButton LeaveGameButton;
    private javax.swing.JButton NewGameButton;
    private javax.swing.JButton PassButton;
    private javax.swing.JLabel RoundBG;
    private javax.swing.JLabel RoundNumberLabel;
    private javax.swing.JPanel RoundPanel;
    private javax.swing.JLabel RoundTimerLabel;
    private javax.swing.JLabel Timer;
    private javax.swing.JLabel UsernameBG;
    private javax.swing.JTextField UsernameField;
    private javax.swing.JPanel UsernamePanel;
    private javax.swing.JLabel UsernamePrompt;
    private javax.swing.JTextArea WaitingPlayers;
    private javax.swing.JPanel WaitingRoom;
    private javax.swing.JLabel WaitingRoomBG;
    private javax.swing.JLabel WaitingRoomLabel;
    private javax.swing.JScrollPane WaitingRoomPane;
    private javax.swing.JScrollPane WinnersPanel;
    private javax.swing.JTextArea WinnersTextArea;
    // End of variables declaration//GEN-END:variables
}
