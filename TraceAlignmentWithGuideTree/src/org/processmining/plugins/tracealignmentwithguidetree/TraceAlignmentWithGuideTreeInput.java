package org.processmining.plugins.tracealignmentwithguidetree;

import org.processmining.plugins.tracealignmentwithguidetree.enumtypes.AlignmentAlgorithm;
import org.processmining.plugins.tracealignmentwithguidetree.enumtypes.RealignmentStrategy;
import org.processmining.plugins.tracealignmentwithguidetree.enumtypes.ScoringMatrix;

public class TraceAlignmentWithGuideTreeInput {
	boolean isDeriveSubstitutionIndelScores;
	String substitutionScoreFileName;
	String indelScoreFileName;
	
	ScoringMatrix scoringMatrix;
	AlignmentAlgorithm alignmentAlgorithm;
	RealignmentStrategy realignmentStrategy;
	
	int incrementLikeSubstitutionScoreValue;
	float indelScoreScaleValue;
	
	boolean isIncrementLikeSubstitutionScores;
	boolean isScaleIndelScores;
	boolean isGapPenalty;
	boolean isPerformRealignment;
	
	public TraceAlignmentWithGuideTreeInput(){
		
	}

	public void setIsDeriveSubstitutionIndelScores(
			boolean isDeriveSubstitutionIndelScores) {
		this.isDeriveSubstitutionIndelScores = isDeriveSubstitutionIndelScores;
	}

	public void setSubstitutionScoreFileName(String substitutionScoreFileName) {
		this.substitutionScoreFileName = substitutionScoreFileName;
	}

	public void setIndelScoreFileName(String indelScoreFileName) {
		this.indelScoreFileName = indelScoreFileName;
	}

	public boolean isDeriveSubstitutionIndelScores() {
		return isDeriveSubstitutionIndelScores;
	}
	
	public void setScoringMatrix(ScoringMatrix scoringMatrix) {
		this.scoringMatrix = scoringMatrix;
	}

	public void setAlignmentAlgorithm(AlignmentAlgorithm alignmentAlgorithm) {
		this.alignmentAlgorithm = alignmentAlgorithm;
	}
	
	public void setIsIncrementLikeSubstitutionScores(
			boolean isIncrementLikeSubstitutionScores) {
		this.isIncrementLikeSubstitutionScores = isIncrementLikeSubstitutionScores;
	}

	public void setIncrementLikeSubstitutionScoreValue(int incrementLikeSubstitutionScoreValue){
		this.incrementLikeSubstitutionScoreValue = incrementLikeSubstitutionScoreValue;
	}
	
	public void setIsGapPenalty(boolean isGapPenalty) {
		this.isGapPenalty = isGapPenalty;
	}

	
	public String getSubstitutionScoreFileName() {
		return substitutionScoreFileName;
	}

	public String getIndelScoreFileName() {
		return indelScoreFileName;
	}

	public AlignmentAlgorithm getAlignmentAlgorithm() {
		return alignmentAlgorithm;
	}

	public ScoringMatrix getScoringMatrix() {
		return scoringMatrix;
	}

	public boolean isIncrementLikeSubstitutionScores() {
		return isIncrementLikeSubstitutionScores;
	}
	
	public boolean isScaleIndelScores(){
		return isScaleIndelScores;
	}
	
	public void setIsScaleIndelScores(boolean isScaleIndelScores){
		this.isScaleIndelScores = isScaleIndelScores;
	}
	
	public void setIndelScoreScaleValue(float indelScoreScaleValue){
		this.indelScoreScaleValue = indelScoreScaleValue;
	}
	
	public void setIsPerformRealignment(boolean isPerformRealignment){
		this.isPerformRealignment = isPerformRealignment;
	}
	
	public void setRealignmentStrategy(RealignmentStrategy realignmentStrategy){
		this.realignmentStrategy = realignmentStrategy;
	}

	public boolean isPerformRealignment(){
		return this.isPerformRealignment;
	}
	
	public RealignmentStrategy getRealignmentStrategy(){
		return realignmentStrategy;
	}
	
	public float getIndelScoreScaleValue(){
		return indelScoreScaleValue;
	}
	
	public int getIncrementLikeSubstitutionScoreValue(){
		return this.incrementLikeSubstitutionScoreValue;
	}

	public boolean isGapPenalty() {
		return isGapPenalty;
	}
}
