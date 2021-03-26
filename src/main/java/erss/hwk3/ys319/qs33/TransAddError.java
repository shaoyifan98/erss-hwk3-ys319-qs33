package erss.hwk3.ys319.qs33;

public class TransAddError implements Error {
    private final String symbol;
    private final int amount;
    private final double limit;
    private final String msg;

    public TransAddError(String _symbol, int _amount, double _limit, String _msg) {
        this.symbol = _symbol;
        this.amount = _amount;
        this.limit = _limit;
        this.msg = _msg;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("   <error sym=\"");
        sb.append(symbol + "\" amount=\"");
        sb.append(amount + "\" limit=\"");
        sb.append(limit + "\">");
        sb.append(msg);
        sb.append("</error>\n");
        return sb.toString();
    }
}
