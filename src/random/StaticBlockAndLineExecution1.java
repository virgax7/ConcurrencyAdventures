package random;

import java.util.concurrent.CountDownLatch;

/*
    "The treatment of static fields with initializers (or fields whose value is initialized in a
    static initialization block [JPL 2.2.1 and 2.5.3]) is somewhat special and offers additional thread-safety
    guarantees. Static initializers are run by the JVM at class initialization time, after class loading
    but before the class is used by any thread. Because the JVM acquires a lock during initialization [JLS 12.4.2] and
    this lock is acquired by each thread at least once to ensure that the class has been loaded,
    memory writes made during static initialization are automatically visible to all threads. Thus statically
    initialized objects require no explicit synchronization either during construction or when being referenced.
    However, this applies only to the as-constructed state—if the object is mutable, synchronization is still
    required by both readers and writers to make subsequent modifications visible and to avoid data corruption"(Goetz 16.2.3)
 */
public class StaticBlockAndLineExecution1 {
    public static void main(String[] args) throws InterruptedException {
//        EagerInitialization eager;  This will not make the jvm initialize foo
//        EagerInitialization eager = new EagerInitialization(); // This will make the jvm initialize foo
//        System.out.println(EagerInitialization.foo);  // This will make the jvm initialize foo
        final CountDownLatch latch = new CountDownLatch(1);
        new Thread(() -> {
            try {
                latch.await();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println(EagerInitialization.foo);
        }).start();
        System.out.println("Hi");
        Thread.sleep(3000); // you will notice that it won't print called static block for three seconds
                                // you will also notice that getBar and getFoo has been called as well because "used" EagerInitialization
                                // where EagerIntialization eager; didn't use it.. so the behavior is lazy initialization
                                // you will also notice that static lines have total order and execute as they appear
                                // lexically in the code. for example if you change the lines to foo, static block, bar, cat
                                // output should change as well. Note that this applies for static inialization and
                                // static blocks and not static methods, for example static int getFoo doesn't get called first
        latch.countDown();
    }
}

class EagerInitialization {
    static int getFoo() {
        System.out.println("called getFoo()");
        return 5;
    }

    static {
        System.out.println("called static block to initialize cat");
        cat = 5;
    }

    static int foo = getFoo();
    static int bar = getBar();
    static  int cat;

    static int getBar() {
        System.out.println("called getBar()");
        return bar;
    }
}


