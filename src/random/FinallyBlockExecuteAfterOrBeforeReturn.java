package random;

// so finally block executes even after a return.. but what's the order of execution?
// finally block has to execute first, before it returns to its caller so this will actually sleep 1s before printing 5;
// but note that everything up to the return statement executes..
// this is why jcip listing 13.4-5 is correct since msg is already sent before returning flag
public class FinallyBlockExecuteAfterOrBeforeReturn {
    public static void main(String[] args) throws InterruptedException {
        System.out.println(getX());
    }

    private static int getX() throws InterruptedException {
        try {
            System.out.println("Will print before finally block");
            return 5;
        } finally {
            Thread.sleep(1000);
            System.out.println("finally block");
        }
    }


}
