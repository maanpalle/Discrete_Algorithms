package graphs;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.List;

public class ColouredGraph extends BasicGraph {

    private final ArrayList<BitSet> colours;

    public ColouredGraph(String graphFilename) {
        super(graphFilename);
        colours = new ArrayList<>();
    }

    public void assignColours() {
        BitSet isColoured = new BitSet(numVertices);
        List<Integer> order = orderByDegree();
        while (isColoured.cardinality() != numVertices) {
            BitSet newColour = new BitSet(numVertices);
            for (Integer id : order) {
                if (!isColoured.get(id) && !adjacencyList[id].intersects(newColour)) {
                    newColour.set(id);
                }
            }
            isColoured.or(newColour);
            colours.add(newColour);
        }
    }

    public List<Integer> orderByColour() {

        if (colours.isEmpty()) {
            assignColours();
        }

        // made with chatGPT
        // Create a list to store the indices of set bits
        List<Integer> indicesInOrder = new ArrayList<>();

        // Iterate through each BitSet in the list
        for (BitSet colour : colours) {
            // Iterate through set bits in the current BitSet
            int nextSetBitIndex = 0;
            while ((nextSetBitIndex = colour.nextSetBit(nextSetBitIndex)) != -1) {
                indicesInOrder.add(nextSetBitIndex);
                nextSetBitIndex++; // Move to the next index to continue the search
            }
        }
        return indicesInOrder;
    }

    public static void main(String[] args) {
        ColouredGraph graph = new ColouredGraph("DIMACS_subset_ascii/C125.9.clq");
        System.out.println(graph.orderByColour());
    }
}
