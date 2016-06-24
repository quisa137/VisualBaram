package baram.dataset.stringkey;

import java.io.ObjectStreamException;
import java.io.Serializable;

public final class S implements Serializable {
	
    private static final long serialVersionUID = -201606291054L;
    public static final S ASCENDING = new S("S.ASCENDING");
    public static final S DESCENDING = new S("S.DESCENDING");
    private String name;
    
    private S(final String name) {
        this.name = name;
    }
    
    public String toString() {
        return this.name;
    }
    
    public boolean equals(final Object obj) {

        if (this == obj) {
            return true;
        }
        if (!(obj instanceof S)) {
            return false;
        }

        final S that = (S) obj;
        if (!this.name.equals(that.toString())) {
            return false;
        }

        return true;
    }
    
    public int hashCode() {
        return this.name.hashCode();
    }

    private Object readResolve() throws ObjectStreamException {
        if (this.equals(S.ASCENDING)) {
            return S.ASCENDING;
        } else if (this.equals(S.DESCENDING)) {
            return S.DESCENDING;
        }
        return null;
    }
}
