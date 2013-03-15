import java.io.*;
import com.sleepycat.bind.tuple.TupleBinding;
import com.sleepycat.bind.tuple.TupleInput;
import com.sleepycat.bind.tuple.TupleOutput;

public class REntryBinding extends TupleBinding {

    public void objectToEntry(Object object, TupleOutput to) {
        REntry s = (REntry) object;
        to.writeInt(s.primary);
    }

    public Object entryToObject(TupleInput ti) {
        int primary = ti.readInt();
        REntry s = new REntry(primary, secondary);
        return s;
    }
}
