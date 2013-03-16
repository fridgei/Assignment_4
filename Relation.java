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
    ArrayList<SEntry> internalList;
    IndexedEntryBinding iBinding = new IndexedEntryBinding();
    UnindexedBinding unBinding = new UnindexedBinding();

    public Relation(Database db) {
        this.db = db;
    }

    public Relation(ArrayList<SEntry> initial){
        this.internalList = initial;
        this.isIntermediate = true;
    }



    public Relation(Database db, boolean isIndex, boolean isIntermediate) {
        this.db = db;
        this.isIndex = isIndex;
        if(!this.isIndex) {
            this.relCursor = this.db.openCursor(null, null);
            this.relCursor.getFirst(this.currentKey, this.currentEntry, LockMode.DEFAULT);
            while(true) {
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
                        break;
                    }
                }
                this.internalList.add((SEntry) unBinding.entryToObject(this.currentEntry));
            }
            this.isIntermediate = true;
        } else {
            this.isIntermediate = isIntermediate;
        }
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

    public SEntry next() {
        if(this.isIndex) {
            return (SEntry) iBinding.entryToObject(this.currentEntry);
        } else if (this.isIntermediate) {
            return this.internalList.get(this.listIndex++);
        }
        return (SEntry) this.unBinding.entryToObject(this.currentEntry);
    }

    public Relation join(Relation other) {
        SEntry c1 = (SEntry) this.next();
        SEntry c2 = (SEntry) other.next();
        ArrayList<SEntry> results = new ArrayList<SEntry>();
        while(true) {
            if(c1.compareTo(c2) == 0) {
                results.add(c1);
            } else if (c1.compareTo(c2) < 0) {
                if(this.hasNext()){
                    c1 = (SEntry) this.next();
                } else {
                    break;
                }
            } else {
                if(other.hasNext()) {
                    c2 = (SEntry) other.next();
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
