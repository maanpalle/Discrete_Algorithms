package be.ugent;


import be.ugent.graphs.BasicGraph;

import java.util.BitSet;

/**
 * This interface defines the contract for classes that implement a maximum clique finding algorithm.
 * A clique in a graph is a subset of vertices such that every two vertices in the subset are adjacent.
 * A maximum clique is a clique that includes the largest possible number of vertices.
 */
public interface MaximumCliqueAlgorithm {

	/**
	 * Finds and returns the maximum clique in the given graph.
	 *
	 * @param graph The graph in which to find the maximum clique.
	 * @return A BitSet representing the vertices in the maximum clique. The vertices are 0-indexed.
	 */
	public BitSet calculateMaxClique(BasicGraph graph);
}
