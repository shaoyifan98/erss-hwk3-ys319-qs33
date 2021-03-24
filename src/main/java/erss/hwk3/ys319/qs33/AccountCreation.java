package erss.hwk3.ys319.qs33;
import java.lang.StringBuilder;

public class AccountCreation implements Action {
    private final String id;
    private final double balance;

    public AccountCreation(String _id, double _balance) {
        this.id = _id;
        this.balance = _balance;
    }

    @Override
    public void execute() {
        
    }

    @Override
    public String toString() {
        StringBuilder strBuild = new StringBuilder("");
        strBuild.append("Creating an account with id " + id);
        strBuild.append(" and balance " + balance + "\n");
        return strBuild.toString();
    }
    
}
