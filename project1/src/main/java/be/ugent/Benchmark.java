package be.ugent;

import be.ugent.graphs.BasicGraph;

import java.util.BitSet;
import java.util.concurrent.*;

public class Benchmark {


    public static void main(String[] args) {
        String[] files = {
                "DIMACS_subset_ascii/C125.9.clq",
                // "DIMACS_subset_ascii/C500.9.clq",
                "DIMACS_subset_ascii/keller4.clq"
        };

        MaximumCliqueAlgorithm[] algorithms = {
                new OstergardBranchAndBound(),
                new BranchAndBound(),
                //new BreakoutLocalSearch(),
                new AMTS(5, 1000)
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
            System.out.println("\n\n Now doing: " + alg.toString());
            for (String fileName : fileNames) {
                long startTime = System.currentTimeMillis(); // Record start time
                Future<BitSet> future = executor.submit(() -> {
                    return processFile(alg, fileName);
                });

                try {
                    BitSet maxClique = future.get(30, TimeUnit.MINUTES);
                    long endTime = System.currentTimeMillis(); // Record end time
                    System.out.println("Processed file " + fileName + ", size: " + maxClique.cardinality() + ", time: " + (endTime - startTime)+" ms");
                } catch (TimeoutException e) {
                    System.err.println("Processing of file " + fileName + " timed out. Moving to the next file.");
                    future.cancel(true); // Cancel the task
                } catch (InterruptedException | ExecutionException e) {
                    e.printStackTrace(); // Handle other exceptions
                }
            }
        }
        executor.shutdown();
    }
}
