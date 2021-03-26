package erss.hwk3.ys319.qs33;

public class TransCancelError implements Error {
    private final String transactId;
    private final String msg;

    public TransCancelError(String _transactId, String _msg) {
        this.transactId = _transactId;
        this.msg = _msg;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("   <error id=\"");
        sb.append(transactId);
        sb.append("\">");
        sb.append(msg);
        sb.append("</error>\n");
        return sb.toString();
    }
}
