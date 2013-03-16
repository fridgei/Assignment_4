import java.io.*;
import java.nio.ByteBuffer;
import java.lang.Long;
import java.lang.String;
import java.lang.Integer;
import java.util.Arrays;
import java.util.ArrayList;
import java.util.Set;
import java.util.HashSet;
import java.lang.Integer;

import com.sleepycat.db.Cursor;
import com.sleepycat.db.DatabaseException;
import com.sleepycat.db.DatabaseEntry;
import com.sleepycat.db.Database;
import com.sleepycat.db.LockMode;
import com.sleepycat.db.OperationStatus;
import com.sleepycat.db.SecondaryCursor;
import com.sleepycat.bind.tuple.IntegerBinding;
import com.sleepycat.bind.tuple.StringBinding;

class IndexedInsertionThread extends Thread {
    Database db;
    File[] paths;
    IndexedEntryBinding binding = new IndexedEntryBinding();
    File primaryPath;
    File secondaryPath;

    public IndexedInsertionThread(Database db, File primaryPath, File secondaryPath) {
        this.db = db;
        this.primaryPath = primaryPath;
        this.secondaryPath = secondaryPath;
    }

    public void run() {
        String primary = "";
        String secondary = "";
        BufferedReader primarybr = null;
        BufferedReader secondarybr = null;
        try {
            primarybr = new BufferedReader(new FileReader(this.primaryPath));
            secondarybr = new BufferedReader(new FileReader(this.secondaryPath));
        } catch (FileNotFoundException e) {
        }
        DatabaseEntry key = new DatabaseEntry();
        DatabaseEntry data = new DatabaseEntry();
        SEntry s;
        try{
            while((primary = primarybr.readLine()) != null) {
                secondary = secondarybr.readLine();
                s = new SEntry(Integer.parseInt(primary), Integer.parseInt(secondary));
                binding.objectToEntry(s, data);
                IntegerBinding.intToEntry(Integer.parseInt(primary), key);
                // GET THIS THIGNS NAME
                this.db.put(null, key, data);
            }
        } catch (DatabaseException e) {
            System.err.println("You fucked up entering in the priamry index");
            e.printStackTrace();
        } catch (IOException e) {
        } catch (NullPointerException npe) {
            System.err.println("You had a null pointer exception in primary index insertion");
            npe.printStackTrace();
        }
    }
}

class UnindexedInsertionThread extends Thread {
    Database db;
    File path;
    UnindexedBinding binding = new UnindexedBinding();

    public UnindexedInsertionThread(Database db, File path) {
        this.db = db;
        this.path = path;
    }

    public void run() {
        String primary = "";
        BufferedReader primarybr = null;
        try {
            primarybr = new BufferedReader(new FileReader(this.path));
        } catch (FileNotFoundException fnf) {
        } catch (IOException ioe) {}

        DatabaseEntry key = new DatabaseEntry();
        DatabaseEntry data = new DatabaseEntry();
        Entry e;
        try{
            while((primary = primarybr.readLine()) != null) {
                e = new Entry(Integer.parseInt(primary));
                IntegerBinding.intToEntry(Integer.parseInt(primary), key);
                IntegerBinding.intToEntry(Integer.parseInt(primary), data);
                this.db.put(null, key, data);
            }
        } catch (DatabaseException error) {
            System.err.println("You fucked up entering in the priamry index");
            error.printStackTrace();
        } catch (NullPointerException npe) {
            System.err.println("You had a null pointer exception in primary index insertion");
            npe.printStackTrace();
        } catch (IOException ioe) {}
    }
}

public class Main {
    Dbs dbs = new Dbs();
    public void populateDb() {
        try {
            dbs.setup("./db_dir/");
        } catch (DatabaseException e) {}
        File SPrimary = new File("SX.dat");
        File SSecondary = new File("SY.dat");
        File RPrimary = new File("RX.dat");
        File TPrimary = new File("TY.dat");
        IndexedInsertionThread primaryIdx = new IndexedInsertionThread(dbs.getPrimaryDB(), SPrimary, SSecondary);
        UnindexedInsertionThread RInsertion = new UnindexedInsertionThread(dbs.getRDB(), RPrimary);
        UnindexedInsertionThread TInsertion = new UnindexedInsertionThread(dbs.getUDB(), TPrimary);
        try {
            primaryIdx.join();
            RInsertion.join();
            TInsertion.join();
        } catch (InterruptedException inter) {
        }
    }
}
