import java.util.ArrayList;

public class Relation {

    public AttributeCost[] costs;

    public Relation(AttributeCost[] costs){
        this.costs = costs;
    }

    public String toString() {
        String value = "";
        for(AttributeCost cost:this.costs) {
            value += cost.path.getName();
        }
        return value;
    }
}
