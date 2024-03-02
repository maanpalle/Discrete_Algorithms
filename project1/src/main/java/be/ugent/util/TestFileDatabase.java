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
						new AbstractMap.SimpleEntry<>("brock200_1", 21),
						new AbstractMap.SimpleEntry<>("brock200_2", 12),
						new AbstractMap.SimpleEntry<>("brock200_3", 15),
						new AbstractMap.SimpleEntry<>("brock200_4", 17),
						new AbstractMap.SimpleEntry<>("brock400_1", 27),
						new AbstractMap.SimpleEntry<>("brock400_2", 29),
						new AbstractMap.SimpleEntry<>("brock400_3", 31),
						new AbstractMap.SimpleEntry<>("brock400_4", 33),
						new AbstractMap.SimpleEntry<>("brock800_1", 23),
						new AbstractMap.SimpleEntry<>("brock800_2", 24),
						new AbstractMap.SimpleEntry<>("brock800_3", 25),
						new AbstractMap.SimpleEntry<>("brock800_4", 26),
						new AbstractMap.SimpleEntry<>("gen200_p0.9_44", 44),
						new AbstractMap.SimpleEntry<>("gen200_p0.9_55", 55),
						new AbstractMap.SimpleEntry<>("gen400_p0.9_55", 55),
						new AbstractMap.SimpleEntry<>("gen400_p0.9_65", 65),
						new AbstractMap.SimpleEntry<>("gen400_p0.9_75", 75),
						new AbstractMap.SimpleEntry<>("hamming6-2", 32),
						new AbstractMap.SimpleEntry<>("hamming6-4", 4),
						new AbstractMap.SimpleEntry<>("hamming8-2", 128),
						new AbstractMap.SimpleEntry<>("hamming8-4", 16),
						new AbstractMap.SimpleEntry<>("hamming10-2", 512),
						new AbstractMap.SimpleEntry<>("hamming10-4", 40),
						new AbstractMap.SimpleEntry<>("MANN_a27", 126),
						new AbstractMap.SimpleEntry<>("MANN_a45", 345),
						new AbstractMap.SimpleEntry<>("MANN_a81", 1100),
						new AbstractMap.SimpleEntry<>("keller4", 11),
						new AbstractMap.SimpleEntry<>("keller5", 27),
						new AbstractMap.SimpleEntry<>("keller6", 59),
						new AbstractMap.SimpleEntry<>("johnson8-2-4", 4),
						new AbstractMap.SimpleEntry<>("johnson8-4-4", 14),
						new AbstractMap.SimpleEntry<>("johnson16-2-4", 8),
						new AbstractMap.SimpleEntry<>("johnson32-2-4", 16),
						new AbstractMap.SimpleEntry<>("p_hat300-1", 8),
						new AbstractMap.SimpleEntry<>("p_hat300-2", 25),
						new AbstractMap.SimpleEntry<>("p_hat300-3", 36)
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