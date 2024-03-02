package be.ugent;


import be.ugent.graphs.BasicGraph;
import be.ugent.util.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.javatuples.Pair;

import java.util.*;


public class BreakoutLocalSearch implements MaximumCliqueAlgorithm {

	private static final Logger logger = LogManager.getLogger(BreakoutLocalSearch.class);

	private static final int SEED = 42;
	private static final int MAX_ITER = 10_000_000;
	private static Random random = new Random(SEED);
	// L_0: Initial jump magnitude factor
	private final float initialJumpMagnitudeFactor = 0.01f;

	private int initialJumpMagnitude;

	// L_{max}: Maximal jump magnitude for restart
	private final float maxJumpMagnitudeFactor = 0.1f;
	private int maxJumpMagnitude;

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

	private BLSTabuList tabuList;

	private int upperBound = 0;

//	private Queue<BLSMove> history = new ArrayDeque<>();

	private List<Pair<Integer, Integer>> OM;
	private BitSet PA;
	private BitSet OC;
	private BitSet cachedClique;


	/**
	 * Constructor for the be.ugent.BreakoutLocalSearch class.
	 */
	public BreakoutLocalSearch(int upperBound) {
		this.tabuList = new BLSTabuList(this.tabuTenure);
		this.upperBound = upperBound;
	}


	// The vertex set PA consists of nodes excluded from the clique C that are connected to all the vertices in C, i.e.,
	// PA = {v: v ∈ V \ C, ∀u ∈ C, (u, v) ∈ E}.
	public BitSet getPA(BasicGraph graph, BitSet clique) {
		if (this.PA != null && cachedClique.equals(clique)) {
			return this.PA;
		}
		this.cachedClique = (BitSet) clique.clone();

		BitSet pa = new BitSet(graph.getNumVertices());
		// Set all bits to true


		int cliqueCardinality = clique.cardinality();
		// Iterate over all vertices not in the clique
		for (int v = clique.nextClearBit(0); v < graph.getNumVertices(); v = clique.nextClearBit(v + 1)) {
			// Get the adjacency list for the vertex
			BitSet adjacent = (BitSet) graph.getAdjacencyBitSet(v).clone();
			// Remove all vertices that are not adjacent to the vertex
			adjacent.and(clique);
			if (adjacent.cardinality() == cliqueCardinality && adjacent.equals(clique)) {
				pa.set(v);
			}
		}
		return pa;
	}

	//The OM set consists of vertex pairs (v,u) such that v is excluded from C and is connected to all vertices in C
	// except to vertex u that is included in C, i.e.,
	// OM = {(v, u): v ∈ V \ C, u ∈ C, ∀w ∈ C \ {u}, (v, w) ∈ E}.
	public List<Pair<Integer, Integer>> getOM(BasicGraph graph, BitSet clique) {
		if (this.OM != null && cachedClique.equals(clique)) {
			return this.OM;
		}
		this.cachedClique = (BitSet) clique.clone();

		List<Pair<Integer, Integer>> om = new ArrayList<>();
		for (int u = clique.nextSetBit(0); u >= 0; u = clique.nextSetBit(u + 1)) {
			BitSet adjacencyU = graph.getAdjacencyBitSet(u);

			// The clique without vertex u
			BitSet cliqueWithoutU = (BitSet) clique.clone();
			cliqueWithoutU.clear(u);

			for (int v = adjacencyU.nextClearBit(0); v < graph.getNumVertices(); v = adjacencyU.nextClearBit(v + 1)) {
				if (clique.get(v)) { // Skip if v is part of the clique
					continue;
				}

				// Ensure v is connected to all vertices in the clique except u
				BitSet adjacencyV = graph.getAdjacencyBitSet(v);
				BitSet tempCliqueWithoutU = (BitSet) cliqueWithoutU.clone();
				tempCliqueWithoutU.and(adjacencyV);

				if (tempCliqueWithoutU.equals(cliqueWithoutU)) {
					om.add(new Pair<>(v, u));
				}
			}
		}
		return om;
	}

	// The OC set consists of all the vertices excluded from the clique C, i.e.,
	// OC = V \ C.
	public BitSet getOC(BasicGraph graph, BitSet clique) {
		BitSet oc = (BitSet) clique.clone();
		oc.flip(0, graph.getNumVertices());
		return oc;
	}

	// The initial solution C used by BLS is generated in the following way. Select uniformly at random a vertex v ∈ V
	// and place it into C.
	// While there exists a vertex u ∈ V\C such that ∀ c ∈ C, {u,c} ∈ E, add u to C. This procedure stops when no
	// vertex can be added to C, giving a valid clique.
	public BitSet generateInitialSolution(BasicGraph graph) {
		Random random = new Random(SEED);
		BitSet clique = new BitSet(graph.getNumVertices());
		int vertex = random.nextInt(graph.getNumVertices());
		clique.set(vertex);
//		history.add(new BLSMove(BLSMoveType.ADDITION, vertex, new BitSet()));
		BitSet notInClique = (BitSet) clique.clone();
		notInClique.flip(0, graph.getNumVertices());
		boolean added = true;
		while (added) {
			added = false;
			for (int i = notInClique.nextSetBit(0); i >= 0; i = notInClique.nextSetBit(i + 1)) {
				BitSet adjacent = (BitSet) graph.getAdjacencyBitSet(i).clone();
				BitSet testSet = (BitSet) clique.clone();
				testSet.and(adjacent);
				if (testSet.cardinality() == clique.cardinality()) {
					clique.set(i);
					notInClique.clear(i);
					added = true;
//					history.add(new BLSMove(BLSMoveType.ADDITION, i, new BitSet()));
				}
			}
		}
		return clique;
	}

	/**
	 * Select a vertex v from the PA set and insert it into the clique C. After this move, the change in the objective
	 * function is 1.
	 *
	 * @param graph  The graph in which to find the maximum clique.
	 * @param clique A BitSet representing the current clique. The vertices are 0-indexed.
	 * @return A BitSet representing the new clique after the move. The vertices are 0-indexed.
	 */
	private BLSMove move1(BasicGraph graph, BitSet clique) {
		BitSet PA = getPA(graph, clique);
		if (PA.cardinality() == 0) {
			return BLSMove.NOOP();
		}
		return new BLSMove(BLSMoveType.ADDITION, PA.nextSetBit(0), new BitSet());
	}

	private List<BLSMove> allMoves1(BasicGraph graph, BitSet clique) {
		BitSet PA = getPA(graph, clique);
		return PA.stream()
				.mapToObj(i -> new BLSMove(BLSMoveType.ADDITION, i, new BitSet()))
				.toList();
	}


	/**
	 * Select a vertex pair (v, u) from the OM set and replace u by v in the clique C. After this move, the change in the
	 * objective function is zero.
	 *
	 * @param graph  The graph in which to find the maximum clique.
	 * @param clique A BitSet representing the current clique. The vertices are 0-indexed.
	 * @return A BitSet representing the new clique after the move. The vertices are 0-indexed.
	 */
	private BLSMove move2(BasicGraph graph, BitSet clique) {
		List<Pair<Integer, Integer>> om = this.getOM(graph, clique);
		if (om.isEmpty()) {
			return BLSMove.NOOP();
		}
		Pair<Integer, Integer> swap = om.get(0);
		BitSet removed = new BitSet();
		removed.set(swap.getValue1());
		return new BLSMove(BLSMoveType.SWAP, swap.getValue0(), removed);
	}

	private List<BLSMove> allMoves2(BasicGraph graph, BitSet clique) {
		List<Pair<Integer, Integer>> OM = getOM(graph, clique);
		return OM.stream()
				.map(pair -> {
					BitSet removed = new BitSet();
					removed.set(pair.getValue1());
					return new BLSMove(BLSMoveType.SWAP, pair.getValue0(), removed);
				})
				.toList();
	}


	/**
	 * Select a vertex v from the clique C and remove it from the clique. After this move, the change in the objective
	 * function is -1.
	 */
	private List<BLSMove> allMoves3(BitSet clique) {
		return clique.stream()
				.mapToObj(i -> new BLSMove(BLSMoveType.REMOVAL, i, new BitSet()))
				.toList();
	}

	private List<BLSMove> allMoves4(BasicGraph graph, BitSet clique, float alpha) {
		BitSet OC = getOC(graph, clique);
		List<BLSMove> moves = new ArrayList<>();
		int vertex = -1;
		while (OC.nextSetBit(vertex + 1) != -1) {
			vertex = OC.nextSetBit(vertex + 1);
			BitSet adjacent = (BitSet) graph.getAdjacencyBitSet(vertex).clone();
			adjacent.and(clique);
			int value = 1 + adjacent.cardinality();
			if (value >= clique.cardinality() * alpha) {
				BitSet removed = (BitSet) clique.clone();
				removed.andNot(graph.getAdjacencyBitSet(vertex));
				if (removed.cardinality() > 0) {
					moves.add(new BLSMove(BLSMoveType.SWAP, vertex, removed));
				}
			}
		}
		return moves;
	}

	private List<BLSMove> directedPerturbationMoves(BasicGraph graph, BitSet clique, BLSTabuList tabuList, int f_best) {
		List<BLSMove> moves = new ArrayList<>();

		moves.addAll(allMoves1(graph, clique));
		moves.addAll(allMoves2(graph, clique));
		moves.addAll(allMoves3(clique));

		return moves.stream()
				.filter(move -> tabuList.isMoveAllowed(move, f_best) || move.value() + clique.cardinality() > f_best)
				.sorted(Comparator.comparingInt(BLSMove::value))
				.toList();
	}

	private BitSet perturb(BitSet clique, float perturbationStrength, BLSMoveList moveList, int iteration, int OMSize) {
		BitSet resultClique = (BitSet) clique.clone();
		for (int i = 0; i < perturbationStrength; i++) {
			List<BLSMove> moves = moveList.getMoves(resultClique);

			if (moves.isEmpty()) {
				return resultClique;
			}
			BLSMove move = moves.get(random.nextInt(moves.size()));
			while (move.isNoop()
			) {
				move = moves.get(random.nextInt(moves.size()));

			}
			resultClique = move.apply(resultClique);
//			history.add(move);
			tabuList.add(move, iteration, OMSize);
		}
		return resultClique;
	}

	private BitSet perturbation(BasicGraph graph, BitSet clique, float perturbationStrength, BLSTabuList tabuList, int iter, int nonImprovingCounter, int f_best) {
		List<Pair<Integer, Integer>> om = getOM(graph, clique);

		BitSet resultClique;
		if (nonImprovingCounter == 0) {

			resultClique = perturb(clique, perturbationStrength,
					(BitSet c) -> allMoves4(graph, c, strongRandomPerturbationMagnitude), iter, om.size());
		} else {
			double probability = Math.exp((double) -nonImprovingCounter / this.maxNumberOfNonImprovingActors);
			probability = Math.max(probability, this.smallestProbability);

			if (random.nextDouble() < probability) {
				resultClique = perturb(clique, perturbationStrength,
						(BitSet c) -> directedPerturbationMoves(graph, c, tabuList, f_best), iter, om.size());
			} else {
				resultClique = perturb(clique, perturbationStrength,
						(BitSet c) -> allMoves4(graph, c, randomPerturbationMagnitude), iter, om.size());
			}
		}
		return resultClique;
	}

	/**
	 * Finds and returns the maximum clique in the given graph.
	 *
	 * @param graph The graph in which to find the maximum clique.
	 * @return A BitSet representing the vertices in the maximum clique. The vertices are 0-indexed.
	 */
	@Override
	public BitSet calculateMaxClique(BasicGraph graph) {
		int iter = 0;
		this.initialJumpMagnitude = (int) (this.initialJumpMagnitudeFactor * graph.getNumVertices());
		this.maxJumpMagnitude = (int) (this.maxJumpMagnitudeFactor * graph.getNumVertices());

		float perturbationStrength = this.initialJumpMagnitudeFactor;

		// Generate initial solution
		BitSet clique = generateInitialSolution(graph);

		// Create initial PA, OM, and OC sets (section 2.1.2)
		List<Pair<Integer, Integer>> OM = getOM(graph, clique);

		// f_c records the objective value of the solution
		int f_c = clique.cardinality();

		// C_best records the best solution found so far
		BitSet C_best = (BitSet) clique.clone();

		//f_best records the best objective value reached so far
		int f_best = f_c;

		// C_p records the last local optimum
		BitSet C_p = (BitSet) clique.clone();

		// Set counter for consecutive non-improving local optima
		int nonImprovingCounter = 0;

		// TODO(@vronsyn): Look at stopping condition
		while (iter < MAX_ITER && f_best < upperBound) {
			// logger.info("Iteration: {} - Current max clique: {} - {}", iter, f_best, clique);

			// Select the best move m from the set of moves formed by the union of M_1 and M_2
			// However, because the graph is unweighted, we can just select a random move
			BLSMove move = move1(graph, clique);
			BitSet bestMoveClique;
			if (move.isNoop()) {
				move = move2(graph, clique);
			}
			bestMoveClique = move.apply(clique);

			while (bestMoveClique.cardinality() > f_c && !move.isNoop()) {
				clique = (BitSet) bestMoveClique.clone();
				f_c = clique.cardinality();

				// Update PA, OM, and OC sets
				OM = getOM(graph, clique);

				// Update tabu list
				tabuList.add(move, iter, OM.size());
//				history.add(move);

				move = move1(graph, clique);
				if (move.isNoop()) {
					move = move2(graph, clique);
				}
				if (!move.isNoop()) {
					bestMoveClique = move.apply(clique);

					iter++;
				}
			}
			if (f_c > f_best) {
				C_best = (BitSet) clique.clone();
				f_best = f_c;
				nonImprovingCounter = 0;
			} else {
				nonImprovingCounter++;
			}
			if (nonImprovingCounter > maxNumberOfNonImprovingActors) {
				// Search seems to be stagnating, strong perturbation required
				perturbationStrength = this.maxJumpMagnitude;
				nonImprovingCounter = 0;

			} else if (clique.equals(C_p)) {
				// Search returned to the previous local optimum, increment perturbation strength
				perturbationStrength += 1;
			} else {
				// Search escaped from the previous local optimum, reinitialize perturbation strength
				perturbationStrength = this.initialJumpMagnitude;
			}

			// Perturb the current local optimum C with perturbation strength L
			C_p = (BitSet) clique.clone();
			clique = perturbation(graph, clique, perturbationStrength, tabuList, iter, nonImprovingCounter, f_best);

			// Increment the iteration counter, this should be done in the perturbation method, but it is not clear
			// from the paper why this is the case, so I'm doing it here
			iter++;


		}


		return C_best;
	}


	public static void main(String[] args) {
		String testfile = "brock200_2";
		TestFileDatabase testFileDatabase = new TestFileDatabase();
		BasicGraph graph = new BasicGraph("DIMACS_subset_ascii/" + testfile + ".clq");
		BreakoutLocalSearch bls = new BreakoutLocalSearch(testFileDatabase.getMaxClique(testfile));
		BitSet maxClique = bls.calculateMaxClique(graph);
		if (maxClique == null) {
			logger.error("Maximum clique not found. Exiting...");
			return;
		}
		logger.info("Maximum clique found!");
		logger.info("\tClique:\t{}", maxClique);
		logger.info("\t Size:\t{}", maxClique.cardinality());
	}
}


