package org.processmining.plugins.tracealignmentwithguidetree.util;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.processmining.plugins.tracealignmentwithguidetree.exceptions.ActivityNotFoundException;

public class IndelSubstitutionScore {
	Map<String, Integer> substitutionScoreMap;
	Map<String, Integer> indelRightGivenLeftScoreMap;
	Map<String, Integer> indelLeftGivenRightScoreMap;
	
	int encodingLength;
	Set<String> symbolSet;
	Map<String, Set<String>> symbolContextSetMap;
	Map<String, Integer> kGramCountMap;
	float[] symbolProbabilityArray;
	
	public IndelSubstitutionScore(){
		
	}
	
	public void generateLikePreferenceScores(int encodingLength, Map<String, String> charActivityMap, List<String> encodedTraceList){
		deriveScores(encodingLength, charActivityMap, encodedTraceList);
		substitutionScoreMap.clear();
		
		Set<String> encodedActivitySet = new HashSet<String>();
		encodedActivitySet.addAll(charActivityMap.keySet());
		
		String activityPair;
		for(String encodedActivityA : encodedActivitySet){
			for(String encodedActivityB : encodedActivitySet){
				activityPair = encodedActivityA+" @ "+encodedActivityB;
				if(encodedActivityA.equals(encodedActivityB))
					substitutionScoreMap.put(activityPair, 1000);
				else
					substitutionScoreMap.put(activityPair, -1000);
			}
		}
	}
	
	public void generateUnitScores(Map<String, String> charActivityMap){
		substitutionScoreMap = new HashMap<String, Integer>();
		indelRightGivenLeftScoreMap = new HashMap<String, Integer>();
		indelLeftGivenRightScoreMap = new HashMap<String, Integer>();
		
		Set<String> encodedActivitySet = new HashSet<String>();
		encodedActivitySet.addAll(charActivityMap.keySet());
		
		String activityPair;
		for(String encodedActivityA : encodedActivitySet){
			for(String encodedActivityB : encodedActivitySet){
				activityPair = encodedActivityA+" @ "+encodedActivityB;
				if(encodedActivityA.equals(encodedActivityB))
					substitutionScoreMap.put(activityPair, 1);
				else
					substitutionScoreMap.put(activityPair, -1);
				
				indelRightGivenLeftScoreMap.put(activityPair, -1);
				indelLeftGivenRightScoreMap.put(activityPair, -1);
			}
		}
	}
	
	public void deriveScores(int encodingLength, Map<String, String> charActivityMap, List<String> encodedTraceList){
		this.encodingLength = encodingLength;
		System.out.println("No. Traces: "+encodedTraceList.size());
		computeKGrams( encodedTraceList);
		getContexts();
		computeSubstitutionScore();
		computeIndelScores();
//		adjustIndelScores();
//		if(incrementLikeSubstitutionScoreValue > 0){
//			incrementLikeSubstitutionScore(incrementLikeSubstitutionScoreValue);
//		}
	}
	
	private void computeKGrams(List<String> encodedTraceList){
		kGramCountMap = new HashMap<String, Integer>();
		/*
		 * Compute the 3-grams Append a (common) symbol as a prefix and suffix
		 * to each charStream to enable the substitution of the first and the
		 * last symbol in the charStream
		 */

		String commonSymbol = ".";
		for (int i = 1; i < encodingLength; i++) {
			commonSymbol += ".";
		}

		String kGram;
		Set<String> encodedTraceKGramSet = new HashSet<String>();
		String combinedEncodedTrace;
		int combinedEncodedTraceLength, gramCount;
		UkkonenSuffixTree suffixTree;
				
		for (String encodedTrace : encodedTraceList) {
			combinedEncodedTrace = commonSymbol + encodedTrace + commonSymbol;
			combinedEncodedTraceLength = combinedEncodedTrace.length() / encodingLength;

			suffixTree = new UkkonenSuffixTree(encodingLength, combinedEncodedTrace);
			// Find the 3-grams
			encodedTraceKGramSet.clear();
			for (int i = 0; i < combinedEncodedTraceLength - 2; i++) {
				kGram = combinedEncodedTrace.substring(i * encodingLength, (i + 3) * encodingLength);
				encodedTraceKGramSet.add(kGram);
			}

			// Get the count of each kgram in this charStream
			for (String KGram : encodedTraceKGramSet) {
				gramCount = suffixTree.noMatches(KGram);
				if (kGramCountMap.containsKey(KGram)) {
					gramCount += kGramCountMap.get(KGram);
				}
				kGramCountMap.put(KGram, gramCount);
			}
		}
	}
	
	private void getContexts() {
		symbolContextSetMap = new HashMap<String, Set<String>>();
		symbolSet = new HashSet<String>();

		Set<String> kGramSet = kGramCountMap.keySet();
		String symbol, symbolContext;
		Set<String> symbolContextSet;
		for (String kGram : kGramSet) {
			// the middle symbol in the 3-gram
			symbol = kGram.substring(encodingLength, 2 * encodingLength);
			symbolSet.add(symbol);
			symbolContext = kGram.substring(0, encodingLength)
					+ kGram.substring(2 * encodingLength, 3 * encodingLength);
			if (symbolContextSetMap.containsKey(symbol)) {
				symbolContextSet = symbolContextSetMap.get(symbol);
			} else {
				symbolContextSet = new HashSet<String>();
			}
			symbolContextSet.add(symbolContext);
			symbolContextSetMap.put(symbol, symbolContextSet);
		}
	}

	private void computeSubstitutionScore() {
		String symbolI, symbolJ;
		Set<String> contextSetSymbolI, contextSetSymbolJ, commonContextSet;
		String kGramSymbolI, kGramSymbolJ;
		int coOccurrenceCount, kGramCountSymbolI, kGramCountSymbolJ;
		commonContextSet = new HashSet<String>();

		int noSymbols = symbolSet.size();

		List<String> symbolList = new ArrayList<String>();
		symbolList.addAll(symbolSet);

		float[][] M = new float[noSymbols][noSymbols];
		symbolProbabilityArray = new float[noSymbols];

		float normCoOccurenceCount = 0;

		for (int i = 0; i < noSymbols; i++) {
			symbolI = symbolList.get(i);
			contextSetSymbolI = symbolContextSetMap.get(symbolI);

			for (int j = 0; j < noSymbols; j++) {
				symbolJ = symbolList.get(j);
				contextSetSymbolJ = symbolContextSetMap.get(symbolJ);

				/*
				 * Get the common contexts for symbolI and symbolJ
				 */
				commonContextSet.clear();
				commonContextSet.addAll(contextSetSymbolI);
				commonContextSet.retainAll(contextSetSymbolJ);

				/*
				 * 
				 */
				coOccurrenceCount = 0;
				for (String commonContext : commonContextSet) {
					kGramSymbolI = commonContext.substring(0, encodingLength) + symbolI
							+ commonContext.substring(encodingLength, 2 * encodingLength);
					kGramSymbolJ = commonContext.substring(0, encodingLength) + symbolJ
							+ commonContext.substring(encodingLength, 2 * encodingLength);

					kGramCountSymbolI = kGramCountMap.get(kGramSymbolI);
					kGramCountSymbolJ = kGramCountMap.get(kGramSymbolJ);

					if (symbolI.equals(symbolJ)) {
						coOccurrenceCount += (kGramCountSymbolI * (kGramCountSymbolI - 1)) / 2;
					} else {
						coOccurrenceCount += kGramCountSymbolI * kGramCountSymbolJ;
					}
				}
				M[i][j] = coOccurrenceCount;
				normCoOccurenceCount += coOccurrenceCount;
			}
		}

		for (int i = 0; i < noSymbols; i++) {
			for (int j = 0; j < noSymbols; j++) {
				M[i][j] /= normCoOccurenceCount;
			}
		}

		float sumProbabilities = 0;
		for (int i = 0; i < noSymbols; i++) {
			symbolProbabilityArray[i] = 0;
			for (int j = 0; j < noSymbols; j++) {
				symbolProbabilityArray[i] += M[i][j];
			}

			sumProbabilities += symbolProbabilityArray[i];
		}

		System.out.println("Sum of Probabilities: " + sumProbabilities);

		substitutionScoreMap = new HashMap<String, Integer>();

		float expectedValue;
		int substitutionScore;
		for (int i = 0; i < noSymbols; i++) {
			symbolI = symbolList.get(i);
			for (int j = 0; j < noSymbols; j++) {
				symbolJ = symbolList.get(j);
				if (i == j) {
					expectedValue = symbolProbabilityArray[i] * symbolProbabilityArray[j];
				} else {
					expectedValue = 2 * symbolProbabilityArray[i] * symbolProbabilityArray[j];
				}
				if (M[i][j] > 0) {
					substitutionScore = new Double(Math.log(M[i][j] / expectedValue)).intValue();
					substitutionScoreMap.put(symbolI + " @ " + symbolJ, substitutionScore);
				}else{
					substitutionScore = new Double(-1*Math.log(1 / expectedValue)).intValue();
					substitutionScoreMap.put(symbolI + " @ " + symbolJ, substitutionScore);
				}
			}
		}
	}

	private void computeIndelScores() {
		indelRightGivenLeftScoreMap = new HashMap<String, Integer>();
		indelLeftGivenRightScoreMap = new HashMap<String, Integer>();

		List<String> symbolList = new ArrayList<String>();
		symbolList.addAll(symbolSet);

		int noSymbols = symbolList.size();

		String symbolI, leftSymbol, rightSymbol;
		Set<String> contextSetSymbolI;

		Map<String, Integer> countGivenLeftMap = new HashMap<String, Integer>();
		Map<String, Integer> countGivenRightMap = new HashMap<String, Integer>();

		int countGivenLeft, countGivenRight;
		float normSymbolGivenLeft, normSymbolGivenRight, normCountSymbolPair;

		String commonSymbol = ".";
		for (int i = 1; i < encodingLength; i++) {
			commonSymbol += ".";
		}

		int indelScore;
		for (int i = 0; i < noSymbols; i++) {
			symbolI = symbolList.get(i);
			normSymbolGivenLeft = normSymbolGivenRight = 0;
			if (symbolContextSetMap.containsKey(symbolI)) {
				contextSetSymbolI = symbolContextSetMap.get(symbolI);
				for (String context : contextSetSymbolI) {
					leftSymbol = context.substring(0, encodingLength);
					rightSymbol = context.substring(encodingLength, 2 * encodingLength);

					countGivenRight = countGivenLeft = kGramCountMap.get(leftSymbol + symbolI + rightSymbol);

					normSymbolGivenLeft += countGivenLeft;
					normSymbolGivenRight += countGivenRight;

					if (countGivenLeftMap.containsKey(leftSymbol + " @ " + symbolI)) {
						countGivenLeft += countGivenLeftMap.get(leftSymbol + " @ " + symbolI);
					}
					countGivenLeftMap.put(leftSymbol + " @ " + symbolI, countGivenLeft);

					if (countGivenRightMap.containsKey(symbolI + " @ " + rightSymbol)) {
						countGivenRight += countGivenRightMap.get(symbolI + " @ " + rightSymbol);
					}
					countGivenRightMap.put(symbolI + " @ " + rightSymbol, countGivenRight);
				}

				for (String indelRightGivenLeftSymbolPair : countGivenLeftMap.keySet()) {
					leftSymbol = indelRightGivenLeftSymbolPair.split(" @ ")[0].trim();
					if (!leftSymbol.equals(commonSymbol)) {
						normCountSymbolPair = countGivenLeftMap.get(indelRightGivenLeftSymbolPair)
								/ normSymbolGivenLeft;
						indelScore = new Double(Math
								.log(normCountSymbolPair
										/ (symbolProbabilityArray[i] * symbolProbabilityArray[symbolList
												.indexOf(leftSymbol)]))).intValue();
						if(indelScore > 100)
							indelScore = 10;
						indelRightGivenLeftScoreMap.put(indelRightGivenLeftSymbolPair, indelScore);
					}
				}

				for (String indelLeftGivenRightSymbolPair : countGivenRightMap.keySet()) {
					rightSymbol = indelLeftGivenRightSymbolPair.split(" @ ")[0].trim();
					if (!rightSymbol.equals(commonSymbol)) {
						normCountSymbolPair = countGivenRightMap.get(indelLeftGivenRightSymbolPair)
								/ normSymbolGivenRight;
						indelScore = new Double(Math
								.log(normCountSymbolPair
										/ (symbolProbabilityArray[i] * symbolProbabilityArray[symbolList
												.indexOf(rightSymbol)]))).intValue();
						if(indelScore > 100)
							indelScore = 10;
						indelLeftGivenRightScoreMap.put(indelLeftGivenRightSymbolPair, indelScore);
					}
				}

				countGivenLeftMap.clear();
				countGivenRightMap.clear();
			}
		}
		
		String indelSymbolPair;
		for(int i = 0; i < noSymbols; i++){
			for(int j = 0; j < noSymbols; j++){
				indelSymbolPair = symbolList.get(i)+" @ "+symbolList.get(j);
				if(!indelRightGivenLeftScoreMap.containsKey(indelSymbolPair)){
					indelScore = new Double(0.2*Math.log(1/(symbolProbabilityArray[i] * symbolProbabilityArray[j]))).intValue();
					indelRightGivenLeftScoreMap.put(indelSymbolPair, -1*indelScore);
				}else{
					indelRightGivenLeftScoreMap.put(indelSymbolPair, new Double(indelRightGivenLeftScoreMap.get(indelSymbolPair)*0.2).intValue());
				}
				
			}
		}
	}

	private void adjustIndelScores(){
		int minSubstitutionScore, maxSubstitionScore;
		float avgSubstitutionScore;
		
		int minIndelScore, maxIndelScore;
		float avgIndelScore;
		
		minSubstitutionScore = Integer.MAX_VALUE;
		maxSubstitionScore = Integer.MIN_VALUE;
		int substitutionScore;
		int sumScore = 0;
		for(String activityPair : substitutionScoreMap.keySet()){
			substitutionScore = substitutionScoreMap.get(activityPair);
			sumScore += substitutionScore;
			if(substitutionScore < minSubstitutionScore)
				minSubstitutionScore = substitutionScore;
			if(substitutionScore > maxSubstitionScore)
				maxSubstitionScore = substitutionScore;
		}
		
		avgSubstitutionScore = (sumScore*1.0f)/substitutionScoreMap.keySet().size();
		
		minIndelScore = Integer.MAX_VALUE;
		maxIndelScore = Integer.MAX_VALUE;
		int indelScore;
		sumScore = 0;
		for(String activityPair : indelRightGivenLeftScoreMap.keySet()){
			indelScore = indelRightGivenLeftScoreMap.get(activityPair);
			sumScore += indelScore;
			if(indelScore < minIndelScore)
				minIndelScore = indelScore;
			if(indelScore > maxIndelScore)
				maxIndelScore = indelScore;
		}
		
		avgIndelScore = (sumScore*1.0f)/indelRightGivenLeftScoreMap.keySet().size();
		System.out.println("AvSubSc: "+avgSubstitutionScore);
		System.out.println("AvInSc: "+avgIndelScore);
		boolean shouldDecrement = false;
		int decrementIndelScoreValue=0;
		if(avgIndelScore > avgSubstitutionScore){
			shouldDecrement = true;
			decrementIndelScoreValue = (int)(avgIndelScore-avgSubstitutionScore);
//			decrementIndelScoreValue = Math.abs((int)Math.ceil(2*avgIndelScore/avgSubstitutionScore));
		}
		
		minIndelScore = Integer.MAX_VALUE;
		maxIndelScore = Integer.MAX_VALUE;
		for(String activityPair : indelLeftGivenRightScoreMap.keySet()){
			indelScore = indelLeftGivenRightScoreMap.get(activityPair);
			sumScore += indelScore;
			if(indelScore < minIndelScore)
				minIndelScore = indelScore;
			if(indelScore > maxIndelScore)
				maxIndelScore = indelScore;
		}
		avgIndelScore = (sumScore*1.0f)/indelLeftGivenRightScoreMap.keySet().size();
		System.out.println("AvInSc: "+avgIndelScore);
		if(avgIndelScore > avgSubstitutionScore){
			shouldDecrement =  true;
			decrementIndelScoreValue = (int)(avgIndelScore-avgSubstitutionScore);
//			int tempValue = Math.abs((int)Math.ceil(2*avgIndelScore/avgSubstitutionScore));
//			if(tempValue > decrementIndelScoreValue)
//				decrementIndelScoreValue = tempValue;
		}
		
		if(shouldDecrement){
			decrementIndelScore(decrementIndelScoreValue);
			incrementLikeSubstitutionScore(decrementIndelScoreValue);
		}
	}
	public void loadIndelRightGivenLeftScores(String indelRightGivenLeftScoreAbsolutePathName, Map<String, String> activityCharMap) throws ActivityNotFoundException{
		FileIO io = new FileIO();
		String delim = "\\^";
		String indelRightGivenLeftScoreInputDir = new File(indelRightGivenLeftScoreAbsolutePathName).getParent();
		String indelRightGivenLeftScoreFileName = new File(indelRightGivenLeftScoreAbsolutePathName).getName();
		
		Map<String, Integer> decodedIndelRightGivenLeftScoreMap = io.readMapStringIntegerFromFile(indelRightGivenLeftScoreInputDir, indelRightGivenLeftScoreFileName, delim);
		String[] decodedActivityPairSplit;
		String activityPair;
		for(String decodedActivityPair : decodedIndelRightGivenLeftScoreMap.keySet()){
			decodedActivityPairSplit = decodedActivityPair.split(" @ ");
			if(activityCharMap.containsKey(decodedActivityPairSplit[0].trim()) && activityCharMap.containsKey(decodedActivityPairSplit[1].trim())){
				activityPair = activityCharMap.get(decodedActivityPairSplit[0].trim())+" @ "+activityCharMap.get(decodedActivityPairSplit[1].trim());
				indelRightGivenLeftScoreMap.put(activityPair, decodedIndelRightGivenLeftScoreMap.get(decodedActivityPair));
			}else{
				throw new ActivityNotFoundException(decodedActivityPair);
			}
		}
	}
	
	public void loadIndelLeftGivenRightScores(String indelLeftGivenRightScoreAbsolutePathName, Map<String, String> activityCharMap) throws ActivityNotFoundException{
		FileIO io = new FileIO();
		String delim = "\\^";
		String indelLeftGivenRightScoreInputDir = new File(indelLeftGivenRightScoreAbsolutePathName).getParent();
		String indelLeftGivenRightScoreFileName = new File(indelLeftGivenRightScoreAbsolutePathName).getName();
		
		Map<String, Integer> decodedIndelLeftGivenRightScoreMap = io.readMapStringIntegerFromFile(indelLeftGivenRightScoreInputDir, indelLeftGivenRightScoreFileName, delim);
		String[] decodedActivityPairSplit;
		String activityPair;
		for(String decodedActivityPair : decodedIndelLeftGivenRightScoreMap.keySet()){
			decodedActivityPairSplit = decodedActivityPair.split(" @ ");
			if(activityCharMap.containsKey(decodedActivityPairSplit[0].trim()) && activityCharMap.containsKey(decodedActivityPairSplit[1].trim())){
				activityPair = activityCharMap.get(decodedActivityPairSplit[0].trim())+" @ "+activityCharMap.get(decodedActivityPairSplit[1].trim());
				indelLeftGivenRightScoreMap.put(activityPair, decodedIndelLeftGivenRightScoreMap.get(decodedActivityPair));
			}else{
				throw new ActivityNotFoundException(decodedActivityPair);
			}
		}
	}
	
	public void loadSubstitutionScores(String substitutionScoreAbsolutePathName, Map<String, String> activityCharMap) throws ActivityNotFoundException{
		FileIO io = new FileIO();
		String delim = "\\^";
		String substitutionScoreInputDir = new File(substitutionScoreAbsolutePathName).getParent();
		String substitutionScoreFileName = new File(substitutionScoreAbsolutePathName).getName();
		
		Map<String, Integer> decodedSubstitutionScoreMap = io.readMapStringIntegerFromFile(substitutionScoreInputDir, substitutionScoreFileName, delim);
		String[] decodedActivityPairSplit;
		String activityPair;
		for(String decodedActivityPair : decodedSubstitutionScoreMap.keySet()){
			decodedActivityPairSplit = decodedActivityPair.split(" @ ");
			if(activityCharMap.containsKey(decodedActivityPairSplit[0].trim()) && activityCharMap.containsKey(decodedActivityPairSplit[1].trim())){
				activityPair = activityCharMap.get(decodedActivityPairSplit[0].trim())+" @ "+activityCharMap.get(decodedActivityPairSplit[1].trim());
				substitutionScoreMap.put(activityPair, decodedSubstitutionScoreMap.get(decodedActivityPair));
			}else{
				throw new ActivityNotFoundException(decodedActivityPair);
			}
		}
	}
	
	public void incrementLikeSubstitutionScore(int incrementLikeSubstitutionScoreValue){
		String[] activityPairSplit;
		int substitutionScore;
		for(String activityPair : substitutionScoreMap.keySet()){
			activityPairSplit = activityPair.split(" @ ");
			if(activityPairSplit[0].equals(activityPairSplit[1])){
				substitutionScore = substitutionScoreMap.get(activityPair);
				substitutionScore += incrementLikeSubstitutionScoreValue;
				substitutionScoreMap.put(activityPair, substitutionScore);
			}
		}
	}
	
	public void decrementIndelScore(int decrementIndelScoreValue){
		int indelScore;
		for(String activityPair : indelRightGivenLeftScoreMap.keySet()){
			indelScore = indelRightGivenLeftScoreMap.get(activityPair);
			indelScore -= decrementIndelScoreValue;
			indelRightGivenLeftScoreMap.put(activityPair, indelScore);
		}
		
		for(String activityPair : indelLeftGivenRightScoreMap.keySet()){
			indelScore = indelLeftGivenRightScoreMap.get(activityPair);
			indelScore -= decrementIndelScoreValue;
			indelLeftGivenRightScoreMap.put(activityPair, indelScore);
		}
	}

	public void scaleIndelScore(float indelScoreScaleValue){
		int indelScore;
		for(String activityPair : indelLeftGivenRightScoreMap.keySet()){
			indelScore = indelLeftGivenRightScoreMap.get(activityPair);
			indelScore *= indelScoreScaleValue;
			indelLeftGivenRightScoreMap.put(activityPair, new Double(Math.ceil(indelScore)).intValue());
		}
		
		for(String activityPair : indelRightGivenLeftScoreMap.keySet()){
			indelScore = indelRightGivenLeftScoreMap.get(activityPair);
			indelScore *= indelScoreScaleValue;
			indelRightGivenLeftScoreMap.put(activityPair, new Double(Math.ceil(indelScore)).intValue());
		}
	}
	
	public Map<String, Integer> getSubstitutionScoreMap() {
		return substitutionScoreMap;
	}

	public Map<String, Integer> getIndelRightGivenLeftScoreMap() {
		return indelRightGivenLeftScoreMap;
	}

	public Map<String, Integer> getIndelLeftGivenRightScoreMap() {
		return indelLeftGivenRightScoreMap;
	}
}
