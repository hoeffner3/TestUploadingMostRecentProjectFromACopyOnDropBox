package org.processmining.plugins.tracealignmentwithguidetree.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import sen.outlierdetector.EventInfo;


public class EventInfoStructurer {
	Map<String, Map<Integer, EventInfo>> structuredEventInfo;
	
	EventInfoStructurer() {
	}
	
	void structureEventInfo(ArrayList<EventInfo> unstructuredEventInfoList) {
		structuredEventInfo = new HashMap<String, Map<Integer, EventInfo>>();

		Iterator<EventInfo> iterator = unstructuredEventInfoList.iterator();
		while (iterator.hasNext()) {
			EventInfo currentEventInfo = iterator.next();
			String currentCaseID = currentEventInfo.getcase_id();
			Integer currentEventLocation = currentEventInfo.getlocation();
			if (structuredEventInfo.containsKey(currentCaseID)) { // if trace exists 
				if (structuredEventInfo.get(currentCaseID).containsKey(currentEventLocation)) {  // if event exists
					// ERROR EVENT CONTAINING THE SAME CASEID AND EVENTLOCATION EXIST
				}
				else { // trace exists.  event doesn't exist.
					// all we have to do here is create an inner hashmap and add it to the outer
					//figure out how to access the already existing trace
					Map<Integer, EventInfo> alreadyExistingTrace = structuredEventInfo.get(currentCaseID);
					alreadyExistingTrace.put(currentEventLocation, currentEventInfo);  // event added to trace
					//structuredEventInfo.put(currentCaseID, traceToAdd);  // trace added to list of traces
				}
			}
			else { // event doesn't exist.  trace doesn't exist.
				Map<Integer, EventInfo> traceToAdd = new HashMap<Integer, EventInfo>();  // trace created
				traceToAdd.put(currentEventLocation, currentEventInfo);  // event added to trace
				structuredEventInfo.put(currentCaseID, traceToAdd);  // trace added to list of traces
				
			}
		}
		
	}
	
	Map<String, Map<Integer, EventInfo>> getStructuredEventInfo() {
		return structuredEventInfo;
	}
}
