package erss.hwk3.ys319.qs33;

public abstract class OrderExec extends TransAction {
    protected final String transactId;

    public OrderExec(String _accountId, String _transactId) {
        super(_accountId);
        this.transactId = _transactId;
    }
}
