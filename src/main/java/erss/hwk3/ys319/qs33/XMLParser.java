package erss.hwk3.ys319.qs33;
import org.w3c.dom.*;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import javax.xml.parsers.*;
import java.io.*;
import java.util.ArrayList;

public class XMLParser {

    public static int getIntFromString(String numStr) {
        if (numStr == null) {
            throw new IllegalArgumentException();
        }
        int num = Integer.parseInt(numStr);
        return num;
    }

    public static double getDoubleFromString(String doubleStr) {
        if (doubleStr == null) {
            throw new IllegalArgumentException();
        }
        double num = Double.parseDouble(doubleStr);
        return num;
    }

    private static Document getXMLFromString(String xmlText) throws ParserConfigurationException, SAXException, IOException {
        DocumentBuilderFactory f = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = f.newDocumentBuilder();
        InputSource input = new InputSource(new StringReader(xmlText));
        return builder.parse(input);
    }

    public static RequestList getRequestList(String xmlText) throws ParserConfigurationException, SAXException, IOException {
        Document document = getXMLFromString(xmlText);
        Element rootElement = document.getDocumentElement();
        NodeList requests = rootElement.getChildNodes();
        if (rootElement.getNodeName().equals("create")) {
            return new CreationRequestList(requests);
        }
        else if (rootElement.getNodeName().equals("transactions")) {
            if (!rootElement.hasAttribute("id")) {
                throw new IllegalArgumentException("No account ID for transactions");
            }
            String accountId = rootElement.getAttribute("id");
            return new TransactRequestList(requests, accountId);
        }
        return null;
    }

    public static ArrayList<Action> parseXMLString(String xmlText) throws ParserConfigurationException, SAXException, IOException {
        return getRequestList(xmlText).collect();
    }
}
