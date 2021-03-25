package erss.hwk3.ys319.qs33;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class DBController {
    private Connection connection = null;
    private Statement statement = null;
    private PreparedStatement psql = null;

    public DBController() {
        try {
            String url = "jdbc:postgresql://67.159.88.156:5432/mydb";
            String user = "postgres";
            String password = "12345";
            Class.forName("org.postgresql.Driver");
            connection = DriverManager.getConnection(url, user, password);
            System.out.println("Connected to " + connection);
            init();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void init() throws SQLException {
        String createAccountTable = "CREATE TABLE if not exists \"account\"(account_id INTEGER primary key,balances NUMERIC(10, 2) NOT NULL)";
        String createUserShareTable = "CREATE TABLE if not exists \"user_share\" (id SERIAL primary key, account_id INTEGER REFERENCES account(account_id), symbol character varying(100) NOT NULL, shares INTEGER NOT NULL)";
        String createTransactionsTable = "CREATE TABLE if not exists \"transactions\"(id SERIAL primary key,account_id INTEGER REFERENCES account(account_id),symbol character varying(100) NOT NULL,shares INTEGER NOT NULL, limit_price NUMERIC(10, 2) NOT NULL, created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP(0), status INTEGER  DEFAULT 0)";
        String createExecutedTable = "CREATE TABLE if not exists \"executed\"(id SERIAL primary key,transaction_id INTEGER REFERENCES transactions(id), account_id INTEGER REFERENCES account(account_id),symbol character varying(100) NOT NULL,shares INTEGER NOT NULL, price NUMERIC(10, 2) NOT NULL, created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP(0))";

        statement = connection.createStatement();
        statement.executeUpdate(createAccountTable);
        statement.executeUpdate(createUserShareTable);
        statement.executeUpdate(createTransactionsTable);
        statement.executeUpdate(createExecutedTable);

    }

    public void createAccount(int id) throws SQLException {
        // int account_id = Integer.parseInt(id);
        psql = connection.prepareStatement("INSERT INTO account(account_id, balances)" + "VALUES(?, ?)");
        psql.setInt(1, id);
        psql.setDouble(2, 100.0);
        psql.executeUpdate();
    }

    public void updateSymbol(int id, String symbol, int share) throws Exception {
        if (share >= 0) {
            tryAddSymbol(id, symbol, share);
        }else{
            tryReduceSymbol(id, symbol, share);
        }
    }

    public void tryAddSymbol(int id, String symbol, int share) throws SQLException {
        if (!hasAccountId(id)) {
            throw new IllegalArgumentException("Account does not exist!");
        }
        // if the account already has the symbol, add into the existed symbol
        if (hasSymbol(id, symbol)) {
            psql = connection
                    .prepareStatement("UPDATE user_share SET shares = shares + ? WHERE account_id = ? AND symbol = ?");
            psql.setInt(1, share);
            psql.setInt(2, id);
            psql.setString(3, symbol);
            psql.executeUpdate();
        } else {
            // insert the symbol to the account
            psql = connection
                    .prepareStatement("INSERT INTO user_share(account_id, symbol, shares)" + "VALUES(?, ?, ?)");
            psql.setInt(1, id);
            psql.setString(2, symbol);
            psql.setInt(3, share);
            psql.executeUpdate();
        }
    }

    /**
     * reduce the symbol. Check 1.account exists 2. enough money
     * @param id
     * @param symbol
     * @param share
     * @throws SQLException
     */
    public void tryReduceSymbol(int id, String symbol, int share) throws SQLException {
        if (!hasAccountId(id)) {
            throw new IllegalArgumentException("Account does not exist!");
        }
        psql = connection
                    .prepareStatement("UPDATE user_share SET shares = shares - ? WHERE account_id = ? AND symbol = ? AND shares >= ?");
        psql.setInt(1, share);
        psql.setInt(2, id);
        psql.setString(3, symbol);
        psql.setInt(4, share);
        if(psql.executeUpdate() == 0){ //if no records are updated
            throw new IllegalArgumentException("Account does not have enough share!");
        }
        
    }

    public boolean hasSymbol(int id, String symbol) {
        try {
            psql = connection.prepareStatement("SELECT id from user_share WHERE account_id = ? AND symbol = ?");
            psql.setInt(1, id);
            psql.setString(2, symbol);
            ResultSet rs = psql.executeQuery();
            if (rs.next()) {
                return true;
            }
            return false;

        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return false;
        }
    }

    public boolean hasAccountId(int id) {
        // int account_id = Integer.parseInt(id);
        try {
            psql = connection.prepareStatement("SELECT account_id from account WHERE account_id = ?");
            psql.setInt(1, id);
            ResultSet rs = psql.executeQuery();
            if (rs.next()) {
                return true;
            }
            return false;

        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return false;
        }

    }

    public void close() {
        try {
            connection.close();
        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        DBController db = new DBController();
        // try {
        // db.createAccount(1);
        // } catch (SQLException e) {
        // // TODO Auto-generated catch block
        // e.printStackTrace();
        // }
        // System.out.println(db.hasAccountId(1));
        // System.out.println(db.hasAccountId(2));
        try {
            db.tryReduceSymbol(1, "bbb", 1);
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        db.close();
    }
}
