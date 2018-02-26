package coreClasses.threadLocal.weakReferenceAndHashMap;

import java.util.Date;
import java.util.Map;
import java.util.WeakHashMap;

public class WeakHashMapDemo {
    public static void main(String[] args) {
        Foo key = new Foo();
        Date value = new Date();

        final Map<Foo, Date> map = new WeakHashMap<>();
        map.put(key, value);

        key = null;
        System.gc();
        // if key is garbage collected then the entry(key,val) is removed from the map
        System.out.println(map);
    }
}


