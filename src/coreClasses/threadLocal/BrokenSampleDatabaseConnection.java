package coreClasses.threadLocal;

import java.util.List;
import java.util.Map;

public class BrokenSampleDatabaseConnection {
    static DBConnectionThatOnlyOperatesAfterThirdReq connection;

    // yes, technically we can just return a new instance of DBConnectionThatOnlyOperatesAfterThirdReq to not make it broken, but this is
    // just to demonstrate why ThreadLocal can be convenient to use sometimes
    public synchronized static DBConnectionThatOnlyOperatesAfterThirdReq getConnection() {
        if (connection != null) {
            return connection;
        }
        connection = new DBConnectionThatOnlyOperatesAfterThirdReq(new SampleDatabase());
        return connection;
    }

    static class DBConnectionThatOnlyOperatesAfterThirdReq {
        private final Database db;
        private int count;
        private boolean open;
        private final String name;

        protected DBConnectionThatOnlyOperatesAfterThirdReq(final Database db) {
            makeConnection();
            this.db = db;
            open = true;
            name = "DB connection id : " + Math.random() + " " + Math.random() + " " + Math.random();
        }

        private void makeConnection() {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        public void addOrOverwriteTable(final String table) {
            if (!open) {
                db.addOrOverwriteTable("table " + table + " failed to write because closed");
                return;
            }
            if (++count >= 3) {
                db.addOrOverwriteTable(table);
            }
        }

        public void addRowIfTableExists(final String table, final String row) {
            if (!open) {
                db.addRowIfTableExists(table, "row " + row + " failed to write because closed");
                return;
            }
            if (++count >= 3) {
                db.addRowIfTableExists(table, row);
            }
        }

        public Map<String, List<String>> getDb() {
            return db.getDb();
        }

        public void close() {
            open = false;
        }

        public String getName() {
            return name;
        }
    }
}
