package coreClasses.threadLocal;

import java.util.concurrent.CountDownLatch;

import static coreClasses.threadLocal.ThreadLocalDBConnection.getConnection;

public class DBAppFixed {
    public static void main(String[] args) throws InterruptedException {
        final CountDownLatch latch = new CountDownLatch(1);
        final Thread thread1 = new Thread(() -> {
            getConnection().addOrOverwriteTable("this one didn't write");
            getConnection().addOrOverwriteTable("this one didn't write");
            getConnection().addOrOverwriteTable("table1");
            getConnection().addRowIfTableExists("table1", "row1 in thread 1 is written");
            getConnection().close();
            System.out.println(getConnection().getName());
            latch.countDown();
        });

        final Thread thread2 = new Thread(() -> {
            try {
                latch.await();
            } catch (InterruptedException e) {}
            getConnection().addOrOverwriteTable("this one didn't write");
            getConnection().addOrOverwriteTable("this one didn't write");
            getConnection().addRowIfTableExists("table1", "row2 in thread 2 is written");
            System.out.println(getConnection().getName());
        });

        thread1.start();
        thread2.start();

        thread1.join();
        thread2.join();

        System.out.println(getConnection().getDb());
    }
}
