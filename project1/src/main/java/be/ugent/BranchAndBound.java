package be.ugent;

import be.ugent.graphs.BasicGraph;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.Comparator;
import java.util.List;

public class BranchAndBound implements MaximumCliqueAlgorithm {
    private BitSet biggestClique;//currently biggest found clique
    private BasicGraph gr;//graph with vertices reordered from smallest to largest degree

    /*
    Tries to add every vertex in remaining to the current clique
    Uses recursion to find the biggest clique in each instance
     */
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

    /*
    Creates a new graph with the vertices reordered from smallest to largest degree,
    tries to find an initial maximal clique with a greedy approach
    Then initializes the recursion function to find the maximum clique
     */
    @Override
    public BitSet calculateMaxClique(BasicGraph graph) {
        this.gr = new BasicGraph(graph);
        List<Integer> vertices = gr.orderByUpwardsDegree();
        gr.reorderVertices(vertices);
        int numVertices = vertices.size();
        biggestClique = greedyClique();
        //Don't sort colors
        BitSet greedyColorClique = greedyColorClique(null);
        if (greedyColorClique.cardinality() > biggestClique.cardinality()) {
            biggestClique = greedyColorClique;
        }
        //Sort colors by number of vertices with that color
        greedyColorClique = greedyColorClique(Comparator.comparingInt(BitSet::cardinality));
        if (greedyColorClique.cardinality() > biggestClique.cardinality()) {
            biggestClique = greedyColorClique;
        }
        //Sort colors by number of vertices with that color
        //break ties by sorting based on the highest degree vertex for each color, highest first
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

    /*
    Colors the given vertices by sequentially adding the vertices with the highest degrees in the original graph
     */
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

    /*
    Finds a maximal clique by sequentially trying to add the vertices
    with the highest relative degree within the remaining vertices
     */
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

    /*
    Colors the given vertices by sequentially adding the vertices
    with the highest relative degrees within the remaining vertices
     */
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

    /*
    Finds a maximal clique by sequentially coloring the remaining vertices,
    sorting the colors according to the given comparator
    and adding the vertex with the biggest degree from the first color
     */
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
}