package baram.dataset.stringkey;

import java.util.Comparator;

public class F implements Comparator<KV> {

    private I type;

    private S order;
    
    public F(I type, S order) {
        if(order==null) {
            throw new IllegalArgumentException("Null 'order' argument.");
        }
        this.type = type;
        this.order = order;
    }
    
    public I getType() {
        return this.type;
    }
    
    public S getOrder() {
        return this.order;
    }
    
    public int compare(KV kv1, KV kv2) {

        if(kv2==null) {
            return -1;
        }
        
        if(kv1==null) {
            return 1;
        }

        int result;

        if(this.type==I.BY_KEY) {
            if (this.order.equals(S.ASCENDING)) {
                result = kv1.k().compareTo(kv2.k());
            } else if (this.order.equals(S.DESCENDING)) {
                result = kv2.k().compareTo(kv1.k());
            } else {
                throw new IllegalArgumentException("Unrecognised sort order.");
            }
        } else if(this.type==I.BY_VALUE) {
            Number n1 = kv1.v();
            Number n2 = kv2.v();
            if(n2==null) {
                return -1;
            }
            if(n1==null) {
                return 1;
            }
            double d1 = n1.doubleValue();
            double d2 = n2.doubleValue();
            if(this.order.equals(S.ASCENDING)) {
                if(d1>d2) {
                    result = 1;
                } else if(d1<d2) {
                    result = -1;
                } else {
                    result = 0;
                }
            } else if(this.order.equals(S.DESCENDING)) {
                if(d1>d2) {
                    result = -1;
                } else if(d1<d2) {
                    result = 1;
                } else {
                    result = 0;
                }
            } else {
                throw new IllegalArgumentException("Unrecognised sort order.");
            }
        } else {
            throw new IllegalArgumentException("Unrecognised type.");
        }

        return result;
    }

}
