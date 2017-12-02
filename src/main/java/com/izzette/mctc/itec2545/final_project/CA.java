package com.izzette.mctc.itec2545.final_project;

import com.izzette.mctc.itec2545.final_project.CARule;

class CA {
	final CARule rule;

	private final int[][] cellArrays;
	private int cellsIndex = 0;

	CA(int[] initialCells, CARule rule) {
		if (initialCells.length < rule.neighbourhoodSize)
			throw new IllegalArgumentException();

		this.cellArrays = new int[2][initialCells.length];
		for (int i = 0; initialCells.length > i; ++i)
			this.cellArrays[cellsIndex][i] = initialCells[i];
		this.rule = rule;
	}

	void stepOnce(int[] out) {
		int nextCellsIndex = getNextCellsIndex();
		for (int i = 0; cellArrays[cellsIndex].length > i; ++i)
			cellArrays[nextCellsIndex][i] = rule.applyRule(i, cellArrays[cellsIndex]);

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
