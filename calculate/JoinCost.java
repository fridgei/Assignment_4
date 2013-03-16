
public class JoinCost implements Comparable {
    public int size;
    public int cost;
    public Relation[] relations;
    public JoinCost joined = null;

    public JoinCost(Relation[] relations, int cost, int size) {
        this.size = size;
        this.cost = cost;
        this.relations = relations;
    }

    public JoinCost(Relation[] relations, int cost) {
        this.cost = cost;
        this.relations = relations;
    }

    public int getRelationSize() {
        return this.relations.length;
    }

    public int getSize() {
        return this.size;
    }

    public int getCost() {
        return this.cost;
    }

    public void computeCost() {
        if(this.joined == null) {
            this.cost = 0;
        } else {
            this.cost = this.joined.getSize();
        }
    }

    public void computeSize() {
        int total = 0;
        for(Relation rel:this.relations) {
            if(total) {
                total *= rel.

    public int compareTo(Object o) {
        JoinCost other = (JoinCost) o;
        return this.getCost() - other.getCost();
    }
}
