package org.processmining.plugins.tracealignmentwithguidetree.listeners;

import java.awt.Color;

public interface ActivityColorListener {
	public void activityColorMappingChanged(Color color);
	public void activityColorChanged(String encodedActivity, Color color);
}
