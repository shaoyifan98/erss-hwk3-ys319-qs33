package clientacctest;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.Scanner;

public class ClientAccTest extends Thread {
    private ObjectInputStream ois;
    private ObjectOutputStream oos;
    private Socket s;
    private String toHandle;
    private static int index = 0;

    public ClientAccTest(String host, int port, String toHandle) {
        try {
            s = new Socket(host, port);
            oos = new ObjectOutputStream(s.getOutputStream());
            ois = new ObjectInputStream(s.getInputStream());
            this.toHandle = toHandle;
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String getServerResponse(String toHandle) throws IOException, ClassNotFoundException {
        oos.writeObject(toHandle);
        String str = (String)ois.readObject();
        return str;
    }

   
    @Override
    public void run() {
        String actual = "";
		try {
			actual = getServerResponse(toHandle);
		}
        catch (Exception e) {
			e.printStackTrace();
		}
        System.out.println(actual);
        BufferedWriter outFile;
        try {
            String filename = "./clientacctest/tests/act" + index + ".txt";
            System.out.println(index);
            index++;
            outFile = new BufferedWriter(new FileWriter(filename));
            outFile.write(actual + "\n");
            outFile.close();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        close();
    }

    public void close() {
        try {
            oos.close();
            ois.close();
            s.close();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) throws ClassNotFoundException, IOException {
        Scanner scan = new Scanner(System.in);
        System.out.println("Please enter the host ip:");
        String host = scan.nextLine();
        System.out.println("Please enter the port:");
        int port = scan.nextInt();
        TestSupplier test = new TestSupplier();
        for (int i = 0; i < test.getTestSize(); i++) {
            String toHandle = test.getIthTest(i);
            ClientAccTest myClient = new ClientAccTest(host, port, toHandle);
            myClient.run();
        }
    }
}

