package org.processmining.plugins.tracealignmentwithguidetree.visualization;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;

import javax.swing.BoxLayout;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.Scrollable;
import javax.swing.SwingConstants;

import org.processmining.plugins.tracealignmentwithguidetree.datatypes.Alignment;
import org.processmining.plugins.tracealignmentwithguidetree.datatypes.DisplayProperties;
import org.processmining.plugins.tracealignmentwithguidetree.datatypes.Sequence;
import org.processmining.plugins.tracealignmentwithguidetree.listeners.AlignmentListener;

@SuppressWarnings("serial")
public class AlignmentPanel extends JPanel implements AlignmentListener, MouseListener, MouseMotionListener, MouseWheelListener, Scrollable{
	protected Alignment alignment;
	protected DisplayProperties displayProperties;
	protected AlignmentFrame owner;
	
	private boolean isMousePressed = false;
	
	public AlignmentPanel(AlignmentFrame owner, Alignment alignment, DisplayProperties displayProperties) {
		super();

		this.owner = owner;
		this.alignment = alignment;
		this.displayProperties = displayProperties;

		setBackground(Color.white);
		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		
		//Add the sequences
		int noSequencesInAlignment = alignment.getNoSequences();
		for (int i = 0; i < noSequencesInAlignment; i++) {
			Sequence currentSequence = alignment.getSequence(i);
			JComponent sequenceComponent = new SequenceComponent(currentSequence, displayProperties);
			sequenceComponent.setLayout(new BoxLayout(sequenceComponent, BoxLayout.Y_AXIS));
			this.add(sequenceComponent);
		}
		
		//Add the listeners
		alignment.addListener(this);
		addMouseListener(this);
		addMouseMotionListener(this);
		addMouseWheelListener(this);
	}
	
	public boolean hasHiddenRows() {
		Component[] components = getComponents();
		for (int i = 0; i < components.length; i++) {
			Component component = components[i];
			if (component instanceof SequenceComponent) {
				SequenceComponent sc = (SequenceComponent) component;
				if (sc.isHidden) {
					return true;
				}
			}
		}
		return false;
	}
	
	/**
	 * Determine the actual row given visible row index; When sequences are
	 * hidden, the cursor based row index (visible row) need to be adjusted for
	 * the actual sequence in order to perform any operations on the sequence
	 * 
	 * @param visibleRow
	 * @return the actual row index of the sequence in the alignment
	 */
	public int getRowIndex(int visibleRow) {
		Component[] components = getComponents();
		int rowIndex = 0;
		for (int i = 0; i < components.length; i++) {
			Component component = components[i];
			if (component instanceof SequenceComponent) {
				SequenceComponent sc = (SequenceComponent) component;
				if (sc.isHidden) {
					continue;
				}
			}

			if (rowIndex == visibleRow) {
				return i;
			}

			rowIndex++;
		}
		return -1;
	}

	public int getNumVisibleRows() {
		int numVisibleRows = 0;

		// count the # of uncollapsed rows from 0 to real row
		Component[] components = getComponents();
		for (int i = 0; i < components.length; i++) {
			Component component = components[i];
			if (component instanceof SequenceComponent) {
				SequenceComponent sc = (SequenceComponent) component;
				if (sc.isHidden) {
					continue;
				}
			}
			numVisibleRows++;
		}
		return numVisibleRows;
	}

	// get visible row given real row
	public int getInverseRowIndex(int realRow) {
		int invRow = 0;

		// count the # of uncollapsed rows from 0 to real row
		Component[] components = getComponents();
		for (int i = 0; i < components.length; i++) {
			if (realRow == i) {
				return invRow;
			}

			Component component = components[i];
			if (component instanceof SequenceComponent) {
				SequenceComponent sc = (SequenceComponent) component;
				if (sc.isHidden) {
					continue;
				}
			}
			invRow++;
		}
		return -1;
	}

	public int getSequenceIndex(Sequence sequence) {
		Component[] components = getComponents();
		for (int i = 0; i < components.length; i++) {
			Component component = components[i];
			if (component instanceof SequenceComponent) {
				SequenceComponent sc = (SequenceComponent) component;
				if (sc.getSequence().equals(sequence)) {
					return i;
				}
			}
		}
		return -1;
	}

	public SequenceComponent getSequenceComponent(int index) {
		Component[] components = getComponents();
		if (index < components.length) {
			return (SequenceComponent) components[index];
		} else {
			return null;
		}
	}

	public SequenceComponent getSequenceComponent(Sequence sequence) {
		int index = getSequenceIndex(sequence);
		if (index < 0) {
			return null;
		}
		return getSequenceComponent(index);
	}

	public void setAllSequenceCollapsed(boolean isHidden, boolean repaint) {
		Component[] components = getComponents();
		for (int i = 0; i < components.length; i++) {
			Component component = components[i];
			if (component instanceof SequenceComponent) {
				SequenceComponent sc = (SequenceComponent) component;
				sc.setIsHidden(isHidden);
			}
		}

		if (repaint) {
			revalidate();
			repaint();
		}
	}

	public void setSequenceCollapsed(int idx, boolean isHidden, boolean repaint) {
		SequenceComponent sc = this.getSequenceComponent(idx);
		sc.setIsHidden(isHidden);

		if (repaint) {
			revalidate();
			repaint();
		}
	}
	
	public Dimension getPreferredSize() {
		return getLayout().preferredLayoutSize(this);
	}

	private int findRow(int x, int y) {
		Component c = findComponentAt(x, y);
		Component[] components = getComponents();
		int index;

		for (index = components.length - 1; index >= 1; index--) {
			if (components[index] == c) {
				break;
			}
		}
		return index;
	}
	
	public void alignmentSeqDeleted(Alignment alignment, int i,
			Sequence sequence) {
		if (alignment != this.alignment) {
			throw new RuntimeException("Bound to incorrect alignment");
		}
		remove(i);
		revalidate();
		repaint();
	}

	public void alignmentSeqSwapped(Alignment alignment, int i, int j) {
		if (alignment != this.alignment) {
			throw new RuntimeException("bound to incorrect alignment");
		}
		((SequenceComponent) getComponent(i)).setSequence(alignment.getSequence(i));
		((SequenceComponent) getComponent(j)).setSequence(alignment.getSequence(j));
	}
	
	
	public void alignmentNameChanged(Alignment alignment) {
		// Nothing to be done here
	}

	public void alignmentSeqActivityChanged(Alignment alignment,
			Sequence sequence) {
		// Nothing to be done here
	}
	
	public void fireActivityBackgroundChanged(){
		Component[] components = this.getComponents();
		for(Component component : components)
			if(component instanceof SequenceComponent)
				((SequenceComponent) component).activityBackgroundChanged(displayProperties);
	}

	@Override
	public void mouseDragged(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	public void mouseMoved(MouseEvent arg0) {
		// Nothing to be done here		
	}

	public void mouseClicked(MouseEvent arg0) {
		// Nothing to be done here
	}

	public void mouseEntered(MouseEvent arg0) {
		// Nothing to be done here
	}

	public void mouseExited(MouseEvent arg0) {
		// Nothing to be done here
	}

	int mouseRowIndex, mouseColIndex;
	public void mousePressed(MouseEvent e) {
//		System.out.println("In Alignment Panel: MousePressed");
		mouseRowIndex = findRow(e.getX(), e.getY());

		if (mouseRowIndex >= 0) {
			SequenceComponent sequenceComponent = (SequenceComponent) getComponent(mouseRowIndex);
			Point p = sequenceComponent.getLocation();

			mouseColIndex = sequenceComponent.findColumn(e.getX() - p.x, e.getY() - p.y);
		} else {
			mouseColIndex = -1;
		}

		//TODO changed the condition for ColIndex to >=0 instead of >= 1
		isMousePressed = (mouseRowIndex >= 0) && (mouseColIndex >= 0);

		if ((e.getClickCount() == 1) && isMousePressed) {
			//			System.out.println("In MousePressed and ClickCount = 1 \t "+mouseRowIndex+" @ "+mouseColIndex);
			displayProperties.setSeqAnnEditing(false);
			displayProperties.setCursorHidden(false);
//			alignment.deselectAllRulerSelections();
			alignment.deselectAllColumns(displayProperties);

			displayProperties.updateCursor(mouseRowIndex, mouseColIndex, e);
		}

		if ((e.getClickCount() >= 2) && isMousePressed) {
//			System.out.println("HERE");
			Sequence seq = alignment.getSequence(mouseRowIndex);
			EditActivityAnnotationDialog editActivityAnnDialog = new EditActivityAnnotationDialog(owner, displayProperties,
					 seq, mouseColIndex);
			editActivityAnnDialog.setVisible(true);
		}
	}

	public void mouseReleased(MouseEvent arg0) {
		isMousePressed = false;
	}

	//Scrollable interface implementation
	public Dimension getPreferredScrollableViewportSize() {
		return getPreferredSize();
	}

	public int getScrollableBlockIncrement(Rectangle visibleRectangle, int orientation, int direction) {
		return orientation == SwingConstants.VERTICAL ? visibleRectangle.height : visibleRectangle.width;	
	}

	public boolean getScrollableTracksViewportHeight() {
		return false;
	}

	public boolean getScrollableTracksViewportWidth() {
		return false;
	}

	public int getScrollableUnitIncrement(Rectangle visibleRectangle, int orientation, int direction) {
		return orientation == SwingConstants.VERTICAL ? displayProperties.getActivityHeight() : displayProperties
				.getActivityWidth();
	}

	@Override
	public void mouseWheelMoved(MouseWheelEvent arg0) {
		// TODO Auto-generated method stub
		
	}
}
