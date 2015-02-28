package org.processmining.plugins.tracealignmentwithguidetree.visualization;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

import org.processmining.plugins.tracealignmentwithguidetree.datatypes.ColumnSort;
import org.processmining.plugins.tracealignmentwithguidetree.datatypes.DisplayProperties;

import com.fluxicon.slickerbox.factory.SlickerFactory;

@SuppressWarnings("serial")
public class ColumnSortDialog extends JDialog implements ActionListener {

	ColumnSort retVal = null;

	AlignmentFrame owner;
	DisplayProperties displayProperties;
	int column;
	ColumnSort prevVal;

	// controls
	final JButton okButton;
	JButton cancelButton;
	final JTextField valueField;

	JComboBox optionComboBox;
	JComboBox priorityComboBox;

	// access through static method
	public static ColumnSort showDialog(AlignmentFrame owner,
			DisplayProperties props, int column, ColumnSort prevVal, Point location) {
		ColumnSortDialog dialog = new ColumnSortDialog(owner, props, column, prevVal, location);
		dialog.setLocationRelativeTo(owner);
		dialog.setVisible(true);

		// if we hit "ok" button this will be seutp...otherwise will be null
		return dialog.retVal;
	}

	// constructor
	protected ColumnSortDialog(AlignmentFrame owner, DisplayProperties displayProperties, int column,
			ColumnSort prevVal, Point location) {
		super(JOptionPane.getFrameForComponent(owner), "Column Sort Dialog");
		setModal(true);
		this.owner = owner;
		this.displayProperties = displayProperties;
		this.column = column;
		this.prevVal = prevVal;

		getContentPane().setLayout(new BorderLayout());

		JPanel subpanel = SlickerFactory.instance().createRoundedPanel();
		Dimension size = new Dimension(500, 120);
		subpanel.setPreferredSize(size);
		getContentPane().add(subpanel, BorderLayout.CENTER);

		JPanel midpanel = SlickerFactory.instance().createRoundedPanel();
		midpanel.setLayout(new BorderLayout());
		subpanel.add(midpanel, BorderLayout.CENTER);

		JPanel panel = SlickerFactory.instance().createRoundedPanel();
		midpanel.add(panel, BorderLayout.NORTH);

		panel.add(new JLabel("Sort sequences where Column: " + column));

		panel = SlickerFactory.instance().createRoundedPanel();
		midpanel.add(panel, BorderLayout.CENTER);

		optionComboBox = SlickerFactory.instance().createComboBox(ColumnSort.getDisplayOpNames());
		panel.add(optionComboBox);

		valueField = new JTextField("", 20);
		valueField.setFont(displayProperties.getFont());
		valueField.requestFocus();
//		valueField.requestDefaultFocus();

		panel.add(valueField);

		panel = SlickerFactory.instance().createRoundedPanel();
		midpanel.add(panel, BorderLayout.SOUTH);

		
		panel.add(SlickerFactory.instance().createLabel("Priority: "));
		String[] priorityNames = { "1", "2", "3", "4", "5", "6", "7", "8", "9", "10" };
		priorityComboBox = SlickerFactory.instance().createComboBox(priorityNames);
		panel.add(priorityComboBox);

		subpanel = SlickerFactory.instance().createRoundedPanel();
		okButton = SlickerFactory.instance().createButton("Ok");
		getRootPane().setDefaultButton(okButton);
		okButton.addActionListener(this);
		subpanel.add(okButton);
		cancelButton = SlickerFactory.instance().createButton("Cancel");
		cancelButton.addActionListener(this);
		subpanel.add(cancelButton);

		getContentPane().add(subpanel, BorderLayout.SOUTH);

		// setup defaults if provided...
		setOptions(prevVal);

		// if this location puts us off the end then move us over
		int overlap = (location.x + size.width) - owner.getWidth();
		if (overlap > 0) {
			location.translate(-overlap, 0);
		}

		Point ownerLoc = owner.getLocation();
		location.translate(ownerLoc.x, ownerLoc.y);
		setLocation(location);

		pack();

		valueField.requestFocus();
	}

	private void setOptions(ColumnSort columnSort) {
		if (columnSort == null) {
			return;
		}
		optionComboBox.setSelectedIndex(columnSort.getSelectedOption());
		valueField.setText(columnSort.getFilterActivitiesString());
		priorityComboBox.setSelectedIndex(columnSort.getPriority() - 1);
	}

	private boolean getOptions() {
		int selectedOption = optionComboBox.getSelectedIndex();
		String filterActivitiesString = valueField.getText();
		int priority = priorityComboBox.getSelectedIndex() + 1;

		if (!owner.hasAnyActivities(filterActivitiesString)) {
			JOptionPane.showMessageDialog(this,
					"You must spectify at least one activity. Activities should be of length "
							+ owner.getEncodingLength()
							+ ". If multiple activities are to be specified, use , as delimiter.", "Error",
					JOptionPane.ERROR_MESSAGE);
			return false;
		}

		ColumnSort columnSort = new ColumnSort(selectedOption, filterActivitiesString, priority);

		// this is what we return...
		retVal = columnSort;
		return true;
	}

	// ActionListener interface implementation
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == cancelButton) {
			dispose();
		} else if (e.getSource() == okButton) {
			if (getOptions()) {
				dispose();
			}
		}
	}
}
