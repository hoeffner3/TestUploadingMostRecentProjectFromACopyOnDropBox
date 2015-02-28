package org.processmining.plugins.tracealignmentwithguidetree.visualization;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ScrollPaneConstants;
import javax.swing.WindowConstants;

import org.processmining.plugins.tracealignmentwithguidetree.datatypes.Alignment;
import org.processmining.plugins.tracealignmentwithguidetree.datatypes.Sequence;
import org.processmining.plugins.tracealignmentwithguidetree.swingx.FixedWidthTextField;
import org.processmining.plugins.tracealignmentwithguidetree.swingx.Utils;

@SuppressWarnings( { "serial" })
public class EditSequenceDialog extends JDialog implements ActionListener {
	JButton okButton, cancelButton;

	private FixedWidthTextField sequenceName, originalSequenceTextField, editableSequenceTextField;
	private final Sequence sequenceToEditOrCreate;
	AlignmentFrame owner = null;

	private final int mouseRowIndex;

	private String editableSeqStr;

	public EditSequenceDialog(AlignmentFrame owner, Sequence s) {
		super(JOptionPane.getFrameForComponent(owner), "Edit Sequence");
		this.owner = owner;
		setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		getContentPane().setLayout(new BorderLayout());

		sequenceToEditOrCreate = s;
		mouseRowIndex = -1;

		buildUI(false);

		setLocationRelativeTo(owner);
		pack();
		Dimension d = getSize();

		setSize(600, d.height);
	}

	/**
	 * Allows user to create and edit a NEW sequence
	 * 
	 * @param owner
	 * @param s
	 * @param a
	 * @param mouseRowIndex
	 */
	public EditSequenceDialog(AlignmentFrame owner, Sequence s, Alignment alignment, int mouseRowIndex) {
		super(JOptionPane.getFrameForComponent(owner), "Edit Trace");
		this.owner = owner;
		setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		getContentPane().setLayout(new BorderLayout());

		sequenceToEditOrCreate = s;
//		this.alignment = alignment;
		this.mouseRowIndex = mouseRowIndex + 1;

		buildUI(true);

		setLocationRelativeTo(owner);
		pack();
		Dimension d = getSize();

		setSize(600, d.height);
	}

	private void buildUI(boolean isNewSequence) {
		String dash = "-";
		for (int i = 1; i < owner.getEncodingLength(); i++) {
			dash += "-";
		}

		String nameForSeq;
		// are we creating a new sequence?
		if (isNewSequence) {
			nameForSeq = "";
		} else {
			// we are editing an existing sequence
			nameForSeq = sequenceToEditOrCreate.getName();
		}

		JPanel subpanel = new JPanel();
		GridBagLayout gb = new GridBagLayout();

		subpanel.setLayout(gb);

		int row = 0;

		Utils.addToGridBag(subpanel, gb, new JLabel("Name:"), 0, row, 1, 1, 1, 1, GridBagConstraints.NONE,
				GridBagConstraints.EAST);

		sequenceName = new FixedWidthTextField(sequenceToEditOrCreate != null ? nameForSeq : "",
				sequenceToEditOrCreate != null ? Math.max(sequenceToEditOrCreate.getName().length(), 25) : 25);

		Utils.addToGridBag(subpanel, gb, sequenceName, 1, row, 1, 1, 1, 1, GridBagConstraints.NONE,
				GridBagConstraints.WEST);
		row++;

		String originalSeqStr;
		// The sequence should not equal null unless something weird happens
		if (sequenceToEditOrCreate != null) {
			Utils.addToGridBag(subpanel, gb, new JLabel("Original:"), 0, row, 1, 1, 1, 1, GridBagConstraints.NONE,
					GridBagConstraints.EAST);
			originalSeqStr = sequenceToEditOrCreate.toString();// This will not
			// contain dummy
			// residue

			originalSequenceTextField = new FixedWidthTextField(originalSeqStr, originalSeqStr.length());
			originalSequenceTextField.setEditable(false);
			Utils.addToGridBag(subpanel, gb, originalSequenceTextField, 1, row, 1, 1, 1, 1, GridBagConstraints.NONE,
					GridBagConstraints.WEST);
			row++;

			// This condition should be true when we set
			// mouseRowIndex to -1 for editing an EXISTING SEQUENCE
			if (mouseRowIndex == -1) {
				// editableSeqStr = new StringBuffer(originalSeqStr);//no dummy
				editableSeqStr = new String(originalSeqStr);
			} else {
				editableSeqStr = new String("");
				// TODO ideally, the originalSeqStr.length() should be divided
				// by encodingLength to get the sequence length; however since
				// this is just str should be ok; check later; Also, ideally the
				// concat should have been based on encodingLength
				for (int i = 0; i < originalSeqStr.length(); i++) {
					editableSeqStr = editableSeqStr.concat("-");// .append('-');
				}
			}
		}
		// if for some reason the sequence was null
		else {
			originalSeqStr = "";
			originalSequenceTextField = null;
		}

		Utils.addToGridBag(subpanel, gb, new JLabel("Trace:"), 0, row, 1, 1, 1, 1, GridBagConstraints.NONE,
				GridBagConstraints.EAST);

		editableSequenceTextField = new FixedWidthTextField(editableSeqStr, originalSeqStr.length());

		Utils.addToGridBag(subpanel, gb, editableSequenceTextField, 1, row, 1, 1, 1, 1, GridBagConstraints.NONE,
				GridBagConstraints.WEST);
		row++;

		Utils.addClipboardBindings(sequenceName);
		Utils.addClipboardBindings(originalSequenceTextField);
		Utils.addClipboardBindings(editableSequenceTextField);

		JScrollPane scrollPane = new JScrollPane(subpanel, ScrollPaneConstants.VERTICAL_SCROLLBAR_NEVER,
				ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);

		scrollPane.getHorizontalScrollBar().setUnitIncrement(editableSequenceTextField.getCharWidth());

		getContentPane().add(scrollPane, BorderLayout.CENTER);

		subpanel = new JPanel();
		okButton = new JButton("OK");
		okButton.addActionListener(this);
		subpanel.add(okButton);
		cancelButton = new JButton("Cancel");
		cancelButton.addActionListener(this);
		subpanel.add(cancelButton);
		getContentPane().add(subpanel, BorderLayout.SOUTH);

	}

	public void actionPerformed(ActionEvent e) {
		int encodingLength = owner.getEncodingLength();
		if (e.getSource() == okButton) {
			//TODO modified from original
			String seqName = sequenceName.getText();
			if (seqName.length() < 1) {
				JOptionPane.showMessageDialog(this, "Trace must have a name.", "Error Message", JOptionPane.ERROR_MESSAGE);
				return;
			}
			String seqToSaveStr = editableSequenceTextField.getText();
			seqToSaveStr = seqToSaveStr.replaceAll(" ", "-");
			int modifiedSeqLength = seqToSaveStr.length() / encodingLength;

			if (modifiedSeqLength != sequenceToEditOrCreate.getLengthWithGaps()) {
				JOptionPane.showMessageDialog(this, "Sequence length should be the same", "Error Message", JOptionPane.ERROR_MESSAGE);
				return;
			}
			String[] modifiedSequence = new String[modifiedSeqLength];
			for (int i = 0; i < modifiedSeqLength; i++) {
				modifiedSequence[i] = seqToSaveStr.substring(i * encodingLength, (i + 1)
						* encodingLength);
			}

			//Check if all the symbols are proper or not
			for (int i = 0; i < modifiedSeqLength; i++) {
				if (!owner.isValidEncodedActivity(modifiedSequence[i])) {
					JOptionPane.showMessageDialog(this, "Unknown encoded activity found : " + modifiedSequence[i], "Error Message", JOptionPane.ERROR_MESSAGE);
					return;
				}
			}
			if (mouseRowIndex == -1) {
				try {
					sequenceToEditOrCreate.setName(seqName);
					sequenceToEditOrCreate.setActivity(modifiedSequence);
				} catch (Exception exp) {
					JOptionPane.showMessageDialog(this, "Unable to edit " + "sequence."+exp.getLocalizedMessage(),"Error Message", JOptionPane.ERROR_MESSAGE);
					return;
				}
			}
			dispose();

		} else if (e.getSource() == cancelButton) {
			dispose();
		}

	}

}
