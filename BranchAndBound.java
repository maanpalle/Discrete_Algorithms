import graphs.BasicGraph;

import java.util.BitSet;

public class BranchAndBound {
    public static void main(String[] args) {
        long start = System.currentTimeMillis();
        BasicGraph graph = new BasicGraph("DIMACS_subset_ascii/C125.9.clq");
        BitSet maxClique = MaxClique(graph);
        System.out.println(maxClique);
        System.out.println(maxClique.cardinality());
        long end = System.currentTimeMillis();
        System.out.println(end - start);
    }

    public static BitSet MaxClique(BitSet clique, BitSet remaining, BitSet biggestClique, BitSet adjacentVertices, BitSet biggerVertices, BasicGraph graph) {
        if (clique.cardinality() < 6) {
            System.out.println(clique);
        }
        if (clique.cardinality() > biggestClique.cardinality()) {
            biggestClique = (BitSet) clique.clone();
        }
        BitSet newRemaining = (BitSet) remaining.clone();
        newRemaining.and(adjacentVertices);
        newRemaining.and(biggerVertices);
        if (clique.cardinality() + newRemaining.cardinality() > biggestClique.cardinality()) {
            BitSet newBigger = (BitSet) biggerVertices.clone();
            int id = newRemaining.nextSetBit(0);
            while (id != -1) {
                clique.set(id);
                newBigger.clear(0, id + 1);
                BitSet maxClique = MaxClique(clique, newRemaining, biggestClique, graph.getAdjacencyList(id), newBigger, graph);
                if (maxClique.cardinality() > biggestClique.cardinality()) {
                    biggestClique = (BitSet) maxClique.clone();
                }
                clique.clear(id);
                id = newRemaining.nextSetBit(id + 1);
            }
        }
        return biggestClique;
    }

    public static BitSet MaxClique(BasicGraph graph) {
        int vertices = graph.getNumVertices();
        BitSet clique = new BitSet(vertices);
        BitSet remaining = new BitSet(vertices);
        BitSet biggestClique = new BitSet(vertices);
        BitSet adjacentVertices = new BitSet(vertices);
        BitSet biggerVertices = new BitSet(vertices);
        remaining.set(0, graph.getNumVertices());
        adjacentVertices.set(0, graph.getNumVertices());
        biggerVertices.set(0, graph.getNumVertices());
        return MaxClique(clique, remaining, biggestClique, adjacentVertices, biggerVertices, graph);
    }
}
