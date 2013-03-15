import java.io.*;
import com.sleepycat.bind.tuple.TupleBinding;
import com.sleepycat.bind.tuple.TupleIntput;
import com.sleepycat.bind.tuple.TupleOutput;

public class SBinding {

    public void objectToEntry(Object object, TupleOutput to) {
        
