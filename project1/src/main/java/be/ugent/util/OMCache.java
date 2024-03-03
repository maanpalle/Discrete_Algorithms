package be.ugent.util;

import org.javatuples.Pair;

import java.util.BitSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class OMCache extends LinkedHashMap<BitSet, List<int[]>> {
    private static final int MAX_ENTRIES = 1000;

    public OMCache() {
        super(MAX_ENTRIES, 0.75f, true); // Initial capacity, load factor, and accessOrder
    }

    @Override
    protected boolean removeEldestEntry(Map.Entry<BitSet, List<int[]>> eldest) {
        return size() > MAX_ENTRIES;
    }

}