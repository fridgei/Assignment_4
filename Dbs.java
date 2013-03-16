import com.sleepycat.db.Database;
import com.sleepycat.db.DatabaseConfig;
import com.sleepycat.db.DatabaseException;
import com.sleepycat.db.DatabaseType;
import com.sleepycat.db.SecondaryDatabase;
import com.sleepycat.db.SecondaryConfig;

import java.util.Comparator;
import java.io.FileNotFoundException;

public class Dbs {

    private Database S = null;
    private Database R = null;
    private Database U = null;
    private String indexedDb = "S";
    private String unindexedDb1 = "R";
    private String unindexedDb2 = "U";
    private IndexedEntryBinding indexedBinding = null;
    private UnindexedBinding unindexedbinding = null;

    public Dbs() {}

    public void setup(String dbNames) throws DatabaseException {
        indexedBinding = new IndexedEntryBinding();
        unindexedbinding = new UnindexedBinding();
        IndexComparator indexCmp = new IndexComparator();

        DatabaseConfig SConfig = new DatabaseConfig();
        DatabaseConfig RConfig = new DatabaseConfig();
        DatabaseConfig UConfig = new DatabaseConfig();

        SConfig.setErrorStream(System.err);
        SConfig.setErrorPrefix(indexedDb);
        SConfig.setType(DatabaseType.BTREE);
        SConfig.setAllowCreate(true);
        SConfig.setTransactional(false);
        //SConfig.setCacheSize(1000000);
		SConfig.setBtreeComparator(indexCmp);

        UConfig.setErrorStream(System.err);
        UConfig.setErrorPrefix(unindexedDb1);
        UConfig.setType(DatabaseType.HASH);
        UConfig.setAllowCreate(true);
        UConfig.setTransactional(false);
        //UConfig.setCacheSize(1000000);
		//UConfig.setBtreeComparator(indexCmp);

        RConfig.setErrorStream(System.err);
        RConfig.setErrorPrefix(unindexedDb2);
        RConfig.setType(DatabaseType.HASH);
        RConfig.setAllowCreate(true);
        RConfig.setTransactional(false);
        //RConfig.setCacheSize(1000000);


        try {
            System.out.println("Database at: " + indexedDb);
            S = new Database(dbNames + "/" + indexedDb, null, SConfig);
        } catch(FileNotFoundException notFound) {
            System.err.println("File not found when creating the S relation " + notFound.toString());
            notFound.printStackTrace();
            System.exit(-1);
        }

        try {
            R = new Database(dbNames + "/" + unindexedDb1, null, RConfig);
        } catch(FileNotFoundException e) {
            System.err.println("File not found when creatig the R relation: " + e.toString());
            e.printStackTrace();
        }

		try {
            U = new Database(dbNames + "/" + unindexedDb2, null, UConfig);
		} catch(FileNotFoundException e) {
			System.err.println("File not found when creating the U relation" + e.toString());
			e.printStackTrace();
		}
    }

    public Database getPrimaryDB() {
        return S;
    }

    public Database getRDB() {
        return R;
    }

    public Database getUDB() {
        return U;
    }

    public void close() {
        try {
            if (S != null) {
                S.close();
            }
			if (R != null) {
				R.close();
            }
            if (U != null) {
                U.close();
            }
        } catch(DatabaseException dbe) {
            System.err.println("Error closing Databases: " + dbe.toString());
            System.exit(-1);
        }
    }
}
