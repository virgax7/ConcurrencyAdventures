package pattern.delegating_thread_safety;

import java.util.concurrent.atomic.AtomicBoolean;

import static java.util.stream.IntStream.range;

public class FittingRoomDelegatingThreadSafetyExample {
    public static void main(String[] args) {
        final FittingRoom fittingRoom = new FittingRoom();
        final String[] names = {"paul", "helen", "huong", "lana", "bethany", "nathali", "julianna"};
        range(0, 7).forEach(i -> {
            new Thread(() -> {
                try {
                    fittingRoom.enter(names[i]);
                    fittingRoom.leave(names[i]);
                } catch (InterruptedException consume) {
                }
            }).start();
        });
    }
}

// all its state, isVacant, is thread-safe
class FittingRoom {
    final AtomicBoolean isVacant = new AtomicBoolean(true);

    synchronized void enter(final String name) throws InterruptedException {
        while (!isVacant.get()) {
            wait();
        }
        System.out.println(name + " has entered the fitting room");
        isVacant.set(false);
        tryOnClothes();
    }

    synchronized void leave(final String name) {
        System.out.println(name + " has left the fitting room");
        isVacant.set(true);
        notify();
    }

    void tryOnClothes() throws InterruptedException {
        Thread.sleep(200);
    }
}
