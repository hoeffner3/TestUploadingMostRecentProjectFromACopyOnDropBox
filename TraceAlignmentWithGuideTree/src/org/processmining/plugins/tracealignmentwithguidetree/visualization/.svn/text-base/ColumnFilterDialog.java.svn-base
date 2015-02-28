package org.processmining.plugins.tracealignmentwithguidetree.visualization;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

import org.processmining.plugins.tracealignmentwithguidetree.datatypes.ColumnFilter;
import org.processmining.plugins.tracealignmentwithguidetree.datatypes.DisplayProperties;

import com.fluxicon.slickerbox.factory.SlickerFactory;

@SuppressWarnings("serial")
public class ColumnFilterDialog extends JDialog implements ActionListener{
	
	ColumnFilter retVal = null;

	AlignmentFrame owner;
	DisplayProperties displayProperties;
	int column;
	ColumnFilter prevVal;

	// controls
	final JButton okButton;
	JButton cancelButton;
	final JTextField valueField;
	JComboBox optionComboBox;

	public static ColumnFilter showDialog(AlignmentFrame owner,
			DisplayProperties props, int column, ColumnFilter prevVal, Point location) {
		ColumnFilterDialog dialog = new ColumnFilterDialog(owner, props, column, prevVal, location);
		dialog.setLocationRelativeTo(owner);
		dialog.setVisible(true);

		// if we hit "ok" button this will be seutp...otherwise will be null
		return dialog.retVal;
	}
	
	protected ColumnFilterDialog(AlignmentFrame owner, DisplayProperties props, int column, ColumnFilter prevVal,
			Point location) {
		//TODO changed the following line
		//		super(owner, "Column Filter Dialog");
		super();

		setModal(true);
		this.owner = owner;
		displayProperties = props;
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

		panel.add(SlickerFactory.instance().createLabel("Show sequences where Column: " + column));

		panel = SlickerFactory.instance().createRoundedPanel();
		midpanel.add(panel, BorderLayout.SOUTH);

		optionComboBox = SlickerFactory.instance().createComboBox(ColumnFilter.getDisplayOpNames());
		panel.add(optionComboBox);

		valueField = new JTextField("", 20);
		valueField.setFont(props.getFont());
		valueField.requestFocus();
//		valueField.requestDefaultFocus();

		panel.add(valueField);

		subpanel = SlickerFactory.instance().createRoundedPanel();
		okButton = SlickerFactory.instance().createButton("Ok");
		getRootPane().setDefaultButton(okButton);
		okButton.addActionListener(this);
		subpanel.add(okButton);
		
		cancelButton = SlickerFactory.instance().createButton("Cancel");
		cancelButton.addActionListener(this);
		subpanel.add(cancelButton);

		getContentPane().add(subpanel, BorderLayout.SOUTH);

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

	private void setOptions(ColumnFilter val) {
		if (val == null) {
			return;
		}
		optionComboBox.setSelectedIndex(val.getSelectedOption());
		valueField.setText(val.getFilterActivitiesString());
	}

	private boolean getOptions() {
		int selectedOption = optionComboBox.getSelectedIndex();
		String filterActivitiesString = valueField.getText();

		if (!owner.hasAnyActivities(filterActivitiesString)) {
			JOptionPane.showMessageDialog(this,
					"You must spectify at least one activity. Activities should be of length "
							+ owner.getEncodingLength()
							+ ". If multiple activities are to be specified, use , as delimiter", "Error",
					JOptionPane.ERROR_MESSAGE);
			return false;
		}

		ColumnFilter opt = new ColumnFilter(selectedOption, filterActivitiesString);

		// this is what we return...
		retVal = opt;
		return true;
	}

	//ActionListener interface
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
