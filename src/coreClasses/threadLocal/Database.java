package coreClasses.threadLocal;

import java.util.List;
import java.util.Map;

public interface Database {
    void addOrOverwriteTable(final String table);

    void addRowIfTableExists(String table, String row);

    Map<String, List<String>> getDb();
}
