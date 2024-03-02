import be.ugent.BreakoutLocalSearch;
import be.ugent.graphs.BasicGraph;
import org.javatuples.Pair;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class BreakoutLocalSearchTest {

	static BasicGraph graph0;
	static BasicGraph graph1;
	static BasicGraph graph2;

	@BeforeAll
	static void setUp() {
		BitSet[] adjacencyList0 = new BitSet[5];
		BitSet[] adjacencyList1 = new BitSet[5];
		BitSet[] adjacencyList2 = new BitSet[5];
		for (int i = 0; i < 5; i++) {
			adjacencyList0[i] = new BitSet(5);
			adjacencyList1[i] = new BitSet(5);
			adjacencyList2[i] = new BitSet(5);
		}
		graph0 = new BasicGraph(adjacencyList0);
		graph1 = new BasicGraph(adjacencyList1);
		graph2 = new BasicGraph(adjacencyList2);

		// max clique is 0, 1, 2, 3, 4
		graph1.addEdge(0, 1);
		graph1.addEdge(0, 2);
		graph1.addEdge(0, 3);
		graph1.addEdge(0, 4);
		graph1.addEdge(1, 2);
		graph1.addEdge(1, 3);
		graph1.addEdge(1, 4);
		graph1.addEdge(2, 3);
		graph1.addEdge(2, 4);
		graph1.addEdge(3, 4);


		// max clique is 0, 1, 2, 3 or 0, 1, 2, 4
		graph2.addEdge(0, 1);
		graph2.addEdge(0, 2);
		graph2.addEdge(0, 3);
		graph2.addEdge(0, 4);
		graph2.addEdge(1, 2);
		graph2.addEdge(1, 3);
		graph2.addEdge(1, 4);
		graph2.addEdge(2, 3);
		graph2.addEdge(2, 4);
	}

	@Test
	void getPA_withEmptyClique_returnsAllVertices() {
		BitSet clique = new BitSet(graph0.getNumVertices());

		BreakoutLocalSearch breakoutLocalSearch = new BreakoutLocalSearch(0);
		BitSet result = breakoutLocalSearch.getPA(graph0, clique);

		BitSet expected = new BitSet(graph0.getNumVertices());
		expected.set(0, graph0.getNumVertices());

		assertEquals(expected, result);
	}

	@Test
	void getPA_withFullClique_returnsEmptySet() {
		BitSet clique = new BitSet(graph1.getNumVertices());
		clique.set(0, graph1.getNumVertices());

		BreakoutLocalSearch breakoutLocalSearch = new BreakoutLocalSearch(0);
		BitSet result = breakoutLocalSearch.getPA(graph1, clique);

		BitSet expected = new BitSet(graph1.getNumVertices());

		assertEquals(expected, result);
	}

	@Test
	void getPA_withPartialClique_returnsCorrectSet() {

		BitSet clique = new BitSet(graph2.getNumVertices());
		clique.set(0, 3); // Set vertices 0, 1 and 2 as part of the clique


		BreakoutLocalSearch breakoutLocalSearch = new BreakoutLocalSearch(0);
		BitSet result = breakoutLocalSearch.getPA(graph2, clique);

		BitSet expected = new BitSet(graph2.getNumVertices());
		expected.set(3); // Expect vertex 3 to be part of the PA set
		expected.set(4); // Expect vertex 4 to be part of the PA set

		assertEquals(expected, result);
	}

	@Test
	void getOC_withEmptyClique_returnsAllVertices() {
		BitSet clique = new BitSet(graph0.getNumVertices());

		BreakoutLocalSearch breakoutLocalSearch = new BreakoutLocalSearch(0);
		BitSet result = breakoutLocalSearch.getOC(graph0, clique);

		BitSet expected = new BitSet(graph0.getNumVertices());
		expected.set(0, graph0.getNumVertices());

		assertEquals(expected, result);
	}

	@Test
	void getOC_withFullClique_returnsEmptySet() {
		BitSet clique = new BitSet(graph1.getNumVertices());
		clique.set(0, graph1.getNumVertices());

		BreakoutLocalSearch breakoutLocalSearch = new BreakoutLocalSearch(0);
		BitSet result = breakoutLocalSearch.getOC(graph1, clique);

		BitSet expected = new BitSet(graph1.getNumVertices());

		assertEquals(expected, result);
	}

	@Test
	void getOC_withPartialClique_returnsCorrectSet() {
		BitSet clique = new BitSet(graph2.getNumVertices());
		clique.set(0, 3); // Set vertices 0, 1 and 2 as part of the clique


		BreakoutLocalSearch breakoutLocalSearch = new BreakoutLocalSearch(0);
		BitSet result = breakoutLocalSearch.getOC(graph2, clique);

		BitSet expected = new BitSet(graph2.getNumVertices());
		expected.set(3, 5); // Expect vertex 3 to be part of the PA set

		assertEquals(expected, result);
	}


	@Test
	void getOM_withEmptyClique_returnsAllVertices() {
		BitSet clique = new BitSet(graph0.getNumVertices());

		BreakoutLocalSearch breakoutLocalSearch = new BreakoutLocalSearch(0);
		List<Pair<Integer, Integer>> result = breakoutLocalSearch.getOM(graph0, clique);

		List<Pair<Integer, Integer>> expected = new ArrayList<>();

		assertEquals(expected, result);
	}

	@Test
	void getOM_withFullClique_returnsEmptySet() {
		BitSet clique = new BitSet(graph1.getNumVertices());
		clique.set(0, graph1.getNumVertices());

		BreakoutLocalSearch breakoutLocalSearch = new BreakoutLocalSearch(0);
		List<Pair<Integer, Integer>> result = breakoutLocalSearch.getOM(graph1, clique);

		List<Pair<Integer, Integer>> expected = new ArrayList<>();

		assertEquals(expected, result);
	}

	@Test
	void getOM_withPartialClique_returnsCorrectSet() {
		BitSet clique = new BitSet(graph2.getNumVertices());
		clique.set(0, 4); // Set vertices 0, 1 and 2 as part of the clique

		BreakoutLocalSearch breakoutLocalSearch = new BreakoutLocalSearch(0);
		List<Pair<Integer, Integer>> result = breakoutLocalSearch.getOM(graph2, clique);

		List<Pair<Integer, Integer>> expected = new ArrayList<>();
		expected.add(
				new Pair<>(4, 3)); // Expect vertex 4 to not be part of the clique and vertex 3 to be part of the clique

		assertEquals(expected, result);
	}

	@Test
	void initialClique_withEmptyGraph_returnsSingletonClique() {
		BreakoutLocalSearch breakoutLocalSearch = new BreakoutLocalSearch(0);
		BitSet result = breakoutLocalSearch.generateInitialSolution(graph0);

		BitSet expected = new BitSet(graph0.getNumVertices());
		expected.set(0);

		boolean isClique = graph0.isClique(result);

		assertEquals(expected, result);
		assertTrue(isClique);
	}

	@Test
	void initialClique_withNonEmptyGraph_returnsCorrectClique() {
		BreakoutLocalSearch breakoutLocalSearch = new BreakoutLocalSearch(0);
		BitSet result = breakoutLocalSearch.generateInitialSolution(graph1);

		boolean isClique = graph1.isClique(result);

		assertTrue(isClique);
	}

	@Test
	void initialClique_withFullGraph_returnsCorrectClique() {
		BreakoutLocalSearch breakoutLocalSearch = new BreakoutLocalSearch(0);
		BitSet result = breakoutLocalSearch.generateInitialSolution(graph2);

		boolean isClique = graph2.isClique(result);

		assertTrue(isClique);
	}
}