package com.izzette.mctc.itec2545.final_project;

import java.math.BigInteger;

class CARule {
	final int neighbourhoodDistance;
	final int neighbourhoodSize;
	final int colors;
	final int[] ruleArray;

	private static int getRuleSize(int neighbourhoodSize, int colors) {
		int ruleSize = 1;
		for (int i = 0; neighbourhoodSize > i; ++i)
			ruleSize *= colors;

		return ruleSize;
	}

	CARule(int neighbourhoodDistance, int colors, BigInteger ruleNumber) {
		this.neighbourhoodDistance = neighbourhoodDistance;
		this.neighbourhoodSize = neighbourhoodDistance * 2 + 1;
		this.colors = colors;

		this.ruleArray = new int[getRuleSize(this.neighbourhoodSize, colors)];
		BigInteger colorsBig = BigInteger.valueOf(colors);
		for (int i = 0; this.ruleArray.length > i; ++i) {
			BigInteger[] results = ruleNumber.divideAndRemainder(colorsBig);
			ruleNumber = results[0];
			this.ruleArray[i] = results[1].intValue();
		}
	}

	int applyRule(int index, int[] cells) {
		int neighbourhood = computeNeighbourhood(index, cells);
		return ruleArray[neighbourhood];
	}

	private int computeNeighbourhood(int index, int[] cells) {
		int neighbourhood = 0;
		int currentIndex = wrapIndex(index - neighbourhoodDistance, cells);

		for (int i = 0; neighbourhoodSize > i; ++i) {
			neighbourhood *= colors;
			neighbourhood += cells[currentIndex];
			currentIndex = wrapIndex(currentIndex + 1, cells);
		}

		return neighbourhood;
	}

	private int wrapIndex(int index, int[] cells) {
		// Java doesn't handle modulus on negative numbers "as expected".
		return (cells.length + index) % cells.length;
	}
}

// vim: set ts=4 sw=4 noet syn=java:
