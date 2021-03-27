package erss.hwk3.ys319.qs33;

import java.util.ArrayList;


public class SymbolCreation implements Action {
    private final String symbolName;
    private final ArrayList<Pair<String, Integer>> targets;

    public SymbolCreation(String _symbolName, ArrayList<Pair<String, Integer>> _targets) {
        this.symbolName = _symbolName;
        this.targets = _targets;
    }

    @Override
    public String execute() {
        try {
            StringBuilder sb = new StringBuilder("");
            for (Pair<String, Integer> target: targets) {
                String resp = DBController.getDBController().createSymbol(target.getFirst(), symbolName, target.getSecond());
                sb.append(resp);
            }
            return sb.toString();
        }
        catch (Exception e) {
            String msg = e.getMessage();
            return msg;
        }
    }

    @Override
    public String toString() {
        StringBuilder strBuild = new StringBuilder("");
        strBuild.append("Creating a symbol with name " + symbolName + " and:\n");
        for (Pair<String, Integer> t: targets) {
            strBuild.append("  " + t.getFirst() + ": " + t.getSecond() + "\n");
        }
        return strBuild.toString();
    }

}
