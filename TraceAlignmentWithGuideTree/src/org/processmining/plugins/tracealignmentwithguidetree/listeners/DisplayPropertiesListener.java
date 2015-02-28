package org.processmining.plugins.tracealignmentwithguidetree.listeners;

import org.processmining.plugins.tracealignmentwithguidetree.datatypes.DisplayProperties;
import org.processmining.plugins.tracealignmentwithguidetree.datatypes.Sequence;

public interface DisplayPropertiesListener {
	public void displayAnnViewChanged(DisplayProperties displayProperties, Sequence sequence, boolean show);
	public void displaySeqSelectChanged(DisplayProperties displayProperties, Sequence sequence, boolean select);
//	public void displayColorSchemeChanged(DisplayProperties displayProperties, ColorScheme old);
	public void displayFontChanged(DisplayProperties displayProperties);
	public void displayRenderGapsChanged(DisplayProperties displayProperties);
	public void displayGroupEditingChanged(DisplayProperties displayProperties);
	public void displayOverwriteChanged(DisplayProperties displayProperties);
	public void displayHighlightsChanged(DisplayProperties displayProperties, Sequence sequence);
	public void displayHighlightsChanged(DisplayProperties displayProperties, Sequence[] sequenceArray);
	public void activityBackgroundChanged(DisplayProperties displayProperties);
}	
