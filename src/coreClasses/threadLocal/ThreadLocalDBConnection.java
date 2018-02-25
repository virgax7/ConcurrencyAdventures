package coreClasses.threadLocal;
import coreClasses.threadLocal.BrokenSampleDatabaseConnection.DBConnectionThatOnlyOperatesAfterThirdReq;

public class ThreadLocalDBConnection {
    private static final SampleDatabase sampleDb = new SampleDatabase();
    private static final ThreadLocal<DBConnectionThatOnlyOperatesAfterThirdReq> tL =
            ThreadLocal.withInitial(() -> new DBConnectionThatOnlyOperatesAfterThirdReq(sampleDb));


    static DBConnectionThatOnlyOperatesAfterThirdReq getConnection() {
        return tL.get();
    }
}

