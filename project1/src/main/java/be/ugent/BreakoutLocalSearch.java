package be.ugent;


import be.ugent.graphs.BasicGraph;
import org.javatuples.Pair;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.List;


public class BreakoutLocalSearch implements MaximumCliqueAlgorithm {
	// L_0: Initial jump magnitude
	private final float initialJumpMagnitudeFactor = 0.01f;

	// L_{max}: Maximal jump magnitude for restart
	private final float maxJumpMagnitudeFactor = 0.1f;

	// T: Max. number of non-improving attractors visited before strong perturb. (restart)
	private int maxNumberOfNonImprovingActors = 1000;

	// \alpha_r: Coefficient a for random perturbations.
	private float randomPerturbationMagnitude = 0.8f;

	// \alpha_s: Coefficient a for strong pertur. (restart)
	private float strongRandomPerturbationMagnitude = 0.8f;

	// \phi: Coefficient for tabu tenure
	private int tabuTenure = 7;

	// P_0: Smallest probability for applying directed perturb.
	private float smallestProbability = 0.75f;


	/**
	 * Constructor for the be.ugent.BreakoutLocalSearch class.
	 */
	public BreakoutLocalSearch() {
		// For now, the constructor is empty
	}

	/**
	 * Finds and returns the maximum clique in the given graph.
	 *
	 * @param graph The graph in which to find the maximum clique.
	 * @return A BitSet representing the vertices in the maximum clique. The vertices are 0-indexed.
	 */
	@Override
	public BitSet calculateMaxClique(BasicGraph graph) {
		// For now, the method is empty
		return null;
	}

	// The vertex set PA consists of nodes excluded from the clique C that are connected to all the vertices in C, i.e.,
	// PA = {v: v ∈ V \ C, ∀u ∈ C, (u, v) ∈ E}.
	private BitSet getPA(BasicGraph graph, BitSet clique) {
		BitSet pa = new BitSet(graph.getNumVertices());
		pa.set(0, graph.getNumVertices());
		pa.andNot(clique);
		for (int i = clique.nextSetBit(0); i >= 0; i = clique.nextSetBit(i + 1)) {
			for (int j = 0; j < graph.getNumVertices(); j++) {
				if (graph.isAdjacent(i, j)) {
					pa.clear(j);
				}
			}
		}
		return pa;
	}

	//The OM set consists of vertex pairs (v,u) such that v is excluded from C and is connected to all vertices in C
	// except to vertex u that is included in C, i.e.,
	// OM = {(v, u): v ∈ V \ C, u ∈ C, ∀w ∈ C \ {u}, (v, w) ∈ E}.
	public List<Pair<Integer, Integer>> getOM(BasicGraph graph, BitSet clique) {
		List<Pair<Integer, Integer>> om = new ArrayList<>();
		for (int u = clique.nextSetBit(0); u >= 0; u = clique.nextSetBit(u + 1)) {
			BitSet adjacent = graph.getAdjacencyBitSet(u);

			BitSet cliqueWithoutU = (BitSet) clique.clone();
			cliqueWithoutU.clear(u);

			BitSet notInClique = (BitSet) adjacent.clone();
			notInClique.andNot(cliqueWithoutU);

			for (int v = notInClique.nextSetBit(0); v >= 0; v = notInClique.nextSetBit(v + 1)) {
				BitSet adjacentToV = graph.getAdjacencyBitSet(v);
				if (adjacentToV.cardinality() > 0 && cliqueWithoutU.cardinality() > 0){
					BitSet testSet = (BitSet) cliqueWithoutU.clone();
					testSet.and(adjacentToV);
					if (testSet.cardinality() == 0) {
						om.add(new Pair<>(v, u));
					}

				}
			}
		}
		return om;
	}

	// The OC set consists of all the vertices excluded from the clique C, i.e.,
	// OC = V \ C.
	private BitSet getOC(BasicGraph graph, BitSet clique) {
		BitSet oc = new BitSet(graph.getNumVertices());
		oc.set(0, graph.getNumVertices());
		oc.andNot(clique);
		return oc;
	}



}


