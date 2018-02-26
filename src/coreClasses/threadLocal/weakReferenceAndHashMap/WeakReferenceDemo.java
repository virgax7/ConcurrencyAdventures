package coreClasses.threadLocal.weakReferenceAndHashMap;

import java.lang.ref.WeakReference;

public class WeakReferenceDemo {
    public static void main(String[] args) {
        // Here is a strong(regular java reference to an object) reference to a Foo object
        Foo foo = new Foo();
        WeakReference<Foo> weakReference = new WeakReference<>(foo);

        System.out.println(weakReference.get());
        foo = null;
        // once we run the garbage collector, or it runs from from the JVM the foo is thrown away and our weakReference to it is null
        System.gc();
        System.out.println(weakReference.get());
    }
}

class Foo {}
