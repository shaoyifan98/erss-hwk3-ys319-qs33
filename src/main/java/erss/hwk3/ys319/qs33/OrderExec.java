package erss.hwk3.ys319.qs33;

public abstract class OrderExec implements Action {
    protected final String transactId;

    public OrderExec(String _transactId) {
        this.transactId = _transactId;
    }
}
