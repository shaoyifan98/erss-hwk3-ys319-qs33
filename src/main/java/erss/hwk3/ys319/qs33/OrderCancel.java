package erss.hwk3.ys319.qs33;

import java.sql.SQLException;

public class OrderCancel extends OrderExec {

    public OrderCancel(String _transactId) {
        super(_transactId);
    }

    @Override
    public String execute() {
        try {
            DBController.getDBController().tryCancelTransaction(Integer.parseInt(transactId));
        }
        catch (Exception e) {
            return e.getMessage();
        }
        return new String("");
    }

    @Override
    public String toString() {
        return "Cancelling " + this.transactId + "\n";
    }
}
