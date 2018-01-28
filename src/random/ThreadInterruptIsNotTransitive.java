package random;

public class ThreadInterruptIsNotTransitive {
    // shows that interrupting a thread does not interrupt its child threads
    public static void main(String[] args) throws InterruptedException {
        final SleepyThread thread = new SleepyThread("thread1");
        thread.start();
        Thread.sleep(1000);
        thread.interrupt();
    }

    static class SleepyThread extends Thread {
        private final String name;
        // doesn't need to be atomic int or volatile in our case because of thread.start h-b guarantee
        static int id = 1;

        SleepyThread(final String name) {
            this.name = name;
        }

        @Override
        public void run() {
            try {
                System.out.println("now running " + name);
                if (id++ < 4) {
                    (new SleepyThread("thread" + id)).start();
                }
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                System.out.println("Thread " + name + " is interrupted");
            }
        }
    }
}
