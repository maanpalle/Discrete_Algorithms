package be.ugent.util;

import java.util.*;

public class BLSTabuList {
    private Map<Integer, Integer> tabuList;
    private int phi;
    private Random random;


    public BLSTabuList(int phi) {
        this.tabuList = new HashMap<>();
        this.phi = phi;
        this.random = new Random();
    }

    public void add(BLSMove move, int currentIteration, int omSize) {
        if (move.isNoop()) {
            return;
        }
        if (move.isRemoval()) {
            int gamma = phi + random.nextInt(omSize + 1) + 1;
            tabuList.put(move.getPrimaryVertex(), currentIteration + gamma);
            return;
        }
        for (int v = move.getRemovedVertices().nextSetBit(0); v >= 0; v = move.getRemovedVertices().nextSetBit(v + 1)) {
            int gamma = phi + random.nextInt(omSize + 1) + 1;
            tabuList.put(v, currentIteration + gamma);
        }
    }

    // Check if a move is allowed
    public boolean isMoveAllowed(BLSMove move, int currentIteration) {
        if (!tabuList.containsKey(move.getPrimaryVertex())) {
            return true;
        }
        Integer tabuUntil = tabuList.get(move.getPrimaryVertex());
        return tabuUntil == null || currentIteration > tabuUntil;
    }

}
