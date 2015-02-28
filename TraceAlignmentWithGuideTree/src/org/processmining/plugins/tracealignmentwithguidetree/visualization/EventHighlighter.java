package org.processmining.plugins.tracealignmentwithguidetree.visualization;

import java.awt.Color;
import java.awt.Graphics;
import java.util.HashMap;
import java.util.Map;

import org.processmining.plugins.tracealignmentwithguidetree.enumtypes.EventClassification;

public class EventHighlighter {
	Map<EventClassification, Color> classificationColorMap;
	int OUTLINEWIDTH = 2;
	
	public EventHighlighter() {
		classificationColorMap = new HashMap<EventClassification, Color>();
		fillClassificationColorMap();
	}
	
	void highlightEvent(EventClassification eventClassification, Graphics g, int x, int y, int activityWidth, int activityHeight, boolean isActivityBackgroundSquare) {
		Color deviationColor = classificationColorMap.get(eventClassification);
		g.setColor(deviationColor);			
		
		// set outline color and fill
		if (isActivityBackgroundSquare)
			g.fillRect(x, 0, activityWidth, activityHeight);
		else
			g.fillRoundRect(x, 0, activityWidth, activityHeight, activityWidth, activityHeight);

		// set the color of the hole in the middle and fill
		g.setColor(Color.white);
		if (isActivityBackgroundSquare)
			g.fillRect(x+OUTLINEWIDTH, 0+OUTLINEWIDTH, activityWidth-(2*OUTLINEWIDTH), activityHeight-(2*OUTLINEWIDTH));
		else
			g.fillRoundRect(x+OUTLINEWIDTH, 0+OUTLINEWIDTH, activityWidth-(2*OUTLINEWIDTH), activityHeight-(2*OUTLINEWIDTH), activityWidth-(2*OUTLINEWIDTH), activityHeight-(2*OUTLINEWIDTH));
	}
	
	void fillClassificationColorMap() {
		classificationColorMap.put(EventClassification.MinorError, Color.yellow);
		classificationColorMap.put(EventClassification.MajorError, Color.red);
		classificationColorMap.put(EventClassification.AcceptableDeviation, Color.green);
		classificationColorMap.put(EventClassification.NonProcessDeviation, Color.blue);
		classificationColorMap.put(EventClassification.Outlier, Color.black);
	}
	
	//steve experimenting here
	void highlightEventWhiteCircleInSquare(EventClassification eventClassification, Graphics g, int x, int y, int activityWidth, int activityHeight, boolean isActivityBackgroundSquare, Color myColor) {
		//Color deviationColor = classificationColorMap.get(eventClassification);
		g.setColor(myColor);			
		
		// set outline color and fill
		g.fillRect(x, 0, activityWidth, activityHeight);

		// set the color of the hole in the middle and fill
		g.setColor(Color.white);
		g.fillRoundRect(x+OUTLINEWIDTH, 0+OUTLINEWIDTH, activityWidth-(2*OUTLINEWIDTH), activityHeight-(2*OUTLINEWIDTH), activityWidth-(2*OUTLINEWIDTH), activityHeight-(2*OUTLINEWIDTH));
		
	}
	
	//steve experimenting here
	void highlightEventWithEventColoredInnerCircleClassificationColoredOuterSquare(EventClassification eventClassification, Graphics g, int x, int y, int activityWidth, int activityHeight, boolean isActivityBackgroundSquare, Color myColor) {
		//Color deviationColor = classificationColorMap.get(eventClassification);
			
		
		// set outline
		Color deviationColor = classificationColorMap.get(eventClassification);
		g.setColor(deviationColor);
		g.fillRect(x, 0, activityWidth, activityHeight);
		g.setColor(Color.white);
		g.fillRect(x+OUTLINEWIDTH, 0+OUTLINEWIDTH, activityWidth-(2*OUTLINEWIDTH), activityHeight-(2*OUTLINEWIDTH));
		
		
		// set the color of the hole in the middle and fill
		g.setColor(myColor);
		g.fillRoundRect(x+OUTLINEWIDTH, 0+OUTLINEWIDTH, activityWidth-(2*OUTLINEWIDTH), activityHeight-(2*OUTLINEWIDTH), activityWidth-(2*OUTLINEWIDTH), activityHeight-(2*OUTLINEWIDTH));
		
	}
	
}
