package erss.hwk3.ys319.qs33;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Server {
    private ServerSocket serverSocket;
    private ExecutorService es;
    public Server() {
        es = Executors.newFixedThreadPool(10);
        try {
            serverSocket = new ServerSocket(12345);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public void recvRequest(){
        while(true){
            try {
                Socket socket = serverSocket.accept();
                es.submit(new RequestHandler(socket));
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
       
    }


}
