package clientacctest;

import java.io.File;
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
        int numOfTests = 8;
        for (int i = 0; i < numOfTests; i++) {
            // erss-hwk3-ys319-qs33/tests/test0.txt
            String testPathStr = "./clientacctest/tests/test" + i + ".txt";
            Path testPath = Paths.get(testPathStr);
            String ansPathStr = "./clientacctest/tests/ans" + i + ".txt";
            Path ansPath = Paths.get(ansPathStr);
            String test = Files.readString(testPath);
            //System.out.println("client content:" + test);
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
