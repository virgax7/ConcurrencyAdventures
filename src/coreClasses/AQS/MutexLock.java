package coreClasses.AQS;

import java.util.concurrent.locks.AbstractQueuedSynchronizer;

public class MutexLock {
    // state 1 means locked, 0 means unlocked
    private static class Sync extends AbstractQueuedSynchronizer {
        public Sync() {
            setState(0); // initially unlocked
        }

        // wrapper methods because acquire(int arg) and release(int arg)
        public final void acquire() {
            acquire(1);
        }

        public final void release() {
            release(1);
        }

        // by default implementation, tryAcquire and tryRelease just throws an UnsupportedOperationException, so you have to override it if you want to
        // use acquire or release, since acquire calls tryAcquire and release calls tryRelease
        @Override
        protected boolean tryAcquire(int unusedVar) {
            return compareAndSetState(0, 1);
        }

        @Override
        protected boolean tryRelease(int unusedVar) {
            setState(0);
            return true;
        }
    }

    private final Sync sync = new Sync();

    public void lock() {
        sync.acquire();
    }

    public void unlock() {
        sync.release();
    }
}
