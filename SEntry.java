public class SEntry implements Comparable {

    public int primary;
    public int secondary;

    public SEntry(int primary, int secondary) {
        this.primary = primary;
        this.secondary = secondary;
    }

    public int getPrimary() {
        return primary;
    }

    public int getSecondary() {
        return secondary;
    }

    public int getValue() {
        return primary;
    }

    public int compareTo(Object o) {
        return this.getValue() - o.getValue();
    }
}
