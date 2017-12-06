package com.izzette.mctc.itec2545.final_project;

import java.math.BigInteger;

/** Cellular automata rule (k, r, and rule). */
class CARule {
	/** The neighbourhood radius (r). */
	final int radius;
	/** The neighbourhood size (2r + 1). */
	final int neighbourhoodSize;
	/** The number of colors cells can have (k). */
	final int colors;
	/** THe rule number (rule). */
	final BigInteger ruleNumber;

	private final int[] ruleArray;

	/** Create a new cellular automata rule.
	 * @param radius The neighbourhood radius (r).
	 * @param colors The number of colors cells can have (k).
	 * @param ruleNumber The rule number (rule).
	 * @throws IllegalArgumentException If the rule is invalid or too large.
	 */
	CARule(int radius, int colors, BigInteger ruleNumber) {
		if (1 > radius)
		   throw new IllegalArgumentException(
				   "Radius must be equal to or greater than 1.");
		if (2 > colors)
			throw new IllegalArgumentException(
					"Colors must be equal to or greater than 2.");
		if (1 <= BigInteger.valueOf(0).compareTo(ruleNumber))
			throw new IllegalArgumentException(
					"Rule number must be postitive.");

		this.radius = radius;
		this.neighbourhoodSize = radius * 2 + 1;
		this.colors = colors;
		// Save a reference to ruleNumber before changing it to produce
		// the rule array.
		this.ruleNumber = ruleNumber;

		int ruleSize;
		try {
			ruleSize = getRuleSize(this.neighbourhoodSize, colors);
		} catch (IllegalArgumentException e) {
			// Pass down the IllegalArgumentException for rule spaces too large.
			throw e;
		}

		this.ruleArray = new int[ruleSize];
		BigInteger colorsBig = BigInteger.valueOf(colors);
		for (int i = 0; this.ruleArray.length > i; ++i) {
			BigInteger[] results = ruleNumber.divideAndRemainder(colorsBig);
			ruleNumber = results[0];
			this.ruleArray[i] = results[1].intValue();
		}

		if (0 != BigInteger.valueOf(0).compareTo(ruleNumber))
			throw new IllegalArgumentException("Rule number larger than rule space.");
	}

	/** Obtain the next state for the cell at the specified index.
	 * @param index The cells index.
	 * @param cells The cells current state.
	 * @return The new state for the cell.
	 */
	int applyRule(int index, int[] cells) {
		int neighbourhood = computeNeighbourhood(index, cells);
		return ruleArray[neighbourhood];
	}

	private static int getRuleSize(int neighbourhoodSize, int colors) {
		int ruleSize = 1;
		for (int i = 0; neighbourhoodSize > i; ++i) {
			if (colors > Integer.MAX_VALUE / ruleSize)
				throw new IllegalArgumentException(
						"Rule space is larger than the maximum integer.");

			ruleSize *= colors;
		}

		return ruleSize;
	}

	private int computeNeighbourhood(int index, int[] cells) {
		int neighbourhood = 0;
		int currentIndex = wrapIndex(index - radius, cells);

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
