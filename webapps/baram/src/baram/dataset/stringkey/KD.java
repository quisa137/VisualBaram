package baram.dataset.stringkey;

import java.util.List;

public interface KD extends VD {
    public String getRowKey(int row);
    public int getRowIndex(String key);
    public List<String> getRowKeys();
    public String getColumnKey(int column);
    public int getColumnIndex(String key);
    public List<String> getColumnKeys();
    public Number getValue(String rowKey, String columnKey);
}
