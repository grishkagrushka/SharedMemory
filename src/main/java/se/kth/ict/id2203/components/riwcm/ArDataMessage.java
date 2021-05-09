package se.kth.ict.id2203.components.riwcm;

import se.kth.ict.id2203.ports.pp2p.Pp2pDeliver;
import se.sics.kompics.address.Address;

public class ArDataMessage extends Pp2pDeliver {

    private static final long serialVersionUID = 8350818550149148803L;

    private final int ts, wr, val, rid;

    protected ArDataMessage(Address source, int ts, int wr, int val, int rid) {
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
