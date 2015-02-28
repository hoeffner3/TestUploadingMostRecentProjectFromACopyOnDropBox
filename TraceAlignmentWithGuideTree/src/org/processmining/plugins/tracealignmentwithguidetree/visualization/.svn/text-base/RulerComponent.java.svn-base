package org.processmining.plugins.tracealignmentwithguidetree.visualization;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.Graphics;

import javax.swing.JPanel;

import org.processmining.plugins.tracealignmentwithguidetree.datatypes.Alignment;
import org.processmining.plugins.tracealignmentwithguidetree.datatypes.DisplayProperties;
import org.processmining.plugins.tracealignmentwithguidetree.datatypes.Sequence;
import org.processmining.plugins.tracealignmentwithguidetree.listeners.AlignmentListener;
import org.processmining.plugins.tracealignmentwithguidetree.listeners.DisplayPropertiesListener;

@SuppressWarnings("serial")
public class RulerComponent extends JPanel implements AlignmentListener,
		DisplayPropertiesListener {

	// rendering properties
	private final DisplayProperties displayProperties;
	
	int size;
	
	public RulerComponent(DisplayProperties displayProperties){
		this.displayProperties = displayProperties;
		size = displayProperties.getAlignment().getMaxLength();
		
		displayProperties.addListener(this);
		displayProperties.getAlignment().addListener(this);
		
		setBackground(Color.white);
	}
	
	public Dimension getPreferredSize() {
		int height = displayProperties.getActivityHeight();
		int width = displayProperties.getActivityWidth() * size;

		return new Dimension(width, height);
	}

	public void paintComponent(Graphics g) {
		super.paintComponent(g);

		int y = 0;

		int fontY = y + displayProperties.getFontYOffset() - 2;
		int activityHeight = displayProperties.getActivityHeight();
		int activityWidth = displayProperties.getActivityWidth();

		int lineHeight = activityHeight - displayProperties.getFontMetrics().getMaxAscent()
				- displayProperties.getFontMetrics().getMaxDescent();

		int lineYStart = y + activityHeight - lineHeight;

		FontMetrics fontMetrics = displayProperties.getFontMetrics();

		g.setFont(displayProperties.getFont());
		g.setColor(Color.black);

		int maxLength = displayProperties.getAlignment().getMaxLength();

		for (int index = maxLength; index > 0; index--) {
			if (index % 4 == 0) {
				int x = index * activityWidth - 1;

				g.drawLine(x, lineYStart, x, lineYStart + 2 * lineHeight);
				String str = Integer.toString(index);
				int strWidth = fontMetrics.stringWidth(str);

				// we don't draw the #s if we are mapping cause they are
				// misleading
				g.drawString(str, x - strWidth + 2, fontY);
			} else if (index % 2 == 0) {
				int x = index * activityWidth - 1;

				g.drawLine(x, lineYStart, x, lineYStart + lineHeight);
			}
		}

	}
	
	private void resizeToAlignment() {
		int newSize = displayProperties.getAlignment().getMaxLength();

		if (newSize != size) {
			size = newSize;
			revalidate();
			repaint();
		}
	}
	
	public void displayFontChanged(DisplayProperties displayProperties) {
		if (this.displayProperties != displayProperties) {
			throw new RuntimeException("Bound to incorrect DisplayProperties");
		}
		revalidate();
		repaint();
	}

	public void displayAnnViewChanged(DisplayProperties displayProperties,
			Sequence sequence, boolean show) {
		// Nothing to be done here
	}

	public void displaySeqSelectChanged(DisplayProperties displayProperties,
			Sequence sequence, boolean select) {
		// Nothing to be done here
	}
	
	public void displayRenderGapsChanged(DisplayProperties displayProperties) {
		// Nothing to be done here
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


	public void alignmentSeqDeleted(Alignment alignment, int i,
			Sequence sequence) {
		if (alignment != displayProperties.getAlignment()) {
			throw new RuntimeException("Bound to incorrect alignment");
		}
		resizeToAlignment();
	}

	public void alignmentSeqActivityChanged(Alignment alignment,
			Sequence sequence) {
		if (alignment != displayProperties.getAlignment()) {
			throw new RuntimeException("Bound to incorrect alignment");
		}
		resizeToAlignment();
	}

	public void alignmentNameChanged(Alignment alignment) {
		// Nothing to be done here
	}

	public void alignmentSeqSwapped(Alignment alignment, int i, int j) {
		// Nothing to be done here
	}

}
