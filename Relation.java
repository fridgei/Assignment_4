import java.util.Iterator;

import com.sleepycat.db.Cursor;
import com.sleepycat.db.DatabaseException;
import com.sleepycat.db.DatabaseEntry;
import com.sleepycat.db.Database;
import com.sleepycat.db.LockMode;
import com.sleepycat.db.OperationStatus;


public class Relation implements Iterator {

    Database db;
    bool isIndex;
    Cursor relCursor = null;
    int listIndex = 0;
    OperationStatus ret;
    DatabaseEntry currentEntry = new DatabaseEntry();
    DatabaseEntry currentKey = new DatabaseEntry();
    ArrayList<Object> internalList;

    public Relation(Database db) {
        this.db = db;
    }

    public Relation(Database db, bool isIndex, bool isIntermediate) {
        this.db = db;
        this.isIndex = isIndex;
        this.isIntermediate = isIntermediate;
    }

    public void setCursor(){
        if(isIndex) {
            this.relCursor = this.db.openCursor(null, null);
            this.relCursor.getFirst(this.currentKey, this.currentEntry, LockMode.DEFAULT)
        } else {
            this.attrIndex = 0;
        }
    }

    public bool hasNext() {
        if(this.isIndex) {
            this.relCursor.getNext(this.currentKey, this.currentEntry, LockMode.DEFAULT);
            return true;
        } else if(this.isIntermediate) {
            if(this.listIndex < this.internalList.size()) {
                return true;
            }
        }
        return false;
    }

    public Object next() {
        if(this.isIndex) {
            if(this.relCursor == null) {
                this.setCursor();
            }
            return this.currentEntry;
        }
        return this.internalList.get(this.listIndex++);
    }

    public void remove() {
        throw new UnsupportedOperationException("Not Implemented");
    }
}
