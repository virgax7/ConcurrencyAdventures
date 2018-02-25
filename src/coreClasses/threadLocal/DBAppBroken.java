package coreClasses.threadLocal;

import java.util.concurrent.CountDownLatch;

import static coreClasses.threadLocal.BrokenSampleDatabaseConnection.getConnection;

public class DBAppBroken {
    public static void main(String[] args) throws InterruptedException {
        final CountDownLatch latch1 = new CountDownLatch(1);
        final CountDownLatch latch2 = new CountDownLatch(1);

        final Thread thread1 = new Thread(() -> {
            getConnection().addOrOverwriteTable("this one didn't write");
            latch1.countDown();
            try {
                latch2.await();
            } catch (InterruptedException consume) { }
            getConnection().addRowIfTableExists("table1", "row2 is not written");
        });

        final Thread thread2 = new Thread(() -> {
            getConnection().addOrOverwriteTable("this one didn't write");
            try {
                latch1.await();
            } catch (InterruptedException consume) {}
            getConnection().addOrOverwriteTable("table1");
            getConnection().addRowIfTableExists("table1", "row1 is written");
            getConnection().close();
            latch2.countDown();
        });

        thread1.start();
        thread2.start();

        thread1.join();
        thread2.join();

        System.out.println(getConnection().getDb());
    }
}
