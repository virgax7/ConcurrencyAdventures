package random;

public class StaticBlockAndLineExecution2 {
    public static void main(String[] args) throws InterruptedException {
        final LazyInitializationInTermsOfClassQuoteUsedQuote lazyClass = new LazyInitializationInTermsOfClassQuoteUsedQuote();
        lazyClass.otherMethod();
        Thread.sleep(3000);
        LazyInitializationInTermsOfClassQuoteUsedQuote.getFoo();
    }
}

// This is thread safe because of the jvm lock acquisition
// pretty much a wrapper so that until LazyInitializationInTermsOfClassQuoteUsedQuote.getFoo is called, the jvm doesn't initialize foo
class LazyInitializationInTermsOfClassQuoteUsedQuote {
    private static class FooHolder {
        public static int foo = getFoo();

        public static int getFoo() {
            System.out.println("called getFoo()");
            return foo;
        }
    }

    static int getFoo() {
        return FooHolder.foo;
    }

    void otherMethod() {
        System.out.println("called otherMethod");
    }
}
