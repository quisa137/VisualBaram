package baram.dataset.stringkey;

import java.io.Serializable;
import java.util.List;

public class Dataset extends A implements Serializable {

    private static final long serialVersionUID = -201204150012L;

    private H data;

    public Dataset() {
        this.data = new H();
    }
    
    public int getRowCount() {
        return this.data.getRowCount();
    }

    public int getColumnCount() {
        return this.data.getColumnCount();
    }

    public Number getValue(int row, int column) {
        return this.data.getValue(row, column);
    }

    public String getRowKey(int row) {
        return this.data.getRowKey(row);
    }

    public int getRowIndex(String key) {
        return this.data.getRowIndex(key);
    }

    public List<String> getRowKeys() {
        return this.data.getRowKeys();
    }

    public String getColumnKey(int column) {
        return this.data.getColumnKey(column);
    }

    public int getColumnIndex(String key) {
        return this.data.getColumnIndex(key);
    }

    public List<String> getColumnKeys() {
        return this.data.getColumnKeys();
    }

    public Number getValue(String rowKey, String columnKey) {
        return this.data.getValue(rowKey, columnKey);
    }

    public void addValue(Number value, String rowKey, String columnKey) {
        this.data.addValue(value, rowKey, columnKey);
        fireDatasetChanged();
    }

    public void addValue(double value, String rowKey, String columnKey) {
        addValue(new Double(value), rowKey, columnKey);
    }

    public void setValue(Number value, String rowKey, String columnKey) {
        this.data.setValue(value, rowKey, columnKey);
        fireDatasetChanged();
    }

    public void setValue(double value, String rowKey, String columnKey) {
        setValue(new Double(value), rowKey, columnKey);
    }

    public void incrementValue(double value, String rowKey, String columnKey) {
        double existing = 0.0;
        Number n = getValue(rowKey, columnKey);
        if(n!=null) {
            existing = n.doubleValue();
        }
        setValue(existing + value, rowKey, columnKey);
    }

    public int getIncrementValue(int value, String rowKey, String columnKey) {
        int existing = 0;
        Number n = getValue(rowKey, columnKey);
        if(n!=null) {
            existing = n.intValue();
        }
        existing = existing+value;
        setValue(existing, rowKey, columnKey);
        return existing;
    }

    public void removeValue(String rowKey, String columnKey) {
        this.data.removeValue(rowKey, columnKey);
        fireDatasetChanged();
    }

    public void removeRow(int rowIndex) {
        this.data.removeRow(rowIndex);
        fireDatasetChanged();
    }

    public void removeRow(String rowKey) {
        this.data.removeRow(rowKey);
        fireDatasetChanged();
    }

    public void removeColumn(int columnIndex) {
        this.data.removeColumn(columnIndex);
        fireDatasetChanged();
    }

    public void removeColumn(String columnKey) {
        this.data.removeColumn(columnKey);
        fireDatasetChanged();
    }
   
    public void clear() {
        this.data.clear();
        fireDatasetChanged();
    }

    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof Dataset)) {
            return false;
        }
        Dataset that = (Dataset) obj;
        if (!getRowKeys().equals(that.getRowKeys())) {
            return false;
        }
        if (!getColumnKeys().equals(that.getColumnKeys())) {
            return false;
        }
        int rowCount = getRowCount();
        int colCount = getColumnCount();
        for (int r = 0; r < rowCount; r++) {
            for (int c = 0; c < colCount; c++) {
                Number v1 = getValue(r, c);
                Number v2 = that.getValue(r, c);
                if (v1 == null) {
                    if (v2 != null) {
                        return false;
                    }
                }
                else if (!v1.equals(v2)) {
                    return false;
                }
            }
        }
        return true;
    }

    public int hashCode() {
        return this.data.hashCode();
    }

}
