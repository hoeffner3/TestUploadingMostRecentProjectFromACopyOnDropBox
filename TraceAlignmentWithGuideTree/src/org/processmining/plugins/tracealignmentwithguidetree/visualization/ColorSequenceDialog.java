package org.processmining.plugins.tracealignmentwithguidetree.visualization;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JColorChooser;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.WindowConstants;

import org.processmining.plugins.tracealignmentwithguidetree.datatypes.Sequence;
import org.processmining.plugins.tracealignmentwithguidetree.swingx.Utils;

@SuppressWarnings( { "serial", "unused" })
public class ColorSequenceDialog extends JDialog implements ActionListener {

	private JButton okButton, cancelButton;
	private final Sequence sequence;
	private JColorChooser colorChooser;
	private int index;

	public ColorSequenceDialog(AlignmentFrame owner, Sequence sequence) {
		super(JOptionPane.getFrameForComponent(owner), "Color Sequence");
		setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		getContentPane().setLayout(new BorderLayout());

		this.sequence = sequence;

		buildUI();

		setLocationRelativeTo(owner);
		pack();
	}

	private void buildUI() {
		JPanel subpanel = new JPanel();
		GridBagLayout gb = new GridBagLayout();

		subpanel.setLayout(gb);

		int row = 0;

		Utils.addToGridBag(subpanel, gb, new JLabel("Name: "), 0, row, 1, 1, 1, 1, GridBagConstraints.NONE,
				GridBagConstraints.EAST);
		Utils.addToGridBag(subpanel, gb, new JLabel(sequence.getName()), 1, row, 1, 1, 1, 1, GridBagConstraints.NONE,
				GridBagConstraints.WEST);
		row++;

		colorChooser = new JColorChooser();
		colorChooser.setColor(sequence.getSequenceNameColor() != null ? sequence.getSequenceNameColor() : Color.black);
		Utils.addToGridBag(subpanel, gb, colorChooser, 0, row, 2, 1, 1, 1, GridBagConstraints.BOTH,
				GridBagConstraints.CENTER);
		row++;
		getContentPane().add(subpanel, BorderLayout.CENTER);

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
		if (e.getSource() == okButton) {
			Color c = colorChooser.getColor();
			sequence.setSequenceNameColor(c);
			dispose();
		} else if (e.getSource() == cancelButton) {
			dispose();
		}
	}

}
