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
        List<BitSet> colors = greedyColor(remaining);
        BitSet color = colors.get(colors.size() - 1);
        int id = color.nextSetBit(0);
        while (id != -1 && clique.cardinality() + colors.size() > biggestClique.cardinality()) {
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
            color.clear(id);
            if (color.isEmpty() && colors.size() != 1) {
                colors.remove(colors.size() - 1);
                color = colors.get(colors.size() - 1);
            }
            id = color.nextSetBit(0);
        }
        return biggestClique;
    }

    @Override
    public BitSet calculateMaxClique(BasicGraph graph) {
        this.gr = new BasicGraph(graph);
        List<Integer> vertices = gr.orderByUpwardsDegree();
        gr.reorderVertices(vertices);
        int numVertices = vertices.size();
        biggestClique = greedyClique();
        BitSet greedyColorClique = greedyColorClique(null);
        if (greedyColorClique.cardinality() > biggestClique.cardinality()) {
            biggestClique = greedyColorClique;
        }
        greedyColorClique = greedyColorClique(Comparator.comparingInt(BitSet::cardinality));
        if (greedyColorClique.cardinality() > biggestClique.cardinality()) {
            biggestClique = greedyColorClique;
        }
        greedyColorClique = greedyColorClique(Comparator.comparingInt(BitSet::cardinality).thenComparing(BitSet::length, Comparator.reverseOrder()));
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

    private BitSet greedyClique() {
        BitSet remaining = new BitSet(gr.getNumVertices());
        remaining.set(0, gr.getNumVertices());
        BitSet biggestClique = new BitSet(gr.getNumVertices());
        List<Integer> vertices = gr.orderByRelativeDegree(remaining);
        int i = vertices.size() - 1;
        while (i >= 0) {
            int id = vertices.get(i);
            biggestClique.set(id);
            remaining.and(gr.getAdjacencyBitSet(id));
            vertices = gr.orderByRelativeDegree(remaining);
            i = vertices.size() - 1;
        }
        return biggestClique;
    }

    private List<BitSet> greedyRelativeColor(BitSet vert) {
        List<BitSet> colors = new ArrayList<>();
        List<Integer> vertices = gr.orderByRelativeDegree(vert);
        int i = 0;
        while (i < vertices.size()) {
            int id = vertices.get(i);
            int color = 0;
            while (color < colors.size() && colors.get(color).intersects(gr.getAdjacencyBitSet(id))) {
                color++;
            }
            if (color == colors.size()) {
                colors.add(new BitSet(gr.getNumVertices()));
            }
            colors.get(color).set(id);
            i++;
        }
        return colors;
    }

    private BitSet greedyColorClique(Comparator<BitSet> comp) {
        BitSet remaining = new BitSet(gr.getNumVertices());
        remaining.set(0, gr.getNumVertices());
        BitSet biggestClique = new BitSet(gr.getNumVertices());
        while (!remaining.isEmpty()) {
            List<BitSet> colors = greedyRelativeColor(remaining);
            if (comp != null) {
                colors.sort(comp);
            }
            int id = colors.get(0).previousSetBit(gr.getNumVertices());
            biggestClique.set(id);
            remaining.and(gr.getAdjacencyBitSet(id));
        }
        return biggestClique;
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
//        bAB.test(new BasicGraph("DIMACS_subset_ascii/DSJC1000_5.clq"));
//        bAB.test(new BasicGraph("DIMACS_subset_ascii/MANN_a27.clq"));
//        bAB.test(new BasicGraph("DIMACS_subset_ascii/brock200_2.clq"));
//        bAB.test(new BasicGraph("DIMACS_subset_ascii/brock200_4.clq"));
//        bAB.test(new BasicGraph("DIMACS_subset_ascii/brock400_2.clq"));
//        bAB.test(new BasicGraph("DIMACS_subset_ascii/brock400_4.clq"));
//        bAB.test(new BasicGraph("DIMACS_subset_ascii/gen200_p0.9_44.clq"));
//        bAB.test(new BasicGraph("DIMACS_subset_ascii/gen200_p0.9_55.clq"));
//        bAB.test(new BasicGraph("DIMACS_subset_ascii/hamming8-4.clq"));
//        bAB.test(new BasicGraph("DIMACS_subset_ascii/keller4.clq"));
//        bAB.test(new BasicGraph("DIMACS_subset_ascii/p_hat300-1.clq"));
//        bAB.test(new BasicGraph("DIMACS_subset_ascii/p_hat300-2.clq"));
//        bAB.test(new BasicGraph("DIMACS_subset_ascii/p_hat300-3.clq"));
//        bAB.test(new BasicGraph("DIMACS_subset_ascii/p_hat700-1.clq"));
//        bAB.test(new BasicGraph("DIMACS_subset_ascii/p_hat700-2.clq"));
//        bAB.test(new BasicGraph("DIMACS_subset_ascii/p_hat1500-1.clq"));
        long end = System.currentTimeMillis();
        System.out.println(end - start);
    }
}