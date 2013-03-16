public class Entry implements Comparable {

    public int primary;

    public Entry(int primary) {
        this.primary = primary;
    }

    public int getPrimary() {
        return primary;
    }

    public int getValue() {
        return primary;
    }

    public int compareTo(Object o) {
        return this.getValue() - o.getValue();
    }
}
