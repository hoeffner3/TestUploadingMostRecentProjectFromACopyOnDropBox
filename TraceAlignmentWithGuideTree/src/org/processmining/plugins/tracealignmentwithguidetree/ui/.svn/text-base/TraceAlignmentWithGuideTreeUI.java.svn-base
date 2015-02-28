package org.processmining.plugins.tracealignmentwithguidetree.ui;

import org.deckfour.uitopia.api.event.TaskListener.InteractionResult;
import org.processmining.contexts.uitopia.UIPluginContext;
import org.processmining.plugins.tracealignmentwithguidetree.TraceAlignmentWithGuideTreeInput;
import org.processmining.plugins.tracealignmentwithguidetree.enumtypes.AlignmentAlgorithm;
import org.processmining.plugins.tracealignmentwithguidetree.enumtypes.RealignmentStrategy;
import org.processmining.plugins.tracealignmentwithguidetree.enumtypes.ScoringMatrix;
import org.processmining.plugins.tracealignmentwithguidetree.settings.TraceAlignmentWithGuideTreeSettingsListener;

public class TraceAlignmentWithGuideTreeUI implements TraceAlignmentWithGuideTreeSettingsListener{
	private UIPluginContext context;

	private int introductionStep;
	private int scoringMatricesConfigurationStep;
	private int alignmentAlgorithmConfigurationStep;
	private int realignmentConfigurationStep;
	
	int noSteps;
	int currentStep;
	private myStep[] mySteps;
	
	TraceAlignmentWithGuideTreeInput input;
	
	public TraceAlignmentWithGuideTreeUI(UIPluginContext context){
		this.context = context;
	}
	
	public TraceAlignmentWithGuideTreeInput readInput(){
		input = new TraceAlignmentWithGuideTreeInput();
		
		InteractionResult result = InteractionResult.NEXT;
		
		noSteps = 0;
		introductionStep = noSteps++;
		scoringMatricesConfigurationStep = noSteps++;
		alignmentAlgorithmConfigurationStep = noSteps++;
		realignmentConfigurationStep = noSteps++;
		
		mySteps = new myStep[noSteps];
		mySteps[introductionStep] = new IntroductionStep();
		mySteps[introductionStep].setListener(this);
		
		mySteps[scoringMatricesConfigurationStep] = new ScoringMatricesConfigurationStep();
		mySteps[scoringMatricesConfigurationStep].setListener(this);
		
		mySteps[alignmentAlgorithmConfigurationStep] = new AlignmentAlgorithmConfigurationStep();
		mySteps[alignmentAlgorithmConfigurationStep].setListener(this);
		
		mySteps[realignmentConfigurationStep] = new RealignmentConfigurationStep();
		mySteps[realignmentConfigurationStep].setListener(this);
		
		while (true) {
			if (currentStep < 0) {
				currentStep = 0;
			}
			if (currentStep >= noSteps) {
				currentStep = noSteps - 1;
			}
			context.log("Current step: " + currentStep);
			result = context.showWizard("Trace Alignment (with Guide Tree) Plugin", currentStep == 0, currentStep == noSteps - 1, mySteps[currentStep]);
			
			switch (result) {
			case NEXT:
				go(1);
				break;
			case PREV:
				go(-1);
				break;
			case FINISHED:
				readSettings();
				return input;
			default:
				context.getFutureResult(0).cancel(true);
				return null;
			}
		}
	}
	
	private int go(int direction) {
		currentStep += direction;
		if (currentStep >= 0 && currentStep < noSteps) {
			if (mySteps[currentStep].precondition()) {
				return currentStep;
			} else {
				return go(direction);
			}
		}
		return currentStep;
	}
	
	private void readSettings(){
		for(int currentStep = 1; currentStep < noSteps; currentStep++){
			context.log("Reading Settings for Step: "+currentStep);
			mySteps[currentStep].readSettings();
		}
	}

	public void setIsDeriveSubstitutionIndelScores(
			boolean isDeriveSubstitutionIndelScores) {
		input.setIsDeriveSubstitutionIndelScores(isDeriveSubstitutionIndelScores);
	}

	public void setSubstitutionScoreFileName(String substitutionScoreFileName) {
		input.setSubstitutionScoreFileName(substitutionScoreFileName);
	}

	public void setIndelScoreFileName(String indelScoreFileName) {
		input.setIndelScoreFileName(indelScoreFileName);
	}

	public void setAlignmentAlgorithm(AlignmentAlgorithm alignmentAlgorithm) {
		input.setAlignmentAlgorithm(alignmentAlgorithm);
	}

	public void setScoringMatrix(ScoringMatrix scoringMatrix) {
		input.setScoringMatrix(scoringMatrix);
	}

	public void setIsIncrementLikeSubstitutionScores(
			boolean isIncrementLikeSubstitutionScores) {
		input.setIsIncrementLikeSubstitutionScores(isIncrementLikeSubstitutionScores);
	}

	public void setIsGapPenalty(boolean isGapPenalty) {
		input.setIsGapPenalty(isGapPenalty);
	}

	public TraceAlignmentWithGuideTreeInput getInput() {
		return input;
	}
	
	public void setIncrementLikeSubstitutionScoreValue(int incrementLikeSubstitutionScoreValue){
		input.setIncrementLikeSubstitutionScoreValue(incrementLikeSubstitutionScoreValue);
	}
	
	public void setIsScaleIndelScores(boolean isScaleIndelScores){
		input.setIsScaleIndelScores(isScaleIndelScores);
	}
	
	public void setIndelScoreScaleValue(float indelScoreScaleValue){
		input.setIndelScoreScaleValue(indelScoreScaleValue);
	}
	
	public void setIsPerformRealignment(boolean isPerformRealignment){
		input.setIsPerformRealignment(isPerformRealignment);
	}
	
	public void setRealignmentStrategy(RealignmentStrategy realignmentStrategy){
		input.setRealignmentStrategy(realignmentStrategy);
	}
}
