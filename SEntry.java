public class SEntry extends BaseEntry {

    public int primary;
    public int secondary;

    public SEntry(int primary, int secondary) {
        this.primary = primary;
        this.secondary = secondary;
    }

    public int getSecondary() {
        return secondary;
    }

}
