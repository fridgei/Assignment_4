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
            System.err.println("Couldn't open primaryPath or secondaryPath in IndexedInsertionThread run()");
            e.printStackTrace();
        }
        DatabaseEntry key = new DatabaseEntry();
        DatabaseEntry data = new DatabaseEntry();
        SEntry s;
        try{
            while((primary = primarybr.readLine()) != null) {
                primary = primary.trim();
                secondary = secondarybr.readLine();
                secondary = secondary.trim();
                s = new SEntry(Integer.parseInt(primary), Integer.parseInt(secondary));
                binding.objectToEntry(s, data);
                IntegerBinding.intToEntry(Integer.parseInt(primary), key);
                //System.out.println(primary);
                // GET THIS THIGNS NAME
                this.db.put(null, key, data);
            }
        } catch (DatabaseException e) {
            System.err.println("Database exception on primary index insertion");
            e.printStackTrace();
        } catch (IOException ioe) {
            System.err.println("IOException in ");
            ioe.printStackTrace();
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
            System.err.println("Couldn't open Unindexed file index in UnindexedInsertionThread run()");
            fnf.printStackTrace();
        } catch (IOException ioe) {
            System.err.println("IOException in UnindexedInsertionThread run()");
            ioe.printStackTrace();
        }

        DatabaseEntry key = new DatabaseEntry();
        DatabaseEntry data = new DatabaseEntry();
        Entry e;
        try{
            while((primary = primarybr.readLine()) != null) {
                primary = primary.trim();
                e = new Entry(Integer.parseInt(primary));
                IntegerBinding.intToEntry(Integer.parseInt(primary), key);
                IntegerBinding.intToEntry(Integer.parseInt(primary), data);
                this.db.put(null, key, data);
            }
        } catch (DatabaseException error) {
            System.err.println("Database exception on secondary index insertion");
            error.printStackTrace();
        } catch (NullPointerException npe) {
            System.err.println("You had a null pointer exception in secondary index insertion");
            npe.printStackTrace();
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
    }
}

public class Main {
    public static Dbs dbs = new Dbs();
    public static void populateDb() {
        try {
            dbs.setup("./db_dir/");
        } catch (DatabaseException e) {
            System.err.println("Databases weren't created right");
            e.printStackTrace();
        }
        File SPrimary = new File("/scratch/CS440Assignment4/SX.dat");
        File SSecondary = new File("/scratch/CS440Assignment4/SY.dat");
        File RPrimary = new File("/scratch/CS440Assignment4/RX.dat");
        File TPrimary = new File("/scratch/CS440Assignment4/TY.dat");
        IndexedInsertionThread primaryIdx = new IndexedInsertionThread(dbs.getPrimaryDB(), SPrimary, SSecondary);
        UnindexedInsertionThread RInsertion = new UnindexedInsertionThread(dbs.getRDB(), RPrimary);
        UnindexedInsertionThread TInsertion = new UnindexedInsertionThread(dbs.getUDB(), TPrimary);
        try {
            primaryIdx.start();
            RInsertion.start();
            TInsertion.start();
            primaryIdx.join();
            RInsertion.join();
            TInsertion.join();
        } catch (InterruptedException inter) {
            inter.printStackTrace();
        }
    }

    public static void main(String[] args) {
        Main.populateDb();
        Relation r = new Relation(dbs.getPrimaryDB(), true, false);
        BaseEntry de = null;

        while (r.hasNext()) {
            de = (BaseEntry) r.next();
            System.out.print(de.getValue());
        }
    }
}
