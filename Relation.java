import java.util.*;

import com.sleepycat.db.Cursor;
import com.sleepycat.db.DatabaseException;
import com.sleepycat.db.DatabaseEntry;
import com.sleepycat.db.Database;
import com.sleepycat.db.LockMode;
import com.sleepycat.db.OperationStatus;


public class Relation implements Iterator {

    Database db;
    boolean isIndex;
    boolean isIntermediate;
    Cursor relCursor = null;
    int listIndex = 0;
    OperationStatus ret;
    DatabaseEntry currentEntry = new DatabaseEntry();
    DatabaseEntry currentKey = new DatabaseEntry();
    ArrayList<BaseEntry> internalList;
    IndexedEntryBinding iBinding = new IndexedEntryBinding();
    UnindexedBinding unBinding = new UnindexedBinding();

    public Relation(Database db) {
        this.db = db;
    }

    public Relation(ArrayList<BaseEntry> initial){
        this.internalList = initial;
        this.isIntermediate = true;
    }

    public Relation(Database db, boolean isIndex, boolean isIntermediate) {
        this.db = db;
        this.isIndex = isIndex;
        this.isIntermediate = isIntermediate;
    }

    public void setCursor(){
        try {
            if(isIndex) {
                this.relCursor = this.db.openCursor(null, null);
                this.relCursor.getFirst(this.currentKey, this.currentEntry, LockMode.DEFAULT);
            } else {
                this.listIndex = 0;
            }
        } catch (DatabaseException e) {
            System.out.println("Set cursor failed");
            e.printStackTrace();
        }
    }

    public boolean hasNext() {
        System.out.println("In hasNext");
        try {
            if(this.isIndex) {

                if(this.relCursor == null) {
                    this.setCursor();
                }

                this.ret = this.relCursor.getNextDup(
                    this.currentKey, this.currentEntry, LockMode.DEFAULT
                );

                if(this.ret != OperationStatus.SUCCESS) {
                    this.ret = this.relCursor.getNext(
                        this.currentKey, this.currentEntry, LockMode.DEFAULT
                    );
                    if(this.ret != OperationStatus.SUCCESS) {
                        return false;
                    }
                }

                return true;

            } else if(this.isIntermediate) {
                System.out.println("Here?");
                if(this.listIndex < this.internalList.size()) {
                    return true;
                }
            }
        } catch (DatabaseException e) {
            System.out.println("Set cursor failed");
            e.printStackTrace();
        }
        return false;
    }

    public BaseEntry next() {
        if(this.isIndex) {
            return (BaseEntry) iBinding.entryToObject(this.currentEntry);
        } else if (this.isIntermediate) {
            return this.internalList.get(this.listIndex++);
        }
        return (BaseEntry) this.unBinding.entryToObject(this.currentEntry);
    }

    public Relation join(Relation other) {
        BaseEntry c1 = (BaseEntry) this.next();
        BaseEntry c2 = (BaseEntry) other.next();
        ArrayList<BaseEntry> results = new ArrayList<BaseEntry>();
        while(true) {
            if(c1.compareTo(c2) == 0) {
                results.add(c1);
            } else if (c1.compareTo(c2) < 0) {
                if(this.hasNext()){
                    c1 = (BaseEntry) this.next();
                } else {
                    break;
                }
            } else {
                if(other.hasNext()) {
                    c2 = (BaseEntry) other.next();
                } else {
                    break;
                }
            }
        }
        Relation r = new Relation(results);
        return r;
    }



    public void remove() {
        throw new UnsupportedOperationException("Not Implemented");
    }
}
