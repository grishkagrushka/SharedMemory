package se.kth.ict.id2203.components.riwcm;

import se.kth.ict.id2203.ports.pp2p.Pp2pDeliver;
import se.sics.kompics.address.Address;

public class AckDataMessage extends Pp2pDeliver {

    private static final long serialVersionUID = 5819778433562502701L;

    private final int r;

    protected AckDataMessage(Address source, int r) {
        super(source);
        this.r = r;
    }

    public int getR() {
        return r;
    }
}
