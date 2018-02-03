package random;

// so finally block executes even after a return.. but what's the order of execution?
// finally block has to execute first, before it returns to its caller so this will actually sleep 1s before printing 5;
public class FinallyBlockExecuteAfterOrBeforeReturn {
    public static void main(String[] args) throws InterruptedException {
        System.out.println(getX());
    }

    private static int getX() throws InterruptedException {
        try {
            return 5;
        } finally {
            Thread.sleep(1000);
            System.out.println("finally block");
        }
    }


}
