package be.ugent.util;

import java.util.*;

public class BLSTabuList {
	private Map<BLSMove, Integer> tabuList;
	private int phi; // Coefficient as per your description
	private Random random;


	public BLSTabuList(int phi) {
		this.tabuList = new HashMap<>();
		this.phi = phi;
		this.random = new Random();
	}

	// Call this method when a vertex is removed from the clique
	public void add(BLSMove move, int currentIteration, int omSize) {
		int gamma = phi + random.nextInt(omSize + 1); // +1 because nextInt is exclusive on the upper bound
		tabuList.put(move, currentIteration + gamma);
	}

	// Check if a move is allowed
	public boolean isMoveAllowed(BLSMove move, int currentIteration) {
		Integer tabuUntil = tabuList.get(move);
		return tabuUntil == null || currentIteration > tabuUntil;
	}

}
