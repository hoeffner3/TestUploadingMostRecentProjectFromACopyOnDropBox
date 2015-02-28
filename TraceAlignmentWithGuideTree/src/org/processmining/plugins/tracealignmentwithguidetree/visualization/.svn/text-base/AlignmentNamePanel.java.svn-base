package org.processmining.plugins.tracealignmentwithguidetree.visualization;

import java.awt.Color;
import java.awt.Component;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.util.Arrays;

import javax.swing.BoxLayout;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;

import org.processmining.plugins.tracealignmentwithguidetree.datatypes.Alignment;
import org.processmining.plugins.tracealignmentwithguidetree.datatypes.DisplayProperties;
import org.processmining.plugins.tracealignmentwithguidetree.datatypes.Sequence;
import org.processmining.plugins.tracealignmentwithguidetree.listeners.AlignmentListener;
import org.processmining.plugins.tracealignmentwithguidetree.swingx.Utils;

@SuppressWarnings("serial")
public class AlignmentNamePanel extends JPanel implements MouseListener,
		ActionListener, AlignmentListener {

	// Underlying display properties
	protected DisplayProperties displayProperties;
	
	AlignmentFrame owner;
	
	//Upon right click, a pop-up window is shown; Define attributes for the popup menu and the options in the popup menu  
	private final JPopupMenu popupMenu;
	private final JMenuItem editSequenceMenuItem;
	private final JMenuItem colorSequenceMenuItem;
	
	private boolean isMousePressed = false;
	private int mouseRowPos;
	private int lastSelectedRow = -1;
	
	public AlignmentNamePanel(AlignmentFrame owner, DisplayProperties props) {
		super();
		setBackground(Color.white);
		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

		displayProperties = props;
		this.owner = owner;
		
		Alignment alignment = displayProperties.getAlignment(); 
		alignment.addListener(this);

		int noSequencesInAlignment = alignment.getNoSequences();
		for (int i = 0; i < noSequencesInAlignment; i++) {
			add(new SequenceNameComponent(owner, alignment.getSequence(i), props));
		}

		popupMenu = new JPopupMenu();
		editSequenceMenuItem = new JMenuItem("Edit Trace");
		editSequenceMenuItem.addActionListener(this);
		popupMenu.add(editSequenceMenuItem);

		popupMenu.addSeparator();

		colorSequenceMenuItem = new JMenuItem("Color Trace Name");
		colorSequenceMenuItem.addActionListener(this);
		popupMenu.add(colorSequenceMenuItem);

		
		addMouseListener(this);

		addMouseMotionListener(new MouseMotionListener() {
			public void mouseDragged(MouseEvent e) {
				handleMouseDrag(e);
			}

			public void mouseMoved(MouseEvent e) {
			}
		});

	}

	private void handleMouseDrag(MouseEvent e) {
		shiftSequence(e);
	}
	
	private void shiftSequence(MouseEvent e) {
		if (editWarning()) {
			return;
		}
		int index = findRow(e.getX(), e.getY());
		if (index == mouseRowPos) {
			return;
		}

		int shift = index - mouseRowPos;
		boolean isInSelection = false;
		Sequence seq = displayProperties.getAlignment().getSequence(mouseRowPos);

		Sequence[] seqs = displayProperties.getAllSelectedSequences();
		for (int i = 0; i < seqs.length; i++) {
			if (seq.equals(seqs[i])) {
				isInSelection = true;
				break;
			}
		}
		
		if (!displayProperties.isGroupEditing() || (displayProperties.getNoSelectedSequences() <= 0) || !isInSelection) {
			seqs = new Sequence[1];
			seqs[0] = displayProperties.getAlignment().getSequence(mouseRowPos);
		}

		try {
			int[] indexArray = new int[seqs.length];

			for (int i = indexArray.length - 1; i >= 0; i--) {
				indexArray[i] = displayProperties.getAlignment().getIndex(seqs[i]);
			}
			Arrays.sort(indexArray);
			if ((shift < 0) && (indexArray[0] + shift >= 0)) {
				for (int i = 0; i < indexArray.length; i++) {
					displayProperties.getAlignment().swapSequence(indexArray[i], indexArray[i] + shift);
				}
				mouseRowPos = index;
			} else if ((shift > 0) && (indexArray[indexArray.length - 1] + shift < displayProperties.getAlignment().getNoSequences())) {
				for (int i = indexArray.length - 1; i >= 0; i--) {
					displayProperties.getAlignment().swapSequence(indexArray[i], indexArray[i] + shift);
				}
				mouseRowPos = index;
			} else {
				Toolkit.getDefaultToolkit().beep();
			}
		} catch (Exception exp) {
			JOptionPane.showMessageDialog(this, "Unable to move " + "sequence in alignment.\n"+exp.getLocalizedMessage(), "Error Message", JOptionPane.ERROR_MESSAGE);
		}
	}
	
	private boolean editWarning() {
		if (displayProperties.isMapping()) {
			JOptionPane.showMessageDialog(owner, "You cannot edit an alignment while columns are hidden");
			return true;
		}
		return false;
	}

	private int findRow(int x, int y) {
		Component c = findComponentAt(x, y);
		Component[] comps = getComponents();
		int index;

		for (index = comps.length - 1; index >= 0; index--) {
			if (comps[index] == c) {
				break;
			}
		}
		return index;
	}

	private boolean maybeShowPopup(MouseEvent e) {
		if (e.isPopupTrigger()) {
			int popupHeight = (int) (popupMenu.getComponent()).getSize().getHeight();
			int aHeight = (int) owner.getSize().getHeight();
			int yOffset = -popupHeight * e.getY() / aHeight;

			yOffset = Math.min(0, Math.max(yOffset, -popupHeight));
			Utils.showPopup(popupMenu, e.getComponent(), e.getX(), e.getY() + yOffset);
			return true;
		}
		return false;
	}
	
	public SequenceNameComponent getSequenceNameComponent(int index) {
		Component[] components = getComponents();
		return (SequenceNameComponent) components[index];
	}

	public void setAllSequenceCollapsed(boolean collapsed, boolean repaint) {
		Component[] components = getComponents();
		for (int i = 0; i < components.length; i++) {
			Component component = components[i];
			if (component instanceof SequenceNameComponent) {
				SequenceNameComponent snc = (SequenceNameComponent) component;
				snc.setCollapsed(collapsed);
			}
		}

		if (repaint) {
			revalidate();
			repaint();
		}
	}
	
	public void setSequenceCollapsed(int idx, boolean collapsed, boolean repaint) {
		SequenceNameComponent snc = getSequenceNameComponent(idx);
		snc.setCollapsed(collapsed);

		if (repaint) {
			revalidate();
			repaint();
		}
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		if (!isMousePressed) {
			return;
		}

		if (e.getSource() == editSequenceMenuItem) {
			Sequence seq = displayProperties.getAlignment().getSequence(mouseRowPos);
			EditSequenceDialog editSequenceDialog = new EditSequenceDialog(owner, seq);
			editSequenceDialog.setVisible(true);
		} else if (e.getSource() == colorSequenceMenuItem) {
			Sequence seq = displayProperties.getAlignment().getSequence(mouseRowPos);
			ColorSequenceDialog d = new ColorSequenceDialog(owner, seq);
			d.setVisible(true);
		}
	}

	public void mouseClicked(MouseEvent e) {
		// Nothing to be done here
	}

	public void mouseEntered(MouseEvent e) {
		// Nothing to be done here
	}

	public void mouseExited(MouseEvent e) {
		// Nothing to be done here
	}

	public void mousePressed(MouseEvent e) {
		mouseRowPos = findRow(e.getX(), e.getY());
		isMousePressed = mouseRowPos >= 0;
		if (!isMousePressed) {
			return;
		}
		SequenceNameComponent sequenceNameComponent = (SequenceNameComponent) getComponent(mouseRowPos);
//		Point p = sequenceNameComponent.getLocation();

		if (maybeShowPopup(e)) {
			return;
		} else if (e.getClickCount() >= 2) {
			sequenceNameComponent.edit(sequenceNameComponent.getLocationOnScreen());
		} else if (e.getClickCount() == 1) {
			Sequence seq = displayProperties.getAlignment().getSequence(mouseRowPos);
			// meta down
			if (e.isControlDown() || e.isMetaDown()) {
				if (displayProperties.isSequenceSelected(seq)) {
					displayProperties.setIsSequenceSelected(seq, false);
				} else {
					displayProperties.setIsSequenceSelected(seq, true);
					lastSelectedRow = mouseRowPos;
				}
			} else if (e.isShiftDown() && (lastSelectedRow >= 0) && (lastSelectedRow < displayProperties.getAlignment().getNoSequences())) {
				displayProperties.clearSelections();

				int start = Math.min(mouseRowPos, lastSelectedRow);
				int end = Math.max(mouseRowPos, lastSelectedRow);

				for (int i = start; i <= end; i++) {
					displayProperties.setIsSequenceSelected(displayProperties.getAlignment().getSequence(i), true);
				}
			} else {
				displayProperties.clearSelections();
				displayProperties.setIsSequenceSelected(seq, true);
				lastSelectedRow = mouseRowPos;
			}
		}

	}

	public void mouseReleased(MouseEvent e) {
		if (maybeShowPopup(e)) {
			return;
		}
		if (popupMenu.isVisible()) {
			return;
		}

		isMousePressed = false;

	}

	// Alignment Listener interface implementation
	public void alignmentSeqDeleted(Alignment alignment, int i,
			Sequence sequence) {
		if (alignment != displayProperties.getAlignment()) {
			throw new RuntimeException("bound to incorrect alignment");
		}
		remove(i);
		revalidate();
		repaint();
	}


	public void alignmentSeqSwapped(Alignment alignment, int i, int j) {
		Alignment thisAlignment = displayProperties.getAlignment();
		if (alignment != thisAlignment) {
			throw new RuntimeException("bound to incorrect alignment");
		}
		((SequenceNameComponent) getComponent(i)).setSequence(alignment.getSequence(i));
		((SequenceNameComponent) getComponent(j)).setSequence(alignment.getSequence(j));

		revalidate();
		repaint();
	}

	public void alignmentNameChanged(Alignment alignment) {
		// Nothing to be done here
	}

	public void alignmentSeqActivityChanged(Alignment alignment, Sequence sequence) {
		// Nothing to be done here
	}

}
