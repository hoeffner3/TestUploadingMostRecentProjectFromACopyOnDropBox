package org.processmining.plugins.tracealignmentwithguidetree.visualization;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;



import org.processmining.plugins.tracealignmentwithguidetree.datatypes.DisplayProperties;
import org.processmining.plugins.tracealignmentwithguidetree.datatypes.Sequence;
import org.processmining.plugins.tracealignmentwithguidetree.swingx.FixedWidthTextField;
import org.processmining.plugins.tracealignmentwithguidetree.swingx.JListX;
import org.processmining.plugins.tracealignmentwithguidetree.swingx.Utils;

@SuppressWarnings("serial")
public class EditSequenceNameDialog extends EditSeqAnnBaseDialog {

	private JButton okButton;

	public EditSequenceNameDialog(AlignmentFrame owner, DisplayProperties props, Sequence s) {
		super(owner, "Trace Details: " + s.getName(), props, s);
		rebuildGUI();

		pack();

		// but make sure width is at least 500 in case we get scroll...
		Dimension size = getSize();

		// limit size...
		if (size.height > 422) {
			size.height = 422;
		}
		if (size.width < 400) {
			size.width = 400;
		}
		setSize(size.width, size.height);
		setLocationRelativeTo(owner);
	}

	public void rebuildGUI() {
		getContentPane().removeAll();

		JPanel subpanel = new JPanel();
		GridBagLayout gb = new GridBagLayout();

		subpanel.setLayout(gb);

		int row = 0;

		sequenceTextField = new FixedWidthTextField(sequence.getName(), 40);

		row++;
		sequenceTextField = new FixedWidthTextField(sequence.getName(), 40);

		Utils.addToGridBag(subpanel, gb, new JLabel("Trace Name: "), 0, row, 1, 1, 1, 1, GridBagConstraints.BOTH,
				GridBagConstraints.EAST);
		Utils.addToGridBag(subpanel, gb, new JLabel(sequence.getName()), 1, row, 1, 1, 1, 1, GridBagConstraints.BOTH,
				GridBagConstraints.CENTER);
		row++;

		int noDuplicateTraces = sequence.getNoDuplicates();
		if (noDuplicateTraces > 0) {
			JListX duplicatePINameList = new JListX(sequence.getDuplicateTraceNameList().toArray());
			
			duplicatePINameList.setEnabled(false);
			Utils.addToGridBag(subpanel, gb, new JLabel(" "), 0, row, 1, 1, 1, 1, GridBagConstraints.BOTH,
					GridBagConstraints.EAST);
			Utils.addToGridBag(subpanel, gb, new JLabel(" "), 1, row, 1, 1, 1, 1, GridBagConstraints.BOTH,
					GridBagConstraints.CENTER);
			row++;
			Utils.addToGridBag(subpanel, gb, new JLabel("No. Identical Traces: "), 0, row, 1, 1, 1, 1,
					GridBagConstraints.BOTH, GridBagConstraints.EAST);
			Utils.addToGridBag(subpanel, gb, new JLabel(noDuplicateTraces + ""), 1, row, 1, 1, 1, 1,
					GridBagConstraints.BOTH, GridBagConstraints.CENTER);
			row++;
			Utils.addToGridBag(subpanel, gb, new JLabel(" "), 0, row, 1, 1, 1, 1, GridBagConstraints.BOTH,
					GridBagConstraints.EAST);
			Utils.addToGridBag(subpanel, gb, new JLabel(" "), 1, row, 1, 1, 1, 1, GridBagConstraints.BOTH,
					GridBagConstraints.CENTER);
			row++;
			JScrollPane duplicateTraceScrollPane = new JScrollPane(duplicatePINameList);

			Utils.addToGridBag(subpanel, gb, new JLabel("Identical Trace Name List"), 0, row, 1, 1, 1, 1,
					GridBagConstraints.BOTH, GridBagConstraints.EAST);
			Utils.addToGridBag(subpanel, gb, duplicateTraceScrollPane, 1, row, 1, 1, 1, 1, GridBagConstraints.BOTH,
					GridBagConstraints.CENTER);

			row++;
		}

		// TODO deleted props related stuff from original

		JScrollPane scroll = new JScrollPane(subpanel);
		getContentPane().add(scroll, BorderLayout.CENTER);

		subpanel = new JPanel();

		okButton = new JButton("OK");
		okButton.addActionListener(this);
		subpanel.add(okButton);

		getContentPane().add(subpanel, BorderLayout.SOUTH);

		// to force this to work we hide/show the button panel...
		subpanel.setVisible(false);
		subpanel.setVisible(true);
	}

	public void actionPerformed(ActionEvent e) {
		super.actionPerformed(e);

		if (e.getSource() == okButton) {
			try {
				String newSeqName = sequenceTextField.getText();
				sequence.setName(newSeqName);
			} catch (Exception exc) {
				// if we can't change it...oh well!
			}

			//TODO deleted some stuff
			//			owner.revalidateAndRepaintAll();

			dispose();
		} 
		return;
	}
}
