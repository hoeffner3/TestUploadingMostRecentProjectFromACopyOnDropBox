//Om Ganesayanamaha
package org.processmining.plugins.tracealignmentwithguidetree.msa;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

public class RefineAlignment {
	String dash;
	int encodingLength;
	boolean isValidRefinement;

	public RefineAlignment(int encodingLength) {
		this.encodingLength = encodingLength;
		dash = "-";
		for (int i = 1; i < encodingLength; i++)
			dash += "-";
	}
	
	public String[] performBlockShiftRight(String[] originalAlignment){
		dash = "-";
		for (int i = 1; i < encodingLength; i++)
			dash += "-";
		String[] refinedAlignment = null;
		
		System.out.println("Original Alignment");
		printAlignment(originalAlignment);
		int noTraces = originalAlignment.length;
		int alignmentLength = originalAlignment[0].length()/encodingLength;
		
		String[][] alignmentMatrix = new String[noTraces][alignmentLength];
		String alignmentSequence;
		for (int i = 0; i < noTraces; i++) {
			alignmentSequence = originalAlignment[i];
			for (int j = 0; j < alignmentLength; j++)
				alignmentMatrix[i][j] = alignmentSequence.substring(j
						* encodingLength, (j + 1) * encodingLength);
		}
		
		List<Set<String>> columnActivitySetList = new ArrayList<Set<String>>();
		Set<String> columnActivitySet;
		
		for (int j = 0; j < alignmentLength; j++) {
			columnActivitySet = new HashSet<String>();
			for (int i = 0; i < noTraces; i++) {
				columnActivitySet.add(alignmentMatrix[i][j]);
			}
			columnActivitySet.remove(dash);
			columnActivitySetList.add(columnActivitySet);
			System.out.println(j + " @ " + columnActivitySet);
		}
		
		return refinedAlignment;
	}

	public String[] performBlockShiftLeft(String[] originalAlignment) {
		System.out.println("Original Alignment");
		printAlignment(originalAlignment);
		int noTraces = originalAlignment.length;
		int alignmentLength = originalAlignment[0].length() / encodingLength;

		String[][] alignmentMatrix = new String[noTraces][alignmentLength];
		String alignmentSequence;
		for (int i = 0; i < noTraces; i++) {
			alignmentSequence = originalAlignment[i];
			for (int j = 0; j < alignmentLength; j++)
				alignmentMatrix[i][j] = alignmentSequence.substring(j
						* encodingLength, (j + 1) * encodingLength);
		}

		List<Set<String>> columnActivitySetList = new ArrayList<Set<String>>();
		Set<String> columnActivitySet;

		for (int j = 0; j < alignmentLength; j++) {
			columnActivitySet = new HashSet<String>();
			for (int i = 0; i < noTraces; i++) {
				columnActivitySet.add(alignmentMatrix[i][j]);
			}
			columnActivitySet.remove(dash);
			columnActivitySetList.add(columnActivitySet);
//			System.out.println(j + " @ " + columnActivitySet);
		}

		Map<String, TreeSet<Integer>> activityColumnIndicesSetMap = new HashMap<String, TreeSet<Integer>>();
		TreeSet<Integer> activityColumnIndicesSet;
		for (int j = 0; j < alignmentLength; j++) {
			columnActivitySet = columnActivitySetList.get(j);
			for (String activity : columnActivitySet) {
				if (activityColumnIndicesSetMap.containsKey(activity)) {
					activityColumnIndicesSet = activityColumnIndicesSetMap
							.get(activity);
				} else {
					activityColumnIndicesSet = new TreeSet<Integer>();
				}
				activityColumnIndicesSet.add(j);
				activityColumnIndicesSetMap.put(activity,
						activityColumnIndicesSet);
			}
		}

		
		for (String activity : activityColumnIndicesSetMap.keySet())
			System.out.println(activity + " @ "
					+ activityColumnIndicesSetMap.get(activity));
		
		
		StringBuilder strBuilder = new StringBuilder();
		boolean isPreviousGap;
		int firstGapPosition = -1;
		String temp;
		for (int i = 0; i < noTraces; i++) {
			strBuilder.setLength(0);
			isPreviousGap = false;
			for (int j = 0; j < alignmentLength; j++) {
				if (!alignmentMatrix[i][j].equals(dash) && !isPreviousGap) {
					strBuilder.append(alignmentMatrix[i][j]);
					isPreviousGap = false;
				} else if (alignmentMatrix[i][j].equals(dash) && !isPreviousGap) {
					firstGapPosition = j;
					isPreviousGap = true;
					while (j < alignmentLength
							&& alignmentMatrix[i][j].equals(dash))
						j++;
					j -= 1;
				} else if (!alignmentMatrix[i][j].equals(dash) && isPreviousGap) {
					activityColumnIndicesSet = activityColumnIndicesSetMap
							.get(alignmentMatrix[i][j]);
					
					for (Integer columnIndex : activityColumnIndicesSet)
						if (columnIndex >= firstGapPosition && columnIndex < j) {
//							System.out.println("can shift "
//									+ alignmentMatrix[i][j] + " at " + j
//									+ " to " + columnIndex);
							temp = alignmentMatrix[i][columnIndex];
							alignmentMatrix[i][columnIndex] = alignmentMatrix[i][j];
							alignmentMatrix[i][j] = temp;
							j = columnIndex;
							break;
						}

					isPreviousGap = false;
				}
			}
		}
		System.out.println("Before removing gap columns");
		printAlignment(alignmentMatrix);
		alignmentMatrix = removeGapColumns(alignmentMatrix);
//		alignmentLength = alignmentMatrix[0].length;
//		System.out.println("Refined");
//		for (int i = 0; i < noTraces; i++) {
//			for (int j = 0; j < alignmentLength; j++) {
//				System.out.print(alignmentMatrix[i][j]);
//			}
//			System.out.println();
//		}
		String[] refinedAlignment = new String[noTraces];
		alignmentLength = alignmentMatrix[0].length;
		
		for(int i = 0; i < noTraces; i++){
			strBuilder.setLength(0);
			for(int j = 0; j < alignmentLength; j++){
				strBuilder.append(alignmentMatrix[i][j]);
			}
			refinedAlignment[i] = strBuilder.toString();
		}

		isValidRefinement = true;
		for(int i = 0; i < noTraces; i++){
			if(!originalAlignment[i].replaceAll(dash, "").equals(refinedAlignment[i].replaceAll(dash, ""))){
					isValidRefinement = false;
					break;
			}
		}
		return refinedAlignment;
	}

	public TreeSet<Integer> getColumnIndices(String[] originalAlignment, Set<String> concurrentActivitySet){
		TreeSet<Integer> concurrentActivityColumnIndicesSet = new TreeSet<Integer>();
		int noTraces = originalAlignment.length;
		int alignmentLength = originalAlignment[0].length()/encodingLength;
		
		//Prepare original alignment matrix
		String[][] alignmentMatrix = new String[noTraces][alignmentLength];
		for(int i = 0; i < noTraces; i++){
			for(int j = 0; j < alignmentLength; j++){
				alignmentMatrix[i][j] = originalAlignment[i].substring(j*encodingLength, (j+1)*encodingLength);	
				if(concurrentActivitySet.contains(alignmentMatrix[i][j])){
					concurrentActivityColumnIndicesSet.add(j);
				}
			}
		}
		
		
		return concurrentActivityColumnIndicesSet;
	}
	
	public String[] removeConcurrent(String[] originalAlignment, String concurrentActivity){
		/**
		 * Currently take care of only those scenarios where the current
		 * activity manifests alone in all columns where it is present; Other scenario need to be handled later
		 * e.g., ab
		 * 		 ab
		 * 		 ba
		 * 	     ba
		 */
		System.out.println("Trying to remove Concurrent Activity: "+concurrentActivity);
//		System.out.println("Original Alignment");
//		printAlignment(originalAlignment);
		int noTraces = originalAlignment.length;
		int alignmentLength = originalAlignment[0].length()/encodingLength;
		String[] refinedAlignment = new String[noTraces];
		
		//Prepare original alignment matrix
		String[][] alignmentMatrix = new String[noTraces][alignmentLength];
		for(int i = 0; i < noTraces; i++){
			for(int j = 0; j < alignmentLength; j++){
				alignmentMatrix[i][j] = originalAlignment[i].substring(j*encodingLength, (j+1)*encodingLength);	
			}
		}
		
		List<Set<String>> columnActivitySetList = new ArrayList<Set<String>>();
		Set<String> columnActivitySet;
		for(int j = 0; j < alignmentLength; j++){
			columnActivitySet = new HashSet<String>();
			for(int i = 0; i < noTraces; i++)
				columnActivitySet.add(alignmentMatrix[i][j]);
			columnActivitySetList.add(columnActivitySet);
		}
		
		
		TreeSet<Integer> concurrentActivityColumnIndicesSet = new TreeSet<Integer>();
		for(int j = 0; j < alignmentLength; j++){
			if(columnActivitySetList.get(j).contains(concurrentActivity)){
				concurrentActivityColumnIndicesSet.add(j);
			}
		}
		
		/*
		 * First check if all columns where the concurrent activity is present
		 * is stand alone Since concurrent activity is anyway present in the
		 * columns, check if this column also contains a dash and if so, its
		 * size should be equal to just 2; if not, then there exists some other
		 * activity as well; we left the handling of those scenarios as future
		 * work
		 */ 
		for(Integer columnIndex : concurrentActivityColumnIndicesSet){
			columnActivitySet = columnActivitySetList.get(columnIndex);
			if(columnActivitySet.contains(dash) && columnActivitySet.size() > 2){
				isValidRefinement = false;
				System.out.println("Cannot Remove: "+columnActivitySet+" @ index "+columnIndex);
				return originalAlignment;
			}else if(!columnActivitySet.contains(dash) && columnActivitySet.size() >=2){
				System.out.println("Cannot Remove: "+columnActivitySet+" @ index "+columnIndex);
				isValidRefinement = false;
				return originalAlignment;
			}
		}
		
		System.out.println("Possible to Refine");
		isValidRefinement = true;
		//It is a simple case here; Set the refinedAlignment by removing those columns where this concurrent activity is present
		StringBuilder strBuilder = new StringBuilder();
		for(int i = 0; i < noTraces; i++){
			strBuilder.setLength(0);
			for(int j = 0; j < alignmentLength; j++){
				if(concurrentActivityColumnIndicesSet.contains(j))
					continue;
				strBuilder.append(alignmentMatrix[i][j]);
			}
			refinedAlignment[i] = strBuilder.toString();
		}
		return refinedAlignment;
	}

	
	public String[] refineConcurrent(String[] originalAlignment, boolean isActivityAcrossAllTraces){
		isValidRefinement = true;
		String[] refinedAlignment;
		System.out.println("Original Alignment");
		printAlignment(originalAlignment);
		
		String[][] alignmentMatrix = convertAlignmentToMatrix(originalAlignment);
		int noTraces = alignmentMatrix.length;
		int alignmentLength = alignmentMatrix[0].length;
		
		/*
		 * Get the set of activities in each column
		 */
		List<Set<String>> columnActivitySetList = new ArrayList<Set<String>>();
		Set<String> columnActivitySet;
		
		for (int j = 0; j < alignmentLength; j++) {
			columnActivitySet = new HashSet<String>();
			for (int i = 0; i < noTraces; i++) {
				columnActivitySet.add(alignmentMatrix[i][j]);
			}
			columnActivitySet.remove(dash);
			columnActivitySetList.add(columnActivitySet);
		}
		
		/*
		 * Get all the columns (in increasing order) in which an activity manifests in the alignment
		 */
		Map<String, TreeSet<Integer>> activityColumnIndicesSetMap = new HashMap<String, TreeSet<Integer>>();
		TreeSet<Integer> activityColumnIndicesSet;
		for (int j = 0; j < alignmentLength; j++) {
			columnActivitySet = columnActivitySetList.get(j);
			
			for (String activity : columnActivitySet) {
				if (activityColumnIndicesSetMap.containsKey(activity)) {
					activityColumnIndicesSet = activityColumnIndicesSetMap
							.get(activity);
				} else {
					activityColumnIndicesSet = new TreeSet<Integer>();
				}
				activityColumnIndicesSet.add(j);
				activityColumnIndicesSetMap.put(activity,
						activityColumnIndicesSet);
			}
		}
		
		System.out.println("Activity - Column Indices Set");
		for (String activity : activityColumnIndicesSetMap.keySet()) {
			activityColumnIndicesSet = activityColumnIndicesSetMap
					.get(activity);
			System.out.println(activity + " @ " + activityColumnIndicesSet);
		}
		
		/*
		 * Check if an activity is concurrent; For each activity that manifests
		 * in more than one column, get the set of rows in which that activity
		 * manifests for each column. If these rows are mutually exclusive
		 * (intersection of these is null), then it is a potentially concurrent
		 * activity.
		 * 
		 * A potentially concurrent activity is a concurrent activity
		 * only if no other activity shares the columns in which the potentially
		 * concurrent activity resides
		 */
		System.out.println("Finding potential concurrent activities");
		Set<String> potentialConcurrentActivitySet = new HashSet<String>();
		Map<Integer, Set<Integer>> columnTraceIndicesSetMap = new HashMap<Integer, Set<Integer>>();
		Set<Integer> columnTraceIndicesSet;
		List<Set<Integer>> columnTraceIndicesSetList = new ArrayList<Set<Integer>>();
		int noColumns, noRows = 0;
		
		for (String activity : activityColumnIndicesSetMap.keySet()) {
			activityColumnIndicesSet = activityColumnIndicesSetMap
					.get(activity);
			System.out.println("Processing activity: "+activity+" "+activityColumnIndicesSet);
			
			if (activityColumnIndicesSet.size() > 1) {
				columnTraceIndicesSetMap.clear();
				columnTraceIndicesSetList.clear();
				for (Integer column : activityColumnIndicesSet) {
					columnTraceIndicesSet = new HashSet<Integer>();
					for (int i = 0; i < noTraces; i++) {
						if (alignmentMatrix[i][column].equals(activity)) {
							columnTraceIndicesSet.add(i);
						}
					}
					columnTraceIndicesSetMap.put(column, columnTraceIndicesSet);
					columnTraceIndicesSetList.add(columnTraceIndicesSet);
				}
				
				noColumns = columnTraceIndicesSetList.size();
				boolean[][] activityTraceColumnIncidenceMatrix = new boolean[noTraces][noColumns];
				for(int i = 0; i < noColumns; i++){
					columnTraceIndicesSet = columnTraceIndicesSetList.get(i);
					for(Integer traceIndex : columnTraceIndicesSet)
						activityTraceColumnIncidenceMatrix[traceIndex][i] = true;
				}
	
				/*
				for(int i = 0; i < noTraces; i++){
					for(int j = 0; j < noColumns; j++)
						if(activityTraceColumnIncidenceMatrix[i][j])
							System.out.print("1 ");
						else
							System.out.print("0 ");
					System.out.println();
				}
				*/
				boolean[] isMutuallyExclusive = new boolean[noTraces];
				boolean[] isAllZeros = new boolean[noTraces];
				
				noRows = 0;
				int noOnes = 0;
				//Check for mutual exclusive property
				for(int i = 0; i < noTraces; i++){
					noOnes = 0;
					if(activityTraceColumnIncidenceMatrix[i][0])
						noOnes++;
					isAllZeros[i] = activityTraceColumnIncidenceMatrix[i][0];
					for(int j = 1; j < noColumns; j++){
						if(activityTraceColumnIncidenceMatrix[i][j])
							noOnes++;
						isAllZeros[i] |= activityTraceColumnIncidenceMatrix[i][j];
					}
					
					isAllZeros[i] = !isAllZeros[i];
					if(noOnes == 1)
						isMutuallyExclusive[i] = true;
					else
						isMutuallyExclusive[i] = false;
					
					System.out.println(originalAlignment[i]+" "+noOnes+" "+isMutuallyExclusive[i]+" @ "+isAllZeros[i]);
				}
				boolean isAllMutuallyExclusive = true;
				for(int i = 0; i < noTraces; i++){
					if(!isMutuallyExclusive[i] && isActivityAcrossAllTraces && !isAllZeros[i]){
						isAllMutuallyExclusive = false;
						break;
					}else if(!isMutuallyExclusive[i] && !isAllZeros[i] && !isActivityAcrossAllTraces){
						isAllMutuallyExclusive = false;
						break;
					}
				}
				if(isAllMutuallyExclusive){
					System.out.println("Is All Mutually Exclusive");
					for(int i = 0; i < noTraces; i++){
						for(int j = 0; j < noColumns; j++){
							if(activityTraceColumnIncidenceMatrix[i][j]){
								noRows++;
								break;
							}
						}
					}
					//Check if this activity is alone in all of the columns in which it manifest
					boolean isIgnorable = false;
					for(Integer columnIndex : activityColumnIndicesSet){
						if(columnActivitySetList.get(columnIndex).size() > 1){
							isIgnorable = true;
							break;
						}
					}
					if(!isIgnorable && isActivityAcrossAllTraces && noRows == noTraces){
						potentialConcurrentActivitySet.add(activity);
					}else if(!isIgnorable && !isActivityAcrossAllTraces){
						potentialConcurrentActivitySet.add(activity);
					}
				}
			}
		}
		
		System.out.println("Concurrent Activities: "+potentialConcurrentActivitySet);
		if(potentialConcurrentActivitySet.size() == 0)
			return originalAlignment;
		
		String[][] tempAlignmentMatrix = new String[noTraces][alignmentLength];
		for(int i = 0; i < noTraces; i++)
			for(int j = 0; j < alignmentLength; j++)
				tempAlignmentMatrix[i][j] = alignmentMatrix[i][j];
		
		String[][] alignmentSansConcurrentActivityMatrix;
		
		
		Map<Integer, TreeSet<Integer>> concurrentActivityPositionTraceIndicesSetMap = new HashMap<Integer, TreeSet<Integer>>();
		TreeSet<Integer> concurrentActivityPositionTraceIndicesSet;
		String[] originalTraces = new String[noTraces];
		for(int i = 0; i < noTraces; i++){
			originalTraces[i] = originalAlignment[i].replaceAll(dash, "");
		}
		
		int p, pos;
		for(String concurrentActivity : potentialConcurrentActivitySet){
			/*
			 * Get the original indices (position) in the traces for the
			 * concurrent activity
			 */
			for(int i = 0; i < noTraces; i++){
				pos = originalTraces[i].indexOf(concurrentActivity);
				if(concurrentActivityPositionTraceIndicesSetMap.containsKey(pos))
					concurrentActivityPositionTraceIndicesSet = concurrentActivityPositionTraceIndicesSetMap.get(pos);
				else
					concurrentActivityPositionTraceIndicesSet = new TreeSet<Integer>();
				concurrentActivityPositionTraceIndicesSet.add(i);
				concurrentActivityPositionTraceIndicesSetMap.put(pos, concurrentActivityPositionTraceIndicesSet);
			}
			
			System.out.println("Concurrent Activity Trace Indices Information");
			for(Integer concurrentActivityPos : concurrentActivityPositionTraceIndicesSetMap.keySet())
				System.out.println("Concurrent Activity Pos: "+concurrentActivityPos+" @ "+concurrentActivityPositionTraceIndicesSetMap.get(concurrentActivityPos));
			
			alignmentLength = tempAlignmentMatrix[0].length;
			activityColumnIndicesSet = activityColumnIndicesSetMap.get(concurrentActivity);
			/*
			 * There could be cases where two different activities can manifest
			 * in the same column as that of the concurrent activity; that needs
			 * to be taken into account; currently ignoring that scenario
			 */
			alignmentSansConcurrentActivityMatrix = new String[noTraces][alignmentLength-activityColumnIndicesSet.size()];
			p = 0;
			for(int j = 0; j < alignmentLength; j++){
				if(activityColumnIndicesSet.contains(j))
					continue;
				for(int i = 0; i < noTraces; i++){
					alignmentSansConcurrentActivityMatrix[i][p] = tempAlignmentMatrix[i][j];
				}
				p++;
			}
			
//			System.out.println("Alignment Matrix Sans Concurrent Activity: "+concurrentActivity);
//			printAlignment(alignmentSansConcurrentActivityMatrix);
			

			tempAlignmentMatrix = performBlockShiftConcurrentPrunedAlignment(alignmentSansConcurrentActivityMatrix);
			alignmentLength = tempAlignmentMatrix[0].length;
			
//			System.out.println("Block Shifted Alignment Matrix Sans Concurrent Activity");
//			printAlignment(tempAlignmentMatrix);
			
			Map<Integer, Integer> traceIndexAlignmentPosMap = new HashMap<Integer, Integer>();
			int nonGapCount;
			for(Integer concurrentActivityPos : concurrentActivityPositionTraceIndicesSetMap.keySet()){
				concurrentActivityPositionTraceIndicesSet = concurrentActivityPositionTraceIndicesSetMap.get(concurrentActivityPos);
				for(Integer traceIndex : concurrentActivityPositionTraceIndicesSet){
					nonGapCount = 0;
					for(int j = 0; j < alignmentLength; j++){
						if(!tempAlignmentMatrix[traceIndex][j].equals(dash))
							nonGapCount++;
						if(nonGapCount == concurrentActivityPos){
							traceIndexAlignmentPosMap.put(traceIndex, j+1);
							break;
						}
					}
				}
			}
			
			TreeSet<Integer> sortedTraceIndicesSet = new TreeSet<Integer>();
			sortedTraceIndicesSet.addAll(traceIndexAlignmentPosMap.keySet());

//			System.out.println("Trace Index - Alignment Pos");
//			for(Integer traceIndex : sortedTraceIndicesSet)
//				System.out.println(traceIndex+" @ "+traceIndexAlignmentPosMap.get(traceIndex));
			
			TreeSet<Integer> sortedAlignmentPositionSet = new TreeSet<Integer>();
			sortedAlignmentPositionSet.addAll(traceIndexAlignmentPosMap.values());
			
			List<Integer> sortedAlignmentPositionList = new ArrayList<Integer>();
			sortedAlignmentPositionList.addAll(sortedAlignmentPositionSet);
			int alignmentPos;
			
			
			for(Integer traceIndex : traceIndexAlignmentPosMap.keySet()){
				alignmentPos = traceIndexAlignmentPosMap.get(traceIndex);
				traceIndexAlignmentPosMap.put(traceIndex, alignmentPos+sortedAlignmentPositionList.indexOf(alignmentPos)+1);
			}
			
			Map<Integer, TreeSet<Integer>> alignmentPosTraceIndicesSetMap = new HashMap<Integer, TreeSet<Integer>>();
			TreeSet<Integer> alignmentPosTraceIndicesSet;
			for(Integer traceIndex : sortedTraceIndicesSet){
//				System.out.println(traceIndex+" @ "+traceIndexAlignmentPosMap.get(traceIndex));
				alignmentPos = traceIndexAlignmentPosMap.get(traceIndex);
				if(alignmentPosTraceIndicesSetMap.containsKey(alignmentPos))
					alignmentPosTraceIndicesSet = alignmentPosTraceIndicesSetMap.get(alignmentPos);
				else
					alignmentPosTraceIndicesSet = new TreeSet<Integer>();
				alignmentPosTraceIndicesSet.add(traceIndex);
				alignmentPosTraceIndicesSetMap.put(alignmentPos, alignmentPosTraceIndicesSet);
			}
			
			String[][] refinedAlignmentMatrix = new String[noTraces][alignmentLength+sortedAlignmentPositionList.size()];
			int colIndex = 0;
			int index = 0;
			for(int i = 0; i < sortedAlignmentPositionList.size(); i++){
				noColumns = sortedAlignmentPositionList.get(i)-colIndex;
				for(int k = 0; k < noColumns; k++){
					for(int traceIndex = 0; traceIndex < noTraces; traceIndex++){
						refinedAlignmentMatrix[traceIndex][index] = tempAlignmentMatrix[traceIndex][colIndex+k];
					}
					index++;
				}
				colIndex += noColumns;
//				System.out.println("Pos: "+sortedAlignmentPositionList.get(i)+" @ "+noColumns+" @ "+index);
//				System.out.println("Index: "+index);
				if(alignmentPosTraceIndicesSetMap.containsKey(index+1)){
					alignmentPosTraceIndicesSet = alignmentPosTraceIndicesSetMap.get(index+1);
					for(int traceIndex = 0; traceIndex < noTraces; traceIndex++){
						if(alignmentPosTraceIndicesSet.contains(traceIndex))
							refinedAlignmentMatrix[traceIndex][index] = concurrentActivity;
						else
							refinedAlignmentMatrix[traceIndex][index] = dash;
					}
					index++;
				}
			}
			
			for(int k = colIndex; k < alignmentLength; k++){
				for(int traceIndex = 0; traceIndex < noTraces; traceIndex++)
					refinedAlignmentMatrix[traceIndex][index] = tempAlignmentMatrix[traceIndex][k];
				index++;
			}
			
//			System.out.println("Refined Alignment Matrix");
//			printAlignment(refinedAlignmentMatrix);
			
			refinedAlignmentMatrix = removeGapColumns(refinedAlignmentMatrix);
			
			alignmentLength = refinedAlignmentMatrix[0].length;
			refinedAlignment = new String[noTraces];
			
			StringBuilder strBuilder = new StringBuilder();
			for(int i = 0; i < noTraces; i++){
				strBuilder.setLength(0);
				for(int j = 0; j < alignmentLength; j++)
					strBuilder.append(refinedAlignmentMatrix[i][j]);
				refinedAlignment[i] = strBuilder.toString();
			}
			
			
			String[] refinedAlignedTraces = new String[noTraces];
			for(int i = 0; i < noTraces; i++){
				refinedAlignedTraces[i] = refinedAlignment[i].replaceAll(dash, "");
				if(!originalTraces[i].equals(refinedAlignedTraces[i])){
					isValidRefinement = false;
					break;
				}
			}
			
			return refinedAlignment;
		}
		
		return originalAlignment;
	}
	
	public Set<String> getConcurrentActivities(String[] originalAlignment, boolean isAcrossAllTraces){
		Set<String> concurrentActivitySet = new HashSet<String>();
		int noTraces = originalAlignment.length;
		int alignmentLength = originalAlignment[0].length()/encodingLength;
		
		String[][] alignmentMatrix = new String[noTraces][alignmentLength];
		for(int i = 0; i < noTraces; i++){
			for(int j = 0; j < alignmentLength; j++){
				alignmentMatrix[i][j] = originalAlignment[i].substring(j*encodingLength, (j+1)*encodingLength);
			}
		}
		
//		System.out.println("Column Activity Set");
		List<Set<String>> columnActivitySetList = new ArrayList<Set<String>>();
		Set<String> columnActivitySet;
		for(int j = 0; j < alignmentLength; j++){
			columnActivitySet = new HashSet<String>();
			for(int i = 0; i < noTraces; i++){
				columnActivitySet.add(alignmentMatrix[i][j]);
			}
			columnActivitySetList.add(columnActivitySet);
//			System.out.println(j+" @ "+columnActivitySet);
		}
		
		Map<String, TreeSet<Integer>> activityColumnIndicesSetMap = new HashMap<String, TreeSet<Integer>>();
		TreeSet<Integer> activityColumnIndicesSet;
		for(int j = 0; j < alignmentLength; j++){
			columnActivitySet = columnActivitySetList.get(j);
			for(String activity : columnActivitySet){
				if(activityColumnIndicesSetMap.containsKey(activity)){
					activityColumnIndicesSet = activityColumnIndicesSetMap.get(activity);
				}else{
					activityColumnIndicesSet = new TreeSet<Integer>();
				}
				activityColumnIndicesSet.add(j);
				activityColumnIndicesSetMap.put(activity, activityColumnIndicesSet);
			}
		}
		
		activityColumnIndicesSetMap.remove(dash);
/*		System.out.println("Activity Column Indices Set Map");
		for(String activity : activityColumnIndicesSetMap.keySet()){
			System.out.println(activity+" @ "+activityColumnIndicesSetMap.get(activity));
		}
*/		
		boolean[][] activityTraceIndicesIndicenceMatrix;
		int[] rowCount = new int[noTraces];
		boolean isConcurrentActivity = true;
		boolean hasZeroRowCount = false;
		for(String activity : activityColumnIndicesSetMap.keySet()){
			
			activityColumnIndicesSet = activityColumnIndicesSetMap.get(activity);
			int noColumns = activityColumnIndicesSet.size();
//			System.out.println("Processing Activity: "+activity+" No Columns: "+noColumns);
			if(noColumns == 1)
				continue;
			activityTraceIndicesIndicenceMatrix = new boolean[noTraces][activityColumnIndicesSet.size()];
			int j = 0;
			for(int columnIndex : activityColumnIndicesSet){
				for(int i = 0; i < noTraces; i++){
					if(alignmentMatrix[i][columnIndex].equals(activity)){
						activityTraceIndicesIndicenceMatrix[i][j] = true;
					}
				}
				j++;
			}
			
			for(int i = 0; i < noTraces; i++){
				rowCount[i] = 0;
				for(j = 0; j < noColumns; j++){
					if(activityTraceIndicesIndicenceMatrix[i][j]){
							rowCount[i]++;
					}
				}
			}

			/*
			//print indicence matrix and row count
			for(int i = 0; i < noTraces; i++){
				for(j = 0; j < noColumns; j++){
					if(activityTraceIndicesIndicenceMatrix[i][j]){
						System.out.print("1 ");
					}else{
						System.out.print("0 ");
					}
				}
				System.out.println(": "+rowCount[i]);
			}
			*/
			isConcurrentActivity = true;
			hasZeroRowCount = false;
			for(int i = 0; i < noTraces; i++){
//				System.out.println(rowCount[i]);
				if(rowCount[i] > 1){
					isConcurrentActivity = false;
					break;
				}else if(rowCount[i] == 0){
					hasZeroRowCount = true;
				}
			}
			
			if(isConcurrentActivity && isAcrossAllTraces && !hasZeroRowCount)
				concurrentActivitySet.add(activity);
			else if(isConcurrentActivity && !isAcrossAllTraces)
				concurrentActivitySet.add(activity);
		}
		
//		System.out.println("Concurrent Activity Set: "+concurrentActivitySet);
		return concurrentActivitySet;
	}
	
	private void printAlignment(String[] alignment){
		for(String alignedTrace : alignment)
			System.out.println(alignedTrace);
	}
	
	private void printAlignment(String[][] alignmentMatrix){
		int alignmentLength = alignmentMatrix[0].length;
		int noTraces = alignmentMatrix.length;
		
		for(int i = 0; i < noTraces; i++){
			for(int j = 0; j < alignmentLength; j++)
				System.out.print(alignmentMatrix[i][j]);
			System.out.println();
		}
	}
	
	private String[][] convertAlignmentToMatrix(String[] alignment){
		int alignmentLength = alignment[0].length() / encodingLength;
		int noTraces = alignment.length;
		String[][] alignmentMatrix = new String[noTraces][alignmentLength];
		String alignmentSequence;
		for (int i = 0; i < noTraces; i++) {
			alignmentSequence = alignment[i];
			for (int j = 0; j < alignmentLength; j++)
				alignmentMatrix[i][j] = alignmentSequence.substring(j
						* encodingLength, (j + 1) * encodingLength);
		}
		
		return alignmentMatrix;
	}
	
	public String[] refineConcurrent0(int encodingLength,
			String[] originalAlignment) {
		System.out.println("Original Alignment");
		for(String alignedTrace : originalAlignment)
			System.out.println(alignedTrace);

		dash = "-";
		for (int i = 1; i < encodingLength; i++)
			dash += "-";

		int alignmentLength = originalAlignment[0].length() / encodingLength;
		int noTraces = originalAlignment.length;
		String[][] alignmentMatrix = new String[noTraces][alignmentLength];
		String alignmentSequence;
		for (int i = 0; i < noTraces; i++) {
			alignmentSequence = originalAlignment[i];
			for (int j = 0; j < alignmentLength; j++)
				alignmentMatrix[i][j] = alignmentSequence.substring(j
						* encodingLength, (j + 1) * encodingLength);
		}

		List<Set<String>> columnActivitySetList = new ArrayList<Set<String>>();
		Set<String> columnActivitySet;

		for (int j = 0; j < alignmentLength; j++) {
			columnActivitySet = new HashSet<String>();
			for (int i = 0; i < noTraces; i++) {
				columnActivitySet.add(alignmentMatrix[i][j]);
			}
			columnActivitySet.remove(dash);
			columnActivitySetList.add(columnActivitySet);
		}

		Map<String, TreeSet<Integer>> activityColumnIndicesSetMap = new HashMap<String, TreeSet<Integer>>();
		TreeSet<Integer> activityColumnIndicesSet;
		for (int j = 0; j < alignmentLength; j++) {
			columnActivitySet = columnActivitySetList.get(j);
			for (String activity : columnActivitySet) {
				if (activityColumnIndicesSetMap.containsKey(activity)) {
					activityColumnIndicesSet = activityColumnIndicesSetMap
							.get(activity);
				} else {
					activityColumnIndicesSet = new TreeSet<Integer>();
				}
				activityColumnIndicesSet.add(j);
				activityColumnIndicesSetMap.put(activity,
						activityColumnIndicesSet);
			}
		}

		System.out.println("Activity Column Indices Set");
		for (String activity : activityColumnIndicesSetMap.keySet()) {
			activityColumnIndicesSet = activityColumnIndicesSetMap
					.get(activity);
			System.out.println(activity + " @ " + activityColumnIndicesSet);
		}

		/*
		 * Check if an activity is concurrent; For each activity that manifests
		 * in more than one column, get the set of rows in which that activity
		 * manifests for each column. If these rows are mutually exclusive
		 * (intersection of these is null), then it is a potentially concurrent
		 * activity.
		 * 
		 * A potentially concurrent activity is a concurrent activity
		 * only if no other activity shares the columns in which the potentially
		 * concurrent activity resides
		 */
		Set<String> potentialConcurrentActivitySet = new HashSet<String>();
		Map<Integer, Set<Integer>> columnRowIndicesSetMap = new HashMap<Integer, Set<Integer>>();
		Set<Integer> columnRowIndicesSet;
		List<Set<Integer>> columnRowIndicesSetList = new ArrayList<Set<Integer>>();
		int noColumns, noRows = 0;
		boolean isMutuallyExclusive;
		Set<Integer> tempSet = new HashSet<Integer>();
		for (String activity : activityColumnIndicesSetMap.keySet()) {
//			System.out.println("Processing activity: "+activity);
			activityColumnIndicesSet = activityColumnIndicesSetMap
					.get(activity);
			if (activityColumnIndicesSet.size() > 1) {
				columnRowIndicesSetMap.clear();
				columnRowIndicesSetList.clear();
				for (Integer column : activityColumnIndicesSet) {
					columnRowIndicesSet = new HashSet<Integer>();
					for (int i = 0; i < noTraces; i++) {
						if (alignmentMatrix[i][column].equals(activity)) {
							columnRowIndicesSet.add(i);
						}
					}
					columnRowIndicesSetMap.put(column, columnRowIndicesSet);
					columnRowIndicesSetList.add(columnRowIndicesSet);
//					System.out.println(column+" @ "+columnRowIndicesSet);
				}
				noColumns = columnRowIndicesSetList.size();
				isMutuallyExclusive = true;
				noRows = 0;
				for (int i = 0; i < noColumns - 1; i++) {
					noRows += columnRowIndicesSetList.get(i).size();
					for (int j = i + 1; j < noColumns; j++) {
						tempSet.clear();
						tempSet.addAll(columnRowIndicesSetList.get(i));
						tempSet.retainAll(columnRowIndicesSetList.get(j));
						if (tempSet.size() > 0) {
							isMutuallyExclusive = false;
							break;
						}
					}
					if (!isMutuallyExclusive)
						break;
				}
				noRows += columnRowIndicesSetList.get(noColumns-1).size();
				if (isMutuallyExclusive && noRows == noTraces) {
					System.out.println("Activity "+activity+" is concurrent");
					potentialConcurrentActivitySet.add(activity);
				}
			}
		}
		System.out.println("No. Traces: "+noTraces+" No. Rows: "+noRows);

		/*
		 * For each concurrent activity, get the actual indices where this
		 * activity manifests in each trace Note that the indices should be that
		 * of the original trace and not that in the alignment
		 */
		String[][] refinedAlignmentMatrix;
		List<Integer> concurrentActivityPositionList = new ArrayList<Integer>();
		if (potentialConcurrentActivitySet.size() > 0) {
			List<String> originalTraceList = new ArrayList<String>();
			for (String alignment : originalAlignment) {
				alignment = alignment.replaceAll(dash, "");
				originalTraceList.add(alignment);
			}

			int originalTraceLength;
			Map<Integer, Set<Integer>> concurrentActivityPositionTraceIndicesSetMap = new HashMap<Integer, Set<Integer>>();
			Set<Integer> concurrentActivityPositionTraceIndicesSet;
			Map<Integer, Set<Integer>> tempConcurrentActivityPositionTraceIndicesSetMap = new HashMap<Integer, Set<Integer>>();
			
			for (String concurrentActivity : potentialConcurrentActivitySet) {
				System.out.println("Processing Concurrent Activity: "+concurrentActivity);
				activityColumnIndicesSet = activityColumnIndicesSetMap
						.get(concurrentActivity);
				concurrentActivityPositionList.clear();

				for (String originalTrace : originalTraceList) {
					originalTraceLength = originalTrace.length()
							/ encodingLength;
					for (int i = 0; i < originalTraceLength; i++) {
						if (originalTrace.substring(i * encodingLength,
								(i + 1) * encodingLength).equals(
								concurrentActivity)) {
							concurrentActivityPositionList.add(i);
							break;
						}
					}
				}
				System.out.println("Concurrent Activity Position List: "+concurrentActivityPositionList);

				refinedAlignmentMatrix = new String[noTraces][alignmentLength
						- activityColumnIndicesSet.size()];
				int index = 0;

				for (int j = 0; j < alignmentLength; j++) {
					if (activityColumnIndicesSet.contains(j))
						continue;
					for (int i = 0; i < noTraces; i++) {
						refinedAlignmentMatrix[i][index] = alignmentMatrix[i][j];
					}
					index++;
				}

				System.out.println("Intermediary Refined Alignment");

				for (int i = 0; i < noTraces; i++) {
					for (int j = 0; j < index; j++) {
						System.out.print(refinedAlignmentMatrix[i][j]);
					}
					System.out.println();
				}

				System.out.println("Performing Block Shift Concurrent Activity");
				refinedAlignmentMatrix = performBlockShiftConcurrentPrunedAlignment(refinedAlignmentMatrix);
				System.out.println("Block Shifted Alignment");
				alignmentLength = refinedAlignmentMatrix[0].length;
				for (int i = 0; i < noTraces; i++) {
					for (int j = 0; j < alignmentLength; j++) {
						System.out.print(refinedAlignmentMatrix[i][j]);
					}
					System.out.println();
				}
				
				alignmentLength = refinedAlignmentMatrix[0].length;
				System.out.println("Concurrent Activity: " + concurrentActivity);
				concurrentActivityPositionTraceIndicesSetMap.clear();
				int position;
				TreeSet<Integer> tracePositionIndicesSet = new TreeSet<Integer>();
				for (int i = 0; i < concurrentActivityPositionList.size(); i++) {
					position = concurrentActivityPositionList.get(i);
					if (concurrentActivityPositionTraceIndicesSetMap
							.containsKey(position)) {
						concurrentActivityPositionTraceIndicesSet = concurrentActivityPositionTraceIndicesSetMap
								.get(position);
					} else {
						concurrentActivityPositionTraceIndicesSet = new HashSet<Integer>();
					}
					concurrentActivityPositionTraceIndicesSet.add(i);
					concurrentActivityPositionTraceIndicesSetMap.put(position,
							concurrentActivityPositionTraceIndicesSet);
					System.out.println(position);
					tracePositionIndicesSet.add(position);
				}

				
				List<Integer> tracePositionIndicesList = new ArrayList<Integer>();
				tracePositionIndicesList.addAll(tracePositionIndicesSet);
				System.out.println("Activity Trace Column Indices Set: "
						+ tracePositionIndicesList);

				/*
				 * Adjust trace indices position in alignment. This is needed to
				 * tackle scenarios such as this: Let the original alignment be:
				 * jgcl-f-ebdi 
				 * jgcl-f-ebd- 
				 * jgclef--bdi 
				 * jgc--flebdi 
				 * jgc--flebd-
				 * 
				 * e and l are concurrent activities here; after processing for 'e', 
				 * the intermediary refined alignment would be
				 * 
				 * jgclf-bdi
				 * jgclf-bd-
				 * jgclf-bdi
				 * jgc-flbdi
				 * jgc-flbd-
				 * 
				 * The position of activity 'e' in the traces would be 5, 5, 4, 5, 5
				 * however, the 5 in traces 1 and 2 should correspond to alignment position 6
				 * We need to consider only those positions where this activity manifests in 
				 * more than one trace 
				 */
				
				for(Integer pos : tracePositionIndicesList){
					System.out.println("Pos: "+pos+" "+concurrentActivityPositionTraceIndicesSetMap.get(pos));
				}
				System.out.println("JJJ");
				List<Integer> refinedAlignmentPositionList = new ArrayList<Integer>();
				int maxAlignPos;
				Set<Integer> maxAlignPosSet = new HashSet<Integer>();
				for(Integer pos : tracePositionIndicesList){
					concurrentActivityPositionTraceIndicesSet = concurrentActivityPositionTraceIndicesSetMap.get(pos);
					System.out.println("Pos: "+pos+" Concurrent Activity Pos Trace Indices Set: "+concurrentActivityPositionTraceIndicesSet);
					if(concurrentActivityPositionTraceIndicesSetMap.containsKey(pos) && concurrentActivityPositionTraceIndicesSet.size() > 1){
						//Get the adjusted position based on the maximum out of the traces in the refined alignment
						maxAlignPos = pos;
						maxAlignPosSet.clear();
						int nonGapCount;
						for(Integer traceIndex : concurrentActivityPositionTraceIndicesSet){
							nonGapCount = 0;
							for(int i = 0; i < alignmentLength; i++){
								if(refinedAlignmentMatrix[traceIndex][i].equals(dash)){
									continue;
								}else{
									nonGapCount++;
									if(nonGapCount == pos){
										System.out.println("Trace: "+traceIndex+" Pos: "+i);
										maxAlignPosSet.add(i);
										if(i > maxAlignPos){
											maxAlignPos = i;
										}
										break;
									}
								}
							}
						}
						System.out.println("Pos: "+pos+" Max Align Pos: "+(maxAlignPos+1));
						if(maxAlignPos >= pos && maxAlignPosSet.size() > 1){
							System.out.println("Refining");
							if(!refinedAlignmentPositionList.contains(maxAlignPos+1))
								refinedAlignmentPositionList.add(maxAlignPos+1);
//							concurrentActivityPositionTraceIndicesSetMap.remove(pos);
							int k = 1;
							while(concurrentActivityPositionTraceIndicesSetMap.containsKey(maxAlignPos+k)){
								k++;
							}
							for(int p = k-1; p >= 1; p--){
								System.out.println("Updating: "+(maxAlignPos+p+1));
								Set<Integer> tempTraceIndicesSet = concurrentActivityPositionTraceIndicesSetMap.get(maxAlignPos+p);
								tempConcurrentActivityPositionTraceIndicesSetMap.put(maxAlignPos+p+1, tempTraceIndicesSet);
							}
							if(concurrentActivityPositionTraceIndicesSetMap.containsKey(maxAlignPos+1)){
//								Set<Integer> tempTraceIndicesSet = concurrentActivityPositionTraceIndicesSetMap.get(maxAlignPos+1);
//								concurrentActivityPositionTraceIndicesSetMap.put(maxAlignPos+2, tempTraceIndicesSet);
							
								concurrentActivityPositionTraceIndicesSet.addAll(concurrentActivityPositionTraceIndicesSetMap.get(maxAlignPos+1));
							}
							System.out.println("Concurrent Acitivy Pos Trace Indices Set: "+concurrentActivityPositionTraceIndicesSet);
							tempConcurrentActivityPositionTraceIndicesSetMap.put(maxAlignPos+1, concurrentActivityPositionTraceIndicesSet);
						}else{
							if(!refinedAlignmentPositionList.contains(pos)){
								refinedAlignmentPositionList.add(pos);
								tempConcurrentActivityPositionTraceIndicesSetMap.put(pos, concurrentActivityPositionTraceIndicesSetMap.get(pos));
							}
						}
					}else{
						if(!refinedAlignmentPositionList.contains(pos)){
							refinedAlignmentPositionList.add(pos);
							tempConcurrentActivityPositionTraceIndicesSetMap.put(pos, concurrentActivityPositionTraceIndicesSetMap.get(pos));
						}
					}
				}
				
				tracePositionIndicesList.clear();
				Set<Integer> sortedRefinedAlignmentPositionSet = new TreeSet<Integer>();
				sortedRefinedAlignmentPositionSet.addAll(refinedAlignmentPositionList);
				tracePositionIndicesList.addAll(sortedRefinedAlignmentPositionSet);
				System.out.println("Refined Alignment Position List: "+tracePositionIndicesList);

				alignmentMatrix = new String[noTraces][refinedAlignmentMatrix[0].length
						+ tracePositionIndicesList.size()];

				int prevIndex = 0;
				index = 0;
				int p = 0;
				int noTraceIndexPositions = tracePositionIndicesList.size();
				
				for (int k = 0; k < noTraceIndexPositions; k++) {
					index = tracePositionIndicesList.get(k) + k;

					for (int j = prevIndex; j < tracePositionIndicesList.get(k); j++) {
						for (int i = 0; i < noTraces; i++) {
							alignmentMatrix[i][p] = refinedAlignmentMatrix[i][j];
						}
						p++;
					}
					concurrentActivityPositionTraceIndicesSet = tempConcurrentActivityPositionTraceIndicesSetMap
							.get(tracePositionIndicesList.get(k));
					for (int i = 0; i < noTraces; i++) {
						if (concurrentActivityPositionTraceIndicesSet
								.contains(i))
							alignmentMatrix[i][p] = concurrentActivity;
						else
							alignmentMatrix[i][p] = dash;
					}
					p++;
					prevIndex = tracePositionIndicesList.get(k);
				}

				for (int j = prevIndex; j < alignmentLength; j++) {
					for (int i = 0; i < noTraces; i++) {
						alignmentMatrix[i][p] = refinedAlignmentMatrix[i][j];
					}
					p++;
				}
				alignmentLength = alignmentMatrix[0].length;
				String[] refinedAlignment = new String[noTraces];
				StringBuilder strBuilder = new StringBuilder();
				for (int i = 0; i < noTraces; i++) {
					strBuilder.setLength(0);
					for (int j = 0; j < alignmentLength; j++) {
						System.out.print(alignmentMatrix[i][j]);
						strBuilder.append(alignmentMatrix[i][j]);
					}
					refinedAlignment[i] = strBuilder.toString();
					System.out.println();
				}

				return refinedAlignment;
				// TreeSet<Integer> sortedPositionSet = new TreeSet<Integer>();
				// sortedPositionSet.addAll(concurrentActivityPositionTraceIndicesSetMap.keySet());
				// int alignmentIndex;
				// String[] refinedAlignmentStrArray = new String[noTraces];

				// for(Integer pos : sortedPositionSet){
				// concurrentActivityPositionTraceIndicesSet =
				// concurrentActivityPositionTraceIndicesSetMap.get(pos);
				// for(Integer traceIndex :
				// concurrentActivityPositionTraceIndicesSet){
				// for(int i = 0; i < alignmentLength; i++){
				// if(alignmentMatrix[traceIndex][i].equals(dash))
				// }
				// }
				// }
			}
		}
		return originalAlignment;
	}

	private String[][] performBlockShiftConcurrentPrunedAlignment(
			String[][] alignmentMatrix) {
		int noTraces = alignmentMatrix.length;
		int alignmentLength = alignmentMatrix[0].length;

		List<Set<String>> alignmentColumnActivitySetList = new ArrayList<Set<String>>();

		Set<String> alignmentColumnActivitySet = new HashSet<String>();

		for (int j = 0; j < alignmentLength; j++) {
			alignmentColumnActivitySet = new HashSet<String>();
			for (int i = 0; i < noTraces; i++) {
				alignmentColumnActivitySet.add(alignmentMatrix[i][j]);
			}
			alignmentColumnActivitySet.remove(dash);
			alignmentColumnActivitySetList.add(alignmentColumnActivitySet);
//			System.out.println(j + " @ " + alignmentColumnActivitySet);
		}

		int index;
		String temp;
		boolean isSwapped;
		boolean debug = false;
		for (int j = 0; j < alignmentLength; j++) {
			isSwapped = false;
			alignmentColumnActivitySet = alignmentColumnActivitySetList.get(j);
			for (int i = 0; i < noTraces; i++) {
				if (!alignmentMatrix[i][j].equals(dash))
					continue;
				// System.out.println("Trace "+i+", "+j);
				index = j + 1;
				while (index < alignmentLength
						&& alignmentMatrix[i][index].equals(dash))
					index++;
				if (index != j
						&& index < alignmentLength
						&& alignmentColumnActivitySet
								.contains(alignmentMatrix[i][index])) {
					// System.out.println("Found in Trace "+i+", "+j+", "+index);
					// swap j and index for trace i
					temp = alignmentMatrix[i][j];
					alignmentMatrix[i][j] = alignmentMatrix[i][index];
					alignmentMatrix[i][index] = temp;
					isSwapped = true;
				}
			}
			if (isSwapped && debug) {
				System.out.println("----");
				for (int k = 0; k < noTraces; k++) {
					for (int m = 0; m < alignmentLength; m++) {
						System.out.print(alignmentMatrix[k][m]);
					}
					System.out.println();
				}
				System.out.println("----");
			}
		}

		if(debug){
			// print alignment matrix
			System.out.println("Alignment Matrix");
			printAlignment(alignmentMatrix);
	
			System.out.println("Gap Columns Removed");
			alignmentMatrix = removeGapColumns(alignmentMatrix);
			printAlignment(alignmentMatrix);
		}
		return alignmentMatrix;
	}

	private String[][] removeGapColumns(String[][] alignmentMatrix) {
		int noTraces = alignmentMatrix.length;
		int alignmentLength = alignmentMatrix[0].length;

		List<Integer> gapColumnList = new ArrayList<Integer>();

		boolean isGapColumn;
		for (int j = 0; j < alignmentLength; j++) {
			isGapColumn = true;
			for (int i = 0; i < noTraces; i++) {
				if (!alignmentMatrix[i][j].equals(dash)) {
					isGapColumn = false;
					break;
				}
			}
			if (isGapColumn)
				gapColumnList.add(j);
		}

		if (gapColumnList.size() == 0) {
			return alignmentMatrix;
		} else {
			String[][] refinedMatrix = new String[noTraces][alignmentLength
					- gapColumnList.size()];
			int index = 0;
			for (int j = 0; j < alignmentLength; j++) {
				if (gapColumnList.contains(j))
					continue;
				for (int i = 0; i < noTraces; i++) {
					refinedMatrix[i][index] = alignmentMatrix[i][j];
				}
				index++;
			}
			return refinedMatrix;
		}

	}

	public boolean isValidRefinement(){
		return isValidRefinement;
	}
	
	@SuppressWarnings("unused")
	public static void main(String[] args) {
		String[] profileAlignment = { "jgc-f----leb--dkl-ebdklebdi",
				"jgclf-----eb--dkl-ebdklebdi", "jgclfebdkleb--dkl-ebdklebdi",
				"jgcl------eb-fdkl-ebdklebdi", "jgcl------ebdf-kl-ebdklebdi",
				"jgcl------eb--dklfebdklebdi", "jgcl---------f----ebdklebdi",
				"jgc----------f--l-ebdklebdi" };
		String[] profileAlignment1 = { "jgc-lfe-b-d-i", "jgc-lfe-b-d--",
				"jgc-l-efb-d-i", "jgc-l-e-b-dfi", "jgc-l-e-b-df-",
				"jgcfl-e-b-d-i", "jgcfl-e-b-d--", "jgc-l-e-bfd-i",
				"jgc-l-e-bfd--", "jgc-l-e------", "jgc-l--------" };
		String[] profileAlignment2 = { "jgclf--ebdklebdi", "jgc-fl-ebdklebdi",
				"jgc-fah-bdklebdi", "jgc-fah-bdklebd-", "jgcaf-h-bdklebdi" };
		String[] profileAlignment3 = {
				"jgcl-f-eb--di",
				"jgcl-f-eb--d-",
				"jgclef--b--di",
				"jgc--fleb--di",
				"jgc--fleb--d-",
				"jgcl---ebdf-i",
				"jgcl---ebdf--",
				"jgcl---eb-fdi",
				"jgcl---eb-fd-"
		};
		String[] profileAlignment4 ={
				"jgcl-f-ebdi",
				"jgcl-f-ebd-",
				"jgclef--bdi",
				"jgc--flebdi",
				"jgc--flebd-",	
		};

		String[] profileAlignment5 = {
				"jgc-l---ebfd-k-l-e-b-d-kl-ebd-----i",
				"jgc-l---eb-dfk-l-e-b-d-kl-ebd-----i",
				"jgc-l---eb-d-k-lfe-b-d-kl-ebd-----i",
				"jgcfl---eb-d-k-l-e-b-d-kl-ebd-----i",
				"jgc-lf--eb-d-k-l-e-b-d-kl-ebd-----i",
				"jgc-lf--eb-d-k-l-e-b-d-kl-ebdklebdi",
				"jgc-a-h--bfd-k-a-h-b-d-kl-ebd-----i",
				"jgc-a-h--b-dfk-a-h-b-d-kl-ebd-----i",
				"jgc-afh--b-d-k-a-h-b-d-kl-ebd-----i",
				"jgc-a-h--b-d-k-afh-b-d-kl-ebd-----i",
				"jgc-a-h--b-d-k-afh-b-d-kl-ebd------",
				"jgc-a-h--b-d-k-a-h-bfd-kl-ebd-----i",
				"jgc-l---e-f-----f--b-d-kl-ebd-----i",
				"jgc-a-h---f-----f--b-d-kl-ebd-----i",
				"jgc-a-h------------b-d-kl-ebd-----i",
				"jgc-l---e----------b-dfkl-ebd-----i",
				"jgc-l---e----------b-dfkl-ebd------",
				"jgc-l---e----------b-d-klfebd-----i",
				"jgc-l---e----------b-d-kl-ebd-----i",
				"jgc-l---e----------b-d-kl-ebd------",
				"jgc-lf--e----------b-d-kl-ebd-----i",
				"jgcfl---e----------b-d-kl-ebd-----i",
				"jgcfa-h------------b-d-kl-ebd-----i",
				"jgcfa-h------------b-d-kl-ebd------",
				"jgc-afh------------b-d-kl-ebd-----i",
				"jgc-a-h--b-d-k-afh-b-d-ka-hbd-----i",
				"jgc-a-h--b-d-k-afh-b-d-ka-hbd------",
				"jgc-a-h--b-d-k-a-h-bfd-ka-hbd-----i",
				"jgc-a-h--b-d-k-a-h-b-d-kafhbd-----i",
				"jgc-a-h--b-d-k-a-h-b-d-kafhbd------",
				"jgc-a-h--b-d-k-a-h-b-dfka-hbd-----i",
				"jgc-a-h--b-d-k-a-h-b-dfka-hbd------",
				"jgc-a-h--b-dfk-a-h-b-d-ka-hbd-----i",
				"jgc-a-h--bfd-k-a-h-b-d-ka-hbd-----i",
				"jgc-a-h--bfd-k-a-h-b-d-ka-hbd------",
				"jgc-afh--b-d-k-a-h-b-d-ka-hbd-----i",
				"jgc-afh--b-d-k-a-h-b-d-ka-hbd------",
				"jgc-a-hf-b-d-k-a-h-b-d-ka-hbd-----i",
				"jgcfa-h--b-d-k-a-h-b-d-ka-hbd-----i",
				"jgcfa-h--b-d-k-a-h-b-d-ka-hbd------",
				"jgc-a-h--b-dfk-a-h-b-d------------i",
				"jgc-a-h--b-dfk-a-h-b-d-------------",
				"jgc-a-h--b-d-kfa-h-b-d------------i",
				"jgc-a-h--bfd-k-a-h-b-d------------i",
				"jgc-a-h--bfd-k-a-h-b-d-------------",
				"jgc-a-h--b-d-k-afh-b-d------------i",
				"jgc-a-h--b-d-k-afh-b-d-------------",
				"jgc-a-h--b-d-k-a-h-bfd------------i",
				"jgc-a-h--b-d-k-lfe-b-d------------i",
				"jgc-a-h--b-d-k-lfe-b-d-------------",
				"jgc-a-h--b-d-k-l-e-bfd------------i",
				"jgc-afh--b-d-k-a-h-b-d------------i",
				"jgc-afh--b-d-k-a-h-b-d-------------",
				"jgc-a-hf-b-d-k-a-h-b-d------------i",
				"jgc-a-h--b-d-k-a-h-b-df-----------i",
				"jgc-a-h--b-d-k-a-h-b-df------------",
				"jgcfa-h--b-d-k-a-h-b-d------------i",
				"jgcfa-h--b-d-k-a-h-b-d-------------",
				"jgc-l-----------fe-b-d------------i",
				"jgc-l-----------fe-b-d-------------",
				"jgc-l------------efb-d------------i",
				"jgcfl------------e-b-d------------i",
				"jgcfl------------e-b-d-------------",
				"jgc-l------------e-b-df-----------i",
				"jgc-l------------e-b-df------------",
				"jgc-l------------e-bfd------------i",
				"jgc-l------------e-bfd-------------",
				"jgc-l------------e-----------------",
				"jgc-l------------------------------",
				"jgc-a-----------fh-b-d------------i",
				"jgc-a-----------fh-b-d-------------",
				"jgcfa------------h-b-d------------i",
				"jgcfa------------h-b-d-------------",
				"jgc-a------------h-b-df-----------i",
				"jgc-a------------h-b-df------------",
				"jgc-a------------h-bfd------------i",
				"jgc-a------------h-bfd-------------"
		};
		
		String[] profileAlignment6 = {
				"jgc-fahbdklebdi",
				"jgc-fahbdklebd-",
				"jgcaf-hbdklebdi"
		};
		String[] profileAlignment7 = {
		"jgc--l-efb-dk-le-bdi",
		"jgc--ah-fb-dk-le-bdi",
		"jgc--ah--bfdk-le-bdi",
		"jgc--l-e-b-dkfle-bdi",
		"jgc--l-e-b-dkfle-bd-",
		"jgc--l-e-b-dk-lefbdi",
		"jgc--l-e-bfdk-le-bdi",
		"jgc--l-e-bfdk-le-bd-",
		"jgclf--e-b-dk-le-bdi",
		"jgc-fl-e-b-dk-le-bdi",
		"jgc-fah--b-dk-le-bdi",
		"jgc-fah--b-dk-le-bd-",
		"jgcaf-h--b-dk-le-bdi"
		};

		String[] profileAlignment8 = {
				"jgc-a-h-bfd-ka-hb-d-kl-ebdi",
				"jgc-a-h-b-dfka-hb-d-kl-ebdi",
				"jgc-afh-b-d-ka-hb-d-kl-ebdi",
				"jgc-a-h-b-d-kafhb-d-kl-ebdi",
				"jgc-a-h-b-d-kafhb-d-kl-ebd-",
				"jgc-a-h-b-d-ka-hbfd-kl-ebdi",
				"jgc-l--e------f-b-d-kl-ebdi",
				"jgc-a-h-------f-b-d-kl-ebdi",
				"jgc-a-h-------f-b-d-kl-ebdi",
				"jgc-l--e--------b-dfkl-ebdi",
				"jgc-l--e--------b-dfkl-ebd-",
				"jgc-l--e--------b-d-klfebdi",
				"jgc-l--e------f-b-d-kl-ebdi",
				"jgc-l--e------f-b-d-kl-ebd-",
				"jgc-lf-e--------b-d-kl-ebdi",
				"jgcfl--e--------b-d-kl-ebdi",
				"jgcfa-h---------b-d-kl-ebdi",
				"jgcfa-h---------b-d-kl-ebd-",
				"jgc-afh---------b-d-kl-ebdi"
		};
		
		String[] profileAlignment9 = {
				"jgcl-eb-fdklebdi",
				"jgcah-b-fdklebdi",
				"jgcah-bdf-klebdi"
		};

		String[] profileAlignment10 = {
				"jgca-fhbdkahbdkahbdi",
				"jgca-fhbdkahbdkahbd-",
				"jgcahf-bdkahbdkahbdi"
		};

		String[] profileAlignment11 = {
				"XKKKKZd00002arar0rararGt--tt",
				"XKKKKZd00002ara--rar--GtWEtt",
				"X-K--Zd020-rara--rararGQY--t"
		};
		
		String[] profileAlignment12 = {
				"XKKKKZdh--hhhkhoPowt-tt",
				"XKKKKZdh--hhhkPoPowt-tt",
				"XKKKKZdh--hhhk--PowtWtt",
				"XKKKKZdh--hhhk--Powt-tt",
				"XKKKKZdhkPhhh----owt-tt"
		};

		String[] profileAlignment13 = {
				"g0--c5--b0e0a5------c2f6g7h1a2f1--e1--------f0f5--------------------b3e3------c0--d3g9a7e6c1b7h2--------g6----b2d4------e5f7----------d6------c7g8----",
				"g0--c5--b0e0a5------c2f6g7h1a2f1--e1--------f0f5--------------------b3e3------c0--d3----e6c1b7----------g6----a7d4------e5f7------h2b2d6------c7g8----",
				"g0b8c5--b0e0a5------c2f6g7h1a2f1--e1--------f0f5a8----c4d1--------h3b3e3------c0a7d3----e6c1b7----------g6h2--b2d4------e5f7----------d6------c7g8----",
				"g0--c5--b0e0a5a9e9a5c2f6g7h1a2f1--e1--------f0f5--------------------b3e3--a7--c0--------e6c1b7----------g6d3g9--d4------e5f7----------d6------c7g8h2b2",
				"g0--c5--b0e0a5------c2f6g7h1a2f1--e1--------f0f5e6c1b7--a7--g6--d4--b3e3------c0--d3--------------------------------------f7----------d6--g8e5c7h2--b2",
				"g0--c5--b0e0a5------c2f6g7h1a2f1--e1--------f0--e7------a7f5g6--d4--b3e3------c0--d3------------------------------------e5f7----------d6--g8--c7h2--b2",
				"g0--c5--b0e0a5------c2f6g7h1a2f1--e1--------f0f5--------a7--e6c1b7--b3e3------c0--d3----------h2b2------g6------d4------e5f7----------d6--g8--c7------",
				"g0--c5--b0e0a5------c2f6g7h1a2f1--e1--------f0f5--------a7--e7------b3e3------c0--d3----------h2b2------g6------d4------e5f7----------d6--g8--c7------",
				"g0--c5--b0e0a5------c2f6g7h1a2f1--e1--------f0f5e7------a7--g6--d4--b3e3------c0--d3----------h2b2----------------------e5f7----------d6--g8--c7------",
				"g0--c5--b0e0a5------c2f6g7h1a2f1--e1--------f0f5--------------------b3e3f3a7e7c0--d3----------h2b2------g6------d4------e5f7----------d6------c7g8----",
				"g0--c5--b0e0a5------c2f6g7h1a2f1--e1--------f0f5--------------------b3e3------c0--d3--a7------h2b2--e7--g6------d4------e5f7----------d6------c7g8----",
				"g0--c5--b0e0a5------c2f6g7h1a2f1--e1--------f0f5--------------------b3e3------c0--d3----------h2b2a7e7--g6------d4------e5f7----------d6--g8--c7------",
				"g0--c5--b0e0a5------c2f6g7h1a2f1--e1--------f0f5--------------------b3e3------c0--d3--a7------h2b2--e7--g6------d4------e5f7----------d6--g8--c7------",
				"g0--c5--b0e0a5------c2f6g7h1a2f1--e1--------f0f5--------------------b3e3------c0--d3g9--e7--a7h2b2------g6------d4b1e7g6d4f7----------d6--g8--c7------",
				"g0--c5b9b0e0a5------c2f6g7h1a2f1--e1--------f0f5--------------------b3e3------c0--d3----e7--------------g6------d4--------f7----------d6--g8--c7a7h2b2",
				"g0--c5--b0e0a5------c2f6g7h1a2f1--e1--------f0f5--------------------b3e3------c0--d3----e6c1b7h2b2--a7--g6------d4--------f7--e5------d6--g8--c7------",
				"g0b8c5--b0e0a5------c2f6g7h1a2f1--e1--------f0f5--------------------b3e3------c0--d3----e7----h2b2--a7--g6------d4--------f7--e5------d6c7g8----------",
				"g0--c5--b0e0a5------c2f6g7h1a2f1a7e1--------f0f5--------------------b3e3------c0--d3----e7--------------g6------d4--------f7--e5--h2b2d6--g8--c7------",
				"g0--c5--b0e0a5------c2f6g7h1a2f1a7e1--------f0f5--------------------b3e3------c0--d3g9--e7--------------g6------d4--------f7d6c7g8h2b2----------------",
				"g0b8c5--b0e0a5------c2f6g7h1a2f1--e1a7e6--c1f0f5--------------------b3e3------c0--d3----------------g9b7g6------d4----h2--f7--e5----b2d6--g8--c7------",
				"g0b8c5--b0e0a5------c2f6g7h1a2f1--e1a7------f0f5--------------------b3e3------c0--d3----------------e7h2g6------d4--------b2--e5----f7d6--g8--c7------",
				"g0b8c5--b0e0a5------c2f6g7h1a2f1--e1a7------f0f5--------------------b3e3------c0--d3----------h2b2--e7--g6------d4--------f7--e5------d6--g8--c7------",
				"g0--c5--b0e0a5------c2f6g7h1a2f1--e1a7------f0f5--------------------b3e3------c0--d3----------h2b2--e7--g6------d4--------f7--e5------d6--g8--c7------",
				"g0--c5--b0e0a5------c2f6g7h1a2f1--e1a7------f0f5--------------------b3e3------c0--d3----------------e7--g6------d4--------f7--e2------d6--g8--c7h2--b2",
				"g0--c5--b0e0a5------c2f6g7h1a2f1--e1a7e7----f0f5--------------------b3e3------c0--d3----------h2b2------g6------d4--------f7----------d6------c7g8----",
				"g0--c5b9b0e0a5------c2f6g7h1a2f1--e1--e7a7--f0f5--------------------b3e3------c0--d3g9--------h2b2------g6------d4--------f7--e5------d6------c7g8----",
				"g0--c5b9b0e0a5------c2f6g7h1a2f1--e1a7e7----f0f5--------------------b3e3------c0--d3----------h2b2------g6------d4--------f7--e5------d6--g8--c7------",
				"g0--c5b9b0e0a5------c2f6g7h1a2f1--e1a7e7----f0f5a8------------------b3e3------c0--d3g9------------------g6------d4--------f7----------d6------c7g8h2b2"
		};
		
		int encodingLength = 2;
		RefineAlignment r = new RefineAlignment(encodingLength);
//		String[] refinedAlignment = r.refineConcurrent(1, profileAlignment8, true);
//		System.out.println("Refined Alignment");
//		for(String refinedTrace : refinedAlignment)
//			System.out.println(refinedTrace);
//		System.out.println(r.isValidRefinement());
//		String[] refinedAlignment = r.performBlockShiftLeft(profileAlignment13);
//		String[] refinedAlignment = r.refineConcurrent(1, profileAlignment,true);
//		System.out.println(r.isValidRefinement());
//		System.out.println("Refined Alignment");
//		r.printAlignment(refinedAlignment);
		
		r.getConcurrentActivities(profileAlignment13, true);
	}
}
