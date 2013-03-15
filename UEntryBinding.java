import java.io.*;
import com.sleepycat.bind.tuple.TupleBinding;
import com.sleepycat.bind.tuple.TupleInput;
import com.sleepycat.bind.tuple.TupleOutput;

public class UEntryBinding extends TupleBinding {

    public void objectToEntry(Object object, TupleOutput to) {
        UEntry s = (UEntry) object;
        to.writeInt(s.primary);
    }

    public Object entryToObject(TupleInput ti) {
        int primary = ti.readInt();
        UEntry s = new UEntry(primary, secondary);
        return s;
    }
}
