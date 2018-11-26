package MerkleServer;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class MerkleServer {

    public static final String END_OF_SESSION = "close";

    @SuppressWarnings("unused")
    public static void main(String[] args) throws IOException {

        ServerSocket listener = new ServerSocket(1111); //set the correct port
               while (true) {

            log("i'm a server and i'm waiting for new connection and buffer select...", "out");
                    Socket socket = listener.accept();
                    BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                    String result = reader.readLine();

                    log("--- Message received: " + result, "err" );

                    if (result.equals("close")) {
                        socket.close();
                        log("\nIt's time to close this connection as we got a close packet", "out");
                    }else if(socket.isConnected()) {

                        List<String> hashNodes = new ArrayList<>();
                        hashNodes.add("3");
                        hashNodes.add("01");
                        hashNodes.add("4567");

                        BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
                        for (String hashNode:hashNodes) {
                            writer.write(hashNode);
                            writer.newLine();
                        }


                        System.out.println("--- Prova sono arrivato quii gesu: ");
                        writer.flush();
                    }
        }
    }

    private static void log(String str, String mode) {
        switch(mode) {
            case "out": {System.out.println(str); break;}
            case "err": {System.err.println(str); break;}
            default: {}
        }
    }
}
