package coreClasses.threadLocal;

import java.util.*;

public class SampleDatabase implements Database {
    private final Map<String, List<String>> db = new HashMap<>();

    @Override
    public synchronized void addOrOverwriteTable(final String table) {
        db.put(table, new ArrayList<>());
    }

    @Override
    public synchronized void addRowIfTableExists(final String table, final String row) {
        if (db.get(table) != null) {
            db.get(table).add(row);
        }
    }

    @Override
    public synchronized Map<String, List<String>> getDb() {
        return Collections.unmodifiableMap(db);
    }

    // other functionalities...
}
