package org.processmining.plugins.tracealignmentwithguidetree.visualization;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;

import org.processmining.plugins.tracealignmentwithguidetree.datatypes.Alignment;
import org.processmining.plugins.tracealignmentwithguidetree.datatypes.ColumnSort;
import org.processmining.plugins.tracealignmentwithguidetree.datatypes.DisplayProperties;

@SuppressWarnings("serial")
public class ColumnSortComponent extends JPanel implements ActionListener, MouseListener, PopupMenuListener{
	private static Color FILL_COLOR = new Color(0.9f, 0.9f, 0.9f);
	
	AlignmentFrame owner;
	
	// display properties
	protected DisplayProperties displayProperties;

	private JPopupMenu popupMenu = null;

	private int clickedColumnIndex;
	private boolean isMousePressed;
	
	public ColumnSortComponent(AlignmentFrame owner, DisplayProperties displayProperties){
		super();
		this.owner = owner;
		this.displayProperties = displayProperties;
		setBackground(Color.white.darker());
		
		setToolTipText("Sort Traces by Column - to turn off use View menu");
		addMouseListener(this);
	}
	
	public Dimension getMinimumSize() {
		return getPreferredSize();
	}

	public Dimension getMaximumSize() {
		Dimension d = getPreferredSize();
		d.width = Integer.MAX_VALUE;
		return d;
	}

	private int getActivityHeight() {
		return (int) (displayProperties.getActivityHeight() * 1.5);
	}

	public Dimension getPreferredSize() {
		int height = getActivityHeight();
		int width = displayProperties.getActivityWidth() * displayProperties.getAlignment().getMaxLength();
		return new Dimension(width, height);
	}
	
	// get a position good for the ColumnSort dialog for editing
	// this column
	public Point getColumnLocationRelativeToOwner(int col) {
		// get location of this column in local space
		int x = displayProperties.getXForColumn(col);
		int y = getActivityHeight();

		// get this location on screen
		Point p = new Point(getLocationOnScreen());
		p.translate(x, y);

		// get location of owner on screen
		Point ownerLoc = owner.getLocationOnScreen();

		// return relative location...
		p.translate(-ownerLoc.x, -ownerLoc.y);

		return p;
	}
	
	// popup
	private JPopupMenu buildPopupMenu(MouseEvent e) {
		JPopupMenu popup = new JPopupMenu();
		popup.setInvoker(this);

		Point p = getColumnLocationRelativeToOwner(clickedColumnIndex);
		Point ownerLoc = owner.getLocation();

		//Shift the popup one residue to the right so we can see the current column
		p.translate(ownerLoc.x + displayProperties.getActivityWidth(), ownerLoc.y);
		popup.setLocation(p);

		JMenuItem menuItem;

		popup.add(menuItem = new JMenuItem(ColumnSort.getDisplayNone()));
		menuItem.addActionListener(this);
		menuItem.setName(ColumnSort.getDisplayNone());
		popup.add(menuItem = new JMenuItem(ColumnSort.getDisplayCustom()));
		menuItem.setName(ColumnSort.getDisplayCustom());
		menuItem.addActionListener(this);
		String[] activitiesInColumnArray = displayProperties.getAlignment().getUniqueActivitiesInColumn(clickedColumnIndex);
		int[] allSequencesCounts = displayProperties.getAlignment().getUniqueActivityCounts(clickedColumnIndex, activitiesInColumnArray);

		int[] selectedSequenceCounts = null;
		if (displayProperties.getNoSelectedSequences() > 1) {
			selectedSequenceCounts = displayProperties.getAlignment().getUniqueActivityCounts(clickedColumnIndex, activitiesInColumnArray,
					displayProperties.getAllSelectedSequences());
		}

		for (int i = 0; i < activitiesInColumnArray.length; i++) {
			String menuItemLabel = activitiesInColumnArray[i] + "  (" + allSequencesCounts[i] + ")";
			if (selectedSequenceCounts != null) {
				menuItemLabel += " [" + selectedSequenceCounts[i] + "]";
			}
			popup.add(menuItem = new JMenuItem(menuItemLabel));
			menuItem.addActionListener(this);
			menuItem.setName(activitiesInColumnArray[i]);
		}

		return popup;
	}

	private void popup(MouseEvent e) {
		if (popupMenu != null) {
			popupMenu.setVisible(false);
		}

		displayProperties.getAlignment().setColumnSortCurrColumn(clickedColumnIndex);
		repaint();

		popupMenu = buildPopupMenu(e);
		popupMenu.addPopupMenuListener(this);
		popupMenu.setVisible(true);
	}

	public int findColumn(int x, int y) {
		int index = x / displayProperties.getActivityWidth();

		index = index >= displayProperties.getAlignment().getMaxLength() ? -1 : index;

		return index;
	}
	
	private class RenderIterator {
		private final int activityWidth;
		private int currentIndex;
		private int startIndex, endIndex;
		private int currentX;

		public RenderIterator(Rectangle clip) {
			activityWidth = displayProperties.getActivityWidth();
			int length = displayProperties.getAlignment().getMaxLength();

			// TODO removed pertaining to mapping
			if (clip == null) {
				//				ErrorDialog.showErrorDialog(this, "CLip is Null");
				startIndex = 0;
				endIndex = length - 1;

				clip = new Rectangle(0, 0, getPreferredSize().width, getPreferredSize().height);
			} else {
				startIndex = (int) Math.floor(((float) clip.x) / ((float) activityWidth)) - 1;

				// TODO removed condition < 1 to < 0 and = 1 to = 0
				if (startIndex < 0) {
					startIndex = 0;
				}

				endIndex = (int) Math.floor(((float) clip.x + clip.width) / ((float) activityWidth)) + 1;
				if (endIndex >= length) {
					endIndex = length - 1;
				}
			}
		}

		private int curr_idx() {
			//TODO removed pertaining to mapping
			return currentIndex;
		}

		public boolean hasNext() {
			return currentIndex < endIndex;
		}

		public void reset() {
			currentIndex = startIndex - 1;
			//TODO removed pertaining to mapping
			currentX = (currentIndex) * activityWidth;
		}

		public int getIndex() {
			return curr_idx();
		}

		public int getX() {
			return currentX;
		}

		public void next() {
			//TODO removed pertaining to non-contiguous
			currentIndex++;
			currentX += activityWidth;
		}

		private String getText() {
			ColumnSort sort = displayProperties.getAlignment().getColumnSort(new Integer(curr_idx()));
			String text;
			if (sort == null) {
				text = " ";
			} else {
				text = sort.getDisplayText();
			}
			return text;
		}

		private String getPriority() {
			ColumnSort sort = displayProperties.getAlignment().getColumnSort(new Integer(curr_idx()));
			String text;
			if (sort == null) {
				text = " ";
			} else {
				text = "" + sort.getPriority();
			}
			return text;
		}
	}

	public void paint(Graphics g) {
		Dimension d = getSize();

		g.setColor(Color.white);
		//TODO removed mapping
		g.fillRect(0, 0, d.width, d.height);

		// figure out the range to render
		Rectangle clip = g.getClipBounds();

		int activityWidth = displayProperties.getActivityWidth();
		int activityHeight = getActivityHeight();
		RenderIterator ri = new RenderIterator(clip);

		int fontX = displayProperties.getFontXOffset();
		int fontY = displayProperties.getFontYOffset() - 4;
		int fontH = displayProperties.getFontMetrics().getHeight();

		int cfCurrCol = displayProperties.getAlignment().getColumnSortCurrentColumn();

		// try to draw as few boxes as possible
		g.setFont(displayProperties.getFont());
		for (ri.reset(); ri.hasNext();) {
			ri.next();
			int col = ri.getIndex();

			if ((cfCurrCol > 0) && (cfCurrCol == col)) {
				g.setColor(Color.lightGray);
				g.fillRect(ri.getX(), 0, activityWidth, activityHeight - 1);
			}

			String text = ri.getText();
			String priority = ri.getPriority();

			g.setColor(FILL_COLOR);
			g.fillRect(ri.getX(), 0, activityWidth, activityHeight - 1);
			g.setColor(Color.black);
			g.drawRect(ri.getX(), 0, activityWidth, activityHeight - 1);
			g.drawString(priority, ri.getX() + fontX, fontY);
			g.drawString(text, ri.getX() + fontX, fontY + fontH);
		}
	}
	
	@Override
	public void mouseClicked(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseEntered(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseExited(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	
	public void mousePressed(MouseEvent e) {
		clickedColumnIndex = findColumn(e.getX(), e.getY());
		//TODO changed the condition >= 1 to >= 0
		isMousePressed = clickedColumnIndex >= 0;

		if ((e.getClickCount() == 1) && isMousePressed) {
			popup(e);
		}

		if ((e.getClickCount() >= 2) && isMousePressed) {
		}
	}

	public void mouseReleased(MouseEvent e) {
		isMousePressed = false;
	}

	public void popupMenuCanceled(PopupMenuEvent e) {
		displayProperties.getAlignment().setColumnSortCurrColumn(-1);
		repaint();
	}

	public void popupMenuWillBecomeInvisible(PopupMenuEvent e) {
		displayProperties.getAlignment().setColumnSortCurrColumn(-1);
		repaint();
	}

	public void popupMenuWillBecomeVisible(PopupMenuEvent e) {
		// Nothing to be done here
	}

	/*
	 * Action Listener interface implementation
	 * (non-Javadoc)
	 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
	 */
	public void actionPerformed(ActionEvent e) {
		Alignment alignment = displayProperties.getAlignment();
		alignment.setColumnSortCurrColumn(clickedColumnIndex);
		repaint();

		ColumnSort existingColumnSort = alignment.getColumnSort(new Integer(clickedColumnIndex));
		boolean doesNotExist = existingColumnSort == null;

		Component c = (Component) e.getSource();
		String val = c.getName();
		if (val.equals(ColumnSort.getDisplayNone())) {
			owner.removeColumnSort(clickedColumnIndex);
		} else if (val.equals(ColumnSort.getDisplayCustom())) {
			//TODO removed the code pertaining to maximum number of column sorts
			// if we are at max already...

			if (doesNotExist && (owner.getNoColumnSorts() >= AlignmentFrame.MaxPriority)) {
				JOptionPane.showMessageDialog(this, "You already have the maximum number of Column Sorts: "+AlignmentFrame.MaxPriority, "Error Message", JOptionPane.ERROR_MESSAGE);
			} else {
				Point p = getColumnLocationRelativeToOwner(clickedColumnIndex);

				ColumnSort currSort = alignment.getCurrentColumnSort();
				ColumnSort sort = ColumnSortDialog.showDialog(owner, displayProperties, clickedColumnIndex,
						currSort, p);
				if (sort != null) {
					owner.addColumnSort(sort, clickedColumnIndex);
				}
			}
		} else {
			// if we are at max already...
			if (doesNotExist && (owner.getNoColumnSorts() >= AlignmentFrame.MaxPriority)) {
				JOptionPane.showMessageDialog(this, "You already have the maximum number of Column Sorts: "
						+ AlignmentFrame.MaxPriority,"Error Message", JOptionPane.ERROR_MESSAGE);
			} else {
				if (doesNotExist) {
					owner.addColumnSort(new ColumnSort(owner.getEncodingLength(), val, owner.getNextFreePriority()), clickedColumnIndex);
				} else {
					owner.addColumnSort(new ColumnSort(owner.getEncodingLength(), val, existingColumnSort.getPriority()),
							clickedColumnIndex);
				}
			}
		}

		popupMenu.setVisible(false);

		// reset
		alignment.setColumnSortCurrColumn(-1);
		repaint();

	}
}
