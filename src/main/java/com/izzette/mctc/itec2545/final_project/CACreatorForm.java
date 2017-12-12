package com.izzette.mctc.itec2545.final_project;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.math.BigInteger;
import java.sql.SQLException;
import java.util.Random;
import java.util.stream.IntStream;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import com.izzette.mctc.itec2545.final_project.CA;
import com.izzette.mctc.itec2545.final_project.CAColorPalletForm;
import com.izzette.mctc.itec2545.final_project.CARender;
import com.izzette.mctc.itec2545.final_project.CARule;
import com.izzette.mctc.itec2545.final_project.CARuleData;
import com.izzette.mctc.itec2545.final_project.CARuleManagerForm;
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
	private JButton manageRulesButton;
	private JButton saveRuleButton;
	private JRadioButton simpleRadioButton;
	private JRadioButton randomRadioButton;
	private JSlider zoomSlider;
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
		saveRuleButton.addActionListener(new SaveRuleButtonActionListener());
		manageRulesButton.addActionListener(new ManageRulesButtonActionListener());
		runButton.addActionListener(new RunButtonActionListener());
		selectColorsButton.addActionListener(new SelectColorsButtonActionListener());
		zoomSlider.addChangeListener(new ZoomSliderChangeListener());
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

	private void setRuleParams(CARuleData.RuleParams ruleParams) {
		kTextField.setText(Integer.toString(ruleParams.k));
		rTextField.setText(Integer.toString(ruleParams.r));
		ruleTextField.setText(ruleParams.rule);
		// TODO: comment field.
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

	private class SaveRuleButtonActionListener implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent ev) {
			CAParams params;

			if (null == (params = getParams()))
				return;

			CARuleData.RuleParams ruleParams = new CARuleData.RuleParams(
					params.r, params.k, params.ruleNumber.toString(), "");

			try {
				CARuleData.getInstance().storeRule(ruleParams);
			} catch (SQLException e) {
				System.err.printf("Failed to save a rule: %s\n", e.getMessage());
				e.printStackTrace();
				System.exit(1);
				return;
			}
		}
	}

	private class ManageRulesButtonActionListener implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent ev) {
			CARuleManagerForm ruleManagerForm =
				new CARuleManagerForm(CACreatorForm.this);

			if (ruleManagerForm.wasLoadSelected())
				setRuleParams(ruleManagerForm.getRuleParamsToLoad());
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

			int[] initialCells;
			if (simpleRadioButton.isSelected()) {
				initialCells = new int[runConfig.cellsWidth];
				initialCells[runConfig.cellsWidth / 2] = 1;
			} else {
				Random random = new Random();
				IntStream ints = random.ints(runConfig.cellsWidth, 0, params.k);
				initialCells = ints.toArray();
			}

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

	private class ZoomSliderChangeListener implements ChangeListener {
		@Override
		public void stateChanged(ChangeEvent ev) {
			caScollPane.setViewportView(caPanel);
		}
	}

	private class CAPanel extends JPanel implements Scrollable {
		private static final long serialVersionUID = 1;

		@Override
		public void paint(Graphics graphics) {
			if (null == caRender)
				return;

			int scale = zoomSlider.getValue();
			((Graphics2D)graphics).scale(scale, scale);
			caRender.drawOnto(graphics, 0, 0);
		}

		@Override
		public Dimension getPreferredSize() {
			int scale = zoomSlider.getValue();
			return new Dimension(scale * caRender.size, scale * caRender.numberOfRows);
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
