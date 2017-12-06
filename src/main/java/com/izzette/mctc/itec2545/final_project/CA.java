package com.izzette.mctc.itec2545.final_project;

import com.izzette.mctc.itec2545.final_project.CARule;

/** Cellular automata (CARule and cells). */
class CA {
	/** The rule for the cellular automata. */
	final CARule rule;
	/** The size of the cells array. */
	final int size;

	private final int[][] cellArrays;
	private int cellsIndex = 0;

	/** Create a new cellular automata.
	 * @param initialCells The initial state for the cells.
	 * @param rule The cellular automata rule to apply.
	 * @throws IllegalArgumentException If the CA is invalid.
	 */
	CA(int[] initialCells, CARule rule) {
		if (initialCells.length < rule.neighbourhoodSize)
			throw new IllegalArgumentException(
					"Size must be at least the neighbourhood size.");

		this.size = initialCells.length;
		this.cellArrays = new int[2][this.size];
		for (int i = 0; this.size > i; ++i)
			this.cellArrays[cellsIndex][i] = initialCells[i];
		this.rule = rule;
	}

	/** Evolve the cellular automata by one step.
	 * @param out Buffer to copy the new cells state to.
	 */
	void stepOnce(int[] out) {
		int nextCellsIndex = getNextCellsIndex();
		for (int i = 0; cellArrays[cellsIndex].length > i; ++i) {
			int nextCellState = rule.applyRule(i, cellArrays[cellsIndex]);
			out[i] = nextCellState;
			cellArrays[nextCellsIndex][i] = nextCellState;
		}

		cellsIndex = nextCellsIndex;
	}

	/** Wrapper for stepOnce allowing multiple steps to be made at once.
	 * @param outArrays Buffers to copy the new cells state to.
	 */
	void stepMany(int[][] outArrays) {
		for (int i = 0; outArrays.length > i; ++i)
			stepOnce(outArrays[i]);
	}

	private int getNextCellsIndex() {
		return (cellsIndex + 1) % cellArrays.length;
	}
}

// vim: set ts=4 sw=4 noet syn=java:
