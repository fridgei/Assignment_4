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
    IndexedEntryBinding binding = new IndexedEntryBinding();

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
        SEntry s;
        try{
            while((primary = primarybr.readLine()) != null) {
                primary = primary.trim();
                int secondary = 0;
                s = new SEntry(Integer.parseInt(primary), secondary);
                binding.objectToEntry(s, data);
                IntegerBinding.intToEntry(Integer.parseInt(primary), key);
                //System.out.println(primary);
                // GET THIS THIGNS NAME
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
    public static void populateDb(String[] args) {
        try {
            dbs.setup("./db_dir/");
        } catch (DatabaseException e) {
            System.err.println("Databases weren't created right");
            e.printStackTrace();
            return;
        }
        File SPrimary = null;
        File SSecondary = null;
        File RPrimary = null;
        File TPrimary = null;
        try {
            SPrimary = new File(args[0]);
            SSecondary = new File(args[1]);
            RPrimary = new File(args[2]);
            TPrimary = new File(args[2]);
        } catch (ArrayIndexOutOfBoundsException e) {
            System.out.println("Usage: <SX.dat> <SY.dat> <RX.dat> <TY.dat>");
            System.exit(1);
        }
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
        Main.populateDb(args);
        /*
        Relation r = new Relation(dbs.getPrimaryDB(), true, false);
        SEntry de = null;

        while (r.hasNext()) {
            de = (SEntry) r.next();
            System.out.println(de.getValue());
        }
        */
        /*
        Relation r = new Relation(dbs.getUDB(), false, false);
        SEntry de = null;

        while (r.hasNext()) {
            de = (SEntry) r.next();
            System.out.println(de.getValue());
        }
        */
        Relation rS = new Relation(dbs.getPrimaryDB(), true, false);
        Relation rU = new Relation(dbs.getUDB(), false, false);
        rS.hasNext();
        rU.hasNext();
        Relation rIntermediate = rS.join(rU);

        Relation rR = new Relation(dbs.getRDB(), false, false);
        Relation result = rIntermediate.join(rR);
        for (int i = 0; i < result.internalArray.length; i++ ) {
            System.out.println(result.internalArray[i].primary);
        }
    }
}
