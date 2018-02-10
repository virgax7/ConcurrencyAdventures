package random;

public class NestedExceptionTryCatchBehaviorTest {
    public static void main(String[] args) {
        try {
            try {
                int x = 0 / 0 ;
            } catch (RuntimeException e) {
                // once caught it is dealt with..
                System.out.println("inner");
            }
        } catch (RuntimeException e) {
            // doesn't run
            System.out.println("outer");
        }
    }
}
