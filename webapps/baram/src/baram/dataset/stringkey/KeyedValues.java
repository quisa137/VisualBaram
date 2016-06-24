package baram.dataset.stringkey;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class KeyedValues implements KVs, Serializable {

    private static final long serialVersionUID = -201204142328L;

    private ArrayList<String> keys;
    private ArrayList<Number> values;
    private HashMap<String,Integer> indexMap;
    
    public KeyedValues() {
        this.keys = new ArrayList<String>();
        this.values = new ArrayList<Number>();
        this.indexMap = new HashMap<String,Integer>();
    }
    
    public int getItemCount() {
        return this.indexMap.size();
    }
    
    public Number getValue(int item) {
        return this.values.get(item);
    }

    public String getKey(int index) {
        return this.keys.get(index);
    }

    public int getIndex(String key) {
    	if(key==null) {
            return -1;
        }
        final Integer i = this.indexMap.get(key);
        if(i==null) {
            return -1;
        }
        return i.intValue();
    }

    public List<String> getKeys() {
        return this.keys;
    }

    public Number getValue(String key) {
        int index = getIndex(key);
        if (index < 0) {
        	return null;
        }
        return getValue(index);
    }

    public void addValue(String key, double value) {
        addValue(key, new Double(value));
    }

    public void addValue(String key, Number value) {
        setValue(key, value);
    }

    public void incrementValue(String key, double value) {
        double existing = 0.0;
        Number n = getValue(key);
        if(n!=null) {
            existing = n.doubleValue();
        }
        setValue(key, existing + value);
    }

    public void setValue(String key, double value) {
        setValue(key, new Double(value));
    }

    public void setValue(String key, Number value) {
        if (key == null) {
            throw new IllegalArgumentException("Null 'key' argument.");
        }
        int keyIndex = getIndex(key);
        if (keyIndex >= 0) {
            this.keys.set(keyIndex, key);
            this.values.set(keyIndex, value);
        }
        else {
            this.keys.add(key);
            this.values.add(value);
            this.indexMap.put(key, new Integer(this.keys.size() - 1));
        }
    }

    public void insertValue(int position, String key, double value) {
        insertValue(position, key, new Double(value));
    }

    public void insertValue(int position, String key, Number value) {
        if (position < 0 || position > getItemCount()) {
            throw new IllegalArgumentException("'position' out of bounds.");
        }
        if (key == null) {
            throw new IllegalArgumentException("Null 'key' argument.");
        }
        int pos = getIndex(key);
        if (pos == position) {
            this.keys.set(pos, key);
            this.values.set(pos, value);
        }
        else {
            if (pos >= 0) {
                this.keys.remove(pos);
                this.values.remove(pos);
            }

            this.keys.add(position, key);
            this.values.add(position, value);
            rebuildIndex();
        }
    }

    private void rebuildIndex () {
        this.indexMap.clear();
        for (int i = 0; i < this.keys.size(); i++) {
            final String key = this.keys.get(i);
            this.indexMap.put(key, new Integer(i));
        }
    }

    public void removeValue(int index) {
        this.keys.remove(index);
        this.values.remove(index);
        rebuildIndex();
    }

    public void removeValue(String key) {
        int index = getIndex(key);
        if (index < 0) {
            return;
        }
        removeValue(index);
    }

    public void clear() {
        this.keys.clear();
        this.values.clear();
        this.indexMap.clear();
    }

    public void sortByKeys(S order) {
        final int size = this.keys.size();
        final E[] data = new E[size];

        for (int i = 0; i < size; i++) {
            data[i] = new E(this.keys.get(i), this.values.get(i));
        }
        Arrays.sort(data, new F(I.BY_KEY, order));
        clear();

        for (int i = 0; i < data.length; i++) {
            final E value = data[i];
            addValue(value.k(), value.v());
        }
    }

    public void sortByValues(S order) {
        final int size = this.keys.size();
        final E[] data = new E[size];
        for (int i = 0; i < size; i++) {
            data[i] = new E(this.keys.get(i), this.values.get(i));
        }
        Arrays.sort(data, new F(I.BY_VALUE, order));
        clear();
        for (int i = 0; i < data.length; i++) {
            final E value = data[i];
            addValue(value.k(), value.v());
        }
    }

    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }

        if (!(obj instanceof KVs)) {
            return false;
        }

        KVs that = (KVs) obj;
        int count = getItemCount();
        if (count != that.getItemCount()) {
            return false;
        }

        for (int i = 0; i < count; i++) {
            String k1 = getKey(i);
            String k2 = that.getKey(i);
            if (!k1.equals(k2)) {
                return false;
            }
            Number v1 = getValue(i);
            Number v2 = that.getValue(i);
            if (v1 == null) {
                if (v2 != null) {
                    return false;
                }
            }
            else {
                if (!v1.equals(v2)) {
                    return false;
                }
            }
        }
        return true;
    }

    public int hashCode() {
        return (this.keys != null ? this.keys.hashCode() : 0);
    }
}
