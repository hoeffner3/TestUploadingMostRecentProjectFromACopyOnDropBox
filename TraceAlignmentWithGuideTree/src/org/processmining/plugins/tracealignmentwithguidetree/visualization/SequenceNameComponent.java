package org.processmining.plugins.tracealignmentwithguidetree.visualization;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Point;

import javax.swing.JPanel;

import org.processmining.plugins.tracealignmentwithguidetree.datatypes.DisplayProperties;
import org.processmining.plugins.tracealignmentwithguidetree.datatypes.Sequence;
import org.processmining.plugins.tracealignmentwithguidetree.listeners.DisplayPropertiesListener;
import org.processmining.plugins.tracealignmentwithguidetree.listeners.SequenceListener;

@SuppressWarnings("serial")
public class SequenceNameComponent extends JPanel implements SequenceListener,
		DisplayPropertiesListener {
	public final static int Spacing = 5;
	
	// Underlying sequence
	private Sequence sequence = null;

	// Underlying sequence display properties
	private final DisplayProperties displayProperties;
	
	Color sequenceNameColor;
	
	AlignmentFrame owner;
	boolean collapsed = false;
	
	public SequenceNameComponent(AlignmentFrame owner, Sequence sequence, DisplayProperties displayProperties) {
		super();
		setBackground(Color.white);
		this.owner = owner;
		this.displayProperties = displayProperties;
		this.displayProperties.addListener(this);
		sequenceNameColor = Color.black;
		
		
		setSequence(sequence);
	}

	public void setSequence(Sequence sequence) {
		if (this.sequence != null) {
			this.sequence.removeListener(this);
		}
		this.sequence = sequence;
		this.sequence.addListener(this);
		revalidate();
		repaint();
	}
	
	public Sequence getSequence() {
		return sequence;
	}

	// default size
	public Dimension getMinimumSize() {
		return getPreferredSize();
	}

	public Dimension getPreferredSize() {
		FontMetrics fontMetrics = displayProperties.getFontMetrics();

		int height = displayProperties.getActivityHeight();

		int width = 2 * displayProperties.getActivityWidth();

		//10 currently for the duplicate count
		String displayName = sequence.getName() + 10;

		if (displayName != null) {
			width += fontMetrics.stringWidth(displayName) + 2;
		}

		width += 30;

		return new Dimension(width, height);
	}

	public Dimension getSize() {
		Dimension d = super.getSize();
		return d;
	}
	
	public Dimension getMaximumSize() {
		//System.out.println("Inside seq name comp - getMaxSize");
		Dimension d = getPreferredSize();
		//System.out.println("Received Dimension:"+d);
		d.width = Integer.MAX_VALUE;
		//System.out.println("Setting width in Dimension with int max value:"+d);
		return d;
	}
	
	protected Color getSequenceNameColor() {
		return sequenceNameColor;
	}

	public void setCollapsed(boolean collapsed) {
		this.collapsed = collapsed;
	}
	
	@SuppressWarnings("deprecation")
	public void edit(Point loc) {
		EditSequenceNameDialog ed = new EditSequenceNameDialog(owner, displayProperties, sequence);
		ed.setLocation(loc);
		ed.show();
	}
	
	public void paint(Graphics g) {
		boolean isSelected = displayProperties.isSequenceSelected(sequence);
		g.setColor(isSelected ? getSequenceNameColor() : Color.white);
		Dimension d = getSize();

		g.fillRect(0, 0, d.width, d.height);

		Font font = displayProperties.getFont();

		int x = Spacing + displayProperties.getActivityWidth() * 2;
		int y = 0;
		int fontY = y + displayProperties.getFontYOffset();
		int activityHeight = displayProperties.getActivityHeight();
		
		g.setFont(font);

		String str = sequence.getName();
		if (sequence.getNoDuplicates() > 0) {
			str += " ("+sequence.getNoDuplicates()+")";
			g.setColor(Color.lightGray);
			int width = getPreferredSize().width-5;
			if (width <= 1) {
				width = 2;
			}
			g.fill3DRect(x, y, width * font.getSize(), activityHeight, false);
		}

		g.setColor(isSelected ? Color.white : getSequenceNameColor());
		g.drawString(str, x, fontY);
	}
	
	@Override
	public void displayAnnViewChanged(DisplayProperties displayProperties,
			Sequence sequence, boolean show) {
		// TODO Auto-generated method stub

	}

	public void displayFontChanged(DisplayProperties displayProperties) {
		if (this.displayProperties != displayProperties) {
			throw new RuntimeException("Bound to incorrect DisplayProperties");
		}

		revalidate();
		repaint();
	}

	public void displayGroupEditingChanged(DisplayProperties dp) {
		// Nothing to be done here
	}

	public void displayHighlightsChanged(DisplayProperties dp, Sequence sequence) {
		// Nothing to be done here
	}

	public void displayHighlightsChanged(DisplayProperties dp, Sequence[] sequenceArray) {
		// Nothing to be done here
	}

	public void displayOverwriteChanged(DisplayProperties dp) {
		// Nothing to be done here
	}

	public void displayRenderGapsChanged(DisplayProperties dp) {
		// Nothing to be done here
	}

	public void activityBackgroundChanged(DisplayProperties dp) {
		// Nothing to be done here
	}

	public void displaySeqSelectChanged(DisplayProperties dp, Sequence sequence, boolean select) {
		// Nothing to be done here
	}
	
	public void sequenceNameChanged(Sequence sequence, String oldName)
			throws Exception {
		if (this.sequence != sequence) {
			throw new RuntimeException("Bound to incorrect Sequence");
		}
		revalidate();
		repaint();
	}

	public void sequenceActivityChanged(Sequence aaseq) {
		// Nothing to be done here
	}

	public void sequenceAnnotationChanged(Sequence sequence) {
		// Nothing to be done here
	}

	@Override
	public void sequenceLineAnnotationsChanged(Sequence sequence) {
		// TODO Auto-generated method stub

	}

	@Override
	public void sequenceColorChanged(Sequence sequence) {
		// TODO Auto-generated method stub

	}

	public void sequenceNameColorChanged(Color color) {
		this.sequenceNameColor = color;
		revalidate();
		repaint();
	}

}
