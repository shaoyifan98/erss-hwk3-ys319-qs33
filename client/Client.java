package client;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Scanner;

public class Client extends Thread {
    private ObjectInputStream ois;
    private ObjectOutputStream oos;
    private Socket s;
    private String toHandle;
    private static int index = 0;

    public Client(String host, int port, String toHandle) {
        try {
            s = new Socket(host, port);
            oos = new ObjectOutputStream(s.getOutputStream());
            ois = new ObjectInputStream(s.getInputStream());
            this.toHandle = toHandle;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String getServerResponse(String toHandle) throws IOException, ClassNotFoundException {
        oos.writeObject(toHandle);
        String str = (String) ois.readObject();
        return str;
    }

    @Override
    public void run() {
        String actual = "";
        try {
            actual = getServerResponse(toHandle);
        } catch (Exception e) {
            e.printStackTrace();
        }

        close();
    }

    // @Override
    // public void run() {
    // String actual = "";
    // try {
    // actual = getServerResponse(toHandle);
    // }
    // catch (Exception e) {
    // e.printStackTrace();
    // }
    // System.out.println(actual);
    // BufferedWriter outFile;
    // try {
    // String filename = "act" + index + ".txt";
    // System.out.println(index);
    // index++;
    // outFile = new BufferedWriter(new FileWriter(filename));
    // outFile.write(actual + "\n");
    // outFile.close();
    // }
    // catch (IOException e) {
    // e.printStackTrace();
    // }
    // close();
    // }

    public void close() {
        try {
            oos.close();
            ois.close();
            s.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) throws ClassNotFoundException, IOException {
        Scanner scan = new Scanner(System.in);
        System.out.println("Please enter the host ip:");
        String host = scan.nextLine();
        System.out.println("Please enter the port:");
        int port = scan.nextInt();
        System.out.println("Please enter the test scale(e.g:10):");
        int scale = scan.nextInt();
        ArrayList<Client> clients = new ArrayList<Client>(scale);
        // TestSupplier test = new TestSupplier();
        for (int i = 0; i < 5; ++i) {
            Client myClient = new Client(host, port, RandActionGenerator.createAccount(i));
            myClient.run();
        }
        long startTime = System.currentTimeMillis();
        System.out.println("Start from:" + startTime);
        for (int i = 0; i < scale; i++) {
            // String toHandle = test.getIthTest(i);
            Client myClient = new Client(host, port, RandActionGenerator.getRandomAction());
            clients.add(myClient);
            myClient.run();
        }
        for (int i = 0; i < clients.size(); ++i) {
            try {
                clients.get(i).join();
            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }

        long endTime = System.currentTimeMillis();
        System.out.println("End to:" + endTime);
        System.out.println(scale + " requests used:" + (endTime - startTime) + "ms");

        
        
    }
}


