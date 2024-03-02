package be.ugent;

import be.ugent.graphs.BasicGraph;

import java.util.BitSet;
import java.util.concurrent.*;

public class Benchmark {


    public static void main(String[] args) {
        String[] files = {
                "DIMACS_subset_ascii/C125.9.clq",
                "DIMACS_subset_ascii/keller4.clq"
        };

        MaximumCliqueAlgorithm[] algorithms = {
                new OstergardBranchAndBound(),
                new BranchAndBound(),
                //new BreakoutLocalSearch(),
                //new AMTS()
        };
        Benchmark benchmark = new Benchmark();
        benchmark.process(algorithms, files);
    }

    public BitSet processFile(MaximumCliqueAlgorithm algorithm, String fileName) {
        BasicGraph graph = new BasicGraph(fileName);
        return algorithm.calculateMaxClique(graph);
    }

    public void process(MaximumCliqueAlgorithm[] algorithms, String[] fileNames) {
        ExecutorService executor = Executors.newSingleThreadExecutor();

        for (MaximumCliqueAlgorithm alg : algorithms) {
            for (String fileName : fileNames) {
                long startTime = System.currentTimeMillis(); // Record start time
                Future<BitSet> future = executor.submit(() -> {
                    return processFile(alg, fileName);
                });

                try {
                    BitSet maxClique = future.get(30, TimeUnit.MINUTES);
                    long endTime = System.currentTimeMillis(); // Record end time
                    System.out.println("Processed file " + fileName + ", with: " + alg.toString());
                    System.out.println("Size of maximum clique: " + maxClique.cardinality());
                    System.out.println("Took: " + (endTime - startTime) + "ms");
                } catch (TimeoutException e) {
                    System.err.println("Processing of file " + fileName + " with: " + alg.toString() + " timed out. Moving to the next file.");
                    future.cancel(true); // Cancel the task
                } catch (InterruptedException | ExecutionException e) {
                    e.printStackTrace(); // Handle other exceptions
                }
            }
        }
        executor.shutdown();
    }
}
