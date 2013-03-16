import java.util.Arrays;
import java.util.ArrayList;
import java.util.Stack;

public class DbUtils {
    public static Relation[] createRelations(AttributeCost ... costs) {
        Arrays.sort(costs);
        ArrayList<AttributeCost> collected;
        ArrayList<Relation> relations = new ArrayList<Relation>();
        collected = new ArrayList<AttributeCost>();
        for(int i = 0; i < costs.length; i++) {
            collected.add(costs[i]);
            while(i + 1 < costs.length && collected.get(collected.size() -1).getPrefix() == costs[i + 1].getPrefix()) {
                i++;
                collected.add(costs[i]);
            }
            relations.add(new Relation(collected.toArray(new AttributeCost[collected.size()])));
            collected = new ArrayList<AttributeCost>();
        }
        return relations.toArray(new Relation[relations.size()]);
    }



    public static JoinCost[] joinableRelations(Relation[] relations) {
        
    }
}
