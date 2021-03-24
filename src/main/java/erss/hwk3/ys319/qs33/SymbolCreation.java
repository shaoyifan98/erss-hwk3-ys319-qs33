package erss.hwk3.ys319.qs33;

public class SymbolCreation implements Action {
    private final String symbolName;
    private final String accountId;
    private final int symbolNum;

    public SymbolCreation(String _symbolName, String _accountId, int _symbolNum) {
        this.symbolName = _symbolName;
        this.accountId = _accountId;
        this.symbolNum = _symbolNum;
    }

    @Override
    public void execute() {
        
    }

}
