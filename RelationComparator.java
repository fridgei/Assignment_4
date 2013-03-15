import java.util.Comparator;

public class RelationSorter implements Comparator {
    public int compare(Relation r1, Relation r2) {
        if(r1.getRelationName() < r2.getRelationName()) {
            return -1;
        else if(r1.getRelationName() > r2.getRelationName()) {
            return 1;
        }
        return 0;
    }

