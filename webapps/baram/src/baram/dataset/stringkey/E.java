package baram.dataset.stringkey;

import java.io.Serializable;

public class E implements KV, Serializable {

    private static final long serialVersionUID = -201205011052L;

    private String key;

    private Number value;

    public E(String key, Number value) {
        if (key == null) {
            throw new IllegalArgumentException("Null 'key' argument.");
        }
        this.key = key;
        this.value = value;
    }

    public String k() {
        return this.key;
    }
    
    public Number v() {
        return this.value;
    }

    public synchronized void setValue(Number value) {
        this.value = value;
    }

    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof E)) {
            return false;
        }
        E that = (E) obj;

        if (!this.key.equals(that.key)) {
            return false;
        }
        if (this.value != null
                ? !this.value.equals(that.value) : that.value != null) {
            return false;
        }
        return true;
    }

    public int hashCode() {
        int result;
        result = (this.key != null ? this.key.hashCode() : 0);
        result = 29 * result + (this.value != null ? this.value.hashCode() : 0);
        return result;
    }

    public String toString() {
        return "(" + this.key.toString() + ", " + this.value.toString() + ")";
    }

}
