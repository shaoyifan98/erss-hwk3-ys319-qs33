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
        this.amount = _amount;
        if (this.amount < 0) {
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
    public void execute() {
        
    }

}
