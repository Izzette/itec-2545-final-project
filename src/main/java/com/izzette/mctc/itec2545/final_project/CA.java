package com.izzette.mctc.itec2545.final_project;

import com.izzette.mctc.itec2545.final_project.CARule;

class CA {
	final CARule rule;
	final int size;

	private final int[][] cellArrays;
	private int cellsIndex = 0;

	CA(int[] initialCells, CARule rule) {
		if (initialCells.length < rule.neighbourhoodSize)
			throw new IllegalArgumentException();

		this.size = initialCells.length;
		this.cellArrays = new int[2][this.size];
		for (int i = 0; this.size > i; ++i)
			this.cellArrays[cellsIndex][i] = initialCells[i];
		this.rule = rule;
	}

	void stepOnce(int[] out) {
		int nextCellsIndex = getNextCellsIndex();
		for (int i = 0; cellArrays[cellsIndex].length > i; ++i) {
			int nextCellState = rule.applyRule(i, cellArrays[cellsIndex]);
			out[i] = nextCellState;
			cellArrays[nextCellsIndex][i] = nextCellState;
		}

		cellsIndex = nextCellsIndex;
	}

	void stepMany(int[][] outArrays) {
		for (int i = 0; outArrays.length > i; ++i)
			stepOnce(outArrays[i]);
	}

	private int getNextCellsIndex() {
		return (cellsIndex + 1) % cellArrays.length;
	}
}

// vim: set ts=4 sw=4 noet syn=java:
