package com.izzette.mctc.itec2545.final_project;

import java.awt.Color;
import java.awt.Component;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.util.Random;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import javax.swing.*;

/** Form for selecting color pallet for cellular automata. */
public class CAColorPalletForm extends JDialog {
	private static final long serialVersionUID = 1;

	private JPanel mainPanel;
	private JList<ColorNumberLabel> colorNumberList;
	private JPanel colorSelectionPanel;
	private JButton okButton;
	private JButton cancelButton;
	private JColorChooser colorChooser = new JColorChooser();

	private boolean changesAccepted = false;

	/** Create a random color pallet numberOfColors in size preserving the current
	 *  colors.
	 * @param numberOfColors The size of the color pallet.
	 * @param currentColors The existing colors to preserve.
	 * @return A color pallet numberOfColors in size.
	 */
	static Color[] ensureColorPallet(int numberOfColors, Color[] currentColors) {
		Color[] colors = new Color[numberOfColors];

		if (numberOfColors >= currentColors.length) {
			System.arraycopy(currentColors, 0, colors, 0, currentColors.length);

			Random random = new Random();
			for (int i = currentColors.length; colors.length > i; ++i)
				colors[i] = new Color(0xffffff & random.nextInt());
		} else {
			System.arraycopy(currentColors, 0, colors, 0, colors.length);
		}

		return colors;
	}

	/** Create and display the form.
	 * @param owner The frame launching this modal dialog.
	 * @param colors The color pallet to start with.
	 * @throws IllegalArgumentException If colors.length is zero.
	 */
	CAColorPalletForm(Frame owner, Color[] colors) {
		super(owner, true);

		if (0 >= colors.length)
			throw new IllegalArgumentException();

		setContentPane(this.mainPanel);
		setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);

		setupUIComponents(colors);

		pack();
		setVisible(true);
	}

	/** Shows whether or not the user clicked "OK" to dispose.
	 * @return true if the user clicked "OK"; otherwise false.
	 */
	boolean areChangesAccepted() {
		return changesAccepted;
	}

	/** Get the selected colors.
	 * @return The colors the user selected whether or not they clicked "OK".
	 */
	Color[] getColors() {
		ListModel<ColorNumberLabel> colorNumberListModel = colorNumberList.getModel();

		Color[] colors = new Color[colorNumberListModel.getSize()];

		for (int i = 0; colorNumberListModel.getSize() > i; ++i) {
			ColorNumberLabel colorNumber = colorNumberListModel.getElementAt(i);
			colors[i] = colorNumber.getColor();
		}

		return colors;
	}

	private void setupUIComponents(Color[] colors) {
		DefaultListModel<ColorNumberLabel> colorNumberListModel =
			new DefaultListModel<>();
		ColorNumberRenderer colorNumberRenderer = new ColorNumberRenderer();

		colorNumberList.setSelectionBackground(Color.LIGHT_GRAY);
		colorNumberList.setModel(colorNumberListModel);
		colorNumberList.setCellRenderer(colorNumberRenderer);
		colorNumberList.getSelectionModel().addListSelectionListener(
				new ColorNumberSelectionListener());

		for (int i = 0; colors.length > i; ++i)
			colorNumberListModel.addElement(new ColorNumberLabel(i, colors[i]));

		colorSelectionPanel.setLayout(
				new BoxLayout(colorSelectionPanel, BoxLayout.PAGE_AXIS));

		colorSelectionPanel.add(colorChooser);
		colorChooser.getSelectionModel().addChangeListener(
				new ColorChooserChangeListener(colorChooser));

		colorNumberList.setSelectedIndex(0);

		okButton.addActionListener(new OKButtonActionListener());
		cancelButton.addActionListener(new CancelButtonActionListener());
	}

	private static class ColorNumberLabel extends JLabel {
		final int number;

		private static final long serialVersionUID = 1;

		private static int colorIconSize = 25;

		private Color color;

		ColorNumberLabel(int number, Color color) {
			super();

			this.number = number;

			setText(Integer.toString(number));
			setColor(color);

			setVisible(true);
		}

		Color getColor() {
			return color;
		}

		void setColor(Color color) {
			this.color = color;

			Image colorImage = new BufferedImage(
					colorIconSize, colorIconSize, BufferedImage.TYPE_INT_RGB);
			Graphics colorImageGraphics = colorImage.getGraphics();
			colorImageGraphics.setColor(color);
			colorImageGraphics.fillRect(0, 0, colorIconSize, colorIconSize);

			ImageIcon colorIcon = new ImageIcon(colorImage);

			setIcon(colorIcon);
		}
	}

	private static class ColorNumberRenderer extends DefaultListCellRenderer {
		private static final long serialVersionUID = 1;

		ColorNumberRenderer() {
			super();

			setHorizontalAlignment(SwingConstants.LEFT);
		}

		@Override
		public Component getListCellRendererComponent(
				JList<?> list, Object value, int index, boolean isSelected,
				boolean cellHasFocus) {
			setText(((JLabel)value).getText());
			setIcon(((JLabel)value).getIcon());

			if (isSelected)
				setBackground(list.getSelectionBackground());
			else
				setBackground(null);

			return this;
		}
	}

	private class ColorNumberSelectionListener implements ListSelectionListener {
		@Override
		public void valueChanged(ListSelectionEvent ev) {
			if (ev.getValueIsAdjusting() || colorNumberList.isSelectionEmpty())
				return;

			ColorNumberLabel colorNumber = colorNumberList.getSelectedValue();
			colorChooser.setColor(colorNumber.getColor());
		}
	}

	private class ColorChooserChangeListener implements ChangeListener {
		private final JColorChooser colorChooser;

		ColorChooserChangeListener(JColorChooser colorChooser) {
			this.colorChooser = colorChooser;
		}

		@Override
		public void stateChanged(ChangeEvent ev) {
			if (colorNumberList.isSelectionEmpty())
				return;

			int selectedIndex = colorNumberList.getSelectedIndex();
			ColorNumberLabel colorNumber = colorNumberList.getSelectedValue();
			colorNumber.setColor(colorChooser.getColor());
			colorNumberList.repaint();
		}
	}

	private class OKButtonActionListener implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent ev) {
			changesAccepted = true;

			dispose();
		}
	}

	private class CancelButtonActionListener implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent ev) {
			dispose();
		}
	}
}

// vim: set ts=4 sw=4 noet syn=java:
