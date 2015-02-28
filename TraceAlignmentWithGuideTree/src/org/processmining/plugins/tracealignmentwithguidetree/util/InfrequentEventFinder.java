package org.processmining.plugins.tracealignmentwithguidetree.util;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.processmining.plugins.tracealignmentwithguidetree.datatypes.DisplayProperties;
import org.processmining.plugins.tracealignmentwithguidetree.datatypes.Sequence;
import org.processmining.plugins.tracealignmentwithguidetree.visualization.AlignmentFrame;

//steve class
public class InfrequentEventFinder { // stevenote: I would like to rename this to eventAnalyzer or something more broad as I am going to use this for other things as well
	private final Sequence[] sequencesInAlignment;
	//DisplayProperties displayProperties;
	AlignmentFrame owner;
	//List<String> activityList;
	int minimumAllowableFrequencyNominal;
	Map<String, Integer> activityFrequencyCountMap = new HashMap<String, Integer>();
	double minimumAllowableFrequencyPercent;
	boolean byPercent = false, byNominalValue = false;
	//int activityFrequencyInColumnTable[][];
	int totalNumberOfActivities=0;

	
	
	//public InfrequentEventFinder(AlignmentFrame owner, DisplayProperties displayProperties, int percentFrequency) {
		//this(owner, displayProperties, displayProperties.getAlignment().getAllSequences(), percentFrequency);
	//public InfrequentEventFinder(AlignmentFrame owner, DisplayProperties displayProperties, int percentFrequency) {
		//this(owner, displayProperties.getAlignment().getAllSequences(), percentFrequency);
	public InfrequentEventFinder(DisplayProperties displayProperties, int minimumAllowableFrequency) {
		this(displayProperties.getAlignment().getAllSequences(), minimumAllowableFrequency);
	}
	
	public InfrequentEventFinder(DisplayProperties displayProperties, double minimumAllowableFrequencyPercent) {
		this(displayProperties.getAlignment().getAllSequences(), minimumAllowableFrequencyPercent);
	}
	
	public InfrequentEventFinder(Sequence[] seqs, double minimumAllowableFrequencyPercent) {
		byPercent = true;
		sequencesInAlignment = seqs; 
		//this.owner = owner;
		this.minimumAllowableFrequencyPercent = minimumAllowableFrequencyPercent;
		//activityList = new ArrayList<String>();
		//activityList.addAll(owner.getEncodedActivityColorMap().keySet());		
	}
		
		
	//protected InfrequentEventFinder(AlignmentFrame owner, DisplayProperties displayProperties, Sequence[] seqs, double percentFrequency) {
	//public InfrequentEventFinder(AlignmentFrame owner, Sequence[] seqs, int percentFrequency) {
	public InfrequentEventFinder(Sequence[] seqs, int minimumAllowableFrequency) {
		byNominalValue = true;
		sequencesInAlignment = seqs; 
		//this.owner = owner;
		this.minimumAllowableFrequencyNominal = minimumAllowableFrequency;
		//activityList = new ArrayList<String>();
		//activityList.addAll(owner.getEncodedActivityColorMap().keySet());		
	}

	
	public Map<String, Integer> getActivityFrequencyCountMap() {
		return activityFrequencyCountMap;
	}
	
	public Set<String> getInfrequentActivitiesSet() {
		generateActivityFrequencyCountMap();
		Map<String, Integer> prunedActivityFrequencyCountMap = pruneMap(getActivityFrequencyCountMap());
		Set<String> setOfInfrequentActivityNames = prunedActivityFrequencyCountMap.keySet(); 
		return setOfInfrequentActivityNames;		
		
	}
	
	private Map<String, Integer> pruneMap(Map<String, Integer> pruneMe) {
		Iterator it = pruneMe.entrySet().iterator();
		int minimumAllowableFrequency=0;
		while (it.hasNext()) {
			Map.Entry pair = (Map.Entry)it.next();
			if (byNominalValue == true) {
				minimumAllowableFrequency = minimumAllowableFrequencyNominal;
			}
			else if (byPercent == true) {
				//int totalNumberOfActivities=46+46+45+41+44+44;
				minimumAllowableFrequency = (int) (minimumAllowableFrequencyPercent*totalNumberOfActivities);
			}
			if ((int) pair.getValue() >= minimumAllowableFrequency) {
				it.remove();
			}
		}
		return pruneMe;
	}
	
	public void generateActivityFrequencyCountMap() {
		activityFrequencyCountMap.clear();
		totalNumberOfActivities = 0;
		int previousFrequencyCount;
		Sequence currentSequence;
		
		for (int currentSequenceNumber=0; currentSequenceNumber < sequencesInAlignment.length; currentSequenceNumber++) {
			currentSequence = sequencesInAlignment[currentSequenceNumber];
			for (int positionInCurrentSequence = 0; positionInCurrentSequence < currentSequence.getLengthWithGaps(); positionInCurrentSequence++) { // visit every activity in the data set
				String sequenceName = currentSequence.getEncodedActivity(positionInCurrentSequence);
				if (!sequenceName.equals(sequencesInAlignment[0].getDash())) { //TODO stevenote: this is a messy way to get dash think of a better way to do it
					totalNumberOfActivities++;
				}
				
				if (activityFrequencyCountMap.containsKey(sequenceName))
					previousFrequencyCount = activityFrequencyCountMap.get(sequenceName);
				else
					previousFrequencyCount = 0;
				activityFrequencyCountMap.put(sequenceName, previousFrequencyCount+1);
			}
		}
	}
	
	public void printActivityFrequencyCountToFile() {
		generateActivityFrequencyCountMap();
		String outputFileName = "ActivityFrequencyCount.txt";
		String delim = ",";
		String outputDir = System.getProperty("java.io.tmpdir")+"\\TraceAlignmentWithGuideTree";
		FileIO frequencyMapFileIO = new FileIO();
		frequencyMapFileIO.writeToFile(outputDir, outputFileName, activityFrequencyCountMap, delim);
	}
	
	/*
	 * to invoke an infrequent event finder put this in sequencecomponent right inside paintComponent(graphics g)
	 * even though this is a terrible place for it:
	 * 
	 * 		InfrequentEventFinder myInfrequentEventFinder = new InfrequentEventFinder(displayProperties, minFrequencyPercent);

		//stevenote: for now lets just settle for printing the frequency count to the file
		myInfrequentEventFinder.printActivityFrequencyCountToFile();
	 * 
	 * 
	 */
	
	
}