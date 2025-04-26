package Net21.network;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Image;
import java.io.*;
import java.net.Socket;
import javax.swing.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;

public class Client extends javax.swing.JFrame {

    private Socket socket;
    private BufferedReader in;
    private PrintWriter out;

    private String username;

    public Client() {
        initComponents();
        
RoundPanel.remove(BackOfCardLabel);
RoundPanel.add(BackOfCardLabel, new org.netbeans.lib.awtextra.AbsoluteConstraints(
        430, 230, 150, 200), 0);  
         BackOfCardLabel.setPreferredSize(new Dimension(150, 200));
    BackOfCardLabel.setMinimumSize(new Dimension(150, 200));
    BackOfCardLabel.setSize(new Dimension(150, 200));
    BackOfCardLabel.setBorder(javax.swing.BorderFactory.createLineBorder(Color.BLACK, 1));
     RoundPanel.add(BackOfCardLabel, new org.netbeans.lib.awtextra.AbsoluteConstraints(
            840, 310, 150, 200));
        initBackgroundImages();
      
       
        UsernamePanel.setVisible(true);
        ConnectionRoom.setVisible(false);
        WaitingRoom.setVisible(false);
        RoundPanel.setVisible(false);
        LeaderBoardPanel.setVisible(false);

        ConnectButton.addActionListener(evt -> connectToServer());
        JoinButton.addActionListener(evt -> joinWaitingRoom()); 

        HitButton.addActionListener(evt -> hit());
        PassButton.addActionListener(evt -> pass());
        LeaveGameButton.addActionListener(evt -> LeaveGame());
        NewGameButton.addActionListener(evt -> LeaveGame());
          ensureBackgroundAtBottom();
              revalidate();
    repaint();

       
        this.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                sendExitMessage();
                System.exit(0); 
            }
        });
    }

   private void initBackgroundImages() {
    try {
        
        ImageIcon carpetIcon = new ImageIcon(getClass().getResource("/images/Carpet.jpg"));
        Image carpetImage = carpetIcon.getImage();
        
       
        Image scaledCarpetRound = carpetImage.getScaledInstance(
            RoundBG.getWidth() > 0 ? RoundBG.getWidth() : 1440, 
            RoundBG.getHeight() > 0 ? RoundBG.getHeight() : 800, 
            Image.SCALE_SMOOTH);
        
        
        RoundBG.setIcon(new ImageIcon(scaledCarpetRound));
        LeaderboardBG.setIcon(new ImageIcon(scaledCarpetRound));
        WaitingRoomBG.setIcon(new ImageIcon(scaledCarpetRound));
        ConnectionRoomBG.setIcon(new ImageIcon(scaledCarpetRound));
        UsernameBG.setIcon(new ImageIcon(scaledCarpetRound));
        
    } catch (Exception e) {
        e.printStackTrace();
        JOptionPane.showMessageDialog(this, "Error loading background images: " + e.getMessage());
    }
}

    private void sendExitMessage() {
        if (out != null) {
            out.println("exit"); 
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
            out = new PrintWriter(socket.getOutputStream(), true); 

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

        out.println("join"); 

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
    try {
        
        ImageIcon cardIcon = card.getImage();
        
       
        BackOfCardLabel.setIcon(cardIcon);
        
        
        BackOfCardLabel.setVisible(true);
        BackOfCardLabel.setOpaque(true);
        
        
        RoundPanel.setComponentZOrder(BackOfCardLabel, 0);
        
        
        BackOfCardLabel.revalidate();
        BackOfCardLabel.repaint();
        RoundPanel.revalidate();
        RoundPanel.repaint();
        
        System.out.println("Card displayed: " + card.toString() + " - Is Visible: " + BackOfCardLabel.isVisible());
    } catch (Exception e) {
        e.printStackTrace();
        System.err.println("Error displaying card: " + e.getMessage());
    }
}
    
   

    public void pass() {
        out.println("Pass");
        HitButton.setEnabled(false);
        PassButton.setEnabled(false);
        System.out.println("Pass button clicked");
    }

    public void LeaveGame() {
        out.println("leave game");

        
        RoundPanel.setVisible(false);
        LeaderBoardPanel.setVisible(false);
        WinnersPanel.setVisible(false);

        
        ConnectionRoom.setVisible(true);
        ConnectionRoomLabel.setVisible(true);  
        JoinButton.setVisible(true);         
        ConnectedPlayerPane.setVisible(true); 
        
        NewGameButton.setVisible(false);
        HitButton.setEnabled(true);
        PassButton.setEnabled(true);

        
        ConnectionRoom.revalidate();
        ConnectionRoom.repaint();
    }

    public void handleMessage(String message) {
        SwingUtilities.invokeLater(() -> {
            System.out.println("Received message: " + message);

           
            if (message.startsWith("Waiting Players:")) {
                WaitingRoom.setVisible(true); 
                WaitingRoomLabel.setVisible(true);
                WaitingPlayers.setText(""); 

               
                String playerList = message.substring("Waiting Players: ".length());
                playerList = playerList.replace(" ", "\n");

                WaitingPlayers.setText(playerList); 
                WaitingPlayers.setCaretPosition(WaitingPlayers.getDocument().getLength());
            } else if (message.startsWith("00:")) {
                Timer.setText("");
                Timer.setText(message);
                if(message.equalsIgnoreCase("00:00")){
                    Timer.setText("");
                    Timer.setText("خلط...");
                }
            } else if (message.startsWith("Cannot join game at this time.")) {
                javax.swing.JOptionPane.showMessageDialog(this, message);
                ConnectionRoom.setVisible(true);
                WaitingRoom.setVisible(false);
            } 
            else if (message.startsWith("Connected Players:")) {
                ConnectedPlayers.setText("");
                String displayMessage = message.substring("Connected Players: ".length());
                displayMessage = displayMessage.replace(" ", "\n");
               
                ConnectedPlayers.setText(displayMessage);
                ConnectedPlayers.setCaretPosition(ConnectedPlayers.getDocument().getLength());
                ConnectionRoom.revalidate();
            } else if (message.startsWith("Round ")) {
                String round;
                if(message.equalsIgnoreCase("Round 1")){
                    round = "الشوط الاول";
                } else if (message.equalsIgnoreCase("Round 2")){
                    round = "الشوط الثاني";
                }else if (message.equalsIgnoreCase("Round 3")){
                    round = "الشوط الثالث";
                }else if (message.equalsIgnoreCase("Round 4")){
                    round = "الشوط الرابع";
                } else{
                    round = "الشوط الخامس";
                }
                HitButton.setEnabled(true);
                PassButton.setEnabled(true);
                RoundNumberLabel.setText("");
                RoundNumberLabel.setText(round);
                WaitingRoom.setVisible(false);
                RoundPanel.setVisible(true);
                NewGameButton.setVisible(false);
                  displayBackOfCard();

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
                WinnersPanel.setVisible(true);

            } else if (message.startsWith("Card:")) {
                
                String[] parts = message.split(":");
                Card drawnCard = new Card(parts[1], parts[2], Integer.parseInt(parts[3]), parts[4]);
                displayPrivateCard(drawnCard);
            } else if(message.startsWith("start")){
                Timer.setText("");
                Timer.setText("...خلط");
            }
            
        });
    }
    
private void ensureBackgroundAtBottom() {
   
    if (RoundBG != null) RoundPanel.setComponentZOrder(RoundBG, RoundPanel.getComponentCount() - 1);
    if (LeaderboardBG != null) LeaderBoardPanel.setComponentZOrder(LeaderboardBG, LeaderBoardPanel.getComponentCount() - 1);
    if (WaitingRoomBG != null) WaitingRoom.setComponentZOrder(WaitingRoomBG, WaitingRoom.getComponentCount() - 1);
    if (ConnectionRoomBG != null) ConnectionRoom.setComponentZOrder(ConnectionRoomBG, ConnectionRoom.getComponentCount() - 1);
    if (UsernameBG != null) UsernamePanel.setComponentZOrder(UsernameBG, UsernamePanel.getComponentCount() - 1);
    
    
}

private void displayBackOfCard() {
    try {
        
        InputStream imgStream = getClass().getResourceAsStream("/images/backofcard.jpeg");
        ImageIcon cardBack = null;
        
        if (imgStream != null) {
            BufferedImage img = ImageIO.read(imgStream);
            if (img != null) {
                cardBack = scaleImage(new ImageIcon(img), 150, 200);
                imgStream.close();
                System.out.println("Successfully loaded back of card image");
            }
        } else {
            
            File imgFile = new File(System.getProperty("user.dir") + "/images/backofcard.jpeg");
            if (imgFile.exists()) {
                BufferedImage img = ImageIO.read(imgFile);
                cardBack = scaleImage(new ImageIcon(img), 150, 200);
                System.out.println("Successfully loaded back of card from file");
            }
        }
        
     
        if (cardBack != null) {
            BackOfCardLabel.setIcon(cardBack);
        } else { 
        
          
        }
        
        
        BackOfCardLabel.setVisible(true);
        
    } catch (Exception e) {
        System.err.println("Failed loading back of card image: " + e.getMessage());
    }}

private ImageIcon scaleImage(ImageIcon icon, int width, int height) {
    java.awt.Image img = icon.getImage();
    java.awt.Image scaledImg = img.getScaledInstance(width, height, java.awt.Image.SCALE_SMOOTH);
    return new ImageIcon(scaledImg);
}
/**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    
    @SuppressWarnings("unchecked")
    
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

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
        CurrentScoresText = new javax.swing.JTextArea() {{     setComponentOrientation(java.awt.ComponentOrientation.RIGHT_TO_LEFT); }};
        RoundBG = new javax.swing.JLabel();
        ConnectionRoom = new javax.swing.JPanel();
        ConnectionRoomLabel = new javax.swing.JLabel();
        JoinButton = new javax.swing.JButton();
        ConnectedPlayerPane = new javax.swing.JScrollPane();
        ConnectedPlayers = new javax.swing.JTextArea() {{     setComponentOrientation(java.awt.ComponentOrientation.RIGHT_TO_LEFT); }};
        ConnectionRoomBG = new javax.swing.JLabel();
        WaitingRoom = new javax.swing.JPanel();
        WaitingRoomLabel = new javax.swing.JLabel();
        Timer = new javax.swing.JLabel();
        WaitingRoomPane = new javax.swing.JScrollPane();
        WaitingPlayers = new javax.swing.JTextArea() {{     setComponentOrientation(java.awt.ComponentOrientation.RIGHT_TO_LEFT); }};
        WaitingRoomBG = new javax.swing.JLabel();
        LeaderBoardPanel = new javax.swing.JPanel();
        LeaderBoardLabel = new javax.swing.JLabel();
        NewGameButton = new javax.swing.JButton();
        WinnersPanel = new javax.swing.JScrollPane();
        WinnersTextArea = new javax.swing.JTextArea() {{     setComponentOrientation(java.awt.ComponentOrientation.RIGHT_TO_LEFT); }};
        LeaderboardBG = new javax.swing.JLabel();
        UsernamePanel = new javax.swing.JPanel();
        GameName = new javax.swing.JLabel();
        UsernamePrompt = new javax.swing.JLabel();
        UsernameField = new javax.swing.JTextField();
        ConnectButton = new javax.swing.JButton();
        UsernameBG = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("21");
        setExtendedState(6);
        setResizable(false);
        setSize(new java.awt.Dimension(2560, 1600));
        getContentPane().setLayout(new java.awt.CardLayout());

        RoundPanel.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        RoundNumberLabel.setFont(new java.awt.Font("Waseem", 0, 48)); // NOI18N
        RoundNumberLabel.setForeground(new java.awt.Color(255, 255, 255));
        RoundNumberLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        RoundNumberLabel.setText("الشوط");
        RoundNumberLabel.setToolTipText("");
        RoundPanel.add(RoundNumberLabel, new org.netbeans.lib.awtextra.AbsoluteConstraints(780, 170, 240, 40));

        RoundTimerLabel.setForeground(new java.awt.Color(255, 255, 255));
        RoundTimerLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        RoundPanel.add(RoundTimerLabel, new org.netbeans.lib.awtextra.AbsoluteConstraints(860, 240, 100, 30));

        BackOfCardLabel.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        RoundPanel.add(BackOfCardLabel, new org.netbeans.lib.awtextra.AbsoluteConstraints(840, 310, 150, 200));

        PassButton.setFont(new java.awt.Font("Waseem", 0, 24)); // NOI18N
        PassButton.setText("تمرير");
        PassButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                PassButtonActionPerformed(evt);
            }
        });
        RoundPanel.add(PassButton, new org.netbeans.lib.awtextra.AbsoluteConstraints(780, 550, 110, 40));

        HitButton.setFont(new java.awt.Font("Waseem", 0, 24)); // NOI18N
        HitButton.setText("ضربه");
        HitButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                HitButtonActionPerformed(evt);
            }
        });
        RoundPanel.add(HitButton, new org.netbeans.lib.awtextra.AbsoluteConstraints(950, 550, 110, 40));

        CurrentScoreLabel.setFont(new java.awt.Font("Waseem", 0, 24)); // NOI18N
        CurrentScoreLabel.setForeground(new java.awt.Color(255, 255, 255));
        CurrentScoreLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        CurrentScoreLabel.setText("النقاط");
        RoundPanel.add(CurrentScoreLabel, new org.netbeans.lib.awtextra.AbsoluteConstraints(390, 160, 150, 50));

        CurrentScore.setBackground(new java.awt.Color(255, 255, 255));
        CurrentScore.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        CurrentScore.setText("0");
        CurrentScore.setOpaque(true);
        RoundPanel.add(CurrentScore, new org.netbeans.lib.awtextra.AbsoluteConstraints(410, 220, 130, 40));

        LeaveGameButton.setFont(new java.awt.Font("Waseem", 0, 24)); // NOI18N
        LeaveGameButton.setText("ترك اللعبة");
        LeaveGameButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                LeaveGameButtonActionPerformed(evt);
            }
        });
        RoundPanel.add(LeaveGameButton, new org.netbeans.lib.awtextra.AbsoluteConstraints(880, 620, 110, 40));

        CurrentScoreBoardLabel.setFont(new java.awt.Font("Waseem", 0, 24)); // NOI18N
        CurrentScoreBoardLabel.setForeground(new java.awt.Color(255, 255, 255));
        CurrentScoreBoardLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        CurrentScoreBoardLabel.setText("النتائج");
        RoundPanel.add(CurrentScoreBoardLabel, new org.netbeans.lib.awtextra.AbsoluteConstraints(400, 300, 150, 40));

        CurrentScoreboardPane.setHorizontalScrollBarPolicy(javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        CurrentScoreboardPane.setVerticalScrollBarPolicy(javax.swing.ScrollPaneConstants.VERTICAL_SCROLLBAR_NEVER);

        CurrentScoresText.setEditable(false);
        CurrentScoresText.setColumns(20);
        CurrentScoresText.setFont(new java.awt.Font("Waseem", 0, 24)); // NOI18N
        CurrentScoresText.setRows(5);
        CurrentScoreboardPane.setViewportView(CurrentScoresText);

        RoundPanel.add(CurrentScoreboardPane, new org.netbeans.lib.awtextra.AbsoluteConstraints(350, 370, 260, 280));
        RoundPanel.add(RoundBG, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 1440, 800));

        getContentPane().add(RoundPanel, "card2");

        ConnectionRoom.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        ConnectionRoomLabel.setFont(new java.awt.Font("Waseem", 0, 100)); // NOI18N
        ConnectionRoomLabel.setForeground(new java.awt.Color(255, 255, 255));
        ConnectionRoomLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        ConnectionRoomLabel.setText("غرفة الاتصال");
        ConnectionRoom.add(ConnectionRoomLabel, new org.netbeans.lib.awtextra.AbsoluteConstraints(530, 180, -1, -1));

        JoinButton.setFont(new java.awt.Font("Waseem", 0, 24)); // NOI18N
        JoinButton.setText("انضم");
        ConnectionRoom.add(JoinButton, new org.netbeans.lib.awtextra.AbsoluteConstraints(660, 570, 110, 40));

        ConnectedPlayerPane.setHorizontalScrollBarPolicy(javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        ConnectedPlayerPane.setVerticalScrollBarPolicy(javax.swing.ScrollPaneConstants.VERTICAL_SCROLLBAR_NEVER);

        ConnectedPlayers.setEditable(false);
        ConnectedPlayers.setColumns(20);
        ConnectedPlayers.setFont(new java.awt.Font("Waseem", 0, 24)); // NOI18N
        ConnectedPlayers.setRows(5);
        ConnectedPlayerPane.setViewportView(ConnectedPlayers);

        ConnectionRoom.add(ConnectedPlayerPane, new org.netbeans.lib.awtextra.AbsoluteConstraints(520, 330, 400, 210));
        ConnectionRoom.add(ConnectionRoomBG, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 1600, 800));

        getContentPane().add(ConnectionRoom, "card2");

        WaitingRoom.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        WaitingRoomLabel.setFont(new java.awt.Font("Waseem", 0, 100)); // NOI18N
        WaitingRoomLabel.setForeground(new java.awt.Color(255, 255, 255));
        WaitingRoomLabel.setText("غرفة الانتظار");
        WaitingRoom.add(WaitingRoomLabel, new org.netbeans.lib.awtextra.AbsoluteConstraints(540, 210, -1, -1));

        Timer.setFont(new java.awt.Font("Waseem", 0, 24)); // NOI18N
        Timer.setForeground(new java.awt.Color(255, 255, 255));
        Timer.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        WaitingRoom.add(Timer, new org.netbeans.lib.awtextra.AbsoluteConstraints(700, 340, 80, 30));

        WaitingRoomPane.setHorizontalScrollBarPolicy(javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        WaitingRoomPane.setVerticalScrollBarPolicy(javax.swing.ScrollPaneConstants.VERTICAL_SCROLLBAR_NEVER);

        WaitingPlayers.setEditable(false);
        WaitingPlayers.setColumns(20);
        WaitingPlayers.setFont(new java.awt.Font("Waseem", 0, 24)); // NOI18N
        WaitingPlayers.setRows(5);
        WaitingRoomPane.setViewportView(WaitingPlayers);

        WaitingRoom.add(WaitingRoomPane, new org.netbeans.lib.awtextra.AbsoluteConstraints(550, 390, 370, 210));
        WaitingRoom.add(WaitingRoomBG, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 1600, 800));

        getContentPane().add(WaitingRoom, "card2");

        LeaderBoardPanel.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        LeaderBoardLabel.setFont(new java.awt.Font("Waseem", 0, 100)); // NOI18N
        LeaderBoardLabel.setForeground(new java.awt.Color(255, 255, 255));
        LeaderBoardLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        LeaderBoardLabel.setText("النتائج");
        LeaderBoardPanel.add(LeaderBoardLabel, new org.netbeans.lib.awtextra.AbsoluteConstraints(550, 180, 300, 110));

        NewGameButton.setFont(new java.awt.Font("Waseem", 0, 24)); // NOI18N
        NewGameButton.setText("لعبة جديدة");
        LeaderBoardPanel.add(NewGameButton, new org.netbeans.lib.awtextra.AbsoluteConstraints(650, 580, 110, 40));

        WinnersPanel.setHorizontalScrollBarPolicy(javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        WinnersPanel.setVerticalScrollBarPolicy(javax.swing.ScrollPaneConstants.VERTICAL_SCROLLBAR_NEVER);

        WinnersTextArea.setEditable(false);
        WinnersTextArea.setColumns(20);
        WinnersTextArea.setFont(new java.awt.Font("Waseem", 0, 24)); // NOI18N
        WinnersTextArea.setRows(5);
        WinnersPanel.setViewportView(WinnersTextArea);

        LeaderBoardPanel.add(WinnersPanel, new org.netbeans.lib.awtextra.AbsoluteConstraints(520, 330, 360, 200));
        LeaderBoardPanel.add(LeaderboardBG, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 1440, 800));

        getContentPane().add(LeaderBoardPanel, "card2");

        UsernamePanel.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        GameName.setFont(new java.awt.Font("Waseem", 0, 100)); // NOI18N
        GameName.setForeground(new java.awt.Color(255, 255, 255));
        GameName.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        GameName.setText("٢١");
        UsernamePanel.add(GameName, new org.netbeans.lib.awtextra.AbsoluteConstraints(700, 180, -1, -1));

        UsernamePrompt.setFont(new java.awt.Font("Waseem", 0, 36)); // NOI18N
        UsernamePrompt.setForeground(new java.awt.Color(255, 255, 255));
        UsernamePrompt.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        UsernamePrompt.setText("ادخل اسم اللاعب:");
        UsernamePanel.add(UsernamePrompt, new org.netbeans.lib.awtextra.AbsoluteConstraints(590, 350, 270, 30));

        UsernameField.setFont(new java.awt.Font("Waseem", 0, 18)); // NOI18N
        UsernameField.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        UsernameField.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                UsernameFieldActionPerformed(evt);
            }
        });
        UsernamePanel.add(UsernameField, new org.netbeans.lib.awtextra.AbsoluteConstraints(580, 410, 300, 40));

        ConnectButton.setFont(new java.awt.Font("Waseem", 0, 24)); // NOI18N
        ConnectButton.setText("اتصل");
        UsernamePanel.add(ConnectButton, new org.netbeans.lib.awtextra.AbsoluteConstraints(690, 490, 100, 40));
        UsernamePanel.add(UsernameBG, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 1440, 800));

        getContentPane().add(UsernamePanel, "card2");

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
        } //</editor-fold>
     

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
