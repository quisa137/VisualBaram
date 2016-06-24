package baram.dataset.stringkey;

public class C extends java.util.EventObject {

	private static final long serialVersionUID = -201204142309L;
    private D d;
    public C(Object source, D d) {
        super(source);
        this.d = d;
    }
    public D getDataset() {
        return this.d;
    }
}
