package com.izzette.mctc.itec2545.final_project;

import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.TableModel;

import com.izzette.mctc.itec2545.final_project.CARuleData;
import javax.swing.*;

/** Form for loading and deleting saved rules. */
public class CARuleManagerForm extends JDialog {
	private static final long serialVersionUID = 1;

	private JPanel mainPanel;
	private JButton loadButton;
	private JButton cancelButton;
	private JButton deleteButton;
	private JTable ruleTable;

	private RuleDataModel ruleDataModel;

	private boolean loadSelected = false;
	private CARuleData.RuleParams ruleParamsToLoad = null;

	/** Create and display the form.
	 * @param owner The fram launching this modal dialog.
	 */
	CARuleManagerForm(Frame owner) {
		super(owner, true);

		setContentPane(this.mainPanel);
		setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);

		setupUIComponents();

		pack();
		setVisible(true);
	}

	/** Shows whether or not the user clicked "Load" to dispose.
	 * @return true if the user clicked "Load"; otherwise false.
	 */
	boolean wasLoadSelected() {
		return loadSelected;
	}

	/** Get the rule selected to load.
	 * @return The rule params to load; or null if (false == wasLoadSelected()).
	 */
	CARuleData.RuleParams getRuleParamsToLoad() {
		return ruleParamsToLoad;
	}

	private void setupUIComponents() {
		CARuleData.RuleParams[] allRuleParams = getAllRules();

		ruleDataModel = new RuleDataModel(allRuleParams);
		ruleTable.setModel(ruleDataModel);
		ruleTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

		loadButton.addActionListener(new LoadButtonActionListener());
		deleteButton.addActionListener(new DeleteButtonActionListener());
		cancelButton.addActionListener(new CancelButtonActionListener());
	}

	private CARuleData.RuleParams[] getAllRules() {
		try {
			return CARuleData.getInstance().getAllRules();
		} catch (SQLException e) {
			System.err.printf("Failed to obtain rule list: %s\n", e.getMessage());
			e.printStackTrace();
			System.exit(1);
			return null;
		}
	}

	private void displayErrorMessage(String title, String message) {
		JOptionPane.showMessageDialog(
				this, message, title, JOptionPane.ERROR_MESSAGE);
	}

	private class RuleDataModel implements TableModel {
		private List<TableModelListener> listeners = new LinkedList<>();
		private CARuleData.RuleParams[] allRuleParams;

		private RuleDataModel(CARuleData.RuleParams[] allRuleParams) {
			update(allRuleParams);
		}

		private void update(CARuleData.RuleParams[] allRuleParams) {
			this.allRuleParams = allRuleParams;

			TableModelEvent ev = new TableModelEvent(this);

			for (TableModelListener listener : listeners)
				listener.tableChanged(ev);
		}

		public CARuleData.RuleParams getRuleAt(int row) {
			return allRuleParams[row];
		}

		@Override
		public int getRowCount() {
			return allRuleParams.length;
		}

		@Override
		public int getColumnCount() {
//			return 4;
			return 3;
		}

		@Override
		public String getColumnName(int col) {
			switch (col) {
				case 0:
					return "Neighbourhood Radius (r)";
				case 1:
					return "Number of Colors (k)";
				case 2:
					return "Rule Number (rule)";
//				case 3:
//					return "Comment";
				default:
					throw new IndexOutOfBoundsException();
			}
		}

		@Override
		public Class<?> getColumnClass(int col) {
			switch (col) {
				case 0:
				case 1:
					return Integer.class;
				case 2:
//				case 3:
					return String.class;
				default:
					throw new IndexOutOfBoundsException();
			}
		}

		@Override
		public boolean isCellEditable(int row, int col) {
			return false;
		}

		@Override
		public Object getValueAt(int row, int col) {
			CARuleData.RuleParams ruleParams = allRuleParams[row];

			switch (col) {
				case 0:
					return ruleParams.r;
				case 1:
					return ruleParams.k;
				case 2:
					return ruleParams.rule;
//				case 3:
//					return ruleParams.comment;
				default:
					throw new IndexOutOfBoundsException();
			}
		}

		@Override
		public void setValueAt(Object o, int row, int col) {
			throw new UnsupportedOperationException();
		}

		@Override
		public void addTableModelListener(TableModelListener listener) {
			listeners.add(listener);
		}

		@Override
		public void removeTableModelListener(TableModelListener listener) {
			listeners.remove(listener);
		}
	}

	private class LoadButtonActionListener implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent ev) {
			int selectedRow = ruleTable.getSelectedRow();
			if (0 > selectedRow) {
				displayErrorMessage("Load Error", "No rule selected.");
				return;
			}

			ruleParamsToLoad = ruleDataModel.getRuleAt(selectedRow);
			loadSelected = true;
			dispose();
		}
	}

	private class DeleteButtonActionListener implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent ev) {
			int selectedRow = ruleTable.getSelectedRow();
			if (0 > selectedRow) {
				displayErrorMessage("Delete Error", "No rule selected.");
				return;
			}

			CARuleData.RuleParams ruleSelected = ruleDataModel.getRuleAt(selectedRow);

			try {
				CARuleData.getInstance().deleteRule(ruleSelected);
			} catch (SQLException e) {
				System.err.printf("Failed to delete rule: %s\n", e.getMessage());
				e.printStackTrace();
				System.exit(1);
				return;
			}

			ruleDataModel.update(getAllRules());
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
