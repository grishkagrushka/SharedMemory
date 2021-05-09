package se.kth.ict.id2203.components.riwcm;

import se.kth.ict.id2203.ports.beb.BebDeliver;
import se.sics.kompics.address.Address;

public class WriteBebDataMessage extends BebDeliver {

    private static final long serialVersionUID = -119820391103656248L;

    private final int ts;
    private final int wr;
    private final int val;
    private final int rid;

    public WriteBebDataMessage(Address source, int ts, int wr, int val, int rid) {
        super(source);
        this.ts = ts;
        this.wr = wr;
        this.val = val;
        this.rid = rid;
    }

    public int getTs() {
        return ts;
    }

    public int getWr() {
        return wr;
    }

    public int getVal() {
        return val;
    }

    public int getRid() {
        return rid;
    }
}
