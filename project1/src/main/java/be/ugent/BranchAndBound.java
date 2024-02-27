package be.ugent;

import be.ugent.graphs.BasicGraph;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.Comparator;
import java.util.List;

public class BranchAndBound implements MaximumCliqueAlgorithm {
    private BitSet biggestClique;
    private BasicGraph gr;

    private BitSet MaxClique(BitSet clique, BitSet remaining) {
        int id = remaining.nextSetBit(0);
        while (id != -1 && clique.cardinality() + countColors(remaining) > biggestClique.cardinality()) {
            clique.set(id);
            remaining.clear(id);
            BitSet newRemaining = (BitSet) remaining.clone();
            newRemaining.and(gr.getAdjacencyBitSet(id));
            BitSet maxClique = clique;
            if (newRemaining.cardinality() != 0) {
                maxClique = MaxClique(clique, newRemaining);
            }
            if (maxClique.cardinality() > biggestClique.cardinality()) {
                biggestClique = (BitSet) maxClique.clone();
            }
            clique.clear(id);
            id = remaining.nextSetBit(id + 1);
        }
        return biggestClique;
    }

    @Override
    public BitSet calculateMaxClique(BasicGraph graph) {
        this.gr = new BasicGraph(graph);
        List<Integer> vertices = gr.orderByUpwardsDegree();
        gr.reorderVertices(vertices);
        int numVertices = vertices.size();
        greedyClique();
        BitSet greedyColorClique = greedyColorClique();
        if (greedyColorClique.cardinality() > biggestClique.cardinality()) {
            biggestClique = greedyColorClique;
        }
        BitSet allVertices = new BitSet(numVertices);
        allVertices.set(0, numVertices);
        BitSet maxClique = MaxClique(new BitSet(numVertices), allVertices);
        BitSet solution = new BitSet(numVertices);
        int id = maxClique.nextSetBit(0);
        while (id != -1) {
            solution.set(vertices.get(id));
            id = maxClique.nextSetBit(id + 1);
        }
        return solution;
    }

    private int countColors(BitSet vert) {
        return greedyColor(vert).size();
    }

    private List<BitSet> greedyColor(BitSet vert) {
        List<BitSet> colors = new ArrayList<>();
        int id = vert.previousSetBit(gr.getNumVertices());
        while (id != -1) {
            int color = 0;
            while (color < colors.size() && colors.get(color).intersects(gr.getAdjacencyBitSet(id))) {
                color++;
            }
            if (color == colors.size()) {
                colors.add(new BitSet(gr.getNumVertices()));
            }
            colors.get(color).set(id);
            id = vert.previousSetBit(id - 1);
        }
        return colors;
    }

    private int nextColoredVertex(BitSet vert) {
        if (vert.cardinality() == 0) {
            return -1;
        }
        List<BitSet> colors = greedyColor(vert);
        colors.sort(Comparator.comparingInt(BitSet::cardinality));
        return colors.get(0).previousSetBit(gr.getNumVertices());
    }

    private BitSet greedyColorClique() {
        BitSet remaining = new BitSet(gr.getNumVertices());
        remaining.set(0, gr.getNumVertices());
        BitSet biggestClique = new BitSet(gr.getNumVertices());
        int id = nextColoredVertex(remaining);
        while (id != -1) {
            biggestClique.set(id);
            remaining.and(gr.getAdjacencyBitSet(id));
            id = nextColoredVertex(remaining);
        }
        return biggestClique;
    }

    private void greedyClique() {
        BitSet remaining = new BitSet(gr.getNumVertices());
        remaining.set(0, gr.getNumVertices());
        biggestClique = new BitSet(gr.getNumVertices());
        int id = remaining.previousSetBit(gr.getNumVertices() + 1);
        while (id != -1) {
            biggestClique.set(id);
            remaining.and(gr.getAdjacencyBitSet(id));
            id = remaining.previousSetBit(id - 1);
        }
    }

    private void test(BasicGraph graph) {
        BitSet maxClique = calculateMaxClique(graph);
        System.out.println(maxClique);
        System.out.println(maxClique.cardinality());
        System.out.println(graph.isClique(maxClique));
    }

    public static void main(String[] args) {
        long start = System.currentTimeMillis();
        BranchAndBound bAB = new BranchAndBound();
        bAB.test(new BasicGraph("DIMACS_subset_ascii/C125.9.clq"));
//        bAB.test(new BasicGraph("DIMACS_subset_ascii/DSJC500_5.clq"));
//        bAB.test(new BasicGraph("DIMACS_subset_ascii/MANN_a27.clq"));
//        bAB.test(new BasicGraph("DIMACS_subset_ascii/brock200_2.clq"));
//        bAB.test(new BasicGraph("DIMACS_subset_ascii/brock200_4.clq"));
//        bAB.test(new BasicGraph("DIMACS_subset_ascii/gen200_p0.9_44.clq"));
//        bAB.test(new BasicGraph("DIMACS_subset_ascii/gen200_p0.9_55.clq"));
//        bAB.test(new BasicGraph("DIMACS_subset_ascii/hamming8-4.clq"));
//        bAB.test(new BasicGraph("DIMACS_subset_ascii/keller4.clq"));
//        bAB.test(new BasicGraph("DIMACS_subset_ascii/p_hat300-1.clq"));
//        bAB.test(new BasicGraph("DIMACS_subset_ascii/p_hat300-2.clq"));
//        bAB.test(new BasicGraph("DIMACS_subset_ascii/p_hat300-3.clq"));
//        bAB.test(new BasicGraph("DIMACS_subset_ascii/p_hat700-1.clq"));
//        bAB.test(new BasicGraph("DIMACS_subset_ascii/p_hat1500-1.clq"));
        long end = System.currentTimeMillis();
        System.out.println(end - start);
    }
}