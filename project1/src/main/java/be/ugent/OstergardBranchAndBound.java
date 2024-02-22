package be.ugent;


import be.ugent.graphs.BasicGraph;
import be.ugent.graphs.ColouredGraph;

import java.util.BitSet;


public class OstergardBranchAndBound implements MaximumCliqueAlgorithm {

    private int max;
    private BitSet maxClique;
    private boolean found;
    private Integer[] c;
    private ColouredGraph graph;

    public static void main(String[] args) {
        long start = System.currentTimeMillis();
        BasicGraph graph = new BasicGraph("DIMACS_subset_ascii/C125.9.clq");
        OstergardBranchAndBound obab = new OstergardBranchAndBound();
        BitSet maxClique = obab.calculateMaxClique(graph);
        System.out.println(maxClique);
        System.out.println(graph.isClique(maxClique));
        System.out.println(maxClique.cardinality());
        long end = System.currentTimeMillis();
        System.out.println(end - start);
    }

    private void calculateMaxClique(BitSet vertices, int size, BitSet clique) {

        if (vertices.cardinality() == 0) {
            if (size > this.max) {
                this.max = size;
                this.maxClique = clique;
                this.found = true;
            }
            return;
        }

        while (vertices.cardinality() != 0) {

            if (size + vertices.cardinality() <= this.max) {
                break;
            }

            int min_index = vertices.nextSetBit(0);
            if (size + this.c[min_index] <= this.max) {
                break;
            }
            vertices.flip(min_index);

            // only keep neighbours of min_index vertex
            BitSet newVertices = (BitSet) vertices.clone();
            newVertices.and(this.graph.getAdjacencyBitSet(min_index));

            // add the min_index vertex to the clique
            BitSet newClique = (BitSet) clique.clone();
            newClique.set(min_index);

            calculateMaxClique(newVertices, size + 1, newClique);

            if (this.found) {
                break;
            }
        }
    }

    public BitSet calculateMaxClique(BasicGraph graph) {
        int numVertices = graph.getNumVertices();
        this.graph = new ColouredGraph(graph).orderByColour();
        this.max = 0;
        this.c = new Integer[numVertices];
        for (int i = numVertices - 1; i >= 0; i -= 1) {
            this.found = false;
            System.out.println("Round: " + i);

            // initial vertices to start the round
            BitSet roundVertices = new BitSet(numVertices);
            roundVertices.set(i, numVertices);
            roundVertices.and(this.graph.getAdjacencyBitSet(i));

            BitSet clique = new BitSet(numVertices);
            clique.set(i);

            calculateMaxClique(roundVertices, 1, clique);
            this.c[i] = this.max;
        }
        return this.graph.getOriginalVertexIds(maxClique);
    }
}
