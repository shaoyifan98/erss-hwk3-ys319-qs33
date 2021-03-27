package erss.hwk3.ys319.qs33;


public class OrderCancel extends OrderExec {

    public OrderCancel(String _accountId, String _transactId) {
        super(_accountId, _transactId);
    }

    @Override
    public String execute() {
        try {
            return DBController.getDBController().cancelTransaction(accountId, transactId);
        }
        catch (Exception e) {
            return e.getMessage();
        }
    }

    @Override
    public String toString() {
        return "Cancelling " + this.transactId + " of account " + accountId + "\n";
    }
}
