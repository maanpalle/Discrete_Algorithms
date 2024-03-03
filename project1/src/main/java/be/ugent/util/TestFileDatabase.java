package be.ugent.util;

import java.util.AbstractMap;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.function.Consumer;

public class TestFileDatabase implements Iterable<Map.Entry<String, Integer>> {

    private Map<String, Integer> testFiles;

    public TestFileDatabase() {
        this.testFiles = new HashMap<>(
                Map.ofEntries(
                        new AbstractMap.SimpleEntry<>("DIMACS_subset_ascii/brock200_1.clq", 21),
                        new AbstractMap.SimpleEntry<>("DIMACS_subset_ascii/brock200_2.clq", 12),
                        new AbstractMap.SimpleEntry<>("DIMACS_subset_ascii/brock200_3.clq", 15),
                        new AbstractMap.SimpleEntry<>("DIMACS_subset_ascii/brock200_4.clq", 17),
                        new AbstractMap.SimpleEntry<>("DIMACS_subset_ascii/brock400_1.clq", 27),
                        new AbstractMap.SimpleEntry<>("DIMACS_subset_ascii/brock400_2.clq", 29),
                        new AbstractMap.SimpleEntry<>("DIMACS_subset_ascii/brock400_3.clq", 31),
                        new AbstractMap.SimpleEntry<>("DIMACS_subset_ascii/brock400_4.clq", 33),
                        new AbstractMap.SimpleEntry<>("DIMACS_subset_ascii/brock800_1.clq", 23),
                        new AbstractMap.SimpleEntry<>("DIMACS_subset_ascii/brock800_2.clq", 24),
                        new AbstractMap.SimpleEntry<>("DIMACS_subset_ascii/brock800_3.clq", 25),
                        new AbstractMap.SimpleEntry<>("DIMACS_subset_ascii/brock800_4.clq", 26),
                        new AbstractMap.SimpleEntry<>("DIMACS_subset_ascii/gen200_p0.9_44.clq", 44),
                        new AbstractMap.SimpleEntry<>("DIMACS_subset_ascii/gen200_p0.9_55.clq", 55),
                        new AbstractMap.SimpleEntry<>("DIMACS_subset_ascii/gen400_p0.9_55.clq", 55),
                        new AbstractMap.SimpleEntry<>("DIMACS_subset_ascii/gen400_p0.9_65.clq", 65),
                        new AbstractMap.SimpleEntry<>("DIMACS_subset_ascii/gen400_p0.9_75.clq", 75),
                        new AbstractMap.SimpleEntry<>("DIMACS_subset_ascii/hamming6-2.clq", 32),
                        new AbstractMap.SimpleEntry<>("DIMACS_subset_ascii/hamming6-4.clq", 4),
                        new AbstractMap.SimpleEntry<>("DIMACS_subset_ascii/hamming8-2.clq", 128),
                        new AbstractMap.SimpleEntry<>("DIMACS_subset_ascii/hamming8-4.clq", 16),
                        new AbstractMap.SimpleEntry<>("DIMACS_subset_ascii/hamming10-2.clq", 512),
                        new AbstractMap.SimpleEntry<>("DIMACS_subset_ascii/hamming10-4.clq", 40),
                        new AbstractMap.SimpleEntry<>("DIMACS_subset_ascii/MANN_a27.clq", 126),
                        new AbstractMap.SimpleEntry<>("DIMACS_subset_ascii/MANN_a45.clq", 345),
                        new AbstractMap.SimpleEntry<>("DIMACS_subset_ascii/MANN_a81.clq", 1100),
                        new AbstractMap.SimpleEntry<>("DIMACS_subset_ascii/keller4.clq", 11),
                        new AbstractMap.SimpleEntry<>("DIMACS_subset_ascii/keller5.clq", 27),
                        new AbstractMap.SimpleEntry<>("DIMACS_subset_ascii/keller6.clq", 59),
                        new AbstractMap.SimpleEntry<>("DIMACS_subset_ascii/johnson8-2-4.clq", 4),
                        new AbstractMap.SimpleEntry<>("DIMACS_subset_ascii/johnson8-4-4.clq", 14),
                        new AbstractMap.SimpleEntry<>("DIMACS_subset_ascii/johnson16-2-4.clq", 8),
                        new AbstractMap.SimpleEntry<>("DIMACS_subset_ascii/johnson32-2-4.clq", 16),
                        new AbstractMap.SimpleEntry<>("DIMACS_subset_ascii/p_hat300-1.clq", 8),
                        new AbstractMap.SimpleEntry<>("DIMACS_subset_ascii/p_hat300-2.clq", 25),
                        new AbstractMap.SimpleEntry<>("DIMACS_subset_ascii/p_hat300-3.clq", 36),
                        new AbstractMap.SimpleEntry<>("DIMACS_subset_ascii/C125.9.clq", 34),
                        new AbstractMap.SimpleEntry<>("DIMACS_subset_ascii/C250.9.clq", 44),
                        new AbstractMap.SimpleEntry<>("DIMACS_subset_ascii/C500.9.clq", 57),
                        new AbstractMap.SimpleEntry<>("DIMACS_subset_ascii/DSJC500_5.clq", 13),
                        new AbstractMap.SimpleEntry<>("DIMACS_subset_ascii/p_hat700-1.clq", 11),
                        new AbstractMap.SimpleEntry<>("DIMACS_subset_ascii/p_hat1500-1.clq", 12)
                )
        );

    }

    public int getMaxClique(String name) {
        return testFiles.get(name);
    }

    public Map<String, Integer> getTestFiles() {
        return testFiles;
    }

    @Override
    public Iterator<Map.Entry<String, Integer>> iterator() {
        return this.testFiles.entrySet().iterator();
    }

    @Override
    public void forEach(Consumer<? super Map.Entry<String, Integer>> action) {
        this.testFiles.entrySet().forEach(action);
    }


}