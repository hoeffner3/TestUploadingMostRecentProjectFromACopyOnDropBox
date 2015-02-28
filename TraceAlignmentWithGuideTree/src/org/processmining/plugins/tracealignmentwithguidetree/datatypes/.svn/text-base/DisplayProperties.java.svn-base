package org.processmining.plugins.tracealignmentwithguidetree.datatypes;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import javax.swing.JOptionPane;

import org.processmining.plugins.tracealignmentwithguidetree.listeners.AlignmentListener;
import org.processmining.plugins.tracealignmentwithguidetree.listeners.DisplayPropertiesListener;
import org.processmining.plugins.tracealignmentwithguidetree.listeners.SequenceListener;

public class DisplayProperties implements AlignmentListener, SequenceListener{
	int encodingLength;
	
	private final Alignment alignment;

	// Activity text font
	private Font font;

	// Activity font metrics
	private FontMetrics fontMetrics;

	// Activity width, height
	private int activityWidth, activityHeight;

	// Activity font x-offset y-offset
	private int fontXOffset, fontYOffset;

	// spacing 
	private int spacingWidth;

	// cursor position details
	int cursorRow = 0, cursorColumn = 0;
	int previousCursorRow = 0, previousCursorColumn = 0;

	// Set of selected and hidden sequences

	Set<Sequence> selectedSequencesSet = new HashSet<Sequence>();
	Set<Sequence> hiddenSequencesSet = new HashSet<Sequence>();

	// is cursor in RulerAnnotationComponent or SequenceAnnComponent?
	private boolean cursorHidden = false;

	// Set whether fast rendering mode or not
	private boolean fastRender = true;

	// Set whether to render gaps or not
	private boolean renderGaps = true;

	// Set whether encodedActivity is to be displayed or not
	private boolean renderEncodedActivity = true;

	// Set whether the background for activity is square or not
	private boolean isActivityBackGroundSquare = true;

	private boolean isAlignmentConcurrentRefined = false;
	private boolean isAlignmentBlockShifted = false;
	private boolean isDisplayConcurrentActivities = false;
	private boolean isDisplayConcurrentActivitiesAcrossAllTraces = false;
	
	// Set whether group editing
	private final boolean isGroupEditing = false;
	private boolean isSeqAnnEditing = false;
	private boolean isRulerEditing = false;
	private boolean isAlignmentRecentlyEdited = false;
	
	private final boolean isMapping = false; // if true, we are column mapping...always true when printing and when columns are hidden

	// Capture the sequences and the activity positions that are highlighted
	Map<Sequence, TreeSet<Integer>> sequenceActivityHighLightsMap = new HashMap<Sequence, TreeSet<Integer>>();

	// List of listeners
	List<DisplayPropertiesListener> listenersList = new ArrayList<DisplayPropertiesListener>();
	
	public DisplayProperties(Alignment alignment, Font font, FontMetrics fontMetrics,
			boolean fastRender, boolean renderGaps) {
		this.encodingLength = alignment.getEncodingLength();
		this.alignment = alignment;
		this.fastRender = fastRender;
		this.renderGaps = renderGaps;

		setFont(font, fontMetrics);

		alignment.addListener(this);

		int noSequencesInAlignment = alignment.getNoSequences();
		
		try{
			for (int i = 0; i < noSequencesInAlignment; i++) {
				alignment.getSequence(i).addListener(this);
			}
		}catch(NullPointerException e){
			JOptionPane.showMessageDialog(alignment.owner,"Null Pointer Exception when Accessing a Sequence in an Alignment", "Null Pointer Exception", JOptionPane.ERROR_MESSAGE);
		}
	}

	private void setFont(Font font, FontMetrics fontMetrics) {
		this.font = font;
		this.fontMetrics = fontMetrics;

		if (fastRender) {
			activityWidth = fontMetrics.stringWidth("A") * encodingLength + 3;
			fontXOffset = 3;
		} else {
			activityWidth = fontMetrics.stringWidth("A") * encodingLength + 3;
			fontXOffset = 1;
		}

		spacingWidth = (int) (activityWidth * 1.5);
		activityHeight = fontMetrics.getMaxAscent() + fontMetrics.getMaxDescent() + 5;
		fontYOffset = fontMetrics.getMaxAscent() + 3;

		for (DisplayPropertiesListener listener : listenersList) {
			listener.displayFontChanged(this);
		}
	}
	
	public void addListener(DisplayPropertiesListener listener) {
		listenersList.add(listener);
	}
	
	public void removeListener(DisplayPropertiesListener listener) {
		listenersList.remove(listener);
	}
	
	public Color inverseRGB(Color c) {
		int brightness = c.getRed() + c.getGreen() + c.getBlue();

		if (brightness < 382) {
			return Color.white;
		} else {
			return Color.black;
		}
	}
	
	// select a particular sequence
	public void setIsSequenceSelected(Sequence sequence, boolean isSelected) {
		boolean notify = false;

		if (isSelected && selectedSequencesSet.add(sequence)) {
			notify = true;
		} else if (!isSelected && selectedSequencesSet.remove(sequence)) {
			notify = true;
		}
		if (notify) {
			for (DisplayPropertiesListener listener : listenersList) {
				listener.displaySeqSelectChanged(this, sequence, isSelected);
			}
		}
	}
	
	public void clearSelections() {
		if (selectedSequencesSet.size() < 1) {
			return;
		}
		
		for (Sequence sequence : selectedSequencesSet) {
			for (DisplayPropertiesListener listener : listenersList) {
				listener.displaySeqSelectChanged(this, sequence, false);
			}
		}
		
		selectedSequencesSet.clear();
	}
	
	// fire sequence selected listeners
	public void fireSeqSelect(Sequence sequence) {
		boolean select = selectedSequencesSet.contains(sequence);
		for (DisplayPropertiesListener listener : listenersList) {
			listener.displaySeqSelectChanged(this, sequence, select);
		}
	}
	
	public int sign(int a) {
		return (a >= 0) ? 1 : -1;
	}
	
	public void setRenderGaps(boolean renderGaps) {
		if (this.renderGaps != renderGaps) {
			this.renderGaps = renderGaps;
			for (DisplayPropertiesListener listener : listenersList) {
				listener.displayRenderGapsChanged(this);
			}
		}
	}
	
	public void setRenderEncodedActivity(boolean showEncodedActivity) {
		if (renderEncodedActivity != showEncodedActivity) {
			renderEncodedActivity = showEncodedActivity;
			for (DisplayPropertiesListener listener : listenersList) {
				listener.displayRenderGapsChanged(this);
			}
		}
	}

	public void setActivityBackgroundSquare(boolean activityBackgroundSquare) {
		if (isActivityBackGroundSquare != activityBackgroundSquare) {
			isActivityBackGroundSquare = activityBackgroundSquare;
			for (DisplayPropertiesListener listener : listenersList) {
				listener.activityBackgroundChanged(this);
			}
		}
	}
	
	public void fireActivityBackGroundChanged(){
		for (DisplayPropertiesListener listener : listenersList) {
			listener.activityBackgroundChanged(this);
		}
	}
	
	public void setCursorHidden(boolean b) {
		cursorHidden = b;
	}
	
	public boolean isSequenceHidden(int row) {
		return hiddenSequencesSet.contains(alignment.getSequence(row));
	}
	
	@SuppressWarnings("unused")
	public void setSeqHidden(Sequence s, boolean hidden) {
		boolean notify = false;

		if (hidden && hiddenSequencesSet.add(s)) {
			notify = true;
		} else if (!hidden && hiddenSequencesSet.remove(s)) {
			notify = true;
		}
	}

	
	//TODO getSeqSelect is changed to isSequenceSelected
	public boolean isSequenceSelected(Sequence s) {
		return selectedSequencesSet.contains(s);
	}

	public boolean isMapping() {
		return isMapping;
	}
	
	public boolean isGapRendered() {
		return renderGaps;
	}
	
	public void setRulerEditing(boolean isRulerEditing){
		this.isRulerEditing = isRulerEditing;
	}
	
	public boolean isRulerEditing(){
		return isRulerEditing;
	}
	
	public void setAlignmentRecentlyEdited(boolean isAlignmentRecentlyEdited){
		this.isAlignmentRecentlyEdited = isAlignmentRecentlyEdited;
	}
	
	public boolean isAlignmentRecentlyEdited(){
		return isAlignmentRecentlyEdited;
	}

	public boolean isEncodedActivityRendered() {
		return renderEncodedActivity;
	}

	public boolean isAlignmentConcurrentRefined(){
		return isAlignmentConcurrentRefined;
	}
	
	public boolean isAlignmentBlockShifted(){
		return isAlignmentBlockShifted;
	}
	
	public void setIsAlignmentConcurrentRefined(boolean isAlignmentConcurrentRefined){
		this.isAlignmentConcurrentRefined = isAlignmentConcurrentRefined;
	}
	
	public void setIsAlignmentBlockShifted(boolean isAlignmentBlockShifted){
		this.isAlignmentBlockShifted = isAlignmentBlockShifted;
	}

	public void setIsDisplayConcurrentActivities(boolean isDisplayConcurrentActivities){
		this.isDisplayConcurrentActivities = isDisplayConcurrentActivities;
	}
	
	public void setIsDisplayConcurrentActivitiesAcrossAllTraces(boolean isDisplayConcurrentActivitiesAcrossAllTraces){
		this.isDisplayConcurrentActivitiesAcrossAllTraces = isDisplayConcurrentActivitiesAcrossAllTraces;
	}
	
	public boolean isDisplayConcurrentActivities(){
		return isDisplayConcurrentActivities;
	}
	
	public boolean isDisplayConcurrentActivitiesAcrossAllTraces(){
		return isDisplayConcurrentActivitiesAcrossAllTraces;
	}
	
	public boolean isActivityBackgroundSquare() {
		return isActivityBackGroundSquare;
	}
	
	public boolean isGroupEditing() {
		return isGroupEditing;
	}

	public int getEncodingLength(){
		return encodingLength;
	}
	
	public Alignment getAlignment(){
		return alignment;
	}
	
	public Font getFont() {
		return font;
	}
	
	public FontMetrics getFontMetrics() {
		return fontMetrics;
	}
	
	public int getFontXOffset() {
		return fontXOffset;
	}

	public int getFontYOffset() {
		return fontYOffset;
	}

	public int getActivityWidth() {
		return activityWidth;
	}

	public int getActivityHeight() {
		return activityHeight;
	}
	
	public int getSpacingWidth(){
		return spacingWidth;
	}

	public int getCursorRow() {
		return cursorRow;
	}

	public int getPrevCursorRow() {
		return previousCursorRow;
	}

	public int getCursorColumn() {
		return cursorColumn;
	}

	public int getPrevCursorColumn() {
		return previousCursorColumn;
	}
	
	public int getXForColumn(int column) {
		int w = getActivityWidth();
		//TODO haven't added the if condition on isMapping; check original
		return column * w;
	}
	
	public Sequence[] getAllSelectedSequences() {
		return getAllSelectedSequences(false);
	}
	
	public Sequence[] getAllSelectedSequences(boolean inViewOrder) {
		Sequence[] selectedSequences = new Sequence[selectedSequencesSet.size()];
		int index = 0;
		if (inViewOrder) {
			Sequence[] sequencesInAlignment = alignment.getSequencesInAlignment();
			for(Sequence sequence : sequencesInAlignment){
				if(selectedSequencesSet.contains(sequence))
					selectedSequences[index++] = sequence;
			}
		} else {
			for (Sequence sequence : selectedSequencesSet) {
				selectedSequences[index++] = sequence;
			}
		}
		return selectedSequences;
	}
	
	public int getNoSelectedSequences() {
		return selectedSequencesSet.size();
	}
	
	public int[] getHighlights(Sequence sequence) {
		TreeSet<Integer> highlightedColumnIndicesSet = sequenceActivityHighLightsMap.get(sequence);

		if ((highlightedColumnIndicesSet == null) || (highlightedColumnIndicesSet.size() == 0)) {
			return null;
		}

		int[] highlightedColumnIndicesArray = new int[highlightedColumnIndicesSet.size()];

		int index = 0;
		for (Integer columnIndex : highlightedColumnIndicesSet) {
			highlightedColumnIndicesArray[index++] = columnIndex.intValue();
		}

		return highlightedColumnIndicesArray;
	}

	public void clearHighlights() {
		if (sequenceActivityHighLightsMap.size() < 1) {
			return;
		}
		
		for (Sequence sequence : sequenceActivityHighLightsMap.keySet()) {
			for (DisplayPropertiesListener listener : listenersList) {
				listener.displayHighlightsChanged(this, sequence);
			}
		}
		sequenceActivityHighLightsMap.clear();
	}

	public Map<String, Color> getEncodedActivityColorMap(){
		return alignment.getEncodedActivityColorMap();
	}
	
	
	public void updateCursor(int newCursorRow, int newCursorColumn) {
		updateCursor(newCursorRow, newCursorColumn, false, false);
	}

	public void updateCursor(int newCursorRow, int newCursorColumn, MouseEvent e) {
		// is meta down
		updateCursor(newCursorRow, newCursorColumn, e.isShiftDown(), e.isControlDown() || e.isMetaDown());
	}

	public void updateCursor(int newCursorRow, int newCursorColumn, boolean isShiftDown, boolean isControlDown) {
		//		System.out.println("updating cursor: isShiftDown="+isShiftDown+" @ isControlDown="+isControlDown);
		previousCursorRow = cursorRow;
		previousCursorColumn = cursorColumn;
		cursorRow = newCursorRow;
		cursorColumn = newCursorColumn;

		if (!cursorHidden) {
			if (!isShiftDown && !isControlDown) {
				clearHighlights();
			}

			int activityIndex = newCursorColumn;

			if (!isShiftDown && !isControlDown) {
				setSeqHighlight(alignment.getSequence(cursorRow), cursorColumn, true);
				alignment.setLastActivityIndexSelected(cursorRow, activityIndex);
			} else if (isShiftDown && !isControlDown) {
				int lastActivityIndex = alignment.getLastActivityIndexSelected();
				int lastActivityRow = alignment.getLastActivityRowSelected();
				if ((lastActivityIndex != -1) && (lastActivityRow == cursorRow)) {
					int sgn = sign(lastActivityIndex - activityIndex);
					for (int i = activityIndex; i != (lastActivityIndex + sgn); i = i + sgn) {
						setSeqHighlight(alignment.getSequence(cursorRow), i, true);
					}
					alignment.setLastActivityIndexSelected(cursorRow, activityIndex);
				} else {
					setSeqHighlight(alignment.getSequence(cursorRow), cursorColumn, true);
					alignment.setLastActivityIndexSelected(cursorRow, activityIndex);
				}
			} else if (!isShiftDown && isControlDown) {
				if (isSeqHighlight(alignment.getSequence(cursorRow), activityIndex)) {
					setSeqHighlight(alignment.getSequence(cursorRow), cursorColumn, false);
				} else {
					setSeqHighlight(alignment.getSequence(cursorRow), cursorColumn, true);
					alignment.setLastActivityIndexSelected(cursorRow, activityIndex);
				}
			} else if (isShiftDown && isControlDown) {
				setSeqHighlight(alignment.getSequence(cursorRow), cursorColumn, true);
				alignment.setLastActivityIndexSelected(cursorRow, activityIndex);
			}

			alignment.clearLastRulerAnnIndexSelected();
			alignment.clearLastColumnIndexSelected();
		}
	}
	
	public boolean isSeqHighlight(Sequence s, int idx) {
		TreeSet<Integer> highLightedIndicesSet = sequenceActivityHighLightsMap.get(s);
		if (highLightedIndicesSet == null) {
			return false;
		}

		return (highLightedIndicesSet.contains(new Integer(idx)));
	}
	
	public void setSeqHighlight(Sequence s, int idx, boolean highlight) {
		boolean notify = false;
		TreeSet<Integer> highLightedIndicesSet = sequenceActivityHighLightsMap.get(s);

		if (highlight) {
			if (highLightedIndicesSet == null) {
				highLightedIndicesSet = new TreeSet<Integer>();
				highLightedIndicesSet.add(new Integer(idx));
				sequenceActivityHighLightsMap.put(s, highLightedIndicesSet);
				notify = true;
			} else if (highLightedIndicesSet.add(new Integer(idx))) {
				notify = true;
			}
		} else {
			if ((highLightedIndicesSet != null) && highLightedIndicesSet.remove(new Integer(idx))) {
				if (highLightedIndicesSet.size() == 0) {
					sequenceActivityHighLightsMap.remove(s);
				}
				notify = true;
			}
		}
		if (notify) {
			for (DisplayPropertiesListener listener : listenersList) {
				listener.displayHighlightsChanged(this, s);
			}
		}
	}
	
	public boolean isSeqAnnEditing(){
		return isSeqAnnEditing;
	}
	
	public void setSeqAnnEditing(boolean b) {
		isSeqAnnEditing = b;
	}
	
	// highlight a particular sequence

	public void setSeqHighlight(Sequence s, int idx, boolean highlight, boolean notify) {
		boolean innerNotify = false;

		TreeSet<Integer> highLightedIndicesSet = sequenceActivityHighLightsMap.get(s);

		if (highlight) {
			if (highLightedIndicesSet == null) {
				highLightedIndicesSet = new TreeSet<Integer>();
				highLightedIndicesSet.add(new Integer(idx));
				sequenceActivityHighLightsMap.put(s, highLightedIndicesSet);
				innerNotify = true;
			} else if (highLightedIndicesSet.add(new Integer(idx))) {
				innerNotify = true;
			}
		} else {
			if ((highLightedIndicesSet != null) && highLightedIndicesSet.remove(new Integer(idx))) {
				if (highLightedIndicesSet.size() == 0) {
					sequenceActivityHighLightsMap.remove(s);
				}
				innerNotify = true;
			}
		}
		if (notify && innerNotify) {
			fireDisplayHighlightsChanged(s);
		}
	}
	
	public void fireDisplayHighlightsChanged(Sequence s) {
		for (DisplayPropertiesListener listener : listenersList) {
			listener.displayHighlightsChanged(this, s);
		}
	}
	
	public void fireDisplayHighlightsChanged(Sequence[] s) {
		for (DisplayPropertiesListener listener : listenersList) {
			listener.displayHighlightsChanged(this, s);
		}
	}
	
	/*
	 * Alignment Listeners implementation
	 */
	
	public void alignmentNameChanged(Alignment alignment) {
		// Nothing to be done for this here; Alignment frame is supposed to handle this
	}

	public void alignmentSeqDeleted(Alignment alignment, int i,
			Sequence sequence) {
		// Nothing to be done for this here;
	}

	public void alignmentSeqSwapped(Alignment alignment, int i, int j) {
		// Nothing to be done for this here;
	}

	public void alignmentSeqActivityChanged(Alignment alignment,
			Sequence sequence) {
		// Nothing to be done for this here;
	}
	
	
	/*
	 * Sequence Listeners implementation
	 */

	public void sequenceActivityChanged(Sequence sequence) {
		sequenceActivityHighLightsMap.remove(sequence);

		// invoke the highlight changed method in the listener
		for (DisplayPropertiesListener listener : listenersList) {
			listener.displayHighlightsChanged(this, sequence);
		}
	}

	public void sequenceNameChanged(Sequence sequence, String oldName)
			throws Exception {
		// Nothing to be done here
	}


	@Override
	public void sequenceAnnotationChanged(Sequence sequence) {
		// TODO Auto-generated method stub
	}

	@Override
	public void sequenceLineAnnotationsChanged(Sequence aaseq) {
		// TODO Auto-generated method stub
		
	}

	public void sequenceColorChanged(Sequence sequence) {
		// Nothing to be done here
	}

	@Override
	public void sequenceNameColorChanged(Color color) {
		// TODO Auto-generated method stub
		
	}
	
}
