import java.io.*;
import com.sleepycat.bind.tuple.TupleBinding;
import com.sleepycat.bind.tuple.TupleInput;
import com.sleepycat.bind.tuple.TupleOutput;

public class UnindexedBinding extends TupleBinding {

    public void objectToEntry(Object object, TupleOutput to) {
        SEntry s = (SEntry) object;
        to.writeInt(s.primary);
    }

    public Object entryToObject(TupleInput ti) {
        int primary = ti.readInt();
        SEntry s = new SEntry(primary);
        return s;
    }
}
