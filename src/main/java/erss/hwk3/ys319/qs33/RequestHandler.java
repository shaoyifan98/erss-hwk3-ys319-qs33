package erss.hwk3.ys319.qs33;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.SAXException;

public class RequestHandler implements Runnable {
    private Socket socket;
    private ObjectInputStream ois;
    private ObjectOutputStream oos;
    private List<Action> actions;

    public RequestHandler(Socket socket) {

        this.socket = socket;
        try {
            ois = new ObjectInputStream(socket.getInputStream());
            oos = new ObjectOutputStream(socket.getOutputStream());

        }
        catch (IOException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void run() {
        try {
            String req = (String) ois.readObject();
            //System.out.println("received:");
            //System.out.println(req);
            actions = XMLParser.parseXMLString(req);
            StringBuilder sb = new StringBuilder("<results>\n");
            //execute each action
            for(int i = 0; i < actions.size(); ++i) {
                String result = actions.get(i).execute();
                sb.append(result);
            }
            sb.append("</results>\n");
            String resp = sb.toString();
            oos.writeObject(resp);
            ois.close();
            oos.close();
            socket.close();
        }
        catch(IllegalArgumentException e){
            e.printStackTrace();
        }
        catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        catch (ParserConfigurationException e) {
            e.printStackTrace();
        }
        catch (SAXException e) {
            e.printStackTrace();
        }

    }

   
    
}
