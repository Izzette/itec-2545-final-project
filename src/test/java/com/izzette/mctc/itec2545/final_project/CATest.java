package com.izzette.mctc.itec2545.final_project;

import com.izzette.mctc.itec2545.final_project.CA;
import com.izzette.mctc.itec2545.final_project.CARule;

import java.util.Arrays;
import org.junit.Test;
import junit.framework.TestCase;

public class CATest {
	private static final int[] singleCellInitial = new int[101];
	private static final int[] singleCellTwoInitial = new int[101];

	static {
		singleCellInitial[50] = 1;
		singleCellTwoInitial[50] = 2;
	}

	@Test
	public void testNearestNeighbourTwoColor() {
		int[] out = new int[singleCellInitial.length];

		CARule rule30 = new CARule(1, 2, 30L);
		CA rule30CA = new CA(singleCellInitial, rule30);

		System.out.println("k=2, r=1 rule 30");
		printRow(singleCellInitial);
		for (int i = 0; 200 > i; ++i) {
			rule30CA.stepOnce(out);
			printRow(out);
		}
		System.out.println();

		TestCase.assertEquals("Rule 30 (200 step hash)",
				1455750616, Arrays.hashCode(out));

		CARule rule110 = new CARule(1, 2, 110L);
		CA rule110CA = new CA(singleCellInitial, rule110);

		System.out.println("k=2, r=1 rule 110");
		printRow(singleCellInitial);
		for (int i = 0; 200 > i; ++i) {
			rule110CA.stepOnce(out);
			printRow(out);
		}
		System.out.println();

		TestCase.assertEquals("Rule 110 (200 step hash)",
				-1729395764, Arrays.hashCode(out));
	}

	@Test
	public void testNearestNeighbourThreeColor() {
		int[] out = new int[singleCellInitial.length];

		CARule code2040 = new CARule(1, 3, 7284586781490L);
		CA code2040CA = new CA(singleCellTwoInitial, code2040);

		System.out.println("k=3, r=1 totalistic code 2040");
		printRow(singleCellTwoInitial);
		for (int i = 0; 200 > i; ++i) {
			code2040CA.stepOnce(out);
			printRow(out);
		}
		System.out.println();

		TestCase.assertEquals("Rule 7284586781490 (T. Code 2040, 200 step hash)",
				1046369691, Arrays.hashCode(out));
	}

	@Test
	public void testNextNearestNeighbourTwoColor() {
		int[] out = new int[singleCellInitial.length];

		CARule rule1436965290 = new CARule(2, 2, 1436965290L);
		CA rule1436965290CA = new CA(singleCellInitial, rule1436965290);

		System.out.println("k=2, r=2 rule 1436965290");
		printRow(singleCellInitial);
		for (int i = 0; 100 > i; ++i) {
			rule1436965290CA.stepOnce(out);
			printRow(out);
		}
		System.out.println();

		TestCase.assertEquals("Rule 1436965290 (Radius 2, 100 step hash)",
				-503049923, Arrays.hashCode(out));
	}

	private static void printRow(int[] row) {
		final char[] symbols = { ' ', '*', '#' };
		for (int cell : row)
			System.out.print(symbols[cell]);
		System.out.println();
	}
}

// vim: set ts=4 sw=4 noet syn=java:
