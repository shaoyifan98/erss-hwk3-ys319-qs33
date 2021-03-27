package erss.hwk3.ys319.qs33;

import java.util.ArrayList;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class TransactRequestList implements RequestList {
    private String accountId;
    private NodeList requests;
    private ArrayList<Action> collectedActions;

    public TransactRequestList(NodeList _requests, String _accountId) {
        this.requests = _requests;
        this.accountId = _accountId;
        this.collectedActions = null;
    }

    private Action parseOrderAdd(Element element) {
        if (!element.hasAttribute("sym") || !element.hasAttribute("amount") || !element.hasAttribute("limit")) {
            throw new IllegalArgumentException("Invalid order add");
        }
        String symbolName = element.getAttribute("sym");
        String amountStr = element.getAttribute("amount");
        int amount = XMLParser.getIntFromString(amountStr);
        String limitStr = element.getAttribute("limit");
        double limit = XMLParser.getDoubleFromString(limitStr);
        return new OrderAdd(accountId, symbolName, amount, limit);
    }

    private Action parseOrderQuery(Element element) {
        if (!element.hasAttribute("id")) {
            throw new IllegalArgumentException("Invalid order query");
        }
        String transactId = element.getAttribute("id");
        return new OrderQuery(accountId, transactId);
    }

    private Action parseOrderCancel(Element element) {
        if (!element.hasAttribute("id")) {
            throw new IllegalArgumentException("Invalid order cancel");
        }
        String transactId = element.getAttribute("id");
        return new OrderCancel(accountId, transactId);
    }

    private Action parseTransact(Element element) {
        if (element.getNodeName().equals("order")) {
            return parseOrderAdd(element);
        }
        else if (element.getNodeName().equals("query")) {
            return parseOrderQuery(element);
        }
        else if (element.getNodeName().equals("cancel")) {
            return parseOrderCancel(element);
        }
        else {
            throw new IllegalArgumentException("Invalid Transaction");
        }
    }

    @Override
    public ArrayList<Action> collect() {
        if (requests == null) {
            return null;
        }
        ArrayList<Action> actions = new ArrayList<>();
        for (int i = 0; i < requests.getLength(); i++) {
            if (requests.item(i).getNodeType() == Node.ELEMENT_NODE) {
                Element element = (Element) requests.item(i);
                Action action = parseTransact(element);
                actions.add(action);
            }
        }
        collectedActions = actions;
        return actions;
    }

    @Override
    public String toString() {
        if (collectedActions == null) {
            collect();
        }
        StringBuilder strBuild = new StringBuilder("");
        for (Action a: collectedActions) {
            strBuild.append(a.toString());
        }
        return strBuild.toString();
    }

}
