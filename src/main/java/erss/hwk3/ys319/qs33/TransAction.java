package erss.hwk3.ys319.qs33;

public abstract class TransAction implements Action {
    public final String accountId;

    public TransAction(String _accountId) {
        this.accountId = _accountId;
    }
}
