package be.ugent.util;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.BitSet;

public class BLSMove {

	private static final Logger logger = LogManager.getLogger(BLSMove.class);
	private final BLSMoveType type;
	private final int primaryVertex;
	private final BitSet removedVertices;

	public BLSMove(BLSMoveType type, int vertex1, BitSet removedVertices) {
		this.type = type;
		this.primaryVertex = vertex1;
		this.removedVertices = removedVertices;
	}

	@Override
	public String toString() {
		if (type == BLSMoveType.NOOP) {
			return "NOOP";
		}
		if (type == BLSMoveType.REMOVAL) {
			return "REM(" + primaryVertex + ")";
		}
		if (type == BLSMoveType.SWAP) {
			return "SWP("+ primaryVertex + ", " + removedVertices + ")";
		}
		if (type == BLSMoveType.ADDITION) {
			return "ADD(" + primaryVertex + ")";
		}
		return "UNKNOWN";
	}

	public static BLSMove NOOP() {
		return new BLSMove(BLSMoveType.NOOP, -1, new BitSet());
	}

	public boolean isNoop() {
		return type == BLSMoveType.NOOP;
	}

	public boolean hasRemovedVertices() {
		return removedVertices.cardinality() > 0;
	}

	public BLSMoveType getType() {
		return type;
	}

	public int getPrimaryVertex() {
		return primaryVertex;
	}

	public BitSet getRemovedVertices() {
		return removedVertices;
	}

	public int value() {
		if (type == BLSMoveType.REMOVAL) {
			return -1;
		}
		if (type == BLSMoveType.SWAP) {
			return 1 -removedVertices.cardinality();
		}
		if (type == BLSMoveType.ADDITION) {
			return 1;
		}
		return 0;
	}

	public BitSet apply(BitSet clique) {
		if (isNoop()) {
			return clique;
		}
		BitSet newClique = (BitSet) clique.clone();

		if (type == BLSMoveType.REMOVAL) {
			if (!clique.get(primaryVertex)) {
				logger.error("Trying to remove a vertex that is not in the clique");
				logger.error("Clique: {}, Vertex: {}", clique, primaryVertex);
			}
			newClique.clear(primaryVertex);
			return newClique;
		}
		if (type == BLSMoveType.SWAP) {
			int vertex = removedVertices.nextSetBit(0);
			while (vertex != -1) {
				if (!newClique.get(vertex)) {
					logger.error("Trying to remove a vertex that is not in the clique");
					logger.error("Clique: {}, Vertex: {}", clique, vertex);
				}
				newClique.clear(vertex);
				vertex = removedVertices.nextSetBit(vertex + 1);
			}
		}
		newClique.set(primaryVertex);
		return newClique;
	}
}