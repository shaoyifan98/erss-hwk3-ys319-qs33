package erss.hwk3.ys319.qs33;

import java.util.ArrayList;
import java.util.Random;

public class RandActionGenerator {

    public static Random generator = new Random(991);
    // private static int accountIndex = 0;
    // private static int symbolIndex = 0;
    // private static int orderIndex = 0;

    public RandActionGenerator() {
        //this.generator = new Random(991);
    }

    public static String createAccount(int i) {
        StringBuilder sb = new StringBuilder();
        sb.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
        sb.append("<create>\n");
        sb.append("  <account id=\"" + i + "\" balance=\"1000000\"/>\n");
        sb.append("  <symbol sym=\"A\">\n");
        sb.append("    <account id=\"" + i + "\">2000</account>\n");
        sb.append("  </symbol>\n");
        sb.append("  <symbol sym=\"B\">\n");
        sb.append("    <account id=\"" + i + "\">2000</account>\n");
        sb.append("  </symbol>\n");
        sb.append("  <symbol sym=\"C\">\n");
        sb.append("    <account id=\"" + i + "\">2000</account>\n");
        sb.append("  </symbol>\n");
        sb.append("</create>");
        return sb.toString();
    }

    public static String getRandomAction() {
        StringBuilder sb = new StringBuilder();
        int i = Math.abs(generator.nextInt()) % 2; // sell or buy
        int account = Math.abs(generator.nextInt()) % 5; // acount from 0 - 4
        char stock = (char) ('A' + Math.abs(generator.nextInt()) % 3); // A, B C
        int amount = Math.abs(generator.nextInt()) % 200;
        int price = Math.abs(generator.nextInt()) % 10;
        if (i == 0) { // sell
            sb.append("<transactions id=\"" + account + "\">\n");
            sb.append("  <order sym=\"" + stock + "\" amount=\"" + (-amount) + "\" limit=\"" + price + "\"/> \n");
            sb.append("</transactions>\n");
        } else if (i == 1) { // buy
            sb.append("<transactions id=\"" + account + "\">\n");
            sb.append("  <order sym=\"" + stock + "\" amount=\"" + (amount) + "\" limit=\"" + price + "\"/> \n");
            sb.append("</transactions>\n");
        }
        return sb.toString();

    }

    // private Action createRandAccount() {
    //     String userName = "" + accountIndex;
    //     accountIndex++;
    //     return new AccountCreation(userName, 50000.0);
    // }

    // private Action createSymbol() {
    //     String symbolName = "SYM" + symbolIndex;
    //     symbolIndex++;
    //     ArrayList<Pair<String, Integer>> targets = new ArrayList<>();
    //     if (accountIndex == 0) {
    //         // add nothing if there is no account
    //         return new SymbolCreation(symbolName, targets);
    //     }
    //     // add the symbol for 5 times
    //     for (int i = 0; i < 5; i++) {
    //         int targetUser = generator.nextInt(accountIndex);
    //         targets.add(new Pair<String, Integer>("" + targetUser, 100));
    //     }
    //     return new SymbolCreation(symbolName, targets);
    // }

    // private Action createOrderAdd() {
    //     orderIndex++;
    //     int targetUser = generator.nextInt(accountIndex);
    //     int targetSymbol = generator.nextInt(symbolIndex);
    //     // amount: 20, 30 or 40
    //     int amount = 20;
    //     int checker = generator.nextInt();
    //     if (checker % 3 == 0) {
    //         amount = 40;
    //     } else if (checker % 2 == 0) {
    //         amount = 30;
    //     }
    //     if (generator.nextInt() % 2 == 0) {
    //         amount = -amount;
    //     }
    //     double limit = 100 + 50 * generator.nextDouble();
    //     return new OrderAdd("" + targetUser, "SYM" + targetSymbol, amount, limit);
    // }

    // private Action createOrderQuery() {
    //     int targetUser = generator.nextInt(accountIndex);
    //     int targetOrder = generator.nextInt(orderIndex);
    //     return new OrderQuery("" + targetUser, "" + targetOrder);
    // }

    // private Action createOrderCancel() {
    //     int targetUser = generator.nextInt(accountIndex);
    //     int targetOrder = generator.nextInt(orderIndex);
    //     return new OrderCancel("" + targetUser, "" + targetOrder);
    // }

    // public ArrayList<Action> createCreationRequests() {
    //     ArrayList<Action> requests = new ArrayList<>();
    //     // create 5 accounts and then 5 account and 5 symbols
    //     for (int i = 0; i < 5; i++) {
    //         requests.add(createRandAccount());
    //     }
    //     for (int i = 0; i < 5; i++) {
    //         requests.add(createRandAccount());
    //         requests.add(createSymbol());
    //     }
    //     return requests;
    // }

    // public ArrayList<Action> createTransActions() {
    //     ArrayList<Action> requests = new ArrayList<>();
    //     for (int i = 0; i < 3; i++) {
    //         requests.add(createOrderAdd());
    //         requests.add(createOrderQuery());
    //         requests.add(createOrderAdd());
    //         requests.add(createOrderAdd());
    //         requests.add(createOrderCancel());
    //         requests.add(createOrderAdd());
    //     }
    //     return requests;
    // }

    

    // public static void main(String[] args) {

    //     RandActionGenerator r = new RandActionGenerator();
    //     for (int i = 0; i < 10; ++i) {
    //         System.out.println(r.createAccount(i));
    //     }
    // }
}
