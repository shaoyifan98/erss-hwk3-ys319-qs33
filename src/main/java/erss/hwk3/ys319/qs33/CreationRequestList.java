package erss.hwk3.ys319.qs33;

import java.util.ArrayList;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class CreationRequestList implements RequestList {
    private NodeList requests;
    private ArrayList<Action> collectedActions;

    public CreationRequestList(NodeList _requests) {
        this.requests = _requests;
        this.collectedActions = null;
    }

    private Action parseAccCreation(Element element) {
        if (!element.hasAttribute("id") || !element.hasAttribute("balance")) {
            throw new IllegalArgumentException("Invalid Creation");
        }
        String accountId = element.getAttribute("id");
        String balanceStr = element.getAttribute("balance");
        double balance = XMLParser.getDoubleFromString(balanceStr);
        return new AccountCreation(accountId, balance);
    }

    private Action parseSymCreation(Element element) {
        String symbolName = element.getAttribute("sym");
        ArrayList<Pair<String, Integer>> targets = new ArrayList<>();
        NodeList pairsList = element.getChildNodes();
        for (int i = 0; i < pairsList.getLength(); i++) {
            if (pairsList.item(i).getNodeType() == Node.ELEMENT_NODE) {
                Element pairsElement = (Element) pairsList.item(i);
                if (!pairsElement.getNodeName().equals("account") || !pairsElement.hasAttribute("id")) {
                    throw new IllegalArgumentException();
                }
                String accountId = pairsElement.getAttribute("id");
                String numStr = pairsElement.getTextContent();
                int num = XMLParser.getIntFromString(numStr);
                Pair<String, Integer> target = new Pair<>(accountId, num);
                targets.add(target);
            }
        }
        return new SymbolCreation(symbolName, targets);
    }

    private Action parseCreation(Element element) throws IllegalArgumentException {
        if (element.getNodeName().equals("account")) {
            return parseAccCreation(element);
        }
        else if (element.getNodeName().equals("symbol")) {
            return parseSymCreation(element);
        }
        else {
            throw new IllegalArgumentException();
        }
    }

    @Override
    public ArrayList<Action> collect() {
        ArrayList<Action> actions = new ArrayList<>();
        for (int i = 0; i < requests.getLength(); i++) {
            if (requests.item(i).getNodeType() == Node.ELEMENT_NODE) {
                Element element = (Element) requests.item(i);
                Action action = parseCreation(element);
                actions.add(action);
            }
        }
        this.collectedActions = actions;
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
