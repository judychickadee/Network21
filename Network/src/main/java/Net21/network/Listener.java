//Copy

package Net21.network;

import java.net.Socket;
import java.io.*;

class Listener implements Runnable {

    private Socket socket;
    private BufferedReader in;
    private Client client;

    public Listener(Socket socket, Client client) throws IOException {
        this.socket = socket;
        this.in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        this.client = client;

    }

    @Override
    public void run() {
        try {
            String serverMessage;
            
            while ((serverMessage = in.readLine()) != null) {
                if (serverMessage.trim().isEmpty()){
                    continue;
                }
                System.out.println("Received from server: " + serverMessage);
                client.handleMessage(serverMessage);
               
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}



