package org.processmining.plugins.tracealignmentwithguidetree.exceptions;

@SuppressWarnings("serial")
public class ActivityNotFoundException extends Exception {
	String activity;
	
	public ActivityNotFoundException(String activity){
		this.activity = activity;
	}
	
	public String getActivity(){
		return activity;
	}
}
