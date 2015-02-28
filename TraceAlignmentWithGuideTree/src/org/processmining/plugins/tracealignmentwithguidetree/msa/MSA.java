package org.processmining.plugins.tracealignmentwithguidetree.msa;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class MSA {
	int encodingLength;
	List<String[]> msaList;
	List<String> charStreamList;
	int N;
	int[][] itemsJoined;

	public MSA(int encodingLength, List<String> charStreamList, int[][] itemsJoined,
			Map<String, Integer> substitutionScoreMap, Map<String, Integer> indelRightGivenLeftScoreMap,
			int incrementLikeSubstitutionScore, float scaleFactor, int gapPenalty, float lengthRatioThreshold) {
		this.encodingLength = encodingLength;
		this.charStreamList = charStreamList;
		this.itemsJoined = itemsJoined;
		N = itemsJoined.length + 1;
		computeAlignment(substitutionScoreMap, indelRightGivenLeftScoreMap, incrementLikeSubstitutionScore,
				scaleFactor, gapPenalty, lengthRatioThreshold);
	}

	private void computeAlignment(Map<String, Integer> substitutionScoreMap,
			Map<String, Integer> indelRightGivenLeftScoreMap, int incrementLikeSubstitutionScore, float scaleFactor,
			int gapPenalty, float lengthRatioThreshold) {
		int K = itemsJoined.length;
		//		System.out.println("Items Joined Length: "+K);

		SeqAlignment s = new SeqAlignment(encodingLength, substitutionScoreMap, indelRightGivenLeftScoreMap,
				scaleFactor);
		if (incrementLikeSubstitutionScore != Integer.MAX_VALUE) {
			s.incrementLikeSubstitutions(incrementLikeSubstitutionScore);
		} else {
			s.incrementLikeSubstitutions(3);
		}
		s.setLengthRatioThreshold(lengthRatioThreshold);
		s.setGapPenalty(gapPenalty);

		if (msaList == null) {
			msaList = new ArrayList<String[]>();
		} else {
			msaList.clear();
		}

		String[] alignmentStringArray;
		for (int i = 0; i < K; i++) {
			//			System.out.println("------------------------------------------");
			//			System.out.println(itemsJoined[i][0]+" @ "+itemsJoined[i][1]);

			if ((itemsJoined[i][0] <= K) && (itemsJoined[i][1] <= K)) {
				//				System.out.println("S: "+charStreamList.get(itemsJoined[i][0]));
				//				System.out.println();
				//				System.out.println("S: "+charStreamList.get(itemsJoined[i][1]));
				alignmentStringArray = s.getPairWiseGlobalAlign(charStreamList.get(itemsJoined[i][0]), charStreamList
						.get(itemsJoined[i][1]));
			} else {
				if ((itemsJoined[i][0] <= K) && (itemsJoined[i][1] > K)) {
					//					String[] tString = msaList.get(itemsJoined[i][1]-K-1);
					//					for(String str : tString)
					//						System.out.println("P: "+str);
					//					System.out.println();
					//					System.out.println("S: "+charStreamList.get(itemsJoined[i][0]));

					alignmentStringArray = s.getProfileSequenceGlobalAlign(msaList.get(itemsJoined[i][1] - K - 1),
							charStreamList.get(itemsJoined[i][0]));
				} else if ((itemsJoined[i][0] > K) && (itemsJoined[i][1] <= K)) {
					//					String[] tString = msaList.get(itemsJoined[i][0]-K-1);
					//					for(String str : tString)
					//						System.out.println("P: "+str);
					//					System.out.println();
					//					System.out.println("S: "+charStreamList.get(itemsJoined[i][1]));

					alignmentStringArray = s.getProfileSequenceGlobalAlign(msaList.get(itemsJoined[i][0] - K - 1),
							charStreamList.get(itemsJoined[i][1]));
				} else {
					//					String[] tString = msaList.get(itemsJoined[i][0]-K-1);
					//					for(String str : tString)
					//						System.out.println("P1: "+str);
					//					System.out.println();
					//					tString = msaList.get(itemsJoined[i][1]-K-1);
					//					for(String str : tString)
					//						System.out.println("P2: "+str);

					alignmentStringArray = s.getProfileProfileGlobalAlign(msaList.get(itemsJoined[i][0] - K - 1),
							msaList.get(itemsJoined[i][1] - K - 1));
				}
			}
			//			System.out.println();
			msaList.add(alignmentStringArray);

			//			for(String alignmentString : alignmentStringArray)
			//				System.out.println(alignmentString);
		}

	}

	public List<String[]> getAlignments(int noClusters) {
		List<String[]> clusterAlignmentList = new ArrayList<String[]>();
		if (noClusters == 1) {
			clusterAlignmentList.add(msaList.get(msaList.size() - 1));
		} else {
			Set<Integer> alignmentIndexSet = getClusterItems(noClusters);
			for (Integer alignmentIndex : alignmentIndexSet) {
				clusterAlignmentList.add(msaList.get(alignmentIndex));
			}
		}

		return clusterAlignmentList;
	}

	public Set<Integer> getClusterItems(int noClusters) {
		//		System.out.println("N: "+N);
		Set<Integer> itemIndexSet = new HashSet<Integer>();
		int i = 0;

		while (i < noClusters) {
			itemIndexSet.addAll(getItemElements(N - 2 - i, noClusters));
			i++;
		}

		return itemIndexSet;
	}

	public Set<Integer> getItemElements(int index, int noClusters) {
		Set<Integer> itemElementsSet = new HashSet<Integer>();
		if (index >= N - noClusters) {
			if (itemsJoined[index][0] - N >= N - noClusters) {
				itemElementsSet.addAll(getItemElements(itemsJoined[index][0] - N - 1, noClusters));
			} else {
				itemElementsSet.add(itemsJoined[index][0] - N);
			}

			if (itemsJoined[index][1] - N >= N - noClusters) {
				itemElementsSet.addAll(getItemElements(itemsJoined[index][1] - N - 1, noClusters));
			} else {
				itemElementsSet.add(itemsJoined[index][1] - N);
			}
		}
		return itemElementsSet;
	}
}
