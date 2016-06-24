package baram.dataset.stringkey;

import java.io.IOException;
import java.io.InvalidObjectException;
import java.io.ObjectInputStream;
import java.io.ObjectInputValidation;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Arrays;
import java.util.EventListener;

import javax.swing.event.EventListenerList;

public abstract class A implements D, Serializable, ObjectInputValidation {

    private static final long serialVersionUID = -201205011208L;

    private G group;

    private transient EventListenerList listenerList;

    protected A() {
        this.group = new G();
        this.listenerList = new EventListenerList();
    }

    public G getGroup() {
        return this.group;
    }

    public void setGroup(G group) {
        if(group==null) {
            throw new IllegalArgumentException("Null 'group' argument.");
        }
        this.group = group;
    }

    public void addChangeListener(B listener) {
        this.listenerList.add(B.class, listener);
    }

    public void removeChangeListener(B listener) {
        this.listenerList.remove(B.class, listener);
    }

    public boolean hasListener(EventListener listener) {
        return Arrays.asList(this.listenerList.getListenerList()).contains(listener);
    }

    protected void fireDatasetChanged() {
        notifyListeners(new C(this, this));
    }

    protected void notifyListeners(C event) {
        Object[] listeners = this.listenerList.getListenerList();
        for (int i = listeners.length - 2; i >= 0; i -= 2) {
            if (listeners[i] == B.class) {
                ((B) listeners[i + 1]).datasetChanged(event);
            }
        }

    }

    private void writeObject(ObjectOutputStream stream) throws IOException {
        stream.defaultWriteObject();
    }

    private void readObject(ObjectInputStream stream) throws IOException, ClassNotFoundException {
        stream.defaultReadObject();
        this.listenerList = new EventListenerList();
        stream.registerValidation(this, 10);
    }

    public void validateObject() throws InvalidObjectException {
        fireDatasetChanged();
    }

}
