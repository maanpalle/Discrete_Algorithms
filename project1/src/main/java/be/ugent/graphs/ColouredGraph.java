package be.ugent.graphs;

import java.util.ArrayList;
import java.util.BitSet;

public class ColouredGraph extends BasicGraph {

    private final ArrayList<Integer> colourOrderMapping;


    public ColouredGraph(BasicGraph graph) {
        super(graph);
        this.colourOrderMapping = new ArrayList<>();
    }

    public void assignColours() {
        BitSet isColoured = new BitSet(this.numVertices); // to check if whole graph is coloured

        while (isColoured.cardinality() != this.numVertices) {
            BitSet newColour = new BitSet(this.numVertices); // make new colour
            for (Integer id : orderByDegree()) {
                if (!isColoured.get(id) && !this.adjacencyList[id].intersects(newColour)) {
                    // add to colour if the id isn't coloured yet and no neighbour already has this colour
                    newColour.set(id);
                    this.colourOrderMapping.add(0, id); // add at the start
                }
            }
            isColoured.or(newColour);
        }
    }

    public ColouredGraph orderByColour() {
        /*
        Change ids of the vertices so that they are order according to the colouring heuristic of Ostergard
         */

        if (this.colourOrderMapping.isEmpty()) {
            assignColours();
        }

        BitSet[] newAdjacencyList = new BitSet[this.numVertices];
        for (int index = 0; index < this.numVertices; index++) { // initialize new empty lists
            newAdjacencyList[index] = new BitSet(this.numVertices);
        }

        for (int newVertex1 = 0; newVertex1 < this.numVertices; newVertex1++) {
            int oldVertex1 = this.colourOrderMapping.get(newVertex1);

            for (int newVertex2 = 0; newVertex2 < this.numVertices; newVertex2++) { // fill in the graph but with the changed vertices
                int oldVertex2 = this.colourOrderMapping.get(newVertex2);
                newAdjacencyList[newVertex2].set(newVertex1, this.adjacencyList[oldVertex2].get(oldVertex1));
            }
        }
        this.adjacencyList = newAdjacencyList;

        return this;
    }

    public BitSet getOriginalVertexIds(BitSet clique) {
        /*
        Returns a clique where the ids of the original ordering are used
         */
        BitSet originalClique = new BitSet(this.numVertices);
        int nextSetBitIndex = 0;
        while ((nextSetBitIndex = clique.nextSetBit(nextSetBitIndex)) != -1) {
            int originalId = this.colourOrderMapping.get(nextSetBitIndex);
            originalClique.set(originalId);
            nextSetBitIndex++;
        }
        return originalClique;
    }

}
