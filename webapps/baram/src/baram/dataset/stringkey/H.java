package baram.dataset.stringkey;

import java.io.Serializable;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

public class H implements KD, Serializable {

    private static final long serialVersionUID = -201204142349L;
    private List<String> rowKeys;
    private List<String> columnKeys;

    private List<KeyedValues> rows;
    private boolean sortRowKeys;
    public H() {
        this(false);
    }

    public H(boolean sortRowKeys) {
        this.rowKeys = new java.util.ArrayList<String>();
        this.columnKeys = new java.util.ArrayList<String>();
        this.rows = new java.util.ArrayList<KeyedValues>();
        this.sortRowKeys = sortRowKeys;
    }

    public int getRowCount() {
        return this.rowKeys.size();
    }

    public int getColumnCount() {
        return this.columnKeys.size();
    }

    public Number getValue(int row, int column) {
        Number result = null;
        KeyedValues rowData = this.rows.get(row);
        if (rowData != null) {
            String columnKey = this.columnKeys.get(column);
            // the row may not have an entry for this key, in which case the
            // return value is null
            int index = rowData.getIndex(columnKey);
            if (index >= 0) {
                result = rowData.getValue(index);
            }
        }
        return result;
    }

    public String getRowKey(int row) {
        return this.rowKeys.get(row);
    }

    public int getRowIndex(String key) {
        if (key == null) {
            throw new IllegalArgumentException("Null 'key' argument.");
        }
        if (this.sortRowKeys) {
            return Collections.binarySearch(this.rowKeys, (String)key);
        }
        else {
            return this.rowKeys.indexOf(key);
        }
    }

    public List<String> getRowKeys() {
        return Collections.unmodifiableList(this.rowKeys);
    }

    public String getColumnKey(int column) {
        return this.columnKeys.get(column);
    }

    public int getColumnIndex(String key) {
        if (key == null) {
            throw new IllegalArgumentException("Null 'key' argument.");
        }
        return this.columnKeys.indexOf(key);
    }

    public List<String> getColumnKeys() {
        return Collections.unmodifiableList(this.columnKeys);
    }

    public Number getValue(String rowKey, String columnKey) {
        if (rowKey == null) {
            throw new IllegalArgumentException("Null 'rowKey' argument.");
        }
        if (columnKey == null) {
            throw new IllegalArgumentException("Null 'columnKey' argument.");
        }

        if (!(this.columnKeys.contains(columnKey))) {
            return null;
        }

        int row = getRowIndex(rowKey);
        if (row >= 0) {
            KeyedValues rowData
                = (KeyedValues) this.rows.get(row);
            int col = rowData.getIndex(columnKey);
            return (col >= 0 ? rowData.getValue(col) : null);
        } else {
        	return null;
        }
    }

    public void addValue(Number value, String rowKey, String columnKey) {
        setValue(value, rowKey, columnKey);
    }

    public void setValue(Number value, String rowKey, String columnKey) {

        KeyedValues row;
        int rowIndex = getRowIndex(rowKey);

        if (rowIndex >= 0) {
            row = this.rows.get(rowIndex);
        }
        else {
            row = new KeyedValues();
            if (this.sortRowKeys) {
                rowIndex = -rowIndex - 1;
                this.rowKeys.add(rowIndex, rowKey);
                this.rows.add(rowIndex, row);
            }
            else {
                this.rowKeys.add(rowKey);
                this.rows.add(row);
            }
        }
        row.setValue(columnKey, value);

        int columnIndex = this.columnKeys.indexOf(columnKey);
        if (columnIndex < 0) {
            this.columnKeys.add(columnKey);
        }
    }

    public void removeValue(String rowKey, String columnKey) {
        setValue(null, rowKey, columnKey);

        boolean allNull = true;
        int rowIndex = getRowIndex(rowKey);
        KeyedValues row = (KeyedValues) this.rows.get(rowIndex);

        for (int item = 0, itemCount = row.getItemCount(); item < itemCount; item++) {
            if(row.getValue(item) != null) {
                allNull = false;
                break;
            }
        }

        if (allNull) {
            this.rowKeys.remove(rowIndex);
            this.rows.remove(rowIndex);
        }

        allNull = true;
        
        for (int item = 0, itemCount = this.rows.size(); item < itemCount;
             item++) {
            row = (KeyedValues) this.rows.get(item);
            int columnIndex = row.getIndex(columnKey);
            if (columnIndex >= 0 && row.getValue(columnIndex) != null) {
                allNull = false;
                break;
            }
        }

        if (allNull) {
            for (int item = 0, itemCount = this.rows.size(); item < itemCount;
                 item++) {
                row = (KeyedValues) this.rows.get(item);
                int columnIndex = row.getIndex(columnKey);
                if (columnIndex >= 0) {
                    row.removeValue(columnIndex);
                }
            }
            this.columnKeys.remove(columnKey);
        }
    }

    public void removeRow(int rowIndex) {
        this.rowKeys.remove(rowIndex);
        this.rows.remove(rowIndex);
    }

    public void removeRow(String rowKey) {
        if (rowKey == null) {
            throw new IllegalArgumentException("Null 'rowKey' argument.");
        }
        int index = getRowIndex(rowKey);
        if (index >= 0) {
            removeRow(index);
        } else {
            return;
        }
    }
    
    public void removeColumn(int columnIndex) {
        String columnKey = getColumnKey(columnIndex);
        removeColumn(columnKey);
    }
    
    public void removeColumn(String columnKey) {
        if (columnKey == null) {
            throw new IllegalArgumentException("Null 'columnKey' argument.");
        }
        if (!this.columnKeys.contains(columnKey)) {
           return;
        }
        Iterator<KeyedValues> iterator = this.rows.iterator();
        while (iterator.hasNext()) {
            KeyedValues rowData = iterator.next();
            int index = rowData.getIndex(columnKey);
            if (index >= 0) {
                rowData.removeValue(columnKey);
            }
        }
        this.columnKeys.remove(columnKey);
    }
    
    public void clear() {
        this.rowKeys.clear();
        this.columnKeys.clear();
        this.rows.clear();
    }
    
    public boolean equals(Object o) {

        if (o == null) {
            return false;
        }
        if (o == this) {
            return true;
        }

        if (!(o instanceof KD)) {
            return false;
        }
        KD kv2D = (KD) o;
        if (!getRowKeys().equals(kv2D.getRowKeys())) {
            return false;
        }
        if (!getColumnKeys().equals(kv2D.getColumnKeys())) {
            return false;
        }
        int rowCount = getRowCount();
        if (rowCount != kv2D.getRowCount()) {
            return false;
        }

        int colCount = getColumnCount();
        if (colCount != kv2D.getColumnCount()) {
            return false;
        }

        for (int r = 0; r < rowCount; r++) {
            for (int c = 0; c < colCount; c++) {
                Number v1 = getValue(r, c);
                Number v2 = kv2D.getValue(r, c);
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
        }
        return true;
    }
    
    public int hashCode() {
        int result;
        result = this.rowKeys.hashCode();
        result = 29 * result + this.columnKeys.hashCode();
        result = 29 * result + this.rows.hashCode();
        return result;
    }
}
