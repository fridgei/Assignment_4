import java.lang.Integer;

class IndexedInsertionThread extends thread {

    Database db;
    File[] paths;
    IndexedBinding binding = new IndexedBinding();
    File primaryPath;
    File secondarypath;

    public IndexedInsertionThread(Database db, File primaryPath, File secondaryPath) {
        this.db = db;
        this.primaryPath = primaryPath;
        this.secondaryPath = secondaryPath;
    }

    public void run() {
        String primary = "";
        String secondary = "";
        BufferedReader primarybr = new BufferedReader(new FileReader(this.primaryPath));
        BufferedReader secondarybr = new BufferedReader(new FileReader(this.secondaryPath));
        DatabaseEntry key = new DatabaseEntry();
        DatabaseEntry data = new DatabaseEntry();
        SEntry s;
        try{
            while((primary = primarybr.readLine()) != null) {
                secondary = secondarybr.readLine();
                s = new S(Integer.parseInt(primary), Integer.parseInt(secondary));
                binding.objectToEntry(s, data);
                IntegerBinding(key, Integer.parseInt(primary));
                // GET THIS THIGNS NAME
                this.dbs.getSDB().put(null, key, data);
            }
        } catch (DatabaseEntry e) {
            System.err.println("You fucked up entering in the priamry index");
            e.printStackTrace();
        } catch (NullPointerException npe) {
            System.err.println("You had a null pointer exception in primary index insertion");
            npe.printStackTrace();
        }
    }
}

class UnindexedInsertionThread extends thread {

    Database db;
    File path;
    UnindexedBinding binding = new UnindexedBinding();

    public UnindexedInsertionThread(Database db, File path) {
        this.db = db;
        this.path = path;
    }

    public void run() {
        String primary = "";
        BufferedReader primarybr = new BufferedReader(new FileReader(this.path));
        DatabaseEntry key = new DatabaseEntry();
        DatabaseEntry data = new DatabaseEntry();
        Entry e;
        try{
            while((primary = primarybr.readLine()) != null) {
                e = new Entry(Integer.parseInt(primary));
                IntegerBinding(key, Integer.parseInt(primary));
                IntegerBinding(data, Integer.parseInt(primary));
                this.db.put(null, key, data);
            }
        } catch (DatabaseENtry e) {
            System.err.println("You fucked up entering in the priamry index");
            e.printStackTrace();
        } catch (NullPointerException npe) {
            System.err.println("You had a null pointer exception in primary index insertion");
            npe.printStackTrace();
        }
    }
}

public class Main {
    Dbs dbs = new Dbs();
    public static void populateDb() {
        dbs.setup();
        File SPrimary = new File("SX.dat");
        File SSecondary = new File("SY.dat");
        File RPrimary = new File("RX.dat");
        File TPrimary = new File("TY.dat");
        IndexedInsertionThread primaryIdx = new IndexedInsertionThread(dbs.getPrimary(), SPrimary, SSecondary);
        UnindexedInsertionThread RInsertion = new UninexedInsertionThread(dbs.getRPrimary(), RPrimary);
        UnindexedInsertionThread TInsertion = new UninexedInsertionThread(dbs.getTPrimary(), TPrimary);
        primaryIdx.join();
        RInsertion.join();
        TInsertion.join();
    }
}
