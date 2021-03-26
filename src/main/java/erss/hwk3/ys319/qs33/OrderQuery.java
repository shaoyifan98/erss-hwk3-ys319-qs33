package erss.hwk3.ys319.qs33;

public class OrderQuery extends OrderExec {

    public OrderQuery(String _transactId) {
        super(_transactId);
    }

    @Override
    public String execute() {
        try {
            return DBController.getDBController().queryTransaction(transactId);
        }
        catch (Exception e) {
            return e.getMessage();
        }
    }

    @Override
    public String toString() {
        return "Querying " + this.transactId + "\n";
    }

}
