package org.processmining.plugins.tracealignmentwithguidetree.visualization;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;
import java.text.DecimalFormat;

import javax.swing.JPanel;

import org.processmining.plugins.tracealignmentwithguidetree.datatypes.ConsensusSequence;
import org.processmining.plugins.tracealignmentwithguidetree.datatypes.DisplayProperties;
import org.processmining.plugins.tracealignmentwithguidetree.datatypes.Sequence;
import org.processmining.plugins.tracealignmentwithguidetree.listeners.DisplayPropertiesListener;
import org.processmining.plugins.tracealignmentwithguidetree.listeners.SequenceListener;

@SuppressWarnings("serial")
public class ConsensusComponent extends JPanel implements SequenceListener,
		DisplayPropertiesListener {

	// underlying sequence
	private ConsensusSequence consensus = null;

	// rendering properties
	private final DisplayProperties displayProperties;

	public ConsensusComponent(ConsensusSequence consensus, DisplayProperties displayProperties) {
		super();
		setBackground(Color.white);
		this.displayProperties = displayProperties;
		displayProperties.addListener(this);
		setConsensus(consensus);

		setToolTipText("");
	}
	
	public void setConsensus(ConsensusSequence consensus) {
		if (this.consensus != null) {
			this.consensus.removeListener(this);
		}
		this.consensus = consensus;
		revalidate();
		repaint();
	}
	
	public Dimension getPreferredSize() {
		// Increased multiplier from 3 to 4 to leave some headroom
		int height = displayProperties.getActivityHeight() * 4;
		int width = displayProperties.getActivityWidth() * consensus.getLengthWithGaps();

		return new Dimension(width, height);
	}

	public Color getConsensusColorAt(int i) {
		return Color.LIGHT_GRAY;
	}

	public String getToolTipText(MouseEvent e) {
//		if (!displayProperties.isAnnotationPopupEnabled()) {
//			return null;
//		}

		// find out which col we are talking...
		int x = e.getX();
		int total = displayProperties.getAlignment().getMaxLength();
		int col = x / displayProperties.getActivityWidth();

		// out of range means nein!
		if ((col < 0) || (col >= total)) {
			return null;
		}

		//TODO changed 
		double value = getConservationScoreAtColumnIndex(col);
		DecimalFormat decimalFormat = new DecimalFormat("#.##");
		return decimalFormat.format(value);
	}
	
	public double getConservationScoreAtColumnIndex(int i) {
		return consensus.getInformationInColumnGapsIncluded()[i];
	}

	public void paint(Graphics g) {
		g.setColor(Color.white);

		Dimension d = getPreferredSize(); // need to reset size just in case
		this.setSize(d); // alignment length changed

		g.fillRect(0, 0, d.width, d.height);

		int len = consensus.getLengthWithGaps();

		int activityWidth = displayProperties.getActivityWidth();
		int activityHeight = displayProperties.getActivityHeight();
		int blockHeight = 3 * activityHeight;
		boolean render_gaps = displayProperties.isGapRendered();

		g.setFont(displayProperties.getFont());
		int start;
		int end;

		Rectangle clip = g.getClipBounds();
		if (clip == null) {
			//			ErrorDialog.showErrorDialog(this, "CLip is Null");
			start = 0;
			end = len - 1;

			clip = new Rectangle(0, 0, getPreferredSize().width, getPreferredSize().height);
		} else {
			start = (clip.x / activityWidth); // skip dummy residue
			end = (clip.x + clip.width) / activityWidth;
		}

		if (end >= len) {
			end = len - 1;
		}

		if (start < 0) {
			start = 0; // skip dummy residue
		}

		// ////////////////////////////////////////
		// Draw a red line at the beginning of component
		// this is useful for debugging incorrect placement
		// of the component
		g.setColor(new Color(156, 1, 1));
		g.drawLine(0, activityHeight, d.width, activityHeight);
		// ///////////////////////////////////////////////////

		// iterate over current clip and paint
		for (int index = start; index <= end; index++) {
			int x = (index) * activityWidth;
			if (clip.intersects(x, 0, activityWidth, blockHeight)) {
				String encodedActivity = consensus.getEncodedActivity(index);
				
				boolean ignoreGaps = true;
				double consensusValue = consensus.getSimplePercentOccurrence(index, ignoreGaps);
				Color consensusColor = getConsensusColorFromLuminosity(consensusValue);
				//double consensusValue = getConservationScoreAtColumnIndex(index);  // old way
				//Color consensusColor = getConsensusColorAt(index);  // old way
				int height = (int) (2 * activityHeight * Math.abs(consensusValue));

				g.setColor(consensusColor);
				g.fillRect(x, 3 * activityHeight - height, activityWidth, height);
				g.setColor(Color.GRAY);
				g.drawRect(x, 3 * activityHeight - height, activityWidth, height);

				if ((!encodedActivity.contains("-") || render_gaps)) {
					g.setColor(Color.black);
					g.drawString(encodedActivity, x + displayProperties.getFontXOffset(), 3 * activityHeight
							+ displayProperties.getFontYOffset());
				}
			}
		}
	}
	
	public void displayAnnViewChanged(DisplayProperties displayProperties,
			Sequence sequence, boolean show) {
		// Nothing to be done here
	}

	public void displaySeqSelectChanged(DisplayProperties displayProperties,
			Sequence sequence, boolean select) {
		// Nothing to be done here
	}

	public void displayFontChanged(DisplayProperties displayProperties) {
		if (this.displayProperties != displayProperties) {
			throw new RuntimeException("bound to incorrect DisplayProperties");
		}
		revalidate();
		repaint();
	}

	public void displayRenderGapsChanged(DisplayProperties displayProperties) {
		if (this.displayProperties != displayProperties) {
			throw new RuntimeException("bound to incorrect DisplayProperties");
		}
		revalidate();
		repaint();
	}

	public void displayGroupEditingChanged(DisplayProperties displayProperties) {
		// Nothing to be done here
	}

	public void displayOverwriteChanged(DisplayProperties displayProperties) {
		// Nothing to be done here
	}

	public void displayHighlightsChanged(DisplayProperties displayProperties,
			Sequence sequence) {
		// Nothing to be done here
	}

	public void displayHighlightsChanged(DisplayProperties displayProperties,
			Sequence[] sequenceArray) {
		// Nothing to be done here
	}

	public void activityBackgroundChanged(DisplayProperties displayProperties) {
		// Nothing to be done here
	}

	public void sequenceActivityChanged(Sequence sequence) {
		if (sequence != consensus) {
			throw new RuntimeException("bound to incorrect Sequence");
		}
		revalidate();
		repaint();
	}

	public void sequenceNameColorChanged(Color color) {
		// Nothing to be done here
	}

	public void sequenceAnnotationChanged(Sequence sequence) {
		// Nothing to be done here
	}

	public void sequenceColorChanged(Sequence sequence) {
		// Nothing to be done here
	}

	public void sequenceColumnAnnotationsChanged(Sequence sequence, int column) {
		// Nothing to be done here
	}

	public void sequenceLineAnnotationsChanged(Sequence aaseq) {
		// Nothing to be done here
	}

	public void sequenceNameChanged(Sequence sequence, String oldName) throws Exception {
		// Nothing to be done here
	}
	
	private Color getConsensusColorFromLuminosity(double luminosity) {
		Color color = new Color((float)luminosity, 0, 0);
		return color;
	}
}
