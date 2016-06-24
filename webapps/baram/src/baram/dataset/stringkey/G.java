package baram.dataset.stringkey;

import java.io.Serializable;

public class G implements Serializable {

    private static final long serialVersionUID = -201205011211L;

    private String id;
    public G() {
        super();
        this.id = "NOID";
    }

    public G(String id) {
        if (id == null) {
            throw new IllegalArgumentException("Null 'id' argument.");
        }
        this.id = id;
    }

    public String getID() {
        return this.id;
    }

    public boolean equals(Object obj) {
        if(obj == this) {
            return true;
        }
        if(!(obj instanceof G)) {
            return false;
        }
        G that = (G) obj;
        if(!this.id.equals(that.id)) {
            return false;
        }
        return true;
    }
}
