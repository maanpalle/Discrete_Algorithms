import graphs.BasicGraph;

import java.util.BitSet;

public class BranchAndBound {
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

    public static BitSet MaxClique(BitSet clique, BitSet remaining, BitSet biggestClique, BitSet adjacentVertices, BasicGraph graph) {
        remaining.and(adjacentVertices);
        int id = remaining.nextSetBit(0);
        while (id != -1 && clique.cardinality() + remaining.cardinality() > biggestClique.cardinality()) {
            clique.set(id);
            remaining.clear(id);
            BitSet maxClique = MaxClique(clique, (BitSet) remaining.clone(), biggestClique, graph.getAdjacencyList(id), graph);
            if (maxClique.cardinality() > biggestClique.cardinality()) {
                biggestClique = (BitSet) maxClique.clone();
            }
            clique.clear(id);
            id = remaining.nextSetBit(id);
        }
        if (clique.cardinality() > biggestClique.cardinality()) {
            biggestClique = (BitSet) clique.clone();
        }
        return biggestClique;
    }

    public static BitSet MaxClique(BasicGraph graph) {
        int vertices = graph.getNumVertices();
        BitSet empty = new BitSet(vertices);
        BitSet full = new BitSet(vertices);
        full.set(0, vertices);
        return MaxClique(empty, full, (BitSet) empty.clone(), (BitSet) full.clone(), graph);
    }
}
