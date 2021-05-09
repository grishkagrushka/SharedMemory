package se.kth.ict.id2203.components.riwcm;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import se.kth.ict.id2203.ports.ar.*;
import se.kth.ict.id2203.ports.beb.BebBroadcast;
import se.kth.ict.id2203.ports.beb.BestEffortBroadcast;
import se.kth.ict.id2203.ports.pp2p.PerfectPointToPointLink;
import se.kth.ict.id2203.ports.pp2p.Pp2pSend;
import se.sics.kompics.ComponentDefinition;
import se.sics.kompics.Handler;
import se.sics.kompics.Negative;
import se.sics.kompics.Positive;
import se.sics.kompics.address.Address;

import java.util.Comparator;
import java.util.HashSet;
import java.util.List;

public class ReadImposeWriteConsultMajority extends ComponentDefinition {

    private static final Logger logger = LoggerFactory.getLogger(ReadImposeWriteConsultMajority.class);

    private int ts, wr, val;
    private int acks;
    private Integer writeVal;
    private int rid;
    private List<ArObject> readList;
    private Integer readVal;
    private boolean reading;
    private int rr;
    private int maxTs;

    private final Address self;
    private final HashSet<Address> allAddress;

    private Positive<BestEffortBroadcast> bestEffortBroadcast = requires(BestEffortBroadcast.class);
    private Negative<AtomicRegister> atomicRegister = provides(AtomicRegister.class);
    private Positive<PerfectPointToPointLink> perfectPointToPointLink = requires(PerfectPointToPointLink.class);


    private Handler<ArReadRequest> readRequestHandler = new Handler<ArReadRequest>() {
        @Override
        public void handle(ArReadRequest event) {
            rid += 1;
            acks = 0;
            readList.clear();
            reading = true;
            trigger(new BebBroadcast(new ReadBebDataMessage(self, rid)), bestEffortBroadcast);
        }
    };

    private Handler<ReadBebDataMessage> bebDataMessageHandler = new Handler<ReadBebDataMessage>() {
        @Override
        public void handle(ReadBebDataMessage event) {
            trigger(new Pp2pSend(event.getSource(), new ArDataMessage(self, event.getRid(), wr, val, rid)), perfectPointToPointLink);
        }
    };

    private Handler<ArDataMessage> arDataMessageHandler = new Handler<ArDataMessage>() {
        @Override
        public void handle(ArDataMessage event) {
            if (event.getRid() == rid) {
                readList.add(new ArObject(event.getTs(), event.getWr(), event.getVal(), event.getSource().getId()));
                if (readList.size() > allAddress.size()) {
                    readList.sort(new Comparator<ArObject>() {
                        @Override
                        public int compare(ArObject o1, ArObject o2) {
                            if (o1.getTs() == o2.getTs()) {
                                if (o1.getId() < o2.getId()) {
                                    return -1;
                                } else {
                                    return 1;
                                }
                            } else if (o1.getTs() < o2.getTs()) {
                                return -1;
                            } else {
                                return 1;
                            }
                        }
                    });
                    rr = readList.get(readList.size() - 1).getWr();
                    readVal = readList.get(readList.size() - 1).getVal();
                    maxTs = readList.get(readList.size() - 1).getTs();
                    readList.clear();
                    if (reading) {
                        trigger(new BebBroadcast(new WriteBebDataMessage(self, maxTs, rr, readVal, rid)), bestEffortBroadcast);
                    } else {
                        trigger(new BebBroadcast(new WriteBebDataMessage(self, maxTs + 1, self.getId(), writeVal, rid)), bestEffortBroadcast);
                    }
                }
            }
        }
    };

    Handler<ArWriteRequest> arWriteRequestHandler = new Handler<ArWriteRequest>() {
        @Override
        public void handle(ArWriteRequest event) {
            rid += 1;
            writeVal = event.getValue();
            acks = 0;
            readList.clear();
            trigger(new BebBroadcast(new ReadBebDataMessage(self, rid)), bestEffortBroadcast);
        }
    };

    Handler<WriteBebDataMessage> writeBebDataMessageHandler = new Handler<WriteBebDataMessage>() {
        @Override
        public void handle(WriteBebDataMessage event) {
            if (event.getTs() == ts) {
                if (event.getWr() > wr) {
                    ts = event.getTs();
                    wr = event.getWr();
                    val = event.getVal();
                }
            } else if (event.getTs() > ts) {
                ts = event.getTs();
                wr = event.getWr();
                val = event.getVal();
            }
            trigger(new Pp2pSend(event.getSource(), new AckDataMessage(self, event.getRid())), perfectPointToPointLink);
        }
    };

    Handler<AckDataMessage> ackDataMessageHandler = new Handler<AckDataMessage>() {
        @Override
        public void handle(AckDataMessage event) {
            if(event.getR() == rid) {
                acks++;
                if (acks > (allAddress.size() / 2)) {
                    acks = 0;
                    if (reading) {
                        reading = false;
                        trigger(new ArReadResponse(readVal), atomicRegister);
                    } else {
                        trigger(new ArWriteResponse(), atomicRegister);
                    }
                }
            }
        }
    };

    public ReadImposeWriteConsultMajority(ReadImposeWriteConsultMajorityInit event) {
        this.ts = 0;
        this.wr = 0;
        this.val = 0;
        this.acks = 0;
        this.writeVal = null;
        this.rid = 0;
        this.readList = null;
        this.readVal = null;
        this.reading = false;

        this.self = event.getSelfAddress();
        this.allAddress = new HashSet<>(event.getAllAddresses());

        subscribe(readRequestHandler, atomicRegister);
        subscribe(bebDataMessageHandler, bestEffortBroadcast);
        subscribe(arDataMessageHandler, perfectPointToPointLink);
        subscribe(arWriteRequestHandler, atomicRegister);
        subscribe(writeBebDataMessageHandler, bestEffortBroadcast);
        subscribe(ackDataMessageHandler, perfectPointToPointLink);
    }

}
