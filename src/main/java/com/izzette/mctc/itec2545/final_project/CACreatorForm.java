package com.izzette.mctc.itec2545.final_project;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.math.BigInteger;

import com.izzette.mctc.itec2545.final_project.CA;
import com.izzette.mctc.itec2545.final_project.CARule;
import javax.swing.*;

public class CACreatorForm extends JFrame {
	private static final long serialVersionUID = 1;

	private JPanel mainPanel;
	private JTextField kTextField;
	private JTextField rTextField;
	private JTextField cellsWidthTextField;
	private JTextField iterationsTextField;
	private JTextField ruleTextField;
	private JButton runButton;
	private JScrollPane caScollPane;

	private CARule rule;

	CACreatorForm(CARule rule) {
		this.rule = rule;

		setContentPane(mainPanel);
		setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

		setupUIComponenets();

		pack();
		setVisible(true);
	}

	CACreatorForm() {
		this(null);
	}

	private void setupUIComponenets() {
		runButton.addActionListener(new RunButtonActionListener());
	}

	private BigInteger getBigIntInput(
			JTextField textField, String name, String[] error) {
		BigInteger value;
		try {
			value = new BigInteger(textField.getText());
		} catch (NumberFormatException e) {
			error[0] = String.format("%s must be a valid integer.", name);
			return null;
		}

		return value;
	}

	private Integer getIntInput(
			JTextField textField, String name, String[] error) {
		BigInteger result = getBigIntInput(textField, name, error);
		if (null == result)
			return null;

		if (0 > BigInteger.valueOf(Integer.MAX_VALUE).compareTo(result)) {
			error[0] = String.format("%s must be less than or equal to %d.",
					name, Integer.MAX_VALUE);
			return null;
		}

		return result.intValue();
	}

	private Integer getKInput(String[] error) {
		return getIntInput(kTextField, "k", error);
	}

	private Integer getRInput(String[] error) {
		return getIntInput(rTextField, "r", error);
	}

	private BigInteger getRuleInput(String[] error) {
		return getBigIntInput(ruleTextField, "rule", error);
	}

	private Integer getCellsWidthInput(String[] error) {
		return getIntInput(cellsWidthTextField, "Cells Width", error);
	}

	private Integer getIterationsInput(String[] error) {
		return getIntInput(iterationsTextField, "Number of Iterations", error);
	}

	private void displayErrorMessage(String title, String message) {
		JOptionPane.showMessageDialog(
				this, message, title, JOptionPane.ERROR_MESSAGE);
	}

	private CAParams getParams() {
		String[] error = { null };

		Integer k, r;
		BigInteger ruleNumber;
		if (       null == (k          = getKInput(error))
				|| null == (r          = getRInput(error))
				|| null == (ruleNumber = getRuleInput(error))) {
			displayErrorMessage("Invalid Parameters", error[0]);
			return null;
		}

		return new CAParams(k, r, ruleNumber);
	}

	private CARunConfig getRunConfig() {
		final String errorTitle = "Invalid Run Configuration";
		String[] error = { null };

		Integer cellsWidth, iterations;
		if (       null == (cellsWidth = getCellsWidthInput(error))
				|| null == (iterations = getIterationsInput(error))) {
			displayErrorMessage(errorTitle, error[0]);
			return null;
		}

		if (0 > iterations) {
			final String message =
				"Number of Iterations must be positive.";
			displayErrorMessage(errorTitle, message);
			return null;
		}
		if (1 > cellsWidth) {
			final String message =
				"Cells Width must be equal to or greater than 1.";
			displayErrorMessage(errorTitle, message);
			return null;
		}

		return new CARunConfig(cellsWidth, iterations);
	}

	// TODO: implement serializable.
	private static class CAParams {
		public final int k, r;
		public final BigInteger ruleNumber;

		public CAParams(int k, int r, BigInteger ruleNumber) {
			this.k = k;
			this.r = r;
			this.ruleNumber = ruleNumber;
		}
	}

	private static class CARunConfig {
		public final int cellsWidth, iterations;

		public CARunConfig(int cellsWidth, int iterations) {
			this.cellsWidth = cellsWidth;
			this.iterations = iterations;
		}
	}

	private class RunButtonActionListener implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent ev) {
			CAParams params;
			CARunConfig runConfig;

			if (null == (params = getParams()) || null == (runConfig = getRunConfig()))
				return;

			try {
				rule = new CARule(params.r, params.k, params.ruleNumber);
			} catch (IllegalArgumentException e) {
				displayErrorMessage("Invalid Rule", e.getMessage());
				return;
			}

			int[] initialCells = new int[runConfig.cellsWidth];
			// TODO: allow selection of initial state.
			initialCells[runConfig.cellsWidth / 2] = 1;

			CA ca;
			try {
				ca = new CA(initialCells, rule);
			} catch (IllegalArgumentException e) {
				displayErrorMessage("Invalid Cellular Automata", e.getMessage());
				return;
			}

			// TODO: Draw to scroll pane.
			int[] out = new int[ca.size];
			displayRow(initialCells);
			for (int i = 0; runConfig.iterations > i; ++i) {
				ca.stepOnce(out);
				displayRow(out);
			}
		}

		@Deprecated
		private void displayRow(int[] cells) {
			for (int cell : cells) {
				char c;
				switch (cell) {
					case 0:
						c = ' ';
						break;
					case 1:
						c = '*';
						break;
					case 2:
						c = '%';
						break;
					case 3:
						c = '#';
						break;
					default:
						c = '@';
				}

				System.out.print(c);
			}

			System.out.println();
		}
	}
}

// vim: set ts=4 sw=4 noet syn=java:
