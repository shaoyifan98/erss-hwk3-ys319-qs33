package erss.hwk3.ys319.qs33;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class Client {
    private ObjectInputStream ois;
    private ObjectOutputStream oos;
    private Socket s;

    public Client(String host, int port) {
        try {
            s = new Socket(host, port);
            oos = new ObjectOutputStream(s.getOutputStream());
            ois = new ObjectInputStream(s.getInputStream());
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String getServerResponse(String toHandle) throws IOException, ClassNotFoundException {
        oos.writeObject(new String(""));
        String str = (String)ois.readObject();
        return str;
    }

    public static void main(String[] args) throws ClassNotFoundException, IOException {
        TestSupplier test = new TestSupplier();
        Client myClient = new Client("127.0.0.1", 12345);
        for (int i = 0; i < test.getTestSize(); i++) {
            String toHandle = test.getIthTest(i);
            String actual = myClient.getServerResponse(toHandle);
            test.verifyIthResponse(actual, i);
        }
    }
}


