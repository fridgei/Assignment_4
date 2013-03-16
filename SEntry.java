public class SEntry implements Comparable {

    public int primary;
    public int secondary;

    public SEntry(int primary, int secondary) {
        this.primary = primary;
        this.secondary = secondary;
    }

    public SEntry(int primary) {
        this.primary = primary;
        this.secondary = 0;
    }

    public int getSecondary() {
        return secondary;
    }

    public int getPrimary() {
        return primary;
    }

    public int getValue() {
        return primary;
    }

    public int compareTo(Object o) {
        SEntry be = (SEntry) o;
        return this.getValue() - be.getValue();
    }

}
