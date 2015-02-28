package org.processmining.plugins.tracealignmentwithguidetree.visualization;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Random;

import javax.swing.JPanel;

import org.processmining.plugins.tracealignmentwithguidetree.datatypes.Alignment;
import org.processmining.plugins.tracealignmentwithguidetree.datatypes.DisplayProperties;
import org.processmining.plugins.tracealignmentwithguidetree.datatypes.Sequence;
import org.processmining.plugins.tracealignmentwithguidetree.enumtypes.EventClassification;
import org.processmining.plugins.tracealignmentwithguidetree.listeners.DisplayPropertiesListener;
import org.processmining.plugins.tracealignmentwithguidetree.listeners.SequenceListener;
import org.processmining.plugins.tracealignmentwithguidetree.util.InfrequentEventFinder;

import sen.outlierdetector.EventInfo;

@SuppressWarnings("serial")
public class SequenceComponent extends JPanel implements SequenceListener, DisplayPropertiesListener{
	// Underlying sequence
	private Sequence sequence = null;

	// Rendering properties
	private final DisplayProperties displayProperties;

	// Rendering colors
	private Color[] foregroundColor, backgroundColor;

	// Whether the sequence is hidden or not
	public boolean isHidden = false;

	public SequenceComponent(Sequence sequence, DisplayProperties displayProperties) {
		super();
		this.sequence = sequence;
		this.displayProperties = displayProperties;

		displayProperties.addListener(this);
		
		foregroundColor = backgroundColor = null;
		setSequence(sequence);
	}
	
	public void setSequence(Sequence sequence) {
		if (this.sequence != null) {
			this.sequence.removeListener(this);
		}

		this.sequence = sequence;
		this.sequence.addListener(this);

		foregroundColor = backgroundColor = null;
		revalidate();
		repaint();
	}
	
	public Sequence getSequence() {
		return sequence;
	}

	public Dimension getMinimumSize() {
		return getPreferredSize();
	}

	public Dimension getMaximumSize() {
		Dimension d = getPreferredSize();
		d.width = Integer.MAX_VALUE;
		return d;
	}
	
	public Dimension getPreferredSize() {
		int height = displayProperties.getActivityHeight();
		int width = displayProperties.getActivityWidth() * sequence.getLengthWithGaps();

		if (isHidden) {
			height = 0;
		}

		return new Dimension(width, height);
	}
	
	public Dimension getSize() {
		Dimension d = super.getSize();
		if (isHidden) {
			d.height = 0;
		}
		return d;
	}
	
	/**
	 * Return the column number based on the coordinate location; Only the
	 * x-coordinate value is required to determine the column number
	 * 
	 * @param x
	 *            - the x-coordinate value
	 * @param y
	 *            - the y-coordinate value
	 * @return the column number or -1 in case of invalid value
	 */
	public int findColumn(int x, int y) {
		int index = x / displayProperties.getActivityWidth();
		return index >= sequence.getLengthWithGaps() ? -1 : index;
	}
	
	/**
	 * Get the coordinate location of an activity given the activity column
	 * (position) number
	 * 
	 * @param activityIndex
	 * @return Coordinate location
	 */
	public Point getActivityPosition(int activityIndex) {
		return new Point(activityIndex * displayProperties.getActivityWidth(), 0);
	}

	public DisplayProperties getDisplayProperties() {
		return displayProperties;
	}
	
	public void setIsHidden(boolean isHidden) {
		this.isHidden = isHidden;
	}

	public boolean getIsHidden() {
		return isHidden;
	}
	
	private class RenderIterator {
		private final int activityWidth;
		private int currentIndex;
		private final int startIndex;
		private int endIndex;
		private int currentX;

		private final int[] highlights;
		private int hIndex;
		private boolean isThisActivityHighlighted = false;

		private final boolean renderGaps;

		public RenderIterator(Rectangle clip) {
			activityWidth = displayProperties.getActivityWidth();
			renderGaps = displayProperties.isGapRendered();

			int length = sequence.getLengthWithGaps();

			startIndex = 0;

			endIndex = (int) Math.floor(((float) clip.x + clip.width) / ((float) activityWidth)) + 1;

			if (endIndex >= length) {
				endIndex = length - 1;
			}

			highlights = displayProperties.getHighlights(sequence);

			if (backgroundColor == null) {
				backgroundColor = new Color[length];
			}
			if (foregroundColor == null) {
				foregroundColor = new Color[length];
			}
		}

		private int currentIndex() {
			return currentIndex;
		}

		public boolean hasNext() {
			return currentIndex < endIndex;
		}

		public void reset() {
			currentIndex = startIndex - 1;
			//TODO
			currentX = (currentIndex) * activityWidth;
			if (highlights != null) {
				hIndex = 0;
			}
		}

		public int getX() {
			return currentX;
		}

		public void next() {
			currentIndex++;
			currentX += activityWidth;

			if (highlights != null) {
				while ((hIndex < highlights.length) && (highlights[hIndex] < currentIndex)) {
					hIndex++;
				}
				isThisActivityHighlighted = (hIndex < highlights.length) && (highlights[hIndex] == currentIndex);
			}
		}

		public Color getBackgroundColor() {
			Color c;
			if (isThisActivityHighlighted) {
				c = foregroundColor[currentIndex()];
				if (c == null) {
					c = Color.black;
					foregroundColor[currentIndex()] = c;
				}
				
			} else {
				c = backgroundColor[currentIndex()];
				if (c == null) {
					String encodedActivity = sequence.getEncodedActivity(currentIndex());
//					Map<String, Color> encodedActivityColorMap = displayProperties.getEncodedActivityColorMap();
//					System.out.println(encodedActivityColorMap.size());
					c = displayProperties.getEncodedActivityColorMap().get(encodedActivity);
//					System.out.println("Encoded Activity: "+encodedActivity+" Color: "+c);
					if (encodedActivity.contains("-")) {
						c = Color.white;
					} else {
						if (c == null) {
							c = new Color(new Random().nextInt());
						}
					}
					backgroundColor[currentIndex()] = c;
				}
			}
			return c;
		}

		public Color getForegroundColor() {
			Color c;

			if (isThisActivityHighlighted) {
				c = backgroundColor[currentIndex()];
				if (c == null) {
					String encodedActivity = sequence.getEncodedActivity(currentIndex());
					c = displayProperties.getEncodedActivityColorMap().get(encodedActivity);
					if (encodedActivity.contains("-")) {
						c = Color.white;
					} else {
						if (c == null) {
							c = new Color(new Random().nextInt());
						}
					}
					backgroundColor[currentIndex()] = c;
				}
			} else {
				c = foregroundColor[currentIndex()];
				if (c == null) {
					c = Color.black;
					foregroundColor[currentIndex()] = Color.black;
				}
			}
			return c;
		}

		private String getEncodedActivity() {
			String encodedActivity = sequence.getEncodedActivity(currentIndex());
			if (!encodedActivity.contains("-") || renderGaps) {
				return encodedActivity;
			}
			String blankSpace = " ";
			for (int i = 1; i <= displayProperties.getEncodingLength(); i++) {
				blankSpace += " ";
			}

			return blankSpace;
		}
	}

	public void paintComponent(Graphics g) {
		//stevenote: this is a terrible place to put this, its just for testing purposes for now:
		//TESTING BEGIN
		double minFrequencyPercent = 0.02;
		InfrequentEventFinder myInfrequentEventFinder = new InfrequentEventFinder(displayProperties, minFrequencyPercent);
		myInfrequentEventFinder.printActivityFrequencyCountToFile();
		//TESTING END
		
		super.paintComponent(g);
		g.setColor(Color.white);

		Dimension d = getSize();
		g.fillRect(0, 0, d.width, d.height);

		Rectangle clip = g.getClipBounds();
		int activityWidth = displayProperties.getActivityWidth();
		int activityHeight = displayProperties.getActivityHeight();
		

		RenderIterator ri = new RenderIterator(clip);
		for (ri.reset(); ri.hasNext();) {
			ri.next();
			g.setColor(ri.getBackgroundColor());
			//adds background color to residue
			if (displayProperties.isActivityBackgroundSquare()) {
				g.fillRect(ri.getX(), 0, activityWidth, activityHeight);
			} else {
				//            	g.fillOval(ri.getX(), 0, activityWidth, activityHeight);
				g.fillRoundRect(ri.getX(), 0, activityWidth, activityHeight, activityWidth, activityHeight);
				//            g.fillOval(ri.getX(), 0, activityWidth, activityHeight);
			}
			//stevenote: this is where we highlight:
			//HIGHLIGHT BEGIN
			//EventHighlighter eventHighlighter = new EventHighlighter();
			//if (myInfrequentEventFinder.getInfrequentActivitiesSet().contains(ri.getEncodedActivity()))
				//eventHighlighter.highlightEvent(EventClassification.AcceptableDeviation, g, ri.getX(), 0, activityWidth, activityHeight, displayProperties.isActivityBackgroundSquare());  // just as a test this should highlight in green
			//HIGHLIGHT END
			
			/*
			EventHighlighter eventHighlighter = new EventHighlighter();
			if (isOutlier(ri.currentIndex()))
				eventHighlighter.highlightEvent(EventClassification.AcceptableDeviation, g, ri.getX(), 0, activityWidth, activityHeight, displayProperties.isActivityBackgroundSquare());  // just as a test this should highlight in green
			// I need to turn a list of EventInfo objects into a list of sequences containing objects we need to identify as well as their with-spaces locations
			// we know what sequence were in, we know what spot in the sequence were in, which means we can connect an EventInfo to a specific pass through this loop.  but we need to do it quicker than just putting it here and searching every time.
			*/
			
			// this is a more general way to find outliers
			EventHighlighter eventHighlighter = new EventHighlighter();			
			EventInfo currentEventInfo = returnCurrentEventInfo(ri.currentIndex());
			if (currentEventInfo != null) {
				if (currentEventInfo.getClassification() == EventClassification.Outlier) {
					// good way uncomment me
					//eventHighlighter.highlightEvent(currentEventInfo.getClassification(), g, ri.getX(), 0, activityWidth, activityHeight, displayProperties.isActivityBackgroundSquare());  // just as a test this should highlight in green
					
					//test way - this works - best way so far
					//eventHighlighter.highlightEventWhiteCircleInSquare(currentEventInfo.getClassification(), g, ri.getX(), 0, activityWidth, activityHeight, displayProperties.isActivityBackgroundSquare(), ri.getBackgroundColor());  // just as a test this should highlight in green
					
					//test way 2
					eventHighlighter.highlightEventWithEventColoredInnerCircleClassificationColoredOuterSquare(currentEventInfo.getClassification(), g, ri.getX(), 0, activityWidth, activityHeight, displayProperties.isActivityBackgroundSquare(), ri.getBackgroundColor());  
					
				}
			}
			
		}

		// draw residue text

		int fontX = displayProperties.getFontXOffset();
		int fontY = displayProperties.getFontYOffset();

		g.setFont(displayProperties.getFont());

		for (ri.reset(); ri.hasNext();) {
			ri.next();
			String encodedActivity = ri.getEncodedActivity();

			if (!encodedActivity.contains(" ")) {
				Color color = ri.getForegroundColor();

				g.setColor(color);
				if (displayProperties.isEncodedActivityRendered()) {
					g.drawString(encodedActivity, ri.getX() + fontX, fontY);
				}
			}
		}
	}
	
	@Override
	public void displayAnnViewChanged(DisplayProperties displayProperties, Sequence sequence,
			boolean show) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void displaySeqSelectChanged(DisplayProperties displayProperties,
			Sequence sequence, boolean select) {
		// TODO Auto-generated method stub
		
	}

	public void displayFontChanged(DisplayProperties displayProperties) {
		if (this.displayProperties != displayProperties) {
			throw new RuntimeException("Bound to incorrect DisplayProperties");
		}
		revalidate();
		repaint();
	}

	public void displayRenderGapsChanged(DisplayProperties displayProperties) {
		if (this.displayProperties != displayProperties) {
			throw new RuntimeException("Bound to incorrect DisplayProperties");
		}
		revalidate();
		repaint();
	}

	public void displayHighlightsChanged(DisplayProperties displayProperties, Sequence sequence) {
		if (this.displayProperties != displayProperties) {
			throw new RuntimeException("Bound to incorrect DisplayProperties");
		}
		if (sequence == this.sequence) {
			revalidate();
			repaint();
		}
		
	}

	public void displayHighlightsChanged(DisplayProperties displayProperties,
			Sequence[] sequenceArray) {
		if (this.displayProperties != displayProperties) {
			throw new RuntimeException("Bound to incorrect DisplayProperties");
		}
		for (int i = 0; i < sequenceArray.length; i++) {
			if (sequence == sequenceArray[i]) {
				revalidate();
				repaint();
				return;
			}
		}
	}

	public void activityBackgroundChanged(DisplayProperties displayProperties) {
		if (this.displayProperties != displayProperties) {
			throw new RuntimeException("Bound to incorrect DisplayProperties");
		}
		backgroundColor = null;
	
		revalidate();
		repaint();
	}

	public void sequenceActivityChanged(Sequence sequence) {
		if (this.sequence != sequence) {
			throw new RuntimeException("Bound to incorrect Sequence");
		}
		foregroundColor = backgroundColor = null;
		revalidate();
		repaint();
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
	public void sequenceLineAnnotationsChanged(Sequence sequence) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void sequenceColorChanged(Sequence sequence) {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public void displayGroupEditingChanged(DisplayProperties displayProperties) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void displayOverwriteChanged(DisplayProperties displayProperties) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void sequenceNameColorChanged(Color color) {
		// TODO Auto-generated method stub
		
	}

	//stevemethod for determining outlier detection
	boolean isOutlier(int positionInSequence) {
		ArrayList<EventInfo> outliersList = new ArrayList<EventInfo>();
		Alignment myAlignment = displayProperties.getAlignment();
		AlignmentFrame myAlignmentFrame = myAlignment.getAlignmentFrame();
		outliersList = myAlignmentFrame.getOutliers();


		int[] activityIndexArray = sequence.getActivityIndexArray();  // this is for translating positions stored in EventInfo to positions stored in the traces with spaces

		String sequenceName = sequence.getName();
		Iterator<EventInfo> iterator = outliersList.iterator();

		while (iterator.hasNext()) {
			EventInfo currentEventInfo = iterator.next();
			int positionOfOutlierInTheSequenceWithoutGaps = currentEventInfo.getlocation()-1;
			
			//stevenote: outlierTranslatedLocation = the location in activityIndexArray at which positionOfOutlierInTheSequenceWithoutGaps appears
			int outlierTranslatedLocation = getIndexOf(positionOfOutlierInTheSequenceWithoutGaps, activityIndexArray);
			
			String outlierSequenceName = currentEventInfo.getcase_id();
			if (outlierSequenceName.equals(sequenceName) && outlierTranslatedLocation == positionInSequence) {
            	return true;
            }
		}
		return false;
	}
	
	//steve helper method
	public int getIndexOf( int toSearch, int[] tab )
	{
	  for( int i=0; i< tab.length ; i ++ )
	    if( tab[ i ] == toSearch)
	     return i;

	  return -1;
	}
	
	EventInfo returnCurrentEventInfo(int positionInSequence) {
		ArrayList<EventInfo> outliersList = new ArrayList<EventInfo>();
		Alignment myAlignment = displayProperties.getAlignment();
		AlignmentFrame myAlignmentFrame = myAlignment.getAlignmentFrame();
		outliersList = myAlignmentFrame.getOutliers();


		int[] activityIndexArray = sequence.getActivityIndexArray();  // this is for translating positions stored in EventInfo to positions stored in the traces with spaces

		String sequenceName = sequence.getName();
		Iterator<EventInfo> iterator = outliersList.iterator();

		while (iterator.hasNext()) {
			EventInfo currentEventInfo = iterator.next();
			int positionOfOutlierInTheSequenceWithoutGaps = currentEventInfo.getlocation()-1;
			
			//stevenote: outlierTranslatedLocation = the location in activityIndexArray at which positionOfOutlierInTheSequenceWithoutGaps appears
			int outlierTranslatedLocation = getIndexOf(positionOfOutlierInTheSequenceWithoutGaps, activityIndexArray);
			
			String outlierSequenceName = currentEventInfo.getcase_id();
			if (outlierSequenceName.equals(sequenceName) && outlierTranslatedLocation == positionInSequence) {
            	return currentEventInfo;
            }
		}
		return null;
	}
	
//	}
//AlignmentFrame getAlignmentFrame() {
	//return owner;	
	
}