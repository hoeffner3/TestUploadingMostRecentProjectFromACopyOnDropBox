package org.processmining.plugins.tracealignmentwithguidetree.msa;

import java.util.HashMap;
import java.util.Map;

import org.processmining.plugins.tracealignmentwithguidetree.enumtypes.TraceBack;

public class SequenceAlignment {
	int encodingLength;
	Map<String, Integer> substitutionScoreMap;
	Map<String, Integer> indelRightGivenLeftScoreMap;
	Map<String, Integer> indelLeftGivenRightScoreMap;
	
	TraceBack[][] T;
	int[][] M;
	
	int minSubstitutionScore, gapOpenCost;
	float lengthRatioThreshold;
	String dash = "-";
	
	public SequenceAlignment(int encodingLength, Map<String, Integer> substitutionScoreMap, Map<String, Integer> indelRightGivenLeftScoreMap, Map<String, Integer> indelLeftGivenRightScoreMap){
		this.encodingLength = encodingLength;
		this.substitutionScoreMap = substitutionScoreMap;
		this.indelLeftGivenRightScoreMap = indelLeftGivenRightScoreMap;
		this.indelRightGivenLeftScoreMap = indelRightGivenLeftScoreMap;
		
		/*
		 * Get the minimum score of the substitution matrix; It would be helpful
		 * to assign the gap-open cost for undefined indel symbolPairs to be <=
		 * minimum score
		 */
		minSubstitutionScore = 0;
		for (String substitutionSymbolPair : substitutionScoreMap.keySet()) {
			if (substitutionScoreMap.get(substitutionSymbolPair) < minSubstitutionScore) {
				minSubstitutionScore = substitutionScoreMap.get(substitutionSymbolPair);
			}
		}
		
		if (gapOpenCost == Integer.MIN_VALUE) {
			gapOpenCost = minSubstitutionScore - 1;
		}

		for (int i = 1; i < encodingLength; i++) {
			dash += "-";
		}
		
		lengthRatioThreshold = 1;
	}
	
	public String[] getPairWiseGlobalAlignment(String sequence1, String sequence2) {
		if(sequence1.length() >= sequence2.length())
			return getPairWiseGlobalAlign(sequence1, sequence2);
		else
			return getPairWiseGlobalAlign(sequence2, sequence1);
	}
	
	public String[] getPairWiseGlobalAlignment(String[] profileAlignment, String sequence){
		return getProfileSequenceGlobalAlign(profileAlignment, sequence);
	}
	
	public String[] getPairWiseGlobalAlignment(String[] profileAlignment1, String[] profileAlignment2){
		return getProfileProfileGlobalAlign(profileAlignment1, profileAlignment2);
	}
	
	private void profileSequenceAlign(String[][] profileAlignmentArray, String[] sequence) {
		String dash = "-";
		for (int i = 1; i < encodingLength; i++) {
			dash += "-";
		}

		int noProfileSequences = profileAlignmentArray.length;
		int profileAlignmentLength = profileAlignmentArray[0].length;

		int sequenceLength = sequence.length;

		float lengthRatio;
		if (sequenceLength > profileAlignmentLength) {
			lengthRatio = (float) sequenceLength / profileAlignmentLength;
		} else {
			lengthRatio = (float) profileAlignmentLength / sequenceLength;
			//		System.out.println("Length Ratio: "+lengthRatio);
		}

		Map<String, Integer> symbolCountMap = new HashMap<String, Integer>();
		String indelSymbolPairSequence, indelSymbolPairProfile, substitutionSymbolPair;
		Map<String, Integer> symbolPairProfileCountMap = new HashMap<String, Integer>();
		int indelScoreSequence, indelScoreProfile, pairCount, substitutionScore, symbolCount, maxScore;

		M = new int[profileAlignmentLength + 1][sequenceLength + 1];
		T = new TraceBack[profileAlignmentLength + 1][sequenceLength + 1];

		M[0][0] = 0;
		T[0][0] = TraceBack.NONE;

		/*
		 * Fill the first row; the first symbol in sequence2 can always be
		 * inserted with no cost
		 */

		M[0][1] = 0;
		T[0][1] = TraceBack.LEFT;
		for (int j = 1; j < sequenceLength; j++) {
			indelSymbolPairSequence = sequence[j - 1] + " @ " + sequence[j];
			if (indelRightGivenLeftScoreMap.containsKey(indelSymbolPairSequence)) {
				indelScoreSequence = indelRightGivenLeftScoreMap.get(indelSymbolPairSequence);
			} else {
				indelScoreSequence = gapOpenCost;
			}
			M[0][j + 1] = M[0][j] + indelScoreSequence;
			T[0][j + 1] = TraceBack.LEFT;
		}

		M[1][0] = 0;
		T[1][0] = TraceBack.UP;
		for (int i = 1; i < profileAlignmentLength; i++) {
			symbolPairProfileCountMap.clear();
			for (int p = 0; p < noProfileSequences; p++) {
				indelSymbolPairProfile = profileAlignmentArray[p][i - 1] + " @ " + profileAlignmentArray[p][i];
				if (!indelSymbolPairProfile.contains(dash)) {
					if (symbolPairProfileCountMap.containsKey(indelSymbolPairProfile)) {
						pairCount = symbolPairProfileCountMap.get(indelSymbolPairProfile) + 1;
					} else {
						pairCount = 1;
					}
					symbolPairProfileCountMap.put(indelSymbolPairProfile, pairCount);
				}
			}
			indelScoreProfile = 0;
			for (String indelSymbolPair : symbolPairProfileCountMap.keySet()) {
				if (indelRightGivenLeftScoreMap.containsKey(indelSymbolPair)) {
					indelScoreProfile += symbolPairProfileCountMap.get(indelSymbolPair)
							* indelRightGivenLeftScoreMap.get(indelSymbolPair);
				} else {
					indelScoreProfile += symbolPairProfileCountMap.get(indelSymbolPair) * gapOpenCost;
				}
			}

			M[i + 1][0] = M[i][0] + indelScoreProfile;
			T[i + 1][0] = TraceBack.UP;
		}

		for (int i = 0; i < profileAlignmentLength; i++) {
			/*
			 * Get the indel Score for sequence1
			 */
			indelScoreProfile = noProfileSequences * gapOpenCost;
			if (i > 0) {
				symbolPairProfileCountMap.clear();
				for (int p = 0; p < noProfileSequences; p++) {
					indelSymbolPairProfile = profileAlignmentArray[p][i - 1] + " @ " + profileAlignmentArray[p][i];
					if (!indelSymbolPairProfile.contains(dash)) {
						if (symbolPairProfileCountMap.containsKey(indelSymbolPairProfile)) {
							pairCount = symbolPairProfileCountMap.get(indelSymbolPairProfile) + 1;
						} else {
							pairCount = 1;
						}
						symbolPairProfileCountMap.put(indelSymbolPairProfile, pairCount);
					}
				}
				indelScoreProfile = 0;
				for (String indelSymbolPair : symbolPairProfileCountMap.keySet()) {
					if (indelRightGivenLeftScoreMap.containsKey(indelSymbolPair)) {
						indelScoreProfile += symbolPairProfileCountMap.get(indelSymbolPair)
								* indelRightGivenLeftScoreMap.get(indelSymbolPair);
					} else {
						indelScoreProfile += symbolPairProfileCountMap.get(indelSymbolPair) * gapOpenCost;
					}
				}
			} else {
				indelScoreProfile = 0;
			}

			for (int j = 0; j < sequenceLength; j++) {
				/*
				 * Get the indel Score for sequence2
				 */
				indelScoreSequence = gapOpenCost;
				if (j > 0) {
					indelSymbolPairSequence = sequence[j - 1] + " @ " + sequence[j];
					if (indelRightGivenLeftScoreMap.containsKey(indelSymbolPairSequence)) {
						indelScoreSequence = indelRightGivenLeftScoreMap.get(indelSymbolPairSequence);
					}
				} else {
					indelScoreSequence = 0;
				}

				/*
				 * Get the substitution score
				 */
				substitutionScore = 0;

				symbolPairProfileCountMap.clear();
				symbolCountMap.clear();
				for (int p = 0; p < noProfileSequences; p++) {
					if (!profileAlignmentArray[p][i].equals(dash)) {
						if (symbolCountMap.containsKey(profileAlignmentArray[p][i])) {
							symbolCount = symbolCountMap.get(profileAlignmentArray[p][i]) + 1;
						} else {
							symbolCount = 1;
						}
						symbolCountMap.put(profileAlignmentArray[p][i], symbolCount);
					}
				}

				for (String profileSymbol : symbolCountMap.keySet()) {
					substitutionSymbolPair = profileSymbol + " @ " + sequence[j];
					if (!substitutionSymbolPair.contains(dash)) {
						if (substitutionScoreMap.containsKey(substitutionSymbolPair)) {
							substitutionScore += symbolCountMap.get(profileSymbol)
									* substitutionScoreMap.get(substitutionSymbolPair);
						} else {
							substitutionSymbolPair = sequence[j] + " @ " + profileSymbol;
							if (substitutionScoreMap.containsKey(substitutionSymbolPair)) {
								substitutionScore += symbolCountMap.get(profileSymbol)
										* substitutionScoreMap.get(substitutionSymbolPair);
							} else {
								substitutionScore += symbolCountMap.get(profileSymbol) * minSubstitutionScore;
							}
						}
					}
				}

				maxScore = Math.max(M[i][j] + substitutionScore, Math.max(M[i][j + 1] + indelScoreProfile, M[i + 1][j]
						+ indelScoreSequence));

				if (lengthRatio < lengthRatioThreshold) {
					if (maxScore == M[i][j] + substitutionScore) {
						M[i + 1][j + 1] = M[i][j] + substitutionScore;
						T[i + 1][j + 1] = TraceBack.DIAGONAL;
					} else if (maxScore == M[i][j + 1] + indelScoreProfile) {
						M[i + 1][j + 1] = M[i][j + 1] + indelScoreProfile;
						T[i + 1][j + 1] = TraceBack.UP;
					} else if (maxScore == M[i + 1][j] + indelScoreSequence) {
						M[i + 1][j + 1] = M[i + 1][j] + indelScoreSequence;
						T[i + 1][j + 1] = TraceBack.LEFT;
					}
				} else {
					/*
					 * The profile and the sequence vary in length a lot; It can
					 * be because of loops or too may repeats; Problems can
					 * arise when a small number of symbols at the end are
					 * consistent (the problem being the sequences would be
					 * biased to get aligned at the end). In such cases, try to
					 * prefer a indel at the end in case two scores are equal
					 */
					if ((maxScore == M[i][j] + substitutionScore)
							&& ((maxScore != M[i][j + 1] + indelScoreProfile) && (maxScore != M[i + 1][j]
									+ indelScoreSequence))) {
						//						System.out.println(maxScore+"@"+(M[i][j]+substitutionScore)+","+(M[i][j+1]+indelScoreSeq1));
						M[i + 1][j + 1] = M[i][j] + substitutionScore;
						T[i + 1][j + 1] = TraceBack.DIAGONAL;
					} else if (maxScore == M[i][j + 1] + indelScoreProfile) {
						M[i + 1][j + 1] = M[i][j + 1] + indelScoreProfile;
						T[i + 1][j + 1] = TraceBack.UP;
					} else if (maxScore == M[i + 1][j] + indelScoreSequence) {
						M[i + 1][j + 1] = M[i + 1][j] + indelScoreSequence;
						T[i + 1][j + 1] = TraceBack.LEFT;
					}
				}
			}
		}

		//		ArrayMatrixFileIO matrixIO = new ArrayMatrixFileIO();
		//		matrixIO.writeToFile("D:\\JC\\JCTemp", "M.txt", M);
		//		matrixIO.writeToFile("D:\\JC\\JCTemp", "T.txt", T);
	}

	/**
	 * This method aligns two profiles; The profiles are provided as a two
	 * dimensional matrix where the element i,j in the matrix corresponds to the
	 * column j of sequence i in the alignment
	 * 
	 * It is assumed that the length of alignment1 is greater than the length of
	 * alignment2; The calling method is supposed to take care of this
	 * 
	 */
	private void profileProfileAlign(String[][] profileAlignmentArray1, String[][] profileAlignmentArray2) {
		String dash = "-";
		for (int i = 1; i < encodingLength; i++) {
			dash += "-";
		}

		/*
		 * Get the number of sequences in profile1 and profile2 Also, get the
		 * length of the alignment of the two profiles
		 */
		int noSequencesProfile1 = profileAlignmentArray1.length;
		int profileAlignment1Length = profileAlignmentArray1[0].length;

		int noSequencesProfile2 = profileAlignmentArray2.length;
		int profileAlignment2Length = profileAlignmentArray2[0].length;

		/*
		 * Compute the ratio of the lengths of the two alignments; This will be
		 * used to define the traceback; The traceback strategy will be biased
		 * towards incorporating indels in the longer alignment in case of
		 * conflict (the score being equal)
		 */
		float lengthRatio;
		if (profileAlignment1Length > profileAlignment2Length) {
			lengthRatio = (float) profileAlignment1Length / profileAlignment2Length;
		} else {
			lengthRatio = (float) profileAlignment2Length / profileAlignment1Length;
			//		System.out.println("Length Ratio: "+lengthRatio);
		}

		/*
		 * The profile vector; Ideally it would have been good if we define a
		 * profile vector; But in most cases, the cardinality of the activity
		 * set is quite large O(100) where as the number of activities at a
		 * position (column) will be quite small; Hence it would be waste of
		 * space to define a vector; A map would be space efficient
		 */
		Map<String, Integer> symbolCountProfile1Map = new HashMap<String, Integer>();
		Map<String, Integer> symbolCountProfile2Map = new HashMap<String, Integer>();

		String indelSymbolPairProfile1, indelSymbolPairProfile2, substitutionSymbolPair;
		Map<String, Integer> symbolPairProfileCountMap = new HashMap<String, Integer>();
		int indelScoreProfile1, indelScoreProfile2, pairCount, substitutionScore, symbolCount, maxScore;

		/*
		 * the score and the traceback matrices
		 */
		M = new int[profileAlignment1Length + 1][profileAlignment2Length + 1];
		T = new TraceBack[profileAlignment1Length + 1][profileAlignment2Length + 1];

		/*
		 * Initialize the score and the traceback matrices
		 */
		M[0][0] = 0;
		T[0][0] = TraceBack.NONE;

		/*
		 * Fill the first row; the first symbol in sequence2 can always be
		 * inserted with no cost
		 */

		M[0][1] = 0;
		T[0][1] = TraceBack.LEFT;
		for (int j = 1; j < profileAlignment2Length; j++) {
			symbolPairProfileCountMap.clear();
			for (int q = 0; q < noSequencesProfile2; q++) {
				indelSymbolPairProfile2 = profileAlignmentArray2[q][j - 1] + " @ " + profileAlignmentArray2[q][j];
				if (!indelSymbolPairProfile2.contains(dash)) {
					if (symbolPairProfileCountMap.containsKey(indelSymbolPairProfile2)) {
						pairCount = symbolPairProfileCountMap.get(indelSymbolPairProfile2) + 1;
					} else {
						pairCount = 1;
					}
					symbolPairProfileCountMap.put(indelSymbolPairProfile2, pairCount);
				}
			}
			indelScoreProfile2 = 0;
			for (String indelSymbolPair : symbolPairProfileCountMap.keySet()) {
				if (indelRightGivenLeftScoreMap.containsKey(indelSymbolPair)) {
					indelScoreProfile2 += symbolPairProfileCountMap.get(indelSymbolPair)
							* indelRightGivenLeftScoreMap.get(indelSymbolPair);
				} else {
					indelScoreProfile2 += symbolPairProfileCountMap.get(indelSymbolPair) * gapOpenCost;
				}
			}
			M[0][j + 1] = M[0][j] + indelScoreProfile2;
			T[0][j + 1] = TraceBack.LEFT;
		}

		/*
		 * Fill the first column;
		 */
		M[1][0] = 0;
		T[1][0] = TraceBack.UP;
		for (int i = 1; i < profileAlignment1Length; i++) {
			symbolPairProfileCountMap.clear();
			for (int p = 0; p < noSequencesProfile1; p++) {
				indelSymbolPairProfile1 = profileAlignmentArray1[p][i - 1] + " @ " + profileAlignmentArray1[p][i];
				if (!indelSymbolPairProfile1.contains(dash)) {
					if (symbolPairProfileCountMap.containsKey(indelSymbolPairProfile1)) {
						pairCount = symbolPairProfileCountMap.get(indelSymbolPairProfile1) + 1;
					} else {
						pairCount = 1;
					}
					symbolPairProfileCountMap.put(indelSymbolPairProfile1, pairCount);
				}
			}
			indelScoreProfile1 = 0;
			for (String indelSymbolPair : symbolPairProfileCountMap.keySet()) {
				if (indelRightGivenLeftScoreMap.containsKey(indelSymbolPair)) {
					indelScoreProfile1 += symbolPairProfileCountMap.get(indelSymbolPair)
							* indelRightGivenLeftScoreMap.get(indelSymbolPair);
				} else {
					indelScoreProfile1 += symbolPairProfileCountMap.get(indelSymbolPair) * gapOpenCost;
				}
			}

			M[i + 1][0] = M[i][0] + indelScoreProfile1;
			T[i + 1][0] = TraceBack.UP;
		}

		/*
		 * Fill the rest of the score matrix using dynamic programming
		 */
		for (int i = 0; i < profileAlignment1Length; i++) {
			/*
			 * Get the indel Score for sequence1
			 */
			//			indelScoreProfile1 = noSequencesProfile1*gapOpenCost;
			if (i > 0) {
				symbolPairProfileCountMap.clear();
				for (int p = 0; p < noSequencesProfile1; p++) {
					indelSymbolPairProfile1 = profileAlignmentArray1[p][i - 1] + " @ " + profileAlignmentArray1[p][i];
					if (!indelSymbolPairProfile1.contains(dash)) {
						if (symbolPairProfileCountMap.containsKey(indelSymbolPairProfile1)) {
							pairCount = symbolPairProfileCountMap.get(indelSymbolPairProfile1) + 1;
						} else {
							pairCount = 1;
						}
						symbolPairProfileCountMap.put(indelSymbolPairProfile1, pairCount);
					}
				}
				indelScoreProfile1 = 0;
				for (String indelSymbolPair : symbolPairProfileCountMap.keySet()) {
					if (indelRightGivenLeftScoreMap.containsKey(indelSymbolPair)) {
						indelScoreProfile1 += symbolPairProfileCountMap.get(indelSymbolPair)
								* indelRightGivenLeftScoreMap.get(indelSymbolPair);
					} else {
						indelScoreProfile1 += symbolPairProfileCountMap.get(indelSymbolPair) * gapOpenCost;
					}
				}
			} else {
				indelScoreProfile1 = 0;
			}

			for (int j = 0; j < profileAlignment2Length; j++) {
				/*
				 * Get the indel Score for sequence2
				 */
				//				indelScoreProfile2 = noSequencesProfile2*gapOpenCost;
				if (j > 0) {
					symbolPairProfileCountMap.clear();
					for (int q = 0; q < noSequencesProfile2; q++) {
						indelSymbolPairProfile2 = profileAlignmentArray2[q][j - 1] + " @ "
								+ profileAlignmentArray2[q][j];
						if (!indelSymbolPairProfile2.contains(dash)) {
							if (symbolPairProfileCountMap.containsKey(indelSymbolPairProfile2)) {
								pairCount = symbolPairProfileCountMap.get(indelSymbolPairProfile2) + 1;
							} else {
								pairCount = 1;
							}
							symbolPairProfileCountMap.put(indelSymbolPairProfile2, pairCount);
						}
					}
					indelScoreProfile2 = 0;
					for (String indelSymbolPair : symbolPairProfileCountMap.keySet()) {
						if (indelRightGivenLeftScoreMap.containsKey(indelSymbolPair)) {
							indelScoreProfile2 += symbolPairProfileCountMap.get(indelSymbolPair)
									* indelRightGivenLeftScoreMap.get(indelSymbolPair);
						} else {
							indelScoreProfile2 += symbolPairProfileCountMap.get(indelSymbolPair) * gapOpenCost;
						}
					}
				} else {
					indelScoreProfile2 = 0;
				}

				/*
				 * Get the substitution score
				 */
				substitutionScore = 0;

				symbolPairProfileCountMap.clear();
				symbolCountProfile1Map.clear();
				for (int p = 0; p < noSequencesProfile1; p++) {
					if (!profileAlignmentArray1[p][i].equals(dash)) {
						if (symbolCountProfile1Map.containsKey(profileAlignmentArray1[p][i])) {
							symbolCount = symbolCountProfile1Map.get(profileAlignmentArray1[p][i]) + 1;
						} else {
							symbolCount = 1;
						}
						symbolCountProfile1Map.put(profileAlignmentArray1[p][i], symbolCount);
					}
				}

				symbolCountProfile2Map.clear();
				for (int q = 0; q < noSequencesProfile2; q++) {
					if (!profileAlignmentArray2[q][j].equals(dash)) {
						if (symbolCountProfile2Map.containsKey(profileAlignmentArray2[q][j])) {
							symbolCount = symbolCountProfile2Map.get(profileAlignmentArray2[q][j]) + 1;
						} else {
							symbolCount = 1;
						}
						symbolCountProfile2Map.put(profileAlignmentArray2[q][j], symbolCount);
					}
				}

				for (String profileSymbol1 : symbolCountProfile1Map.keySet()) {
					for (String profileSymbol2 : symbolCountProfile2Map.keySet()) {
						substitutionSymbolPair = profileSymbol1 + " @ " + profileSymbol2;
						if (!substitutionSymbolPair.contains(dash)) {
							if (substitutionScoreMap.containsKey(substitutionSymbolPair)) {
								substitutionScore += symbolCountProfile1Map.get(profileSymbol1)
										* symbolCountProfile2Map.get(profileSymbol2)
										* substitutionScoreMap.get(substitutionSymbolPair);
							} else {
								substitutionSymbolPair = profileSymbol2 + " @ " + profileSymbol1;
								if (substitutionScoreMap.containsKey(substitutionSymbolPair)) {
									substitutionScore += symbolCountProfile1Map.get(profileSymbol1)
											* symbolCountProfile2Map.get(profileSymbol2)
											* substitutionScoreMap.get(substitutionSymbolPair);
								} else {
									substitutionScore += symbolCountProfile1Map.get(profileSymbol1)
											* symbolCountProfile2Map.get(profileSymbol2) * minSubstitutionScore;
								}
							}
						}
					}
				}

				maxScore = Math.max(M[i][j] + substitutionScore, Math.max(M[i][j + 1] + indelScoreProfile1, M[i + 1][j]
						+ indelScoreProfile2));

				if (lengthRatio <= lengthRatioThreshold) {
					if (maxScore == M[i][j] + substitutionScore) {
						M[i + 1][j + 1] = M[i][j] + substitutionScore;
						T[i + 1][j + 1] = TraceBack.DIAGONAL;
					} 
					else if (maxScore == M[i][j + 1] + indelScoreProfile1) {
						M[i + 1][j + 1] = M[i][j + 1] + indelScoreProfile1;
						T[i + 1][j + 1] = TraceBack.UP;
					} else if (maxScore == M[i + 1][j] + indelScoreProfile2) {
						M[i + 1][j + 1] = M[i + 1][j] + indelScoreProfile2;
						T[i + 1][j + 1] = TraceBack.LEFT;
					} 
				} else {
					/*
					 * The profile and the sequence vary in length a lot; It can
					 * be because of loops or too may repeats; Problems can
					 * arise when a small number of symbols at the end are
					 * consistent (the problem being the sequences would be
					 * biased to get aligned at the end). In such cases, try to
					 * prefer a indel at the end in case two scores are equal
					 */
					if ((maxScore == M[i][j] + substitutionScore)
							&& ((maxScore != M[i][j + 1] + indelScoreProfile1) && (maxScore != M[i + 1][j]
									+ indelScoreProfile2))) {
						//						System.out.println(maxScore+"@"+(M[i][j]+substitutionScore)+","+(M[i][j+1]+indelScoreSeq1));
						M[i + 1][j + 1] = M[i][j] + substitutionScore;
						T[i + 1][j + 1] = TraceBack.DIAGONAL;
					}else if (maxScore == M[i + 1][j] + indelScoreProfile2) {
						M[i + 1][j + 1] = M[i + 1][j] + indelScoreProfile2;
						T[i + 1][j + 1] = TraceBack.LEFT;
					}else if (maxScore == M[i][j + 1] + indelScoreProfile1) {
						M[i + 1][j + 1] = M[i][j + 1] + indelScoreProfile1;
						T[i + 1][j + 1] = TraceBack.UP;
					}
				}
			}
		}

		//		ArrayMatrixFileIO matrixIO = new ArrayMatrixFileIO();
		//		matrixIO.writeToFile("D:\\JC\\JCTemp", "M.txt", M);
		//		matrixIO.writeToFile("D:\\JC\\JCTemp", "T.txt", T);
	}
	
	/**
	 * Global Pairwise Alignment; sequence1 is required to be longer than sequence2 
	 * @param sequence1
	 * @param sequence2
	 * @return
	 */
	private String[] getPairWiseGlobalAlign(String sequence1, String sequence2) {
		String[] alignment = null;
	
		/*
		 * If one sequence is fully contained (subsumed) in the other, it is
		 * wise not to do the alignment at this stage. Just return the longer
		 * sequence; In the final alignment, one can take care of all such
		 * subsumed traces
		 */
		StringBuilder strBuilder = new StringBuilder();
		if (sequence1.contains(sequence2)) {
			int startIndex = (sequence1.indexOf(sequence2) - 1) / encodingLength;
			for (int i = 0; i < startIndex; i++) {
				strBuilder.append(dash);
			}
			strBuilder.append(sequence2);
			int remainingLength = (sequence1.length() - strBuilder.length()) / encodingLength;
			for (int i = 0; i < remainingLength; i++) {
				strBuilder.append(dash);
			}
	
			alignment = new String[] { sequence1, strBuilder.toString() };
			//			alignment = new String[]{sequence1};
			return alignment;
		}else{
			/*
			 * Neither of the sequences is subsumed in the other, we need to do
			 * an alignment; It would be computationally less intensive if we
			 * convert the sequence into a String[]
			 */
			
			int sequence1Length = sequence1.length() / encodingLength;
			int sequence2Length = sequence2.length() / encodingLength;
			String[] seq1, seq2;
			
			seq1 = new String[sequence1Length];
			for (int i = 0; i < sequence1Length; i++) {
				seq1[i] = sequence1.substring(i * encodingLength, (i + 1) * encodingLength);
			}

			seq2 = new String[sequence2Length];
			for (int i = 0; i < sequence2Length; i++) {
				seq2[i] = sequence2.substring(i * encodingLength, (i + 1) * encodingLength);
			}
			
			pairWiseAlign(seq1, seq2);
			
			/*
			 * We no longer need M; set to null
			 */
			M = null;

			StringBuilder strBuilder1 = new StringBuilder();
			StringBuilder strBuilder2 = new StringBuilder();
			int i = seq1.length;
			int j = seq2.length;
			
			while ((i != 0) || (j != 0)) {
				if (T[i][j] == TraceBack.DIAGONAL) {
					strBuilder1.append(reverse(seq1[i - 1]));
					strBuilder2.append(reverse(seq2[j - 1]));
					i--;
					j--;
				} else if (T[i][j] == TraceBack.UP) {
					strBuilder1.append(reverse(seq1[i - 1]));
					strBuilder2.append(dash);
					i--;
				} else if (T[i][j] == TraceBack.LEFT) {
					strBuilder1.append(dash);
					strBuilder2.append(reverse(seq2[j - 1]));
					j--;
				}
			}

			/*
			 * We no longer need T; set to null
			 */
			T = null;

			alignment = new String[2];
			alignment[0] = strBuilder1.reverse().toString();
			alignment[1] = strBuilder2.reverse().toString();
			
			return alignment;
		}
	}
	
	public String[] getProfileSequenceGlobalAlign(String[] profileAlignment, String sequence) {
		String[] alignment;

		int noProfileSequences = profileAlignment.length;
		int profileAlignmentLength = profileAlignment[0].length() / encodingLength;

		/*
		 * Convert the profileAlignment into an array;
		 */
		String[][] profileAlignmentArray = new String[noProfileSequences][profileAlignmentLength];
		for (int i = 0; i < noProfileSequences; i++) {
			for (int j = 0; j < profileAlignmentLength; j++) {
				profileAlignmentArray[i][j] = profileAlignment[i].substring(j * encodingLength, (j + 1)
						* encodingLength);
			}
		}

		int sequenceLength = sequence.length() / encodingLength;
		String[] seq = new String[sequenceLength];
		for (int i = 0; i < sequenceLength; i++) {
			seq[i] = sequence.substring(i * encodingLength, (i + 1) * encodingLength);
		}

		profileSequenceAlign(profileAlignmentArray, seq);

		/*
		 * We no longer need M; set to null
		 */
		M = null;

		StringBuilder[] strBuilder1 = new StringBuilder[noProfileSequences];
		StringBuilder strBuilder2 = new StringBuilder();

		for (int p = 0; p < noProfileSequences; p++) {
			strBuilder1[p] = new StringBuilder();
		}

		int i = profileAlignmentLength;
		int j = sequenceLength;
		//		System.out.println(i+"@"+j);
		while ((i != 0) || (j != 0)) {
			if (T[i][j] == TraceBack.DIAGONAL) {
				for (int p = 0; p < noProfileSequences; p++) {
					strBuilder1[p].append(reverse(profileAlignmentArray[p][i - 1]));
				}
				strBuilder2.append(reverse(seq[j - 1]));
				i--;
				j--;
			} else if (T[i][j] == TraceBack.UP) {
				for (int p = 0; p < noProfileSequences; p++) {
					strBuilder1[p].append(reverse(profileAlignmentArray[p][i - 1]));
				}
				strBuilder2.append(dash);
				i--;
			} else if (T[i][j] == TraceBack.LEFT) {
				for (int p = 0; p < noProfileSequences; p++) {
					strBuilder1[p].append(dash);
				}
				strBuilder2.append(reverse(seq[j - 1]));
				j--;
			}
		}

		/*
		 * We no longer need T; set to null
		 */
		T = null;

		alignment = new String[noProfileSequences + 1];
		for (int p = 0; p < noProfileSequences; p++) {
			alignment[p] = strBuilder1[p].reverse().toString();
		}
		alignment[noProfileSequences] = strBuilder2.reverse().toString();

		return alignment;
	}

	public String[] getProfileProfileGlobalAlign(String[] profileAlignment1, String[] profileAlignment2) {
		String[] alignment;

		int noSequencesProfile1 = profileAlignment1.length;
		int profileAlignment1Length = profileAlignment1[0].length() / encodingLength;

		int noSequencesProfile2 = profileAlignment2.length;
		int profileAlignment2Length = profileAlignment2[0].length() / encodingLength;

		/*
		 * Convert the profileAlignment into an array; Keep the longer length
		 * array as profileAlignmentArray1
		 */
		String[][] profileAlignmentArray1, profileAlignmentArray2;
		if (profileAlignment1Length > profileAlignment2Length) {
			profileAlignmentArray1 = new String[noSequencesProfile1][profileAlignment1Length];
			for (int p = 0; p < noSequencesProfile1; p++) {
				for (int j = 0; j < profileAlignment1Length; j++) {
					profileAlignmentArray1[p][j] = profileAlignment1[p].substring(j * encodingLength, (j + 1)
							* encodingLength);
				}
			}

			profileAlignmentArray2 = new String[noSequencesProfile2][profileAlignment2Length];
			for (int q = 0; q < noSequencesProfile2; q++) {
				for (int j = 0; j < profileAlignment2Length; j++) {
					profileAlignmentArray2[q][j] = profileAlignment2[q].substring(j * encodingLength, (j + 1)
							* encodingLength);
				}
			}
		} else {
			profileAlignmentArray1 = new String[noSequencesProfile2][profileAlignment2Length];
			for (int q = 0; q < noSequencesProfile2; q++) {
				for (int j = 0; j < profileAlignment2Length; j++) {
					profileAlignmentArray1[q][j] = profileAlignment2[q].substring(j * encodingLength, (j + 1)
							* encodingLength);
				}
			}

			profileAlignmentArray2 = new String[noSequencesProfile1][profileAlignment1Length];
			for (int p = 0; p < noSequencesProfile1; p++) {
				for (int j = 0; j < profileAlignment1Length; j++) {
					profileAlignmentArray2[p][j] = profileAlignment1[p].substring(j * encodingLength, (j + 1)
							* encodingLength);
				}
			}

			/*
			 * Reset the number of profiles and profileAlignment Lengths
			 */
			noSequencesProfile1 = profileAlignmentArray1.length;
			noSequencesProfile2 = profileAlignmentArray2.length;
			profileAlignment1Length = profileAlignmentArray1[0].length;
			profileAlignment2Length = profileAlignmentArray2[0].length;
		}

		/*
		 * if(profileAlignment1Length >= profileAlignment2Length)
		 * profileProfileAlign(profileAlignmentArray1, profileAlignmentArray2);
		 * else profileProfileAlign(profileAlignmentArray2,
		 * profileAlignmentArray1);
		 */

		profileProfileAlign(profileAlignmentArray1, profileAlignmentArray2);

		/*
		 * We no longer need M; set to null
		 */
		M = null;

		StringBuilder[] strBuilder1 = new StringBuilder[noSequencesProfile1];
		StringBuilder[] strBuilder2 = new StringBuilder[noSequencesProfile2];

		for (int p = 0; p < noSequencesProfile1; p++) {
			strBuilder1[p] = new StringBuilder();
		}

		for (int q = 0; q < noSequencesProfile2; q++) {
			strBuilder2[q] = new StringBuilder();
		}

		int i = profileAlignment1Length;
		int j = profileAlignment2Length;
		//		System.out.println(i+"@"+j);
		while ((i != 0) || (j != 0)) {
			if (T[i][j] == TraceBack.DIAGONAL) {
				for (int p = 0; p < noSequencesProfile1; p++) {
					strBuilder1[p].append(reverse(profileAlignmentArray1[p][i - 1]));
				}
				for (int q = 0; q < noSequencesProfile2; q++) {
					strBuilder2[q].append(reverse(profileAlignmentArray2[q][j - 1]));
				}

				i--;
				j--;
			} else if (T[i][j] == TraceBack.UP) {
				for (int p = 0; p < noSequencesProfile1; p++) {
					strBuilder1[p].append(reverse(profileAlignmentArray1[p][i - 1]));
				}
				for (int q = 0; q < noSequencesProfile2; q++) {
					strBuilder2[q].append(dash);
				}
				i--;
			} else if (T[i][j] == TraceBack.LEFT) {
				for (int p = 0; p < noSequencesProfile1; p++) {
					strBuilder1[p].append(dash);
				}

				for (int q = 0; q < noSequencesProfile2; q++) {
					strBuilder2[q].append(reverse(profileAlignmentArray2[q][j - 1]));
				}
				j--;
			}
		}

		/*
		 * We no longer need T; set to null
		 */
		T = null;

		alignment = new String[noSequencesProfile1 + noSequencesProfile2];
		for (int p = 0; p < noSequencesProfile1; p++) {
			alignment[p] = strBuilder1[p].reverse().toString();
		}
		for (int q = 0; q < noSequencesProfile2; q++) {
			alignment[noSequencesProfile1 + q] = strBuilder2[q].reverse().toString();
		}

		return alignment;
	}
	
	public String[] getPairWiseSemiGlobalAlignment(String sequence1, String sequence2) {
		if(sequence1.length() >= sequence2.length())
			return getPairWiseSemiGlobalAlign(sequence1, sequence2);
		else
			return getPairWiseSemiGlobalAlign(sequence2, sequence1);
	}
	
	private String[] getPairWiseSemiGlobalAlign(String sequence1, String sequence2) {
		String[] alignment = null;
		
		StringBuilder strBuilder = new StringBuilder();
		if (sequence1.contains(sequence2)) {
			int startIndex = (sequence1.indexOf(sequence2) - 1) / encodingLength;
			for (int i = 0; i < startIndex; i++) {
				strBuilder.append(dash);
			}
			strBuilder.append(sequence2);
			int remainingLength = (sequence1.length() - strBuilder.length()) / encodingLength;
			for (int i = 0; i < remainingLength; i++) {
				strBuilder.append(dash);
			}

			alignment = new String[] { sequence1, strBuilder.toString() };
			return alignment;
		}else{
			/*
			 * Neither of the sequences is subsumed in the other, we need
			 * to do an alignment; It would be computationally less intensive if we
			 * convert the sequence into a String[]
			 */

			int sequence1Length = sequence1.length()/encodingLength;
			int sequence2Length = sequence2.length()/encodingLength;
			
			String[] seq1 = new String[sequence1Length];
			for (int i = 0; i < sequence1Length; i++) {
				seq1[i] = sequence1.substring(i * encodingLength, (i + 1) * encodingLength);
			}

			String[] seq2 = new String[sequence2Length];
			for (int i = 0; i < sequence2Length; i++) {
				seq2[i] = sequence2.substring(i * encodingLength, (i + 1) * encodingLength);
			}
			
			pairWiseAlign(seq1, seq2);
			
			StringBuilder strBuilder1 = new StringBuilder();
			StringBuilder strBuilder2 = new StringBuilder();
			int i = seq1.length;
			int j = seq2.length;

			int maxI = 0, maxJ = 0;
			int maxJScore = Integer.MIN_VALUE;
			int maxIScore = Integer.MIN_VALUE;
			// Get the maximum score in the last column
			for (int k = 0; k <= seq1.length; k++) {
				if (M[k][j] > maxJScore) {
					maxJScore = M[k][j];
					maxI = k;
				}
			}

			//Check the maximum score over the last row and the index
			for (int k = 0; k <= seq2.length; k++) {
				if (M[i][k] > maxIScore) {
					maxIScore = M[i][k];
					maxJ = k;
				}
			}

			if (maxJScore >= maxIScore) {
				while (i > maxI) {
					strBuilder1.append(reverse(seq1[i - 1]));
					strBuilder2.append(dash);
					i--;
				}
			} else {
				while (j > maxJ) {
					strBuilder1.append(dash);
					strBuilder2.append(reverse(seq2[j - 1]));
					j--;
				}
			}

			while ((i != 0) || (j != 0)) {
				if (T[i][j] == TraceBack.DIAGONAL) {
					strBuilder1.append(reverse(seq1[i - 1]));
					strBuilder2.append(reverse(seq2[j - 1]));
					i--;
					j--;
				} else if (T[i][j] == TraceBack.UP) {
					strBuilder1.append(reverse(seq1[i - 1]));
					strBuilder2.append(dash);
					i--;
				} else if (T[i][j] == TraceBack.LEFT) {
					strBuilder1.append(dash);
					strBuilder2.append(reverse(seq2[j - 1]));
					j--;
				}
			}

			/*
			 * We no longer need M & T; set to null
			 */
			M = null;
			T = null;

			alignment = new String[2];
			alignment[0] = strBuilder1.reverse().toString();
			alignment[1] = strBuilder2.reverse().toString();
			
			return alignment;
		}
	}
	
	private void pairWiseAlign(String[] seq1, String[] seq2) {
		
		int sequence1Length = seq1.length;
		int sequence2Length = seq2.length;
		float lengthRatio;
		if (sequence1Length > sequence2Length) {
			lengthRatio = (float) sequence1Length / sequence2Length;
		} else {
			lengthRatio = (float) sequence2Length / sequence1Length;
		}
//		System.out.println("Length Ratio: "+lengthRatio+" @ Length Ratio Threshold: "+lengthRatioThreshold);
		int indelScoreSeq1, indelScoreSeq2, substitutionScore, maxScore;
		String indelSymbolPairSeq1, indelSymbolPairSeq2, substitutionSymbolPair;

		T = new TraceBack[sequence1Length + 1][sequence2Length + 1];

		// the alignment score matrix
		M = new int[sequence1Length + 1][sequence2Length + 1];

		M[0][0] = 0;
		T[0][0] = TraceBack.NONE;

		/*
		 * Fill the first row; the first symbol in sequence2 can always be
		 * inserted with no cost
		 */
		M[0][1] = 0;
		T[0][1] = TraceBack.LEFT;
		for (int j = 1; j < sequence2Length; j++) {
			indelSymbolPairSeq2 = seq2[j - 1] + " @ " + seq2[j];
			if (indelRightGivenLeftScoreMap.containsKey(indelSymbolPairSeq2)) {
				indelScoreSeq2 = indelRightGivenLeftScoreMap.get(indelSymbolPairSeq2);
			} else {
				indelScoreSeq2 = gapOpenCost;
			}
			M[0][j + 1] = M[0][j] + indelScoreSeq2;
			T[0][j + 1] = TraceBack.LEFT;
		}

		M[1][0] = 0;
		T[1][0] = TraceBack.UP;
		for (int i = 1; i < sequence1Length; i++) {
			indelSymbolPairSeq1 = seq1[i - 1] + " @ " + seq1[i];
			if (indelRightGivenLeftScoreMap.containsKey(indelSymbolPairSeq1)) {
				indelScoreSeq1 = indelRightGivenLeftScoreMap.get(indelSymbolPairSeq1);
			} else {
				indelScoreSeq1 = gapOpenCost;
			}
			M[i + 1][0] = M[i][0] + indelScoreSeq1;
			T[i + 1][0] = TraceBack.UP;
		}

		for (int i = 0; i < sequence1Length; i++) {
			/*
			 * Get the indel Score for sequence1
			 */
			indelScoreSeq1 = gapOpenCost;
			if (i > 0) {
				indelSymbolPairSeq1 = seq1[i - 1] + " @ " + seq1[i];
				if (indelRightGivenLeftScoreMap.containsKey(indelSymbolPairSeq1)) {
					indelScoreSeq1 = indelRightGivenLeftScoreMap.get(indelSymbolPairSeq1);
				}
			} else {
				indelScoreSeq1 = 0;
			}

			for (int j = 0; j < sequence2Length; j++) {
				/*
				 * Get the indel Score for sequence2
				 */
				indelScoreSeq2 = gapOpenCost;
				if (j > 0) {
					indelSymbolPairSeq2 = seq2[j - 1] + " @ " + seq2[j];
					if (indelRightGivenLeftScoreMap.containsKey(indelSymbolPairSeq2)) {
						indelScoreSeq2 = indelRightGivenLeftScoreMap.get(indelSymbolPairSeq2);
					}
				} else {
					indelScoreSeq2 = 0;
				}

				/*
				 * Get the substitution score
				 */
				substitutionScore = minSubstitutionScore;

				substitutionSymbolPair = seq1[i] + " @ " + seq2[j];
				if (substitutionScoreMap.containsKey(substitutionSymbolPair)) {
					substitutionScore = substitutionScoreMap.get(substitutionSymbolPair);
				} else {
					substitutionSymbolPair = seq2[j] + " @ " + seq1[i];
					if (substitutionScoreMap.containsKey(substitutionSymbolPair)) {
						substitutionScore = substitutionScoreMap.get(substitutionSymbolPair);
					}
				}

				maxScore = Math.max(M[i][j] + substitutionScore, Math.max(M[i][j + 1] + indelScoreSeq1, M[i + 1][j]
						+ indelScoreSeq2));

				if (lengthRatio <= lengthRatioThreshold) {
					if (maxScore == M[i][j] + substitutionScore && ((maxScore != M[i][j + 1] + indelScoreSeq1) && (maxScore != M[i + 1][j] + indelScoreSeq2))) {
						M[i + 1][j + 1] = M[i][j] + substitutionScore;
						T[i + 1][j + 1] = TraceBack.DIAGONAL;
					}else if (maxScore == M[i][j + 1] + indelScoreSeq1) {
						M[i + 1][j + 1] = M[i][j + 1] + indelScoreSeq1;
						T[i + 1][j + 1] = TraceBack.UP;
					}else if (maxScore == M[i + 1][j] + indelScoreSeq2) {
						M[i + 1][j + 1] = M[i + 1][j] + indelScoreSeq2;
						T[i + 1][j + 1] = TraceBack.LEFT;
					}  
				} else {
					/*
					 * The two sequences vary in length a lot; It can be because
					 * of loops or too may repeats; Problems can arise when a
					 * small number of symbols at the end are consistent (the
					 * problem being the sequences would be biased to get
					 * aligned at the end). In such cases, try to prefer a indel
					 * at the end in case two scores are equal
					 */
					if ((maxScore == M[i][j] + substitutionScore)
							&& ((maxScore != M[i][j + 1] + indelScoreSeq1) && (maxScore != M[i + 1][j] + indelScoreSeq2))) {
						//						System.out.println(maxScore+"@"+(M[i][j]+substitutionScore)+","+(M[i][j+1]+indelScoreSeq1));
						M[i + 1][j + 1] = M[i][j] + substitutionScore;
						T[i + 1][j + 1] = TraceBack.DIAGONAL;
					}else if (maxScore == M[i][j + 1] + indelScoreSeq1) {

						M[i + 1][j + 1] = M[i][j + 1] + indelScoreSeq1;
						T[i + 1][j + 1] = TraceBack.UP;
					}else if (maxScore == M[i + 1][j] + indelScoreSeq2) {
						M[i + 1][j + 1] = M[i + 1][j] + indelScoreSeq2;
						T[i + 1][j + 1] = TraceBack.LEFT;
					}
				}
			}
		}
		
		
		System.out.println("\n\n");
		printFScoreMatrix(seq1, seq2, M);
		System.out.println("\n\n");
		printTraceBack(seq1, seq2, T);
		
	}
	
	public void printFScoreMatrix(String[] seq1, String[] seq2, int[][] M){
		int noRows = seq1.length+1;
		int noCols = seq2.length+1;
		
		System.out.print("\t");
		for(int j = 1; j < noCols; j++){
			System.out.print(seq2[j-1]+"\t");
		}
		System.out.println();
		
		for(int i = 0; i < noRows; i++){
			if(i > 0)
				System.out.print(seq1[i-1]+"\t");
			else
				System.out.print("\t");
			
			for(int j = 0; j < noCols; j++){
				System.out.print(M[i][j]+"\t");
			}
			System.out.println();
		}
	}

	public void printTraceBack(String[] seq1, String[] seq2, TraceBack[][] T){
		int noRows = seq1.length+1;
		int noCols = seq2.length+1;
		
		System.out.print("\t");
		for(int j = 1; j < noCols; j++){
			System.out.print(seq2[j-1]+"\t");
		}
		System.out.println();
		
		for(int i = 0; i < noRows; i++){
			if(i > 0)
				System.out.print(seq1[i-1]+"\t");
			else
				System.out.print("\t\t");
			for(int j = 0; j < noCols; j++){
				if(T[i][j] == TraceBack.DIAGONAL)
					System.out.print("\\\t");
				else if(T[i][j] == TraceBack.UP)
					System.out.print("|\t");
				else
					System.out.print("--\t");
			}
			System.out.println();
		}
	}
	
	private String reverse(String str) {
		return new StringBuilder(str).reverse().toString();
	}
}