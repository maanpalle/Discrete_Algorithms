package be.ugent;

import be.ugent.graphs.BasicGraph;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.List;

public class BranchAndBound implements MaximumCliqueAlgorithm {
    private BitSet biggestClique;
    private BasicGraph gr;

    private BitSet MaxClique(BitSet clique, BitSet remaining, List<BitSet> colors) {
        int id = remaining.nextSetBit(0);
        while (id != -1 && clique.cardinality() + countColors(remaining, colors) > biggestClique.cardinality()) {
            clique.set(id);
            remaining.clear(id);
            BitSet newRemaining = (BitSet) remaining.clone();
            newRemaining.and(gr.getAdjacencyBitSet(id));
            BitSet maxClique = clique;
            if (newRemaining.cardinality() != 0) {
                maxClique = MaxClique(clique, newRemaining, colors);
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
        this.gr = new BasicGraph(graph);
        List<Integer> vertices = gr.orderByUpwardsDegree();
        gr.reorderVertices(vertices);
        int numVertices = vertices.size();
        float minSize = 0;
        for (int i = 0; i < numVertices; i++) {
            minSize += (1.0f / (numVertices - gr.degree(i)));
        }
        biggestClique = new BitSet(numVertices);
        biggestClique.set(0, (int) minSize);
        BitSet allVertices = new BitSet(numVertices);
        allVertices.set(0, numVertices);
        List<BitSet> colors = greedyColor(allVertices);
        System.out.println(colors.size());
        BitSet maxClique = MaxClique(new BitSet(numVertices), allVertices, colors);
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

    private int countColors(BitSet vertices, List<BitSet> colors) {
        int count = 0;
        for (BitSet color : colors) {
            if (color.intersects(vertices)) {
                count++;
            }
        }
        return count;
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