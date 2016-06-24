package baram.dataset.stringkey;

import java.util.List;

public interface KVs extends Vs {
    public String getKey(int index);
    public int getIndex(String key);
    public List<String> getKeys();
    public Number getValue(String key);
}
