package erss.hwk3.ys319.qs33;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class Client extends Thread{
    private ObjectInputStream ois;
    private ObjectOutputStream oos;
    private Socket s;
    private String toHandle;

    public Client(String host, int port, String toHandle) {
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
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
            //test.verifyIthResponse(actual, i);
        System.out.println(actual);
        close();
    }

    public void close(){
        try {
            oos.close();
            ois.close();
            s.close();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        
    }

    public static void main(String[] args) throws ClassNotFoundException, IOException {
        TestSupplier test = new TestSupplier();
       
        for (int i = 0; i < test.getTestSize(); i++) {
            String toHandle = test.getIthTest(i);
            Client myClient = new Client("127.0.0.1", 12345, toHandle);
            myClient.run();
            //myClient.start();
        }
    }
}


