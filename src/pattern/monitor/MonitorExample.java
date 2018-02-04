package pattern.monitor;

import static java.util.stream.IntStream.range;

public class MonitorExample {
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

// all its state, which is just isVacant, is guarded by it's own intrinsic lock
class FittingRoom {
    boolean isVacant = true;

    synchronized void enter(final String name) throws InterruptedException {
        while (!isVacant) {
            wait();
        }
        System.out.println(name + " has entered the fitting room");
        isVacant = false;
        tryOnClothes();
    }

    synchronized void leave(final String name) {
        System.out.println(name + " has left the fitting room");
        isVacant = true;
        notify();
    }

    void tryOnClothes() throws InterruptedException {
        Thread.sleep(200);
    }
}
