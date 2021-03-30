# Scalability: Exchange Matching

## Introduction

This is ECE568 HW3 by Yifan Shao (ys319) & Qin Sun (qs33). 

## To Run the Server

Run

```bash
sudo docker-compose up
```

If you see a line saying `Server start!`, the server has successfully started. 

## To Use Test Infrastructure
cd into testing/
Please use jdk11 or above java environment to run the test!
if you can not compile the test, please 1.delete old *.class file in the test 2.run with sudo 3. make sure you are in the right file path

### Accuracy Tests

After you have started the server, you can run

```bash
javac clientacctest/*.java
java clientacctest/ClientAccTest
```

It will use the testcases under `tests` folder named `test0.txt`, `test1.txt`, etc., as testcases and generates the test results `act0.txt`, `act1.txt`, etc. You can compare them with the answers `ans0.txt`, `ans1.txt`, etc., with the command

```bash
diff ans0.txt act0.txt
```

Note that the time in the answer files are just placeholders, because the time results always change when you run new tests. 

If you want to add your own new tests, you should name them with the same format. Then you need to go to `tests/TestSupplier.java` and change the `numOfTests` in line 17, and recompile. 

### Scalability Tests

You can refer to `writeup/Report.pdf` for more details. 

To run the randomly-generated scalability test, you can follow the steps like

1. Run `sudo docker-compose up` to start the server. 

2. Run `javac client/*.java` and `java client/Client`. 

3. If you want to limit the CPU usage to 50%, you can run

   ```bash
   cgcreate -g cpu:/test
   cgset -r cpu.cfs period us=100000 test
   cgset -r cpu.cfs quota us=50000 test
   cgexec -g cpu:/test sudo docker-compose up
   ```

   and then run the client as step 2. 
