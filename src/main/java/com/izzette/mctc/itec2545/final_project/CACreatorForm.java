package com.izzette.mctc.itec2545.final_project;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.math.BigInteger;

import javax.swing.*;

/** Form for creating and viewing a cellular automata. */
public class CACreatorForm extends JFrame {
	private static final long serialVersionUID = 1;

	private JPanel mainPanel;
	private JTextField kTextField;
	private JTextField rTextField;
	private JTextField cellsWidthTextField;
	private JTextField iterationsTextField;
	private JTextField ruleTextField;
	private JButton runButton;
	private JButton selectColorsButton;
	private JScrollPane caScollPane;
	private JPanel caPanel;

	private CARender caRender = null;

	private Color[] colorPallet = { Color.WHITE };

	/** Create and display the form. */
	CACreatorForm() {
		this.caPanel = new CAPanel();

		setContentPane(this.mainPanel);
		setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

		setupUIComponenets();

		pack();
		setVisible(true);
	}

	private void setupUIComponenets() {
		runButton.addActionListener(new RunButtonActionListener());
		selectColorsButton.addActionListener(new SelectColorsButtonActionListener());
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
	/** Cellular automata rule parameters structure. */
	public static class CAParams {
		/** The number of colors (k), and neighbourhood radius (r). */
		public final int k, r;
		/** The rule number (rule). */
		public final BigInteger ruleNumber;

		/** Create a new set of cellular automata rule parameters.
		 * @param k The number of colors (k).
		 * @param r The neighbourhood radius (r).
		 * @param ruleNumber The rule number (rule).
		 */
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

			CARule rule;
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

			colorPallet = CAColorPalletForm.ensureColorPallet(params.k, colorPallet);
			caRender = new CARender(ca.size, runConfig.iterations + 1, colorPallet);

			int[] out = new int[ca.size];
			caRender.drawRow(initialCells, 0);
			for (int i = 1; runConfig.iterations >= i; ++i) {
				ca.stepOnce(out);
				caRender.drawRow(out, i);
			}

			caScollPane.setViewportView(caPanel);
		}
	}

	private class SelectColorsButtonActionListener implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent ev) {
			String errorTitle = "Invalid Number of Colors";
			String[] error = new String[1];

			Integer k;
			if (null == (k = getKInput(error))) {
				displayErrorMessage(errorTitle, error[0]);
				return;
			}

			if (0 >= k) {
				displayErrorMessage(
						errorTitle, "k must be greater than zero");
				return;
			}

			colorPallet = CAColorPalletForm.ensureColorPallet(k, colorPallet);
			CAColorPalletForm colorPalletForm = new CAColorPalletForm (
					CACreatorForm.this, colorPallet);

			if (colorPalletForm.areChangesAccepted())
				colorPallet = colorPalletForm.getColors();
		}
	}

	private class CAPanel extends JPanel implements Scrollable {
		private static final long serialVersionUID = 1;

		@Override
		public void paint(Graphics graphics) {
			if (null != caRender)
				caRender.drawOnto(graphics, 0, 0);
		}

		@Override
		public Dimension getPreferredSize() {
			return new Dimension(caRender.size, caRender.numberOfRows);
		}

		@Override
		public Dimension getPreferredScrollableViewportSize() {
			return getPreferredSize();
		}

		@Override
		public int getScrollableBlockIncrement(
				Rectangle visibleRect, int orientation, int direction) {
			return 1;
		}

		@Override
		public int getScrollableUnitIncrement(
				Rectangle visibleRect, int orientation, int direction) {
			int position, visibleLength, length;
			if (SwingConstants.VERTICAL == orientation) {
				position = visibleRect.y;
				visibleLength = visibleRect.height;
				length = getPreferredScrollableViewportSize().height;
			} else {
				position = visibleRect.x;
				visibleLength = visibleRect.width;
				length = getPreferredScrollableViewportSize().width;
			}

			int remaining;
			if (direction > 0)
				remaining = length - (position + visibleLength);
			else
				remaining = position;

			return (remaining < visibleLength ? remaining : visibleLength);
		}


		@Override
		public boolean getScrollableTracksViewportWidth() {
			return false;
		}

		@Override
		public boolean getScrollableTracksViewportHeight() {
			return false;
		}
	}
}

// vim: set ts=4 sw=4 noet syn=java:
