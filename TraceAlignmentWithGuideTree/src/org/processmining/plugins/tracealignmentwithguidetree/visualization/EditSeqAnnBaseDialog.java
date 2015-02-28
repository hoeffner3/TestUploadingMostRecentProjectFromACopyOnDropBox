package org.processmining.plugins.tracealignmentwithguidetree.visualization;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import javax.swing.WindowConstants;
import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;

import org.processmining.plugins.tracealignmentwithguidetree.datatypes.DisplayProperties;
import org.processmining.plugins.tracealignmentwithguidetree.datatypes.Sequence;

@SuppressWarnings("serial")
public abstract class EditSeqAnnBaseDialog extends JDialog implements ActionListener, CaretListener {
	
	protected AlignmentFrame owner;
	protected Sequence sequence;

	protected DisplayProperties displayProperties;
	protected JTextField sequenceTextField;

	public EditSeqAnnBaseDialog(AlignmentFrame owner, String title, DisplayProperties displayProperties,
			Sequence sequence) {
		super(JOptionPane.getFrameForComponent(owner), title);

		setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		getContentPane().setLayout(new BorderLayout());

		this.sequence = sequence;
		this.owner = owner;
		this.displayProperties = displayProperties;
	}

	protected abstract void rebuildGUI();

	public void actionPerformed(ActionEvent arg0) {
		// TODO Auto-generated method stub

	}

	public void caretUpdate(CaretEvent arg0) {
		// TODO Auto-generated method stub

	}

}
