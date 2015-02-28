package org.processmining.plugins.tracealignmentwithguidetree.ui;

import javax.swing.ButtonGroup;
import javax.swing.JLabel;
import javax.swing.JRadioButton;

import org.processmining.plugins.guidetreeminer.swingx.ScrollableGridLayout;
import org.processmining.plugins.tracealignmentwithguidetree.enumtypes.AlignmentAlgorithm;
import org.processmining.plugins.tracealignmentwithguidetree.settings.TraceAlignmentWithGuideTreeSettingsListener;

import com.fluxicon.slickerbox.factory.SlickerFactory;

@SuppressWarnings("serial")
public class AlignmentAlgorithmConfigurationStep extends myStep {
	TraceAlignmentWithGuideTreeSettingsListener listener;
	
	JRadioButton profileAlignmentRadioButton;
	JRadioButton partialOrderAlignmentRadioButton;
	
	public AlignmentAlgorithmConfigurationStep(){
		initComponents();
	}
	
	private void initComponents(){
		final ScrollableGridLayout alignmentAlgorithmConfigurationLayout = new ScrollableGridLayout(this, 1, 3, 0, 0);
		alignmentAlgorithmConfigurationLayout.setRowFixed(0, true);
		alignmentAlgorithmConfigurationLayout.setRowFixed(1, true);
		alignmentAlgorithmConfigurationLayout.setRowFixed(2, true);
		
		this.setLayout(alignmentAlgorithmConfigurationLayout);
		
		JLabel headerLabel = SlickerFactory.instance().createLabel("<html><h1>Alignment Algorithm Configuration Step</h1>");
		alignmentAlgorithmConfigurationLayout.setPosition(headerLabel, 0, 0);
		add(headerLabel);
		
		profileAlignmentRadioButton = SlickerFactory.instance().createRadioButton("Profile Alignment");
		partialOrderAlignmentRadioButton = SlickerFactory.instance().createRadioButton("Partial-Order Alignment");
		ButtonGroup alignmentAlgorithmButtonGroup = new ButtonGroup();
		alignmentAlgorithmButtonGroup.add(profileAlignmentRadioButton);
		alignmentAlgorithmButtonGroup.add(partialOrderAlignmentRadioButton);
		
		profileAlignmentRadioButton.setSelected(true);
		partialOrderAlignmentRadioButton.setEnabled(false);
		
		alignmentAlgorithmConfigurationLayout.setPosition(profileAlignmentRadioButton, 0, 1);
		add(profileAlignmentRadioButton);
		
		alignmentAlgorithmConfigurationLayout.setPosition(partialOrderAlignmentRadioButton, 0, 2);
		add(partialOrderAlignmentRadioButton);
	}
	
	public boolean precondition() {
		return true;
	}

	public void setListener(TraceAlignmentWithGuideTreeSettingsListener listener) {
		this.listener = listener;
	}
	
	public void readSettings() {
		if(profileAlignmentRadioButton.isSelected())
			listener.setAlignmentAlgorithm(AlignmentAlgorithm.ProfileAlignment);
		else if(partialOrderAlignmentRadioButton.isSelected())
			listener.setAlignmentAlgorithm(AlignmentAlgorithm.PartialOrderAlignment);
	}

}
