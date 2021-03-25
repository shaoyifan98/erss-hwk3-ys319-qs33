package erss.hwk3.ys319.qs33;

public class OrderAdd extends TransAction {
    private final String symbolName;
    private final boolean isSell;
    private int amount;
    private final double limit;

    public OrderAdd(String _accountId, String _symbolName, int _amount, double _limit) {
        super(_accountId);
        this.symbolName = _symbolName;
        this.limit = _limit;
        this.amount = Math.abs(_amount);
        if (_amount < 0) {
            isSell = true;
        }
        else {
            isSell = false;
        }
    }

    public boolean orderIsSell() {
        return isSell;
    }

    @Override
    public String execute() {
        try {
            if (isSell) {
                DBController.getDBController().trySellSymbol(Integer.parseInt(accountId), symbolName, amount, limit);
            }
            else {
                DBController.getDBController().tryBuySymbol(Integer.parseInt(accountId), symbolName, amount, limit);
            }
        }
        catch (Exception e) {
            return e.getMessage();
        }
        return new String("");
    }

    @Override
    public String toString() {
        StringBuilder strBuild = new StringBuilder("");
        strBuild.append("Opening a ");
        if (isSell) {
            strBuild.append("selling order");
        }
        else {
            strBuild.append("buying order");
        }
        strBuild.append(" of " + amount + " " + symbolName + " with limit " + limit + "\n");
        return strBuild.toString();
    }

}
