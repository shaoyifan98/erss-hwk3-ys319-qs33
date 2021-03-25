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
    private static DBController dbc = new DBController();

    private DBController() {
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
        String createTransactionsTable = "CREATE TABLE if not exists \"transactions\"(id SERIAL primary key,account_id INTEGER REFERENCES account(account_id),symbol character varying(100) NOT NULL,shares INTEGER NOT NULL, limit_price NUMERIC(10, 2) NOT NULL, is_sell BOOLEAN NOT NULL, created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP(0), status INTEGER  DEFAULT 0)";
        String createExecutedTable = "CREATE TABLE if not exists \"executed\"(id SERIAL primary key,transaction_id INTEGER REFERENCES transactions(id), account_id INTEGER REFERENCES account(account_id),symbol character varying(100) NOT NULL,shares INTEGER NOT NULL, price NUMERIC(10, 2) NOT NULL, created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP(0))";

        statement = connection.createStatement();
        statement.executeUpdate(createAccountTable);
        statement.executeUpdate(createUserShareTable);
        statement.executeUpdate(createTransactionsTable);
        statement.executeUpdate(createExecutedTable);

    }

    public void createAccount(int id, double balances) {
        // int account_id = Integer.parseInt(id);
        try {
            psql = connection.prepareStatement("INSERT INTO account(account_id, balances)" + "VALUES(?, ?)");
            psql.setInt(1, id);
            psql.setDouble(2, balances);
            psql.executeUpdate();
        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            throw new IllegalArgumentException("Account already exists!");
        }
       
    }

    // public void updateSymbol(int id, String symbol, int share) throws Exception {
    // if (share >= 0) {
    // tryAddSymbol(id, symbol, share);
    // }else{
    // tryReduceSymbol(id, symbol, share);
    // }
    // }

    public void tryAddBalance(int id, double balances) throws SQLException {
        psql = connection.prepareStatement("UPDATE account SET balances = balances + ? WHERE account_id = ?");
        psql.setDouble(1, balances);
        psql.setInt(2, id);
        psql.executeUpdate();
    }

    public void tryReduceBalance(int id, double balances) throws SQLException {
        psql = connection.prepareStatement("UPDATE account SET balances = balances - ? WHERE account_id = ? AND balances >= ?");
        psql.setDouble(1, balances);
        psql.setInt(2, id);
        psql.setDouble(3, balances);
        if (psql.executeUpdate() == 0) { // if no records are updated
            throw new IllegalArgumentException("Account does not have enough money!");
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
     * reduce the symbol. Check 1.account exists 2. enough share
     * 
     * @param id
     * @param symbol
     * @param share
     * @throws SQLException
     */
    public void tryReduceSymbol(int id, String symbol, int share) throws SQLException {
        if (!hasAccountId(id)) {
            throw new IllegalArgumentException("Account does not exist!");
        }
        psql = connection.prepareStatement(
                "UPDATE user_share SET shares = shares - ? WHERE account_id = ? AND symbol = ? AND shares >= ?");
        psql.setInt(1, share);
        psql.setInt(2, id);
        psql.setString(3, symbol);
        psql.setInt(4, share);
        if (psql.executeUpdate() == 0) { // if no records are updated
            throw new IllegalArgumentException("Account does not have enough share!");
        }

    }

    public void tryAddTransactionShare(int id,  int share) throws SQLException {
        psql = connection.prepareStatement(
                "UPDATE transactions SET shares = shares + ? WHERE id = ?");
        psql.setInt(1, share);
        psql.setInt(2, id);
        psql.executeUpdate();
    }

     public void tryReduceTransactionShare(int id,  int share) throws SQLException {
        System.out.println("reduce order id" + id + "   " + share);
        psql = connection.prepareStatement(
                "UPDATE transactions SET shares = shares - ? WHERE id = ?");
        psql.setInt(1, share);
        psql.setInt(2, id);
        psql.executeUpdate();
    }
    

    public void trySellSymbol(int id, String symbol, int share, double price) throws Exception {
        if (!hasAccountId(id)) {
            throw new IllegalArgumentException("Account does not exist!");
        }
        ResultSet rs = null;
        tryReduceSymbol(id, symbol, share);

        //first, the order into the transaction table
        psql = connection.prepareStatement(
                "INSERT INTO transactions(account_id, symbol, shares, limit_price, is_sell)" + "VALUES(?, ?, ?, ?, ?)", PreparedStatement.RETURN_GENERATED_KEYS);
        psql.setInt(1, id);
        psql.setString(2, symbol);
        psql.setInt(3, share);
        psql.setDouble(4, price);
        psql.setBoolean(5, true);
        psql.executeUpdate();
        rs = psql.getGeneratedKeys();

        //get the auto generated key from the table
        int sellOrderId = 0;
        if(rs.next()){
            sellOrderId = rs.getInt(1);
        }

        //search the potential buyer order to match
        psql = connection.prepareStatement(
                "SELECT id, account_id, shares, limit_price from transactions WHERE symbol = ?  AND is_sell = ?  AND shares != 0 AND status = 0 AND limit_price >= ? ORDER BY limit_price DESC;");
        psql.setString(1, symbol);
        psql.setBoolean(2, false);
        psql.setDouble(3, price);
        psql.executeQuery();
        rs = psql.executeQuery();
        

        double gain = 0; //the money seller get 
        int leftShare = share; //the share which are waited to be matched

        while (rs.next()) {
            if (leftShare == 0) {
                break;
            }
            int buyOrderId = rs.getInt(1);
            int buyerId = rs.getInt(2);
            int buyShare = rs.getInt(3);
            double buy_price = rs.getDouble(4);
            int agreedShare = Math.min(share, buyShare);

            //process buyer
            tryAddSymbol(buyerId, symbol, agreedShare);
            tryReduceTransactionShare(buyOrderId, agreedShare);
            tryAddIntoExecuted(buyOrderId, buyerId, symbol, agreedShare, buy_price);
            //process seller
            tryAddIntoExecuted(sellOrderId, id, symbol, agreedShare, buy_price);
            gain += agreedShare * buy_price;
            leftShare -= agreedShare;
        }

        //update the db for seller
        tryAddBalance(id, gain);
        tryReduceTransactionShare(sellOrderId, share - leftShare);

    }

    public void tryBuySymbol(int id, String symbol, int share, double price) throws Exception {
        if (!hasAccountId(id)) {
            throw new IllegalArgumentException("Account does not exist!");
        }
        ResultSet rs = null;
        tryReduceBalance(id, share * price);

        //first, the order into the transaction table
        psql = connection.prepareStatement(
                "INSERT INTO transactions(account_id, symbol, shares, limit_price, is_sell)" + "VALUES(?, ?, ?, ?, ?)", PreparedStatement.RETURN_GENERATED_KEYS);
        psql.setInt(1, id);
        psql.setString(2, symbol);
        psql.setInt(3, share);
        psql.setDouble(4, price);
        psql.setBoolean(5, false);
        psql.executeUpdate();
        rs = psql.getGeneratedKeys();

        //get the auto generated key from the table
        int buyOrderId = 0;
        if(rs.next()){
            buyOrderId = rs.getInt(1);
        }

        //search the potential sell order to match
        psql = connection.prepareStatement(
                "SELECT id, account_id, shares, limit_price from transactions WHERE symbol = ? AND shares != 0 AND is_sell = ? AND status = 0 AND limit_price <= ? ORDER BY limit_price ASC;");
        psql.setString(1, symbol);
        psql.setBoolean(2, true);
        psql.setDouble(3, price);
        psql.executeQuery();
        rs = psql.executeQuery();
        

        double gain = 0; //the money buyer left 
        int leftShare = share; //the share which are waited to be matched

        while (rs.next()) {
            if (leftShare == 0) {
                break;
            }
            int sellOrderId = rs.getInt(1);
            int sellerId = rs.getInt(2);
            int sellShare = rs.getInt(3);
            double sellPrice = rs.getDouble(4);
            int agreedShare = Math.min(share, sellShare);
            double agreedAmount = agreedShare * sellPrice;
            //process seller
            tryAddBalance(sellerId, agreedAmount);
            tryReduceTransactionShare(sellOrderId, agreedShare);
            tryAddIntoExecuted(sellOrderId, sellerId, symbol, agreedShare, sellPrice);

            //process buyer
            tryAddIntoExecuted(buyOrderId, id, symbol, agreedShare, sellPrice);
            gain += agreedShare * (price - sellPrice);
            leftShare -= agreedShare;
        }

        //update the db for buyer
        tryAddBalance(id, gain);
        tryReduceTransactionShare(buyOrderId, share - leftShare);
    }


    public void tryAddIntoExecuted(int transactionId, int accountId, String symbol, int share, double price)
            throws SQLException {
        psql = connection.prepareStatement(
            "INSERT INTO executed(transaction_id, account_id, symbol, shares, price)" + "VALUES(?, ?, ?, ?, ?)", PreparedStatement.RETURN_GENERATED_KEYS);
        psql.setInt(1, transactionId);
        psql.setInt(2, accountId);
        psql.setString(3, symbol);
        psql.setInt(4, share);
        psql.setDouble(5, price);
        psql.executeUpdate();
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

    public static DBController getDBController() {
        return dbc;
    }

    public static void main(String[] args) {
        DBController db = DBController.getDBController();
        // try {
        // db.createAccount(1);
        // } catch (SQLException e) {
        // // TODO Auto-generated catch block
        // e.printStackTrace();
        // }
        // System.out.println(db.hasAccountId(1));
        // System.out.println(db.hasAccountId(2));
        try {
           // db.tryReduceSymbol(1, "bbb", 1);
        //    db.createAccount(1, 200);
        //    db.createAccount(2, 150);
           //db.createAccount(3, 100);

           db.tryAddSymbol(1, "A", 10);
           db.tryAddSymbol(1, "B", 10);
           db.tryAddSymbol(2, "A", 5);
           db.tryAddSymbol(2, "B", 5);
           db.tryAddSymbol(3, "A", 15);
           db.tryAddSymbol(3, "B", 15);

        // db.tryReduceSymbol(4, "A", 5);
        // db.tryReduceSymbol(2, "B", 1);
        // db.tryReduceSymbol(1, "a", 1);
        //db.tryReduceSymbol(1, "A", 100);

        //db.tryReduceBalance(2, 151);
        // db.tryBuySymbol(2, "B", 2, 1);
        // db.tryBuySymbol(3, "B", 2, 2);
        db.trySellSymbol(1, "B", 3, 0.8);
        
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        db.close();
    }
}
