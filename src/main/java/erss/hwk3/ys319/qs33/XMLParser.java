package erss.hwk3.ys319.qs33;
import org.w3c.dom.*;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import javax.xml.parsers.*;
import java.io.*;

public class XMLParser {
    private static Document getXMLFromString(String xmlText) throws ParserConfigurationException, SAXException, IOException {
        DocumentBuilderFactory f = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = f.newDocumentBuilder();
        InputSource input = new InputSource(new StringReader(xmlText));
        return builder.parse(input);
    }

    public static RequestList parseXMLString(String xmlText) throws ParserConfigurationException, SAXException, IOException {
        Document document = getXMLFromString(xmlText);
        Element rootElement = document.getDocumentElement();
        NodeList requests = rootElement.getChildNodes();
        if (rootElement.getNodeName().equals("create")) {
            return new CreationRequestList(requests);
        }
        else if (rootElement.getNodeName().equals("transaction")) {
            return new TransactionRequestList(requests);
        }
        return null;
    }
}
