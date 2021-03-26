package erss.hwk3.ys319.qs33;


public class OrderCancel extends OrderExec {

    public OrderCancel(String _transactId) {
        super(_transactId);
    }

    @Override
    public String execute() {
        try {
            return DBController.getDBController().cancelTransaction(transactId);
        }
        catch (Exception e) {
            return e.getMessage();
        }
    }

    @Override
    public String toString() {
        return "Cancelling " + this.transactId + "\n";
    }
}
