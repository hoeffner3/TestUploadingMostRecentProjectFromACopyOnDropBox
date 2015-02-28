package org.processmining.plugins.tracealignmentwithguidetree.visualization;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;

import org.processmining.plugins.tracealignmentwithguidetree.datatypes.Alignment;
import org.processmining.plugins.tracealignmentwithguidetree.datatypes.DisplayProperties;
import org.processmining.plugins.tracealignmentwithguidetree.datatypes.Sequence;
import org.processmining.plugins.tracealignmentwithguidetree.listeners.AlignmentListener;
import org.processmining.plugins.tracealignmentwithguidetree.listeners.DisplayPropertiesListener;
import org.processmining.plugins.tracealignmentwithguidetree.swingx.Utils;

@SuppressWarnings("serial")
public class RulerAnnotationComponent extends JPanel implements
		AlignmentListener, DisplayPropertiesListener, MouseListener, ActionListener {

	private final AlignmentFrame owner;
	private final DisplayProperties displayProperties;
	
	JPopupMenu popupMenu;

	JMenu deleteColumnsMenu;
	private final JMenuItem deleteColumnsLeftMenuItem;
	private final JMenuItem deleteColumnsRightMenuItem;
	private final JMenuItem deleteColumnsSelectedMenuItem;
	private final JMenuItem deleteColumnsAllButSelectedMenuItem;
	
	private int size;

	public RulerAnnotationComponent(DisplayProperties displayProperties, AlignmentFrame owner) {
		super();

		this.displayProperties = displayProperties;
		this.owner = owner;

		size = displayProperties.getAlignment().getMaxLength();

		setBackground(Color.white);

		displayProperties.getAlignment().addListener(this);
		this.displayProperties.addListener(this);
		addMouseListener(this);

		//TODO removed KludgeKeyListener from original

		popupMenu = new JPopupMenu();

		deleteColumnsMenu = new JMenu("Delete columns");
		deleteColumnsMenu.addActionListener(this);

		deleteColumnsLeftMenuItem = new JMenuItem("to the left");
		deleteColumnsLeftMenuItem.addActionListener(this);
		deleteColumnsMenu.add(deleteColumnsLeftMenuItem);

		deleteColumnsRightMenuItem = new JMenuItem("to the right");
		deleteColumnsRightMenuItem.addActionListener(this);
		deleteColumnsMenu.add(deleteColumnsRightMenuItem);

		deleteColumnsSelectedMenuItem = new JMenuItem("selected");
		deleteColumnsSelectedMenuItem.addActionListener(this);
		deleteColumnsMenu.add(deleteColumnsSelectedMenuItem);

		deleteColumnsAllButSelectedMenuItem = new JMenuItem("all but selected");
		deleteColumnsAllButSelectedMenuItem.addActionListener(this);
		deleteColumnsMenu.add(deleteColumnsAllButSelectedMenuItem);

		popupMenu.add(deleteColumnsMenu);

	}
	
	public int findMouseColumn(int x) {
		int index = (int) ((float) x / ((float) (displayProperties.getActivityWidth())));
		index = index >= displayProperties.getAlignment().getMaxLength() ? -1 : index;

		// TODO removed isMapping related
		return index;
	}

	private void resizeToAlignment() {
		int newSize = displayProperties.getAlignment().getMaxLength();

		if (newSize != size) {
			size = newSize;
			revalidate();
			repaint();
		}
	}

	public Dimension getPreferredSize() {
		int height = displayProperties.getActivityHeight();
		int width = displayProperties.getActivityWidth() * size;

		return new Dimension(width, height);
	}

	private boolean maybeShowPopup(MouseEvent e) {
		if (e.isPopupTrigger()) {
			boolean colsel = displayProperties.getAlignment().isColumnSelected();

			deleteColumnsMenu.setEnabled(colsel);
			deleteColumnsLeftMenuItem.setEnabled(colsel);
			deleteColumnsRightMenuItem.setEnabled(colsel);
			deleteColumnsSelectedMenuItem.setEnabled(colsel);
			deleteColumnsAllButSelectedMenuItem.setEnabled(colsel);

			Utils.showPopup(popupMenu, e.getComponent(), e.getX(), e.getY());
			return true;
		}
		return false;
	}

	public int sign(int a) {
		if (a == 0) {
			return 1;
		} else {
			return a / Math.abs(a);
		}
	}

	public void paintComponent(Graphics g) {
		Alignment alignment = displayProperties.getAlignment();
		super.paintComponent(g);
		Color bgColor;

		bgColor = Color.white;

		int y = 0;
		int fontY = y + displayProperties.getFontYOffset() + 2;

		int activityHeight = displayProperties.getActivityHeight();
		int activityWidth = displayProperties.getActivityWidth();

		g.setFont(displayProperties.getFont());

		String str = "";

		if (alignment.getMaxLength() > alignment.rulerAnnotationsSize()) {
			for (int i = 0; i < (alignment.getMaxLength() - alignment.rulerAnnotationsSize()); i++) {
				alignment.addRulerAnnotation();
				alignment.addColumn();
			}
		} else if (alignment.rulerAnnotationsSize() > alignment.getMaxLength()) {
			for (int i = 0; i < (alignment.rulerAnnotationsSize() - alignment.getMaxLength()); i++) {
				alignment.removeRulerAnnotationFromEnd();
				alignment.removeColumnFromEnd();
			}
		}

		if (alignment.isRulerAnnotationSelected() || alignment.isColumnSelected()) {
			displayProperties.setCursorHidden(true);
			displayProperties.setRulerEditing(true);
		} else {
			displayProperties.setCursorHidden(false);
			displayProperties.setRulerEditing(false);
		}

		Alignment.RulerAnnotation rulerAnnotation = new Alignment.RulerAnnotation(null, Color.white, false);

		//TODO changed from original - related to Mapping

		int start = 0;
		int end = alignment.rulerAnnotationsSize();

		for (int index = start; index < end; index++) {
			str = " ";
			int x = (index) * activityWidth - 1;

			if (index < alignment.rulerAnnotationsSize()) {
				rulerAnnotation = alignment.getRulerAnnotation(index);
				if (rulerAnnotation.getLetter() != null) {
					str = rulerAnnotation.getLetter();
					bgColor = rulerAnnotation.getColor();
					g.setColor(bgColor);
					g.fillRect(x + 1, 0, activityWidth, activityHeight);
					g.setColor(displayProperties.inverseRGB(bgColor));
					g.drawString(str, x + 1, fontY);
				}
			}
		}

		for (int index = start; index < end; index++) {
			int x = (index) * activityWidth - 1;

			if (index < alignment.rulerAnnotationsSize()) {
				rulerAnnotation = alignment.getRulerAnnotation(index);
				if (rulerAnnotation.isSelected()) {
					bgColor = rulerAnnotation.getColor();
					g.setColor(displayProperties.inverseRGB(bgColor));
					g.drawRect(x + 1, 0, activityWidth, activityHeight);
				}
			}
		}
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

	public void mousePressed(MouseEvent e) {
		Alignment alignment = displayProperties.getAlignment();
		if (((e.getModifiers() == InputEvent.BUTTON1_MASK) || e.isShiftDown() || e.isControlDown())
				&& !displayProperties.isSeqAnnEditing() && (findMouseColumn(e.getX()) >= 0)) {

			if (e.getClickCount() == 1) {

				displayProperties.setCursorHidden(true);
				displayProperties.setRulerEditing(true);

				int idx = findMouseColumn(e.getX());

				displayProperties.updateCursor(-1, idx);

				owner.displayHighlightsChanged(displayProperties, alignment.getSequence(0));
				alignment.setLastRulerAnnIdxSelected(-1);
				alignment.deselectAllRulerSelections();

				if (!e.isShiftDown() && !e.isControlDown() && !e.isMetaDown()) {
					alignment.deselectAllColumns(displayProperties);
					displayProperties.clearHighlights();

					alignment.setColumnSelected(idx, true, displayProperties);
					alignment.setLastColumnIdxSelected(idx);
				} else if (e.isShiftDown() && !e.isControlDown()) {
					int lastColIdx = alignment.getLastColumnIdxSelected();

					if (lastColIdx != -1) {
						int sgn = sign(lastColIdx - idx);

						for (int i = idx; i != (lastColIdx + sgn); i = i + sgn) {
							alignment.setColumnSelected(i, true, displayProperties);
						}
						alignment.setLastColumnIdxSelected(idx);
					}
				} else if (!e.isShiftDown() && e.isControlDown()) {
					if (alignment.isColumnSelected(idx)) {
						alignment.setColumnSelected(idx, false, displayProperties);
					} else {
						alignment.setColumnSelected(idx, true, displayProperties);
						alignment.setLastColumnIdxSelected(idx);
					}
				} else if (e.isShiftDown() && e.isControlDown()) {
					alignment.setColumnSelected(idx, true, displayProperties);
					alignment.setLastColumnIdxSelected(idx);
				}
				revalidate();
				repaint();
			} else if (e.getClickCount() == 2) {
				displayProperties.setCursorHidden(true);
				displayProperties.setRulerEditing(true);
				alignment.deselectAllRulerSelections();
				int idx = findMouseColumn(e.getX());

				displayProperties.updateCursor(-1, idx);
				if (!alignment.isColumnSelected(idx)) {
					alignment.deselectAllColumns(displayProperties);
					displayProperties.clearHighlights();
				} else {
					for (int i = 0; i < alignment.rulerAnnotationsSize(); i++) {
						if (alignment.isColumnSelected(i)) {
							alignment.getRulerAnnotation(i).setSelected(true);
						} else {
							alignment.getRulerAnnotation(i).setSelected(false);
						}
					}
				}
				revalidate();
				repaint();
			}
		}
	}

	public void mouseReleased(MouseEvent e) {
		if (((e.getModifiers() == InputEvent.BUTTON3_MASK) && maybeShowPopup(e))
				&& !displayProperties.isSeqAnnEditing()) {
			return;
		}
		if (popupMenu.isVisible()) {
			return;
		}
	}

	public void displayFontChanged(DisplayProperties displayProperties) {
		if (displayProperties != this.displayProperties) {
			throw new RuntimeException("bound to incorrect DisplayProperties");
		}
		revalidate();
		repaint();
	}
	
	public void displayGroupEditingChanged(DisplayProperties dp) {
		// Nothing to be done here
	}
	
	public void displayAnnViewChanged(DisplayProperties dp, Sequence sequence, boolean show) {
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
	
	public void displayHighlightsChanged(DisplayProperties dp, Sequence sequence) {
		// Nothing to be done here
	}
	
	@Override
	public void alignmentNameChanged(Alignment alignment) {
		// TODO Auto-generated method stub

	}

	public void alignmentSeqDeleted(Alignment alignment, int i,
			Sequence sequence) {
		if (alignment != displayProperties.getAlignment()) {
			throw new RuntimeException("Bound to incorrect alignment");
		}
		resizeToAlignment();
	}

	public void alignmentSeqSwapped(Alignment alignment, int i, int j) {
		// Nothing to be done here

	}

	public void alignmentSeqActivityChanged(Alignment alignment,
			Sequence sequence) {
		if (alignment != displayProperties.getAlignment()) {
			throw new RuntimeException("Bound to incorrect alignment");
		}
		resizeToAlignment();
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		Alignment alignment = displayProperties.getAlignment();
		if (e.getSource() == deleteColumnsLeftMenuItem) {
			Sequence sequence = alignment.getSequence(0);
			int firstIdx = alignment.getFirstColumnSelected();

			if (firstIdx != -1) {
				for (int i = 0; i < alignment.getNoSequences(); i++) {
					sequence = alignment.getSequence(i);
					try {
						//TODO changed -firstIdx+1 to -firstIdx
						sequence.shiftActivity(firstIdx, -firstIdx, true);
						displayProperties.setAlignmentRecentlyEdited(true);
					} catch (Exception exp) {
						JOptionPane.showMessageDialog(owner, "Unable to delete " + "column in sequence "
								+ sequence.getName(), exp.toString(), JOptionPane.ERROR_MESSAGE);
					}
				}
				alignment.removeColumnRange(1, firstIdx);
				alignment.removeRulerAnnotationRange(1, firstIdx);
			}
			alignment.deselectAllColumns(displayProperties);
		} else if (e.getSource() == deleteColumnsRightMenuItem) {
			Sequence sequence = alignment.getSequence(0);
			int lastIdx = alignment.getLastColumnSelected();

			if ((lastIdx != -1) && (lastIdx < alignment.getColumnsSize() - 1)) {
				for (int i = 0; i < alignment.getNoSequences(); i++) {
					sequence = alignment.getSequence(i);
					try {
						sequence.shiftActivity(sequence.getLengthWithGaps() - 1, -sequence.getLengthWithGaps()
								+ lastIdx + 2, true);
						sequence.deleteActivity(sequence.getLengthWithGaps() - 1);
					} catch (Exception exp) {
						JOptionPane.showMessageDialog(owner, "Unable to delete " + "column in sequence "
								+ sequence.getName(), exp.toString(), JOptionPane.ERROR_MESSAGE);
					}
				}
				alignment.removeColumnRange(lastIdx + 1, alignment.getColumnsSize());
				alignment.removeRulerAnnotationRange(lastIdx + 1, alignment.rulerAnnotationsSize());
			}
			alignment.deselectAllColumns(displayProperties);
		} else if (e.getSource() == deleteColumnsSelectedMenuItem) {
			Sequence sequence;
			List<Integer> selectedColumnList = new ArrayList<Integer>();
			List<Integer> rangeCols = new ArrayList<Integer>();
			int left, right, column, neighbors;

			for (int col = 0; col <= alignment.getLastColumnSelected(); col++) {
				if (alignment.isColumnSelected(col)) {
					selectedColumnList.add(new Integer(col));
				}
			}
			System.out.println("Selected Column List: "+selectedColumnList);
			for (int i = 0; i < selectedColumnList.size(); i++) {
				neighbors = 0;
				column = ((Integer) selectedColumnList.get(i)).intValue();
				left = i == 0 ? column : ((Integer) selectedColumnList.get(i - 1)).intValue();
				right = i == selectedColumnList.size() - 1 ? column : ((Integer) selectedColumnList.get(i + 1))
						.intValue();
				if (column - left == 1) {
					neighbors++;
				}
				if (right - column == 1) {
					neighbors++;
				}
				if (neighbors == 0) {
					rangeCols.add(new Integer(column));
					rangeCols.add(new Integer(column));
				}
				if (neighbors == 1) {
					rangeCols.add(new Integer(column));
				}
			}
			int start, end;
			System.out.println("Range Cols: "+rangeCols);
			for (int row = 0; row < alignment.getNoSequences(); row++) {
				sequence = alignment.getSequence(row);
				for (int i = rangeCols.size() - 1; i >= 1; i -= 2) {
					start = ((Integer) rangeCols.get(i)).intValue();
					end = ((Integer) rangeCols.get(i - 1)).intValue();
					try {
						if (row == 0) {
							alignment.removeColumnRange(end, start + 1);
							alignment.removeRulerAnnotationRange(end, start + 1);
						}
						System.out.println(start+" @ "+end);
						sequence.shiftActivity(start + 1, end - start - 1, true);
					} catch (Exception exp) {
						JOptionPane.showMessageDialog(owner, "Unable to delete " + "column in sequence "
								+ sequence.getName(), exp.toString(), JOptionPane.ERROR_MESSAGE);
					}
				}
			}
			alignment.deselectAllColumns(displayProperties);
		} else if (e.getSource() == deleteColumnsAllButSelectedMenuItem) {
			Sequence sequence;
			List<Integer> selectedColumnList = new ArrayList<Integer>();
			List<Integer> rangeCols = new ArrayList<Integer>();
			int left, right, column, neighbors;
			//TODO changed col = 1 initialization to col = 0;
			for (int col = 0; col < alignment.getMaxLength(); col++) {
				if (!alignment.isColumnSelected(col)) {
					selectedColumnList.add(new Integer(col));
				}
			}
			for (int i = 0; i < selectedColumnList.size(); i++) {
				neighbors = 0;
				column = ((Integer) selectedColumnList.get(i)).intValue();
				left = i == 0 ? column : ((Integer) selectedColumnList.get(i - 1)).intValue();
				right = i == selectedColumnList.size() - 1 ? column : ((Integer) selectedColumnList.get(i + 1))
						.intValue();
				if (column - left == 1) {
					neighbors++;
				}
				if (right - column == 1) {
					neighbors++;
				}
				if (neighbors == 0) {
					rangeCols.add(new Integer(column));
					rangeCols.add(new Integer(column));
				}
				if (neighbors == 1) {
					rangeCols.add(new Integer(column));
				}
			}
			int start, end;

			for (int row = 0; row < alignment.getNoSequences(); row++) {
				sequence = alignment.getSequence(row);
				for (int i = rangeCols.size() - 1; i >= 1; i -= 2) {
					start = ((Integer) rangeCols.get(i)).intValue();
					end = ((Integer) rangeCols.get(i - 1)).intValue();

					try {
						if (row == 0) {
							alignment.removeColumnRange(end, start + 1);
							alignment.removeRulerAnnotationRange(end, start + 1);
						}
						sequence.shiftActivity(start + 1, end - start - 1, true);
					} catch (Exception exp) {
						JOptionPane.showMessageDialog(owner, "Unable to delete " + "column in sequence "
								+ sequence.getName(), exp.toString(),JOptionPane.ERROR_MESSAGE);
					}
				}
			}
			alignment.deselectAllColumns(displayProperties);
		} 
	}
}
