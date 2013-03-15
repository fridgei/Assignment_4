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
    private SEntryBinding indexedBinding = null;
    private OtherBinding unindexedbinding = null;
 
    public Dbs() {}

    public void setup(String dbNames) throws DatabaseException {
        indexedDb = new SEntryBinding();
        unindexedBinding = new Unindxedbinding();

        DatabaseConfig SConfig = new DatabaseConfig();
        DatabaseConfig RConfig = new DatabaseConfig();
        DatabaseConfig UConfig = new DatabaseConfig();
       

 
        SConfig.setErrorStream(System.err);
        SConfig.setErrorPrefix(indexedDb);
        SConfig.setType(DatabaseType.BTREE);
        SConfig.setAllowCreate(true);
        SConfig.setTransactional(false);
        SConfig.setCacheSize(1000000);
		SConfig.setBtreeComparator(indexCmp);

        UConfig.setErrorStream(System.err);
        UConfig.setErrorPrefix(unindexedDb1);
        UConfig.setType(DatabaseType.HASH);
        UConfig.setAllowPopulate(true); 
        UConfig.setAllowCreate(true);
        UConfig.setTransactional(false);
        UConfig.setCacheSize(1000000);
		UConfig.setBtreeComparator(indexCmp);

        RConfig.setErrorStream(System.err);
        RConfig.setErrorPrefix(unindexedDb2);
        RConfig.setType(DatabaseType.HASH);
        RConfig.setAllowPopulate(true); 
        RConfig.setAllowCreate(true);
        RConfig.setTransactional(false);
        RConfig.setCacheSize(1000000);


        try {
            System.out.println("Database at: " + indexedDb);
            S = new Database(dbNames + "/" + indexedDb, null, SConfig);
        } catch(FileNotFoundException notFound) {
            System.err.println(" WTF Databases: " + notFound.toString());
            notFound.printStackTrace();
            System.exit(-1);
        }

        try {
            R = new Database(dbNames + "/" + unindexedDb1, null, RConfig);
        } catch(FileNotFoundException e) {
            System.err.println(" Error in Secondary creation : " + e.toString());
            e.printStackTrace();
        }

		try {
            U = new Database(dbNames + "/" + unindexedDb2, null, UConfig);
		} catch(FileNotFoundException e) {
			System.err.println("Error in TextDB creation :" + e.toString());
			e.printStackTrace();
		}
    }

    public Database getIndexedDb() {
        return S;
    }

    public Database getRDb() {
        return R;
    }

    public Database getUDb() {
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
