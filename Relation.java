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
    ArrayList<Object> internalList;
    IndexedEntryBinding iBinding = new IndexedEntryBinding();
    UnIndexedBinding unBinding = new UnIndexedBinding();
    public Relation(Database db) {
        this.db = db;
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

    public Object next() {
        if(this.isIndex) {
            return iBinding.entryToObject(this.currentEntry);
        } else if (this.isIntermediate) {
            return this.internalList.get(this.listIndex++);
        }
        return this.unBinding.entryToObject(this.currentEntry);
    }

    public Relation join(Relation other) {
        Object c1 = this.next();
        Object c2 = other.next();
        ArrayList<DatabaseEntry> results = new ArrayList<DatabaseEntry>();
        while(true) {
            if(c1 == c2) {
                list.add(c1);
            } else if (c1 < c2 ) {
                if(this.hasNext()){ 
                    c1 = this.next();
                } else {
                    break;
                }
            } else { 
                if(other.hasNext()) { 
                    c2 = other.next();
                } else {
                    break;
                }
            }
        }



    public void remove() {
        throw new UnsupportedOperationException("Not Implemented");
    }
}
