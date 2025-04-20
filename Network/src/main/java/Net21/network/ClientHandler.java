// Copy

package Net21.network;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.*;

public class ClientHandler implements Runnable{


    private Socket socket;
    private BufferedReader in;
    private PrintWriter out;
    private String playerName;
    private int score = 0;
    public ArrayList<Card> hand = new ArrayList<Card>();

    public ClientHandler(Socket socket){
        this.socket = socket;
    }

    public int getScore(){
        return score;
    }
    
    public void setScore(int s){
        score = s;
    }
    
    @Override 
    public void run(){
        try{

            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new PrintWriter(socket.getOutputStream(),true);

            playerName = in.readLine();
            Server.clients.add(this);
            System.out.println(playerName + " connected.");

            Server.broadcastClients();

            while (true) {
                String message = in.readLine(); // Read user input for the message
                if (message.equalsIgnoreCase("exit")) { 
                    removeClient();
                    out.println("Client left.");
                    break; 
                }
                if (message.equalsIgnoreCase("join")){
                    String joined = Server.join(this);
                    out.println(joined);
                }
                if (message.equalsIgnoreCase("hit")){
                    hit(Server.deck);
                }
                if (message.equalsIgnoreCase("pass")){
                    pass();
                }
                if(message.equalsIgnoreCase("leave game")){
                    leaveGame();
                }
            }



        } catch (IOException e) {
                    e.printStackTrace();
        }finally{
            
        }

    }

    public void sendMessage(String message){
        System.out.println(message);
        out.println(message);
        out.flush();
    }

    public String getPlayerName(){
        return playerName;
    }

    public void removeClient(){
        Server.clients.remove(this);
        Server.waitingRoom.remove(this);
        Server.broadcastClients();
        Server.broadcastWaitingRoom();
        if (Server.waitingRoom.isEmpty()){
            Server.endGame(); // resets timer
        }
    }
    
    public void hit(Deck deck){
        Card card = deck.drawCard();
                    hand.add(card);
                    System.out.println(getPlayerName() + ": " + card);
        int cardValue=card.getValue();
             score+=cardValue;
             out.println("Points"+ score);
              out.println("Card:" + card.suit + ":" + card.rank + ":" + cardValue + ":" + card.path); // test
             System.out.print("Score="+score);
    }
    public void pass(){
        out.println("Points"+ score);   
        System.out.print("Score="+score);
    }
    
    public void leaveGame(){
        Server.waitingRoom.remove(this);
        score = 0;
        if(Server.waitingRoom.isEmpty()){
            Server.resetGame();
        }
        out.println("Points"+ score);
    }

}
