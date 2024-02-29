package graphs;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

public class BasicGraph {

    // made with chatGPT

    protected BitSet[] adjacencyList;
    protected int numVertices;

    public BasicGraph(String graphFilename) {
        try (BufferedReader br = new BufferedReader(new FileReader(graphFilename))) {
            String line = br.readLine();
            while (!line.startsWith("p")) {
                line = br.readLine();
            }
            numVertices = Integer.parseInt(line.split(" ")[2]);
            adjacencyList = new BitSet[numVertices];
            for (int i = 0; i < numVertices; i++) {
                adjacencyList[i] = new BitSet(numVertices);
            }
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(" ");
                int source = Integer.parseInt(parts[1]) - 1; // DIMACS vertices start from 1
                int destination = Integer.parseInt(parts[2]) - 1; // DIMACS vertices start from 1
                addEdge(source, destination);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Basic function to create an empty graph
     */
    public BasicGraph() {
        adjacencyList = new BitSet[0];
        numVertices = 0;
    }

    /**
     * Function to clone a graph
     * @param graph graph to clone
     * @return cloned graph
     */
    public BasicGraph clone(BasicGraph graph) {
        BasicGraph cloned = new BasicGraph();
        this.adjacencyList = cloned.adjacencyList.clone();
        this.numVertices = cloned.numVertices;
        return cloned;
    }

    /**
     * Function to get the number of nodes in a graph
     * @return
     */
    public int getNumNodes(){
        return adjacencyList.length;
    }

    public void addEdge(int source, int destination) {
        adjacencyList[source].set(destination);
        adjacencyList[destination].set(source); // Since it's an undirected graph
    }

    public boolean hasEdge(int source, int destination) {
        return adjacencyList[source].get(destination);
    }

    public void swapVertices(int vertex1, int vertex2) {
        if (vertex1 == vertex2) {
            return; // No need to swap if the vertices are the same
        }
        // Swap adjacency information for vertex1 and vertex2
        BitSet temp = adjacencyList[vertex1];
        adjacencyList[vertex1] = adjacencyList[vertex2];
        adjacencyList[vertex2] = temp;

        // Update adjacency information for other vertices
        for (int i = 0; i < numVertices; i++) {
            if (i != vertex1 && i != vertex2) {
                boolean tempBit = adjacencyList[i].get(vertex1);
                adjacencyList[i].set(vertex1, adjacencyList[i].get(vertex2));
                adjacencyList[i].set(vertex2, tempBit);
            }
        }
    }

    // Method to order vertices based on their degrees
    public List<Integer> orderByDegree() {
        List<Integer> vertices = new ArrayList<>();
        for (int i = 0; i < numVertices; i++) {
            vertices.add(i);
        }

        // Sort vertices based on their degrees (largest degree first)
        vertices.sort(Comparator.comparingInt(this::degree).reversed());

        return vertices;
    }

    public int getNumVertices() {
        return numVertices;
    }

    // Method to calculate the degree of a vertex
    public int degree(int vertex) {
        return adjacencyList[vertex].cardinality();
    }

    public BitSet getAdjacencyList(int vertex) {
        return adjacencyList[vertex];
    }

    public boolean isClique(BitSet vertices) {
        // boolean clique = true;
        for (int i = 0; i < vertices.size(); i++) {
            if(vertices.get(i)) {
                for (int j = i + 1; j < getNumNodes(); j++) {
                    if(vertices.get(j) && ! adjacencyList[i].get(j)) {
                        return false;
                    }
                }
            }
        }
        return true;
        // int card = vertices.cardinality();
        // int id = vertices.nextSetBit(0);
        // BitSet check = (BitSet) vertices.clone();
        // while (id != 0 && clique) {
        //     check.and(adjacencyList[id]);
        //     clique = (check.cardinality() == card);
        //     id = vertices.nextSetBit(id + 1);
        // }
        // return clique;
    }

    public boolean isClique(int[] vertices) {
        BitSet test = new BitSet(numVertices);
        for (int i : vertices) {
            test.set(i);
        }
        return isClique(test);
    }

    public static void main(String[] args) {
        BasicGraph graph = new BasicGraph("DIMACS_subset_ascii/C125.9.clq");
        System.out.println("Adjacency information:");
        for (int i = 0; i < graph.numVertices; i++) {
            System.out.println("Vertex " + i + ": " + graph.adjacencyList[i]);
        }
        System.out.println(graph.orderByDegree());
    }
}
