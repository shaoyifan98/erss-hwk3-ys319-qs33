package erss.hwk3.ys319.qs33;

public class OrderCancel extends OrderExec {

    public OrderCancel(String _transactId) {
        super(_transactId);
    }

    @Override
    public void execute() {
        
    }

    @Override
    public String toString() {
        return "Cancelling " + this.transactId + "\n";
    }
}
