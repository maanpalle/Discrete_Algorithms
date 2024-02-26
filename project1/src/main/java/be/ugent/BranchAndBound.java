package be.ugent;

import be.ugent.graphs.BasicGraph;

import java.util.*;

public class BranchAndBound implements MaximumCliqueAlgorithm {
    private BitSet biggestClique;
    private BasicGraph graph;
    private BitSet[] adjacencyList;
    private List<BitSet> colors;

    private BitSet MaxClique(BitSet clique, BitSet remaining) {
        int id = remaining.nextSetBit(0);
        while (id != -1 && clique.cardinality() + countColors(remaining) > biggestClique.cardinality()) {
            clique.set(id);
            remaining.clear(id);
            BitSet newRemaining = (BitSet) remaining.clone();
            newRemaining.and(adjacencyList[id]);
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
        double sum = 0;
        for (int i = 0; i < numVertices; i++) {
            sum += (1.0 / (numVertices - graph.degree(i)));
        }
        int minSize = (int) Math.ceil(sum);
        biggestClique = new BitSet(numVertices);
        biggestClique.set(0, minSize - 1);
        BitSet allVertices = new BitSet(numVertices);
        allVertices.set(0, numVertices);
        List<Integer> vertices = graph.orderByDegree();
        Collections.reverse(vertices);
        newAdjacencyList(vertices);
        greedyColor(allVertices);
        BitSet maxClique = MaxClique(new BitSet(numVertices), allVertices);
        BitSet solution = new BitSet(numVertices);
        int id = maxClique.nextSetBit(0);
        while (id != -1) {
            solution.set(vertices.get(id));
            id = maxClique.nextSetBit(id + 1);
        }
        return solution;
    }

    private void newAdjacencyList(List<Integer> vertices) {
        adjacencyList = new BitSet[vertices.size()];
        for (int i = 0; i < vertices.size(); i++) {
            adjacencyList[i] = new BitSet(vertices.size());
        }
        for (int j = 0; j < vertices.size(); j++) {
            int vertexJ = vertices.get(j);
            for (int k = 0; k < vertices.size(); k++) {
                int vertexK = vertices.get(k);
                if (graph.isAdjacent(vertexJ, vertexK)) {
                    adjacencyList[j].set(k);
                    adjacencyList[k].set(j);
                }
            }
        }
    }

    private void greedyColor(BitSet vert) {
        colors = new ArrayList<>();
        int id = vert.previousSetBit(graph.getNumVertices());
        while (id != -1) {
            int color = 0;
            while (color < colors.size() && colors.get(color).intersects(adjacencyList[id])) {
                color++;
            }
            if (color == colors.size()) {
                colors.add(new BitSet(graph.getNumVertices()));
            }
            colors.get(color).set(id);
            id = vert.previousSetBit(id - 1);
        }
    }

    private int countColors(BitSet vertices) {
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