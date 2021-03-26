package erss.hwk3.ys319.qs33;

public class SymbolCreateError implements Error {
    private final String symbol;
    private final String accountId;
    private final String msg;

    public SymbolCreateError(String _symbol, String _accountId, String _msg) {
        this.symbol = _symbol;
        this.accountId = _accountId;
        this.msg = _msg;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("   error sym=\"");
        sb.append(symbol + "\" id=\"");
        sb.append(accountId + "\">");
        sb.append(msg);
        sb.append("</error>\n");
        return sb.toString();
    }
}
