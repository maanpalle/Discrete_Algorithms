package be.ugent.util;

import java.util.BitSet;
import java.util.List;

public interface BLSMoveList {
    List<BLSMove> getMoves(BitSet clique);
}
