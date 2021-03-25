package erss.hwk3.ys319.qs33;
import java.lang.StringBuilder;
import java.sql.SQLException;

public class AccountCreation implements Action {
    private final String id;
    private final double balance;

    public AccountCreation(String _id, double _balance) {
        this.id = _id;
        this.balance = _balance;
    }

    @Override
    public String execute() {

        try {
            DBController.getDBController().createAccount(Integer.parseInt(id), balance);
        } catch (Exception e) {
            // TODO Auto-generated catch block
            return new String("account does not exist");
        } 
        return new String("");
    }

    @Override
    public String toString() {
        StringBuilder strBuild = new StringBuilder("");
        strBuild.append("Creating an account with id " + id);
        strBuild.append(" and balance " + balance + "\n");
        return strBuild.toString();
    }
    
}
