package erss.hwk3.ys319.qs33;

public class AccountCreateError implements Error {
    private final int id;
    private final String msg;

    public AccountCreateError(int _id, String _msg) {
        this.id = _id;
        this.msg = _msg;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("   <error id=\"");
        sb.append(id);
        sb.append("\">");
        sb.append(msg);
        sb.append("</error>\n");
        return sb.toString();
    }
}
