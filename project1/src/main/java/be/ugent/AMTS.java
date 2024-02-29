package be.ugent;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Random;

import be.ugent.graphs.BasicGraph;

public class AMTS implements MaximumCliqueAlgorithm {

    // Density of the graph
    private int rho;

    // Size of the tabuAdd list
    private int Tu;
    // Size of the tabuRemove list
    private int Tv;

    // Maximum allowed consecutive iterations without improving fitness function
    private int tabuSearchDepth;
    // Maximum allowed iterations
    private int maxIter;
    // current iteration
    private int iter;

    // Current k of k-clique
    private int k;

    // List of Ids of nodes with highest cardinalities relative to our candidate 
    // solution
    private ArrayList<Integer> maxIds;
    // List of Ids of nodes with lowest cardinalities relative to our candidate
    // solution
    private ArrayList<Integer> minIds;
    // Tabu list, elements in this list cannot be added again
    private Queue<Integer> tabuAdd;
    // Tabu list, elements in this list cannot be removed again
    private Queue<Integer> tabuRemove;

    // Cardinalities of graph relative to candidate solution
    private int[] d;
    // Frequencies of each node being added or removed from the candidate solution
    private int[] frequencyMemory;

    /**
     * Create a new AMTS instance
     * @param rho Densitity of the graph
     * @param maxIterations the maximum allowed iterations the algorithms can run
     */
    public AMTS(int rho, int maxIterations) {
        this.rho = rho;
        this.maxIter = maxIterations;
    }

    /**
     * Calculate the maximum clique through the adaptive Multi tabu search algorithm
     * It calls the adaptiveMultitabuSearchAlgorithm for increasing k's
     * @param graph 
     * @return The clique as a BitSet
     */
    public BitSet calculateMaxClique(BasicGraph graph) {
        BitSet prevSol;
        BitSet sol = new BitSet();

        this.k = 1;
        this.frequencyMemory = new int[graph.getNumNodes()];
        do {
            this.tabuSearchDepth = graph.getNumNodes() * this.k;
            prevSol = sol;
            sol = adaptiveMultiTabuSearch(graph);
            this.k += 1;

        } while (sol != null && k < graph.getNumNodes());
        return prevSol;
    }

    /**
     * Find a k clique based on the current k of the AMTS class
     * @param graph
     * @return a k-clique as bitset, null if it cannot be found
     */
    public BitSet adaptiveMultiTabuSearch(BasicGraph graph) {
        ArrayList<Integer> sol = generateInitial(graph);
        initializeD(graph, sol);
        iter = 0;

        while (iter < maxIter) {
            sol = tabuSearch(graph, sol);
            if (sol != null) {
                return convertArrayListToBitSet(sol);
            } else {
                System.out.println("resetting search space");
                sol = frequencyBasedInitialization(graph);
                initializeD(graph, sol);
            }
        }
        return null;
    }

    public ArrayList<Integer> tabuSearch(BasicGraph graph, ArrayList<Integer> sol) {
        int I = 0;
        tabuAdd = new LinkedList<Integer>();
        tabuRemove = new LinkedList<Integer>();

        int fs = evalutionFunction(graph, sol);
        int bestFs = 0;

        while (I < tabuSearchDepth) {
            generateTabus(graph, sol, k);
            double l = k * (k - 1.0) / 2.0 - fs;
            double prob = Math.min((l + 2) / graph.getNumNodes(), 0.1);
            Random rand = new Random();
            if (rand.nextDouble(1.0) < prob) {
                randomSwap(graph, sol);
            } else {
                generateNeighborhood(graph, sol);
                selectSwap(graph, sol);
            }

            iter += 1;
            BitSet solBitSet = convertArrayListToBitSet(sol);
            if (graph.isClique(solBitSet)) {
                System.out.println(k + "-clique: " + solBitSet);
                return sol;
            }

            fs = evalutionFunction(graph, sol);
            if (fs <= bestFs) {
                I++;
            } else {
                bestFs = fs;
                I = 0;
            }
        }
        return null;
    }

    public void randomSwap(BasicGraph graph, ArrayList<Integer> candidate) {
        int[] cardinalitiesFullGraph = new int[graph.getNumNodes()];

        for (int id1 : candidate) {
            BitSet adjec = graph.getAdjacencyBitSet(id1);
            for (int id2 = 0; id2 < graph.getNumNodes(); id2++) {
                if (candidate.contains(id2) || id1 == id2)
                    continue;
                if (adjec.get(id2)) {
                    cardinalitiesFullGraph[id2]++;
                }
            }
        }

        Random rand = new Random();
        int toSwap = candidate.get(rand.nextInt(k));
        int node;

        while (true) {
            node = rand.nextInt(graph.getNumNodes());
            if (!tabuAdd.contains(node) && cardinalitiesFullGraph[node] < (k * rho) && !candidate.contains(node)) {
                swap(graph, toSwap, node, candidate);
                break;
            }
        }
    }

    public void initializeD(BasicGraph graph, ArrayList<Integer> candidate) {
        this.d = new int[graph.getNumNodes()];
        // Get the cardinalities for each node in the graph relative to the candidate
        // solution
        for (int id1 : candidate) {
            BitSet adjec = graph.getAdjacencyBitSet(id1);
            for (int id2 = 0; id2 < graph.getNumNodes(); id2++) {
                if (id1 == id2)
                    continue;
                if (adjec.get(id2)) {
                    d[id2]++;
                }
            }
        }
    }

    public ArrayList<Integer> frequencyBasedInitialization(BasicGraph graph) {
        ArrayList<Integer> candidate = new ArrayList<>();

        for (int i = 0; i < k; i++) {
            int min = Integer.MAX_VALUE;
            int minId = -1;
            for (int j = 0; j < frequencyMemory.length; j++) {
                if (!candidate.contains(j) && frequencyMemory[j] < min) {
                    minId = j;
                    min = frequencyMemory[j];
                    frequencyMemory[j] += 1;
                }
            }
            candidate.add(minId);
        }
        Boolean toReset = true;

        for (int i = 0; i < frequencyMemory.length; i++) {
            if (frequencyMemory[i] < k) {
                toReset = false;
            }
        }
        if (toReset) {
            frequencyMemory = new int[graph.getNumNodes()];
        }
        return candidate;

    }

    public ArrayList<Integer> generateInitial(BasicGraph graph) {
        ArrayList<Integer> nodes = new ArrayList<>();
        for (int i = 0; i < graph.getNumNodes(); i++) {
            nodes.add(i);
        }
        ArrayList<Integer> list = new ArrayList<Integer>(nodes);
        Collections.shuffle(list);
        List<Integer> subset = list.subList(0, k);

        ArrayList<Integer> candidate = new ArrayList<>();

        candidate.addAll(subset);
        return candidate;
    }

    public int evalutionFunction(BasicGraph graph, ArrayList<Integer> candidate) {
        int fs = 0;
        for (int id1 : candidate) {
            for (int id2: candidate) {
                if (graph.getAdjacencyBitSet(id1).get(id2)) {
                    fs += 1;
                }
            }
        }
        return (int) Math.round(0.5 * fs);
    }

    public void generateTabus(BasicGraph graph, ArrayList<Integer> candidate, int k) {
        int l1 = k * (k - 1) / 2 - evalutionFunction(graph, candidate);
        int l = Math.min(l1, 10);
        int C = Math.max((int) Math.floor(k / 40.0), 6);

        Random rand = new Random();
        this.Tu = l + rand.nextInt(C);
        this.Tv = (int) Math.round(0.6 * l + rand.nextInt((int) Math.round(0.6 * C)));
    }

    public void selectSwap(BasicGraph graph, ArrayList<Integer> candidate) {
        for (int u : minIds) {
            for (int v : maxIds) {
                if (!graph.getAdjacencyBitSet(u).get(v)) {
                    swap(graph, u, v, candidate);
                    return;
                }
            }
        }
        Random rand = new Random();
        int u = minIds.get(rand.nextInt(0, minIds.size()));
        int v = maxIds.get(rand.nextInt(0, maxIds.size()));
        swap(graph, u, v, candidate);
    }

    public void swap(BasicGraph graph, int u, int v, ArrayList<Integer> candidate) {
        candidate.remove(Integer.valueOf(u));
        candidate.add(v);

        BitSet adjec = graph.getAdjacencyBitSet(u);
        for (int i = 0; i < graph.getNumNodes(); i++) {
            if (adjec.get(i)) {
                d[i] -= 1;
            }
        }
        adjec = graph.getAdjacencyBitSet(v);
        for (int i = 0; i < graph.getNumNodes(); i++) {
            if (adjec.get(i)) {
                d[i] += 1;
            }
        }

        if (tabuAdd.size() > this.Tu)
            tabuAdd.poll();
        if (tabuRemove.size() > this.Tv)
            tabuRemove.poll();
        tabuAdd.add(u);
        tabuRemove.add(v);
        frequencyMemory[u] += 1;
        frequencyMemory[v] += 1;
        // System.out.println(u + ", " + v + ", " + this.Tu + ", " + this.Tv);
    }

    /**
     * Generate the possible neighborhood for the current solution
     * 
     * @param graph
     * @param candidate
     * @param tabu
     * @return
     */
    public void generateNeighborhood(BasicGraph graph, ArrayList<Integer> candidate) {
        // Get the minimum and maximum cardinalities and add the to max/min-Ids
        int min = Integer.MAX_VALUE;
        int max = Integer.MIN_VALUE;
        maxIds = new ArrayList<Integer>();
        minIds = new ArrayList<Integer>();
        for (int node = 0; node < d.length; node++) {
            if (candidate.contains(node)) { continue; }
            int cardinality = d[node];
            if (cardinality > max && !tabuAdd.contains(node)) {
                max = cardinality;
                maxIds = new ArrayList<>();
                maxIds.add(node);
            } else if (cardinality == max && !tabuAdd.contains(node)) {
                maxIds.add(node);
            }
        }
        for (int node : candidate) {
            int cardinality = d[node];
            if (cardinality < min && !tabuRemove.contains(node)) {
                min = cardinality;
                minIds = new ArrayList<>();
                minIds.add(node);
            } else if (cardinality == min && !tabuRemove.contains(node)) {
                minIds.add(node);
            }
        }
    }

    public BitSet convertArrayListToBitSet(ArrayList<Integer> input) {
        BitSet bitSet = new BitSet(input.size());
        for (int i : input) {
            bitSet.set(i);
        }
        return bitSet;
    }
    
    public ArrayList<Integer> convertBitSetToArrayList(BitSet input) {
        ArrayList<Integer> list = new ArrayList<Integer>();
        for (int i = 0; i < input.size(); i++) {
            if (input.get(i)) {
                list.add(i);
            }
        }
        return list;
    }

    public static void main(String[] args) {
        long start = System.currentTimeMillis();
        BasicGraph graph = new BasicGraph("DIMACS_subset_ascii/C125.9.clq");
        AMTS amts = new AMTS(5, 100000);
        BitSet maxClique = amts.calculateMaxClique(graph);
        System.out.println(maxClique);
        System.out.println(graph.isClique(maxClique));
        System.out.println(maxClique.cardinality());
        long end = System.currentTimeMillis();
        System.out.println(end - start);
    }
}
