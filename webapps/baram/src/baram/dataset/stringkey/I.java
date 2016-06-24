package baram.dataset.stringkey;

public final class I {

    public static final I BY_KEY = new I("I.BY_KEY");
    public static final I BY_VALUE = new I("I.BY_VALUE");

    private String name;
    private I(String name) {
        this.name = name;
    }
    public String toString() {
        return this.name;
    }
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof I)) {
            return false;
        }
        I type = (I) o;
        if (!this.name.equals(type.name)) {
            return false;
        }
        return true;
    }
    public int hashCode() {
        return this.name.hashCode();
    }
}
