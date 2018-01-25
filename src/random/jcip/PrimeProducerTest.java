package random.jcip;

import java.math.BigInteger;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

public class PrimeProducerTest {
    public static void main(String[] args) throws InterruptedException {
        // The question debugging this program answers is, if I interrupt on this Thread,
        // does it also interrupt the queue or does the queue need to finish it's put method first?
        final PrimeProducer producer = new PrimeProducer();
        producer.start();
        Thread.sleep(1000);
        producer.cancel();
        // yes the queue is also interrupted because it locks using
        // lock.lockInterruptibly(); in the put method
    }
}

class PrimeProducer extends Thread {
    static final BlockingQueue<BigInteger> queue = new ArrayBlockingQueue<>(3);

    public void run() {
        try {
            BigInteger p = BigInteger.ONE;
            while (!Thread.currentThread().isInterrupted()) {
                queue.put(p = p.nextProbablePrime());
            }
        } catch (InterruptedException consumed) {}
    }

    public void cancel() {
        interrupt();
    }
}
