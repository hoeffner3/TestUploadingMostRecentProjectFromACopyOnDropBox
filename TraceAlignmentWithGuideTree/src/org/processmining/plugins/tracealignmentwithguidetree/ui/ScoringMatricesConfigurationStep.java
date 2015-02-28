package org.processmining.plugins.tracealignmentwithguidetree.ui;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JSlider;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.processmining.plugins.guidetreeminer.swingx.ScrollableGridLayout;
import org.processmining.plugins.tracealignmentwithguidetree.enumtypes.ScoringMatrix;
import org.processmining.plugins.tracealignmentwithguidetree.settings.TraceAlignmentWithGuideTreeSettingsListener;

import com.fluxicon.slickerbox.factory.SlickerFactory;

@SuppressWarnings("serial")
public class ScoringMatricesConfigurationStep extends myStep {
	TraceAlignmentWithGuideTreeSettingsListener listener;
	
	JRadioButton unitCostScoringMatrixRadioButton;
	JRadioButton deriveScoringMatricesRadioButton;
	JRadioButton loadScoringMatricesRadioButton;
	JRadioButton likePreferenceMatrixRadioButton;
	
	JPanel adjustScoresPanel;
	JRadioButton incrementLikeSubstitutionScoresRadioButton;
	JRadioButton scaleIndelScoresRadioButton;
	int incrementLikeSubstitutionScoreValue;
	float indelScoreScaleValue;
	
	JPanel loadScoringMatricesPanel;
	JLabel substitutionScoreFileNameLabel;
	JLabel indelScoreFileNameLabel;
	
	public ScoringMatricesConfigurationStep(){
		initComponents();
	}
	
	private void initComponents(){
		final ScrollableGridLayout scoringMatricesConfigurationLayout = new ScrollableGridLayout(this, 1, 8, 0, 0);
		scoringMatricesConfigurationLayout.setRowFixed(0, true);
		scoringMatricesConfigurationLayout.setRowFixed(1, true);
		scoringMatricesConfigurationLayout.setRowFixed(2, true);
		scoringMatricesConfigurationLayout.setRowFixed(3, true);
		scoringMatricesConfigurationLayout.setRowFixed(4, true);
		scoringMatricesConfigurationLayout.setRowFixed(5, true);
		scoringMatricesConfigurationLayout.setRowFixed(6, true);
		scoringMatricesConfigurationLayout.setRowFixed(7, true);
		
		this.setLayout(scoringMatricesConfigurationLayout);
		
		JLabel headerLabel = SlickerFactory.instance().createLabel("<html><h1>Scoring Matrices Configuration Step</h1>");
		scoringMatricesConfigurationLayout.setPosition(headerLabel, 0, 0);
		add(headerLabel);
		
		unitCostScoringMatrixRadioButton = SlickerFactory.instance().createRadioButton("Unit Cost Scoring Matrix");
		deriveScoringMatricesRadioButton = SlickerFactory.instance().createRadioButton("Derive Scoring Matrices");
		loadScoringMatricesRadioButton = SlickerFactory.instance().createRadioButton("Load Scoring Matrices");
		likePreferenceMatrixRadioButton  = SlickerFactory.instance().createRadioButton("Like Substitution Preferred Matrix");
		
		deriveScoringMatricesRadioButton.setSelected(true);
		
		ButtonGroup scoringMatricesButtonGroup = new ButtonGroup();
		scoringMatricesButtonGroup.add(unitCostScoringMatrixRadioButton);
		scoringMatricesButtonGroup.add(deriveScoringMatricesRadioButton);
		scoringMatricesButtonGroup.add(loadScoringMatricesRadioButton);
		scoringMatricesButtonGroup.add(likePreferenceMatrixRadioButton);
		
		scoringMatricesConfigurationLayout.setPosition(unitCostScoringMatrixRadioButton, 0, 1);
		add(unitCostScoringMatrixRadioButton);
		
		scoringMatricesConfigurationLayout.setPosition(deriveScoringMatricesRadioButton, 0, 2);
		add(deriveScoringMatricesRadioButton);
		
		scoringMatricesConfigurationLayout.setPosition(loadScoringMatricesRadioButton, 0, 3);
		add(loadScoringMatricesRadioButton);
		
		scoringMatricesConfigurationLayout.setPosition(likePreferenceMatrixRadioButton, 0, 4);
		add(likePreferenceMatrixRadioButton);
		
		Component verticalStrut = Box.createVerticalStrut(5);
		scoringMatricesConfigurationLayout.setPosition(verticalStrut, 0, 5);
		add(verticalStrut);
		
		prepareLoadScoringMatricesPanel();
		
		scoringMatricesConfigurationLayout.setPosition(loadScoringMatricesPanel, 0, 6);
		add(loadScoringMatricesPanel);
	
		prepareIncrementScoresPanel();
		scoringMatricesConfigurationLayout.setPosition(adjustScoresPanel, 0, 7);
		add(adjustScoresPanel);
		
		unitCostScoringMatrixRadioButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				if(unitCostScoringMatrixRadioButton.isSelected()){
					loadScoringMatricesPanel.setVisible(false);
				}
			}
		});

		deriveScoringMatricesRadioButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				if(deriveScoringMatricesRadioButton.isSelected()){
					loadScoringMatricesPanel.setVisible(false);
				}
			}
		});
		
		likePreferenceMatrixRadioButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				if(likePreferenceMatrixRadioButton.isSelected()){
					loadScoringMatricesPanel.setVisible(false);
				}
			}
		});
		
		loadScoringMatricesRadioButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				if(loadScoringMatricesRadioButton.isSelected()){
					loadScoringMatricesPanel.setVisible(true);
				}else{
					loadScoringMatricesPanel.setVisible(false);
				}
			}
		});
	}
	
	private void prepareIncrementScoresPanel(){
		adjustScoresPanel = SlickerFactory.instance().createRoundedPanel();
		adjustScoresPanel.setBorder(BorderFactory.createTitledBorder("Adjust Scores"));
		
		ScrollableGridLayout adjustScoresPanelLayout = new ScrollableGridLayout(adjustScoresPanel, 3, 2, 0, 0);
		adjustScoresPanelLayout.setRowFixed(0, true);
		adjustScoresPanelLayout.setRowFixed(1, true);
		adjustScoresPanelLayout.setColumnFixed(0, true);
		adjustScoresPanelLayout.setColumnFixed(1, true);
		adjustScoresPanelLayout.setColumnFixed(2, true);
		adjustScoresPanel.setLayout(adjustScoresPanelLayout);
		
		incrementLikeSubstitutionScoresRadioButton = SlickerFactory.instance().createRadioButton("Increment Like Substitution Scores");
		incrementLikeSubstitutionScoresRadioButton.setSelected(false);
		incrementLikeSubstitutionScoreValue = 4;
		
		final JSlider incrementLikeSubstitutionScoreSlider = SlickerFactory.instance().createSlider(JSlider.HORIZONTAL);
		incrementLikeSubstitutionScoreSlider.setMinimum(0);
		incrementLikeSubstitutionScoreSlider.setMaximum(10);
		incrementLikeSubstitutionScoreSlider.setValue(4);
		incrementLikeSubstitutionScoreSlider.setEnabled(false);
		final JLabel incrementLikeSubstitutionScoreValueLabel = SlickerFactory.instance().createLabel("4");
		
		adjustScoresPanelLayout.setPosition(incrementLikeSubstitutionScoresRadioButton, 0, 0);
		adjustScoresPanel.add(incrementLikeSubstitutionScoresRadioButton);
		adjustScoresPanelLayout.setPosition(incrementLikeSubstitutionScoreSlider, 1, 0);
		adjustScoresPanel.add(incrementLikeSubstitutionScoreSlider);
		adjustScoresPanelLayout.setPosition(incrementLikeSubstitutionScoreValueLabel, 2, 0);
		adjustScoresPanel.add(incrementLikeSubstitutionScoreValueLabel);
		
		scaleIndelScoresRadioButton = SlickerFactory.instance().createRadioButton("Scale Indel Scores");
		final JSlider scaleIndelScoreSlider = SlickerFactory.instance().createSlider(JSlider.HORIZONTAL);
		scaleIndelScoreSlider.setMinimum(0);
		scaleIndelScoreSlider.setMaximum(10);
		scaleIndelScoreSlider.setValue(10);
		scaleIndelScoreSlider.setEnabled(false);
		indelScoreScaleValue = 1;
		final JLabel scaleIndelScoreValueLabel = SlickerFactory.instance().createLabel(indelScoreScaleValue+"");
		
		adjustScoresPanelLayout.setPosition(scaleIndelScoresRadioButton, 0, 1);
		adjustScoresPanel.add(scaleIndelScoresRadioButton);
		adjustScoresPanelLayout.setPosition(scaleIndelScoreSlider, 1, 1);
		adjustScoresPanel.add(scaleIndelScoreSlider);
		adjustScoresPanelLayout.setPosition(scaleIndelScoreValueLabel, 2, 1);
		adjustScoresPanel.add(scaleIndelScoreValueLabel);
		
		incrementLikeSubstitutionScoreSlider.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent arg0) {
				if(!incrementLikeSubstitutionScoreSlider.getValueIsAdjusting()){
					incrementLikeSubstitutionScoreValue = incrementLikeSubstitutionScoreSlider.getValue();
					incrementLikeSubstitutionScoreValueLabel.setText(incrementLikeSubstitutionScoreValue+"");
				}
			}
		});
		
		incrementLikeSubstitutionScoresRadioButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				if(incrementLikeSubstitutionScoresRadioButton.isSelected()){
					incrementLikeSubstitutionScoreSlider.setEnabled(true);
				}else{
					incrementLikeSubstitutionScoreSlider.setEnabled(false);
				}
			}
		});
		
		scaleIndelScoresRadioButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				if(scaleIndelScoresRadioButton.isSelected())
					scaleIndelScoreSlider.setEnabled(true);
				else
					scaleIndelScoreSlider.setEnabled(false);
			}
		});
		
		scaleIndelScoreSlider.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent arg0) {
				if(!scaleIndelScoreSlider.getValueIsAdjusting()){
					indelScoreScaleValue = scaleIndelScoreSlider.getValue()/10.0f;
					scaleIndelScoreValueLabel.setText(indelScoreScaleValue+"");
				}
			}
		});
	}
	
	private void prepareLoadScoringMatricesPanel(){
		loadScoringMatricesPanel = SlickerFactory.instance().createRoundedPanel();
		loadScoringMatricesPanel.setBorder(BorderFactory.createTitledBorder("Load Scoring Matrices"));
		
		ScrollableGridLayout loadScoringMatricesLayout = new ScrollableGridLayout(loadScoringMatricesPanel, 3, 2, 20, 10);
		loadScoringMatricesLayout.setRowFixed(0, true);
		loadScoringMatricesLayout.setRowFixed(1, true);
		
		loadScoringMatricesLayout.setColumnFixed(0, true);
		loadScoringMatricesLayout.setColumnFixed(1, true);
		
		loadScoringMatricesPanel.setLayout(loadScoringMatricesLayout);
		
		JLabel substitutionScoreLabel = SlickerFactory.instance().createLabel("Substitution Score   ");	
		JLabel indelScoreLabel = SlickerFactory.instance().createLabel("Insertion/Deletion (indel) Score   ");
		
		substitutionScoreFileNameLabel = SlickerFactory.instance().createLabel("\t.");
		indelScoreFileNameLabel = SlickerFactory.instance().createLabel("\t.");
		
		JButton chooseSubstitutionScoreButton = SlickerFactory.instance().createButton("Choose");
		JButton chooseIndelScoreButton = SlickerFactory.instance().createButton("Choose");
	
		loadScoringMatricesLayout.setPosition(substitutionScoreLabel, 0, 0);
		loadScoringMatricesPanel.add(substitutionScoreLabel);
		
		loadScoringMatricesLayout.setPosition(chooseSubstitutionScoreButton, 1, 0);
		loadScoringMatricesPanel.add(chooseSubstitutionScoreButton);
		
		loadScoringMatricesLayout.setPosition(substitutionScoreFileNameLabel, 2, 0);
		loadScoringMatricesPanel.add(substitutionScoreFileNameLabel);
	
		loadScoringMatricesLayout.setPosition(indelScoreLabel, 0, 1);
		loadScoringMatricesPanel.add(indelScoreLabel);
		
		loadScoringMatricesLayout.setPosition(chooseIndelScoreButton, 1, 1);
		loadScoringMatricesPanel.add(chooseIndelScoreButton);
		
		loadScoringMatricesLayout.setPosition(indelScoreFileNameLabel, 2, 1);
		loadScoringMatricesPanel.add(indelScoreFileNameLabel);
		
		chooseSubstitutionScoreButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				JFileChooser fileChooser = new JFileChooser();
				int returnVal = fileChooser.showOpenDialog(new JFrame());
		        if (returnVal == JFileChooser.APPROVE_OPTION) {
		            substitutionScoreFileNameLabel.setText(fileChooser.getSelectedFile().getAbsolutePath());
		        }
			}
		});
		
		chooseIndelScoreButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				JFileChooser fileChooser = new JFileChooser();
				int returnVal = fileChooser.showOpenDialog(new JFrame());
		        if (returnVal == JFileChooser.APPROVE_OPTION) {
		            indelScoreFileNameLabel.setText(fileChooser.getSelectedFile().getAbsolutePath());
		        }
			}
		});
		
		loadScoringMatricesPanel.setVisible(false);
	}
	
	public boolean precondition() {
		return true;
	}

	public void setListener(TraceAlignmentWithGuideTreeSettingsListener listener) {
		this.listener = listener;
	}

	public void readSettings() {
		if(deriveScoringMatricesRadioButton.isSelected()){
			listener.setIsDeriveSubstitutionIndelScores(true);
			listener.setScoringMatrix(ScoringMatrix.Derive);
		}else{
			listener.setIsDeriveSubstitutionIndelScores(false);
		}
		
		if(loadScoringMatricesRadioButton.isSelected()){
			listener.setScoringMatrix(ScoringMatrix.Load);
			listener.setSubstitutionScoreFileName(substitutionScoreFileNameLabel.getText().trim());
			listener.setIndelScoreFileName(indelScoreFileNameLabel.getText().trim());
		}
		
		if(unitCostScoringMatrixRadioButton.isSelected())
			listener.setScoringMatrix(ScoringMatrix.Unit);
		
		if(incrementLikeSubstitutionScoresRadioButton.isSelected()){
			listener.setIsIncrementLikeSubstitutionScores(true);
			listener.setIncrementLikeSubstitutionScoreValue(incrementLikeSubstitutionScoreValue);
		}
		
		if(likePreferenceMatrixRadioButton.isSelected()){
			listener.setScoringMatrix(ScoringMatrix.LikePreference);
		}
		
		if(scaleIndelScoresRadioButton.isSelected()){
			listener.setIsScaleIndelScores(true);
			listener.setIndelScoreScaleValue(indelScoreScaleValue);
		}
	}
}
