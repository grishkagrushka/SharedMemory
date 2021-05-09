package se.kth.ict.id2203.components.riwcm;

import se.kth.ict.id2203.ports.beb.BebDeliver;
import se.sics.kompics.address.Address;

public class ReadBebDataMessage extends BebDeliver {

    private static final long serialVersionUID = -4086381042502921961L;

    private final int rid;

    public ReadBebDataMessage(Address source, Integer rid) {
        super(source);
        this.rid = rid;
    }

    public int getRid() {
        return rid;
    }
}
