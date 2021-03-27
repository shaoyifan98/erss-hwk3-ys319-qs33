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
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void init() throws SQLException {
        String clearOldTable = "DROP TABLE IF EXISTS executed, user_share, transactions, account";
        String createAccountTable = 
            "CREATE TABLE if not exists \"account\"(account_id INTEGER primary key,balances NUMERIC(10, 2) NOT NULL)";
        String createUserShareTable = 
            "CREATE TABLE if not exists \"user_share\" (id SERIAL primary key, account_id INTEGER REFERENCES account(account_id), symbol character varying(100) NOT NULL, shares INTEGER NOT NULL)";
        String createTransactionsTable = 
            "CREATE TABLE if not exists \"transactions\"(id SERIAL primary key,account_id INTEGER REFERENCES account(account_id),symbol character varying(100) NOT NULL,shares INTEGER NOT NULL, limit_price NUMERIC(10, 2) NOT NULL, is_sell BOOLEAN NOT NULL, created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP(0), status INTEGER  DEFAULT 0)";
        String createExecutedTable = 
            "CREATE TABLE if not exists \"executed\"(id SERIAL primary key,transaction_id INTEGER REFERENCES transactions(id), account_id INTEGER REFERENCES account(account_id),symbol character varying(100) NOT NULL,shares INTEGER NOT NULL, price NUMERIC(10, 2) NOT NULL, created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP(0))";

        statement = connection.createStatement();
        statement.executeUpdate(clearOldTable);
        statement.executeUpdate(createAccountTable);
        statement.executeUpdate(createUserShareTable);
        statement.executeUpdate(createTransactionsTable);
        statement.executeUpdate(createExecutedTable);
    }

    public synchronized void createAccount(int id, double balances) {
        try {
            psql = connection.prepareStatement("INSERT INTO account(account_id, balances)" + "VALUES(?, ?)");
            psql.setInt(1, id);
            psql.setDouble(2, balances);
            psql.executeUpdate();
        }
        catch (SQLException e) {
            e.printStackTrace();
            throw new IllegalArgumentException("Account already exists");
        }
    }

    private void tryAddBalance(int id, double balances) throws SQLException {
        psql = connection.prepareStatement("UPDATE account SET balances = balances + ? WHERE account_id = ?");
        psql.setDouble(1, balances);
        psql.setInt(2, id);
        psql.executeUpdate();
    }

    private void tryReduceBalance(int id, double balances) throws SQLException {
        psql = connection.prepareStatement("UPDATE account SET balances = balances - ? WHERE account_id = ? AND balances >= ?");
        psql.setDouble(1, balances);
        psql.setInt(2, id);
        psql.setDouble(3, balances);
        if (psql.executeUpdate() == 0) { // if no records are updated
            throw new IllegalArgumentException("Account does not have enough money");
        }
    }

    private synchronized void tryAddSymbol(int id, String symbol, int share) throws SQLException {
        if (!hasAccountId(id)) {
            throw new IllegalArgumentException("Account does not exist");
        }
        // if the account already has the symbol, add into the existed symbol
        if (hasSymbol(id, symbol)) {
            psql = connection
                    .prepareStatement("UPDATE user_share SET shares = shares + ? WHERE account_id = ? AND symbol = ?");
            psql.setInt(1, share);
            psql.setInt(2, id);
            psql.setString(3, symbol);
            psql.executeUpdate();
        }
        else {
            // insert the symbol to the account
            psql = connection
                    .prepareStatement("INSERT INTO user_share(account_id, symbol, shares)" + "VALUES(?, ?, ?)");
            psql.setInt(1, id);
            psql.setString(2, symbol);
            psql.setInt(3, share);
            psql.executeUpdate();
        }
    }

    public synchronized String createSymbol(String idStr, String symbol, int share) {
        try {
            int id = Integer.parseInt(idStr);
            tryAddSymbol(id, symbol, share);
            return "   <created sym=\"" + symbol + "\" id=\"" + id + "\"/>\n";
        }
        catch (Exception e) {
            Error error = new SymbolCreateError(symbol, idStr, e.getMessage());
            String errorXML = error.toString();
            return errorXML;
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
    private synchronized void tryReduceSymbol(int id, String symbol, int share) throws SQLException {
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

    private void tryReduceTransactionShare(int id,  int share) throws SQLException {
        //System.out.println("reduce order id" + id + "   " + share);
        psql = connection.prepareStatement(
                "UPDATE transactions SET shares = shares - ? WHERE id = ?");
        psql.setInt(1, share);
        psql.setInt(2, id);
        psql.executeUpdate();
    }

    public synchronized int trySellSymbol(int id, String symbol, int share, double price) throws Exception {
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
        if (rs.next()) {
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
        return sellOrderId;
    }

    public synchronized int tryBuySymbol(int id, String symbol, int share, double price) throws Exception {
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
        return buyOrderId;
    }

    public int openOrder(String idStr, String symbol, int share, double price, boolean isSell) {
        try {
            int id = Integer.parseInt(idStr);
            if (isSell) {
                return trySellSymbol(id, symbol, share, price);
            }
            else {
                return tryBuySymbol(id, symbol, share, price);
            }
        }
        catch (Exception e) {
            String msg = e.getMessage();
            Error error = new TransAddError(symbol, share, price, msg);
            throw new RuntimeException(error.toString());
        }
    }

    private void tryAddIntoExecuted(int transactionId, int accountId, String symbol, int share, double price)
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

    public synchronized String cancelTransaction(String accountIdStr, String transactionIdStr) throws SQLException {
        try {
            int accountId = Integer.parseInt(accountIdStr);
            int transactionId = Integer.parseInt(transactionIdStr);
            psql = connection.prepareStatement(
                "UPDATE transactions SET status = ? AND create_at = ? WHERE id = ? AND account_id = ?", PreparedStatement.RETURN_GENERATED_KEYS);
            psql.setInt(1, 1);
            psql.setTimestamp(2, new java.sql.Timestamp(System.currentTimeMillis()));
            psql.setInt(3, transactionId);
            psql.setInt(4, accountId);
            if (psql.executeUpdate() == 0) {
                String msg = "Transcation does not exist";
                Error error = new TransCancelError(transactionIdStr, msg);
                throw new IllegalArgumentException(error.toString());
            }
            StringBuilder sb = new StringBuilder("   <canceled id=\"" + transactionId + "\">\n");
            sb.append(tryQueryUnexecuted(transactionId));
            sb.append(tryQueryExecuted(transactionId));
            sb.append("   </canceled>\n");
            return sb.toString();
        }
        catch (NumberFormatException e) {
            String msg = e.getMessage();
            Error error = new TransCancelError(transactionIdStr, msg);
            return error.toString();
        }
    }

    private String tryQueryUnexecuted(int transactionId) throws SQLException {
        psql = connection.prepareStatement(
            "SELECT shares, status, created_at from transactions WHERE transaction_id = ?",
            PreparedStatement.RETURN_GENERATED_KEYS
        );
        ResultSet rs = psql.executeQuery();
        // has a result
        if (rs.next()) {
            int shares = rs.getInt(1);
            if (shares == 0) {
                return "";
            }
            int status = rs.getInt(2);
            if (status == 0) {
                return "      <open shares=\"" + shares + "\"/>";
            }
            else {
                java.sql.Timestamp t = rs.getTimestamp(3);
                return "      <canceled shares=\"" + shares + "\" time=\"" + t + "\"/>";
            }
        }
        else {
            String msg = "Transaction does not exist";
            throw new IllegalArgumentException("   <error id=\"" + transactionId + "\">" + msg + "</error>\n");
        }
    }

    private String tryQueryExecuted(int transactionId) throws SQLException {
        psql = connection.prepareStatement(
            "SELECT shares, price, created_at from executed WHERE transaction_id = ?",
            PreparedStatement.RETURN_GENERATED_KEYS
        );
        ResultSet rs = psql.executeQuery();
        StringBuilder sb = new StringBuilder();
        while (rs.next()) {
            int shares = rs.getInt(1);
            double price = rs.getDouble(2);
            java.sql.Timestamp t = rs.getTimestamp(3);
            sb.append("      <executed shares=\"" + shares + "\" price=\"" + price + "\" time=\"" + t + "\"/>\n");
        }
        return sb.toString();
    }

    public String queryTransaction(String transactionIdStr) throws SQLException {
        StringBuilder result = new StringBuilder("   <status> id=\"" + transactionIdStr + "\">" + "\n");
        try {
            int transactionId = Integer.parseInt(transactionIdStr);
            // query open/cancelled
            result.append(tryQueryUnexecuted(transactionId));
            // query executed
            result.append(tryQueryExecuted(transactionId));
        }
        catch (NumberFormatException e0) {
            String msg = e0.getMessage();
            throw new NumberFormatException("   <error id=\"" + transactionIdStr + "\">" + msg + "</error>\n");
        }
        catch (SQLException e1) {
            String msg = "Unexpected SQL exception";
            throw new SQLException("   <error id=\"" + transactionIdStr + "\">" + msg + "</error>\n");
        }
        catch (IllegalArgumentException e2) {
            throw e2;
        }
        result.append("   </status>\n");
        return result.toString();
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
        }
        catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean hasAccountId(int id) {
        try {
            psql = connection.prepareStatement("SELECT account_id from account WHERE account_id = ?");
            psql.setInt(1, id);
            ResultSet rs = psql.executeQuery();
            if (rs.next()) {
                return true;
            }
            return false;

        }
        catch (SQLException e) {
            e.printStackTrace();
            return false;
        }

    }

    public void close() {
        try {
            connection.close();
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static DBController getDBController() {
        return dbc;
    }

    public static void main(String[] args) {
        DBController db = DBController.getDBController();
        try {
           db.tryAddSymbol(1, "A", 10);
           db.tryAddSymbol(1, "B", 10);
           db.tryAddSymbol(2, "A", 5);
           db.tryAddSymbol(2, "B", 5);
           db.tryAddSymbol(3, "A", 15);
           db.tryAddSymbol(3, "B", 15);
           db.trySellSymbol(1, "B", 3, 0.8);
        
        } catch (Exception e) {
            e.printStackTrace();
        }
        db.close();
    }
}
