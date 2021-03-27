package erss.hwk3.ys319.qs33;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;

public class TestSupplier {
    private final ArrayList<String> tests;
    private final ArrayList<String> answers;

    public TestSupplier() throws IOException {
        this.tests = new ArrayList<>();
        this.answers = new ArrayList<>();
        int numOfTests = 6;
        for (int i = 0; i < numOfTests; i++) {
            String testPathStr = "./tests/test" + i + ".txt";
            Path testPath = Paths.get(testPathStr);
            String ansPathStr = "./tests/ans" + i + ".txt";
            Path ansPath = Paths.get(ansPathStr);
            String test = Files.readString(testPath);
            String ans = Files.readString(ansPath);
            this.tests.add(test);
            this.answers.add(ans);
        }
    }

    public int getTestSize() {
        return tests.size();
    }

    public String getIthTest(int index) {
        return tests.get(index);
    }

    public void verifyIthResponse(String actual, int index) {
        System.out.println("----------------------------");
        System.out.println("Test " + index + " failed:");
        System.out.println("Expected:");
        System.out.print(answers.get(index));
        System.out.println("Actual:");
        System.out.print(actual);
        System.out.println();
    }
}
