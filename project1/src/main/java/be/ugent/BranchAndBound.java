package be.ugent;


import be.ugent.graphs.BasicGraph;

import java.util.BitSet;

public class BranchAndBound implements MaximumCliqueAlgorithm {
    public static void main(String[] args) {
        long start = System.currentTimeMillis();
        BasicGraph graph = new BasicGraph("DIMACS_subset_ascii/C125.9.clq");
        BitSet maxClique = MaxClique(graph);
        System.out.println(maxClique);
        System.out.println(graph.isClique(maxClique));
        System.out.println(maxClique.cardinality());
        long end = System.currentTimeMillis();
        System.out.println(end - start);
    }

    public static BitSet MaxClique(BitSet clique, BitSet remaining, BitSet biggestClique, BasicGraph graph) {
        int id = remaining.nextSetBit(0);
        while (id != -1 && clique.cardinality() + remaining.cardinality() > biggestClique.cardinality()) {
            clique.set(id);
            remaining.clear(id);
            BitSet newRemaining = (BitSet) remaining.clone();
            newRemaining.and(graph.getAdjacencyBitSet(id));
            BitSet maxClique = clique;
            if (newRemaining.cardinality() != 0) {
                maxClique = MaxClique(clique, newRemaining, biggestClique, graph);
            }
            if (maxClique.cardinality() > biggestClique.cardinality()) {
                biggestClique = (BitSet) maxClique.clone();
            }
            clique.clear(id);
            id = remaining.nextSetBit(id);
        }
        return biggestClique;
    }

    public static BitSet MaxClique(BasicGraph graph) {
        int numVertices = graph.getNumVertices();
        BitSet allVertices = new BitSet(numVertices);
        allVertices.set(0, numVertices);
        return MaxClique(new BitSet(numVertices), allVertices, new BitSet(numVertices), graph);
    }

    @Override
    public BitSet calculateMaxClique(BasicGraph graph) {
        return null;
    }
}
