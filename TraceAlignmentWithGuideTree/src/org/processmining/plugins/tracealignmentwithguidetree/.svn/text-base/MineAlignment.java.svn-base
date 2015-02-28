package org.processmining.plugins.tracealignmentwithguidetree;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JFrame;
import javax.swing.JOptionPane;

import org.processmining.plugins.guidetreeminer.tree.GuideTree;
import org.processmining.plugins.tracealignmentwithguidetree.exceptions.ActivityNotFoundException;
import org.processmining.plugins.tracealignmentwithguidetree.msa.GuideTreeAlignment;
import org.processmining.plugins.tracealignmentwithguidetree.tree.AlignmentTree;
import org.processmining.plugins.tracealignmentwithguidetree.util.FileIO;
import org.processmining.plugins.tracealignmentwithguidetree.util.IndelSubstitutionScore;

public class MineAlignment {
	TraceAlignmentWithGuideTreeInput input;
	GuideTree guideTree;
	IndelSubstitutionScore indelSubstitutionScore;
	
	public AlignmentTree mine(GuideTree guideTree, TraceAlignmentWithGuideTreeInput input){
		this.input = input;
		this.guideTree = guideTree;
		
		generateSubstitutionIndelScores();
		
		
		
		printSubstitutionIndelScores();
		

		GuideTreeAlignment g = new GuideTreeAlignment(guideTree, input, indelSubstitutionScore);

	
		return g.getAlignmentTree();
	}
	
	private void generateSubstitutionIndelScores(){
		indelSubstitutionScore = new IndelSubstitutionScore();
		switch(input.scoringMatrix){
		case Unit:
			indelSubstitutionScore.generateUnitScores(guideTree.getCharActivityMap());
			break;
		case Derive:
			indelSubstitutionScore.deriveScores(guideTree.getEncodingLength(), guideTree.getCharActivityMap(), guideTree.getEncodedTraceList());
			break;
		case Load:
			try{
				indelSubstitutionScore.loadIndelRightGivenLeftScores(input.getIndelScoreFileName(), guideTree.getActivityCharMap());
				indelSubstitutionScore.loadIndelLeftGivenRightScores(input.getIndelScoreFileName(), guideTree.getActivityCharMap());
				indelSubstitutionScore.loadSubstitutionScores(input.getSubstitutionScoreFileName(), guideTree.getActivityCharMap());
			}catch(ActivityNotFoundException e){
				JOptionPane.showMessageDialog(new JFrame(),"Activity not found when loading score for activity pair "+e.getActivity(), "Activity Not Found Exception", JOptionPane.ERROR_MESSAGE);
			}
			break;
		case LikePreference:
			indelSubstitutionScore.generateLikePreferenceScores(guideTree.getEncodingLength(), guideTree.getCharActivityMap(), guideTree.getEncodedTraceList());
			break;
		}
		
		if(input.isIncrementLikeSubstitutionScores()){
			indelSubstitutionScore.incrementLikeSubstitutionScore(input.getIncrementLikeSubstitutionScoreValue());
		}
		
		if(input.isScaleIndelScores()){
			indelSubstitutionScore.scaleIndelScore(input.getIndelScoreScaleValue());
		}
	}
	
	protected void printSubstitutionIndelScores(){
		FileIO io = new FileIO();
		String delim = "\\^";
		String outputDir = System.getProperty("java.io.tmpdir")+"\\TraceAlignmentWithGuideTree";
		if(!new File(outputDir).exists())
			new File(outputDir).mkdirs();
		io.writeToFile(outputDir, "ActivityCharMap.txt", guideTree.getActivityCharMap(),delim);
		io.writeToFile(outputDir, "CharActivityMap.txt", guideTree.getCharActivityMap(),delim);
		System.out.println("Output Directory: "+outputDir);
		switch(input.scoringMatrix){
		case Unit:
				io.writeToFile(outputDir, "UnitSubstitutionScoreMap.txt", getDecodedScoreMap(indelSubstitutionScore.getSubstitutionScoreMap()), delim);
				io.writeToFile(outputDir, "UnitIndelLeftGivenRightScoreMap.txt", getDecodedScoreMap(indelSubstitutionScore.getIndelLeftGivenRightScoreMap()), delim);
				io.writeToFile(outputDir, "UnitIndelRightGivenLeftScoreMap.txt", getDecodedScoreMap(indelSubstitutionScore.getIndelRightGivenLeftScoreMap()), delim);
			break;
		case Derive:
			io.writeToFile(outputDir, "DerivedSubstitutionScoreMap.txt", getDecodedScoreMap(indelSubstitutionScore.getSubstitutionScoreMap()), delim);
			io.writeToFile(outputDir, "DerivedIndelLeftGivenRightScoreMap.txt", getDecodedScoreMap(indelSubstitutionScore.getIndelLeftGivenRightScoreMap()), delim);
			io.writeToFile(outputDir, "DerivedIndelRightGivenLeftScoreMap.txt", getDecodedScoreMap(indelSubstitutionScore.getIndelRightGivenLeftScoreMap()), delim);
			break;
		case Load:
			io.writeToFile(outputDir, "LoadedSubstitutionScoreMap.txt", getDecodedScoreMap(indelSubstitutionScore.getSubstitutionScoreMap()), delim);
			io.writeToFile(outputDir, "LoadedIndelLeftGivenRightScoreMap.txt", getDecodedScoreMap(indelSubstitutionScore.getIndelLeftGivenRightScoreMap()), delim);
			io.writeToFile(outputDir, "LoadedIndelRightGivenLeftScoreMap.txt", getDecodedScoreMap(indelSubstitutionScore.getIndelRightGivenLeftScoreMap()), delim);
			break;
		}
	}
	
	private Map<String, Integer> getDecodedScoreMap(Map<String, Integer> encodedScoreMap){
		Map<String, Integer> decodedScoreMap = new HashMap<String, Integer>();
		String[] encodedActivityPairSplit;
		Map<String, String> charActivityMap = guideTree.getCharActivityMap();
		String decodedActivityPair;
		
		for(String encodedActivityPair : encodedScoreMap.keySet()){
			encodedActivityPairSplit = encodedActivityPair.split(" @ ");
			if(charActivityMap.containsKey(encodedActivityPairSplit[0]))
				decodedActivityPair = charActivityMap.get(encodedActivityPairSplit[0]).trim()+" @ ";
			else
				decodedActivityPair = encodedActivityPairSplit[0]+" @ ";
			
			if(charActivityMap.containsKey(encodedActivityPairSplit[1]))
				decodedActivityPair += charActivityMap.get(encodedActivityPairSplit[1]).trim();
			else
				decodedActivityPair += encodedActivityPairSplit[1];
			
			decodedScoreMap.put(decodedActivityPair, encodedScoreMap.get(encodedActivityPair));
		}
		
		return decodedScoreMap;
	}
}
