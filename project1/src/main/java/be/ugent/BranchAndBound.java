package be.ugent;

import be.ugent.graphs.BasicGraph;

import java.util.BitSet;
import java.util.Collections;
import java.util.List;

public class BranchAndBound implements MaximumCliqueAlgorithm {
    private BitSet biggestClique;
    private BasicGraph graph;
    private BitSet[] adjacencyList;

    private BitSet MaxClique(BitSet clique, BitSet remaining) {
        int id = remaining.nextSetBit(0);
        while (id != -1 && clique.cardinality() + remaining.cardinality() > biggestClique.cardinality()) {
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
        biggestClique = new BitSet(numVertices);
        BitSet allVertices = new BitSet(numVertices);
        allVertices.set(0, numVertices);
        List<Integer> vertices = graph.orderByDegree();
        Collections.reverse(vertices);
        newAdjacencyList(vertices);
        BitSet maxClique = MaxClique(new BitSet(numVertices), allVertices);
        BitSet solution = new BitSet(numVertices);
        for (int i = 0; i < numVertices; i++) {
            if (maxClique.get(i)) {
                solution.set(vertices.get(i));
            }
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