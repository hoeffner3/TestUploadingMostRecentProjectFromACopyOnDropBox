package org.processmining.plugins.tracealignmentwithguidetree.ui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;

import org.processmining.plugins.guidetreeminer.swingx.ScrollableGridLayout;
import org.processmining.plugins.tracealignmentwithguidetree.enumtypes.RealignmentStrategy;
import org.processmining.plugins.tracealignmentwithguidetree.settings.TraceAlignmentWithGuideTreeSettingsListener;

import com.fluxicon.slickerbox.factory.SlickerFactory;

@SuppressWarnings("serial")
public class RealignmentConfigurationStep extends myStep {
	TraceAlignmentWithGuideTreeSettingsListener listener;
	
	boolean isPerformRealignment;
	JRadioButton blockShiftRealignmentRadioButton;
	JRadioButton concurrencyFilteredRealignmentRadioButton;
	RealignmentStrategy realignmentStrategy;
	
	JPanel realignmentStrategyPanel;
	
	public RealignmentConfigurationStep(){
		initComponents();
	}
	
	private void initComponents(){
		final ScrollableGridLayout realignmentConfigurationLayout = new ScrollableGridLayout(this, 1, 3, 0, 0);
		realignmentConfigurationLayout.setRowFixed(0, true);
		realignmentConfigurationLayout.setRowFixed(1, true);
		realignmentConfigurationLayout.setRowFixed(2, true);
		
		this.setLayout(realignmentConfigurationLayout);
		
		JLabel headerLabel = SlickerFactory.instance().createLabel("<html><h1>Refine Alignments Configuration Step-Improving Alignment Quality</H1></HTML>");
		realignmentConfigurationLayout.setPosition(headerLabel, 0, 0);
		add(headerLabel);
		
		final JCheckBox performRealignmentCheckBox = SlickerFactory.instance().createCheckBox("Perform Realignment", false);
		realignmentConfigurationLayout.setPosition(performRealignmentCheckBox, 0, 1);
		add(performRealignmentCheckBox);
		
		prepareRealignmentStrategyPanel();
		realignmentConfigurationLayout.setPosition(realignmentStrategyPanel, 0, 2);
		add(realignmentStrategyPanel);
		
		performRealignmentCheckBox.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				if(performRealignmentCheckBox.isSelected()){
					//Warn the user about the increase in computational complexity
					int choice = JOptionPane.showConfirmDialog(RealignmentConfigurationStep.this,
							"<HTML>Realignment selection increases the computational complexity. <BR> You can also try realignment later when exploring the alignments via visualization. <BR>Do you wish to continue? </HTML>", "Warning",
							JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE); 
					if(choice == JOptionPane.NO_OPTION){
						performRealignmentCheckBox.setSelected(false);
						return;
					}
					isPerformRealignment = true;
					realignmentStrategyPanel.setVisible(true);
				}else{
					isPerformRealignment = false;
					realignmentStrategyPanel.setVisible(false);
				}
			}
		});
	}
	
	private void prepareRealignmentStrategyPanel(){
		realignmentStrategyPanel = SlickerFactory.instance().createRoundedPanel();
		realignmentStrategyPanel.setBorder(BorderFactory.createTitledBorder("Realignment Strategies"));
		
		ScrollableGridLayout realignmentStrategyPanelLayout = new ScrollableGridLayout(realignmentStrategyPanel, 1, 2, 0, 0);
		realignmentStrategyPanelLayout.setRowFixed(0, true);
		realignmentStrategyPanelLayout.setRowFixed(1, true);
		
		realignmentStrategyPanel.setLayout(realignmentStrategyPanelLayout);
		
		blockShiftRealignmentRadioButton = SlickerFactory.instance().createRadioButton("Block Shift");
		concurrencyFilteredRealignmentRadioButton = SlickerFactory.instance().createRadioButton("Concurrency Pruning and Realignment");
		
		ButtonGroup realignmentStrategyButtonGroup = new ButtonGroup();
		realignmentStrategyButtonGroup.add(blockShiftRealignmentRadioButton);
		realignmentStrategyButtonGroup.add(concurrencyFilteredRealignmentRadioButton);
		
		blockShiftRealignmentRadioButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				if(blockShiftRealignmentRadioButton.isSelected()){
					realignmentStrategy = RealignmentStrategy.BlockShift;
				}
			}
		});
		
		concurrencyFilteredRealignmentRadioButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				if(concurrencyFilteredRealignmentRadioButton.isSelected()){
					realignmentStrategy = RealignmentStrategy.ConcurrencyFilteredRealignment;
				}
			}
		});
		
		realignmentStrategyPanelLayout.setPosition(blockShiftRealignmentRadioButton, 0, 0);
		realignmentStrategyPanel.add(blockShiftRealignmentRadioButton);
		realignmentStrategyPanelLayout.setPosition(concurrencyFilteredRealignmentRadioButton, 0, 1);
		realignmentStrategyPanel.add(concurrencyFilteredRealignmentRadioButton);
		
		realignmentStrategyPanel.setVisible(false);
	}
	
	public boolean precondition() {
		return true;
	}

	public void readSettings() {
		listener.setIsPerformRealignment(isPerformRealignment);
		listener.setRealignmentStrategy(realignmentStrategy);
	}

	public void setListener(TraceAlignmentWithGuideTreeSettingsListener listener) {
		this.listener = listener;
	}
}
