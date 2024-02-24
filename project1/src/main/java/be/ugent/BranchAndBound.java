package be.ugent;

import be.ugent.graphs.BasicGraph;

import java.util.BitSet;

public class BranchAndBound implements MaximumCliqueAlgorithm {
    private BitSet biggestClique;
    private BasicGraph graph;

    private BitSet MaxClique(BitSet clique, BitSet remaining) {
        int id = remaining.nextSetBit(0);
        while (id != -1 && clique.cardinality() + remaining.cardinality() > biggestClique.cardinality()) {
            clique.set(id);
            remaining.clear(id);
            BitSet newRemaining = (BitSet) remaining.clone();
            newRemaining.and(graph.getAdjacencyBitSet(id));
            BitSet maxClique = clique;
            if (newRemaining.cardinality() != 0) {
                maxClique = MaxClique(clique, newRemaining);
            }
            if (maxClique.cardinality() > biggestClique.cardinality()) {
                biggestClique = (BitSet) maxClique.clone();
            }
            clique.clear(id);
            id = remaining.nextSetBit(id);
        }
        return biggestClique;
    }

    @Override
    public BitSet calculateMaxClique(BasicGraph graph) {
        this.graph = graph;
        int numVertices = graph.getNumVertices();
        biggestClique = new BitSet(numVertices);
        BitSet allVertices = new BitSet(numVertices);
        allVertices.set(0, numVertices);
        return MaxClique(new BitSet(numVertices), allVertices);
    }

    public static void main(String[] args) {
        long start = System.currentTimeMillis();
        BranchAndBound bAB = new BranchAndBound();
        BasicGraph graph = new BasicGraph("DIMACS_subset_ascii/C125.9.clq");
        BitSet maxClique = bAB.calculateMaxClique(graph);
        System.out.println(maxClique);
        System.out.println(maxClique.cardinality());
        System.out.println(graph.isClique(maxClique));
        long end = System.currentTimeMillis();
        System.out.println(end - start);
    }
}