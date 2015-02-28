/*
 * Moliang Zhou 
 * Formated trace event class
 */


package sen.outlierdetector;

import org.processmining.plugins.tracealignmentwithguidetree.enumtypes.EventClassification;

public class EventInfo {
	private Double score = 0.0;
	private String case_id;
	private int location =1;
	private String activity;
	private EventClassification eventClassification = EventClassification.Default;
	
	public EventInfo(Double score, String case_id, int location)
	{
		this.score = score;
		this.case_id = case_id;
		this.location = location;
	}
	
	public EventInfo(Double score,String case_id,int location,String activity)
	{
		this.score = score;
		this.case_id = case_id;
		this.location = location;
		this.activity = activity;
	}
	
	public void setName(String activity)
	{
		this.activity = activity;
	}
	
	public void setClassification(EventClassification eventClassification) {
		this.eventClassification = eventClassification;
	}
	
	public Double getScore()
	{
		return score;
	}
	public String getcase_id()
	{
		return case_id;
	}
	public int getlocation()
	{
		return location;
	}
	public String getactivity()
	{
		return activity;
	}
	public EventClassification getClassification() {
		return eventClassification;
	}
}
