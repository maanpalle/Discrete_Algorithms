package be.ugent.graphs;


import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Paths;
import java.util.*;

public class BasicGraph {

	private static final Logger logger = LogManager.getLogger(BasicGraph.class);


	// made with chatGPT

	protected BitSet[] adjacencyList;
	protected int numVertices;

	public BasicGraph(String graphFilename) {
		int expectedNumberOfEdges = -1; // Initialize with a sentinel value
		int actualNumberOfEdges;

		URL res = getClass().getClassLoader().getResource(graphFilename);
		File file = null;
		try {
			file = Paths.get(res.toURI()).toFile();
			try (BufferedReader br = new BufferedReader(new FileReader(file))) {
				String line;
				while ((line = br.readLine()) != null) {
					line = line.trim();
					// Skip comment lines or empty lines
					if (line.isEmpty() || line.charAt(0) == 'c') {
						continue;
					}
					// Process problem line
					if (line.charAt(0) == 'p') {
						StringTokenizer st = new StringTokenizer(line);
						st.nextToken(); // skip 'p' token
						st.nextToken(); // skip problem type (e.g., 'edge')
						this.numVertices = Integer.parseInt(st.nextToken());
						expectedNumberOfEdges =
								Integer.parseInt(st.nextToken()); // Store the expected number of edges
						// Initialize graph with the number of vertices
						adjacencyList = new BitSet[this.numVertices];
						for (int i = 0; i < this.numVertices; i++) {
							adjacencyList[i] = new BitSet(this.numVertices);
						}
					} else if (line.charAt(0) == 'e') {
						// Process edge line
						String[] parts = line.split(" ");
						int source = Integer.parseInt(parts[1]) - 1; // DIMACS vertices start from 1
						int destination = Integer.parseInt(parts[2]) - 1; // DIMACS vertices start from 1
						addEdge(source, destination);
					}
				}
				actualNumberOfEdges = this.getNumEdges();
				if (actualNumberOfEdges != expectedNumberOfEdges) {

					logger.error(
							"Error: The actual number of edges ({}) does not match the expected number ({}).",
							actualNumberOfEdges,
							expectedNumberOfEdges);
					logger.error("Exiting...");
					System.exit(1);
				}

			} catch (IOException e) {
				logger.error("Error reading file: {}", e.getMessage());
				logger.error("Exiting...");
				System.exit(1);
			}
		} catch (URISyntaxException e) {
			logger.error("Error reading file: {}", e.getMessage());
			logger.error("Exiting...");
			System.exit(1);
		}

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

	public int getNumEdges() {
		int edges = 0;
		for (int i = 0; i < numVertices; i++) {
			edges += adjacencyList[i].cardinality();
		}
		return edges / 2; // Since the graph is undirected
	}

	// Method to calculate the degree of a vertex
	public int degree(int vertex) {
		return adjacencyList[vertex].cardinality();
	}

	public BitSet getAdjacencyBitSet(int vertex) {
		return adjacencyList[vertex];
	}

	public boolean isClique(BitSet vertices) {
		boolean clique = true;
		int card = vertices.cardinality();
		int id = vertices.nextSetBit(0);
		BitSet check = (BitSet) vertices.clone();
		while (id != 0 && clique) {
			check.and(adjacencyList[id]);
			clique = (check.cardinality() == card);
			id = vertices.nextSetBit(id + 1);
		}
		return clique;
	}

	public boolean isClique(int[] vertices) {
		BitSet test = new BitSet(numVertices);
		for (int i : vertices) {
			test.set(i);
		}
		return isClique(test);
	}

	public static void main(String[] args) {
		BasicGraph graph = new BasicGraph("DIMACS_subset_ascii/gen400_p0.9_65.clq");
		logger.info("Number of vertices: {}", graph.getNumVertices());
		logger.info("Number of edges: {}", graph.getNumEdges());

		logger.info("Adjacency information:");
		for (int i = 0; i < graph.numVertices; i++) {
			logger.debug("Vertex {}: {}", i, graph.adjacencyList[i]);
		}
		logger.info(graph.orderByDegree());
	}

	public boolean isAdjacent(int i, int j) {
		return adjacencyList[i].get(j);
	}
}