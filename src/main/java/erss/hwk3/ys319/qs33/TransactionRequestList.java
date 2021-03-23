package erss.hwk3.ys319.qs33;
import org.w3c.dom.*;
import javax.xml.parsers.*;
import java.io.*;

public class TransactionRequestList implements RequestList {
    private NodeList requests;

    public TransactionRequestList(NodeList _requests) {
        this.requests = _requests;
    }

    @Override
    public void excecute() {
        
    }

}
