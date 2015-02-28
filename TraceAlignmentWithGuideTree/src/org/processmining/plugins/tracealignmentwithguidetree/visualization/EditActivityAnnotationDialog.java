package org.processmining.plugins.tracealignmentwithguidetree.visualization;

import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;

import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.WindowConstants;

import org.processmining.plugins.tracealignmentwithguidetree.datatypes.DisplayProperties;
import org.processmining.plugins.tracealignmentwithguidetree.datatypes.Sequence;
import org.processmining.plugins.tracealignmentwithguidetree.swingx.Utils;

import com.fluxicon.slickerbox.factory.SlickerFactory;

@SuppressWarnings("serial")
public class EditActivityAnnotationDialog extends JDialog{

	AlignmentFrame owner;
	private final Sequence sequence;
	DisplayProperties displayProperties;
	int index;
	
	public EditActivityAnnotationDialog(AlignmentFrame owner, DisplayProperties displayProperties, Sequence sequence,
			int index) {
		super(JOptionPane.getFrameForComponent(owner), "Edit Activity Annotation: " + sequence.getName()
				+ " for alignment position " + index);
		setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);

		this.sequence = sequence;
		this.index = index;
		this.owner = owner;
		this.displayProperties = displayProperties;

		init();
		setSize(400, 300);
		pack();
		setLocationRelativeTo(this.owner);
	}
	
	private void init(){
		JPanel subpanel = SlickerFactory.instance().createRoundedPanel();
		GridBagLayout gb = new GridBagLayout();

		subpanel.setLayout(gb);

		int row = 0;
		String encodedActivity = sequence.getEncodedActivity(index);

		Utils.addToGridBag(subpanel, gb, SlickerFactory.instance().createLabel("Encoded Activity: "), 0, row, 1, 1, 1, 1, GridBagConstraints.BOTH,
				GridBagConstraints.EAST);
		Utils.addToGridBag(subpanel, gb, SlickerFactory.instance().createLabel(encodedActivity), 1, row, 1, 1, 1, 1, GridBagConstraints.BOTH,
				GridBagConstraints.CENTER);
		//		Utils.addToGridBag(subpanel, gb, new JLabel(sequence.getEncodedActivity(index)), 1, row, 1, 1, 1, 1,
		//				GridBagConstraints.BOTH, GridBagConstraints.CENTER);
		row++;

		Utils.addToGridBag(subpanel, gb, SlickerFactory.instance().createLabel(" "), 0, row, 1, 1, 1, 1, GridBagConstraints.BOTH,
				GridBagConstraints.EAST);
		row++;

		String decodedActivity = "null";
		if (owner.getCharActivityMap().containsKey(encodedActivity)) {
			decodedActivity = owner.getCharActivityMap().get(encodedActivity);
		} else {
			JOptionPane.showMessageDialog(owner, "Encoded Activity " + encodedActivity + " not found in charActivityMap");
		}
		Utils.addToGridBag(subpanel, gb, SlickerFactory.instance().createLabel("Actual Activity: "), 0, row, 1, 1, 1, 1, GridBagConstraints.BOTH,
				GridBagConstraints.EAST);
		Utils.addToGridBag(subpanel, gb, SlickerFactory.instance().createLabel(decodedActivity), 1, row, 1, 1, 1, 1, GridBagConstraints.BOTH,
				GridBagConstraints.CENTER);
		row++;

		Utils.addToGridBag(subpanel, gb, SlickerFactory.instance().createLabel(" "), 0, row, 1, 1, 1, 1, GridBagConstraints.BOTH,
				GridBagConstraints.EAST);
		row++;

		Utils.addToGridBag(subpanel, gb, SlickerFactory.instance().createLabel("Alignment Index: "), 0, row, 1, 1, 1, 1, GridBagConstraints.BOTH,
				GridBagConstraints.EAST);
		Utils.addToGridBag(subpanel, gb, SlickerFactory.instance().createLabel((index + 1) + ""), 1, row, 1, 1, 1, 1, GridBagConstraints.BOTH,
				GridBagConstraints.CENTER);
		row++;

		Utils.addToGridBag(subpanel, gb, SlickerFactory.instance().createLabel(" "), 0, row, 1, 1, 1, 1, GridBagConstraints.BOTH,
				GridBagConstraints.EAST);
		row++;

		Utils.addToGridBag(subpanel, gb, SlickerFactory.instance().createLabel("Activity Index in Trace: "), 0, row, 1, 1, 1, 1,
				GridBagConstraints.BOTH, GridBagConstraints.EAST);
		Utils.addToGridBag(subpanel, gb, SlickerFactory.instance().createLabel(sequence.getActivityIndex(index) + ""), 1, row, 1, 1, 1, 1,
				GridBagConstraints.BOTH, GridBagConstraints.CENTER);
		row++;
		
		JScrollPane scroll = new JScrollPane(subpanel);
		getContentPane().add(scroll, BorderLayout.CENTER);
		subpanel.setVisible(false);
		subpanel.setVisible(true);
	}
}
