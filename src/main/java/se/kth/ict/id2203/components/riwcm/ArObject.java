package se.kth.ict.id2203.components.riwcm;

public class ArObject {
    private final int ts;
    private final int wr;
    private final int val;
    private final int id;

    public ArObject(int ts, int wr, int val, int id) {
        this.ts = ts;
        this.wr = wr;
        this.val = val;
        this.id = id;
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

    public int getId() {
        return id;
    }
}
