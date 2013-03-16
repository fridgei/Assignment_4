public class BaseEntry implements Comparable {

    public int primary;

    public int getPrimary() {
        return primary;
    }

    public int getValue() {
        return primary;
    }

    public int compareTo(Object o) {
        BaseEntry be = (BaseEntry) o;
        return this.getValue() - be.getValue();
    }

}
