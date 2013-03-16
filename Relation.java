import com.sleepycat.db.Cursor;

public class Relation {
    Database db;
    bool isIndex;
    public Relation(Database db) {
        this.db = db;
    }

    public Relation(Database db, bool isIndex) {
        this.db = db;
        this.isIndex = isIndex;

    }

    public join(Relation other){
        // if other.isIndex
        //      other_cursor = other.getCursor()
    }

    public Cursor getCursor(){
        return db.openCursor(null, null);
    }

    public getDb(){
        return db;
    }

}
