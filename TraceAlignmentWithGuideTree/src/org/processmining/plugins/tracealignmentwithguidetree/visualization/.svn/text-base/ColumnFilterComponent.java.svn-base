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
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;

import org.processmining.plugins.tracealignmentwithguidetree.datatypes.Alignment;
import org.processmining.plugins.tracealignmentwithguidetree.datatypes.ColumnFilter;
import org.processmining.plugins.tracealignmentwithguidetree.datatypes.ColumnSort;
import org.processmining.plugins.tracealignmentwithguidetree.datatypes.DisplayProperties;

@SuppressWarnings("serial")
public class ColumnFilterComponent extends JPanel implements ActionListener,
		MouseListener, PopupMenuListener {
	AlignmentFrame owner;
	
	// display properties
	protected DisplayProperties displayProperties;

	private JPopupMenu popupMenu = null;

	private int clickedColumnIndex;
	private boolean isMousePressed;
	
	public ColumnFilterComponent(AlignmentFrame owner, DisplayProperties displayProperties){
		super();
		this.owner = owner;
		this.displayProperties = displayProperties;
		setBackground(Color.white);
		
		setToolTipText("Filter Traces by Column - to turn off use View menu");
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
	
	// get a position good for the ColumnFilter dialog for editing
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

			//TODO changed pertaining to Mapping
			if (clip == null) {
				//				ErrorDialog.showErrorDialog(this, "CLip is Null");
				startIndex = 0;
				endIndex = length - 1;

				clip = new Rectangle(0, 0, getPreferredSize().width, getPreferredSize().height);
			} else {
				startIndex = (int) Math.floor(((float) clip.x) / ((float) activityWidth)) - 1;

				//TODO changed condition from < 1 to < 0 
				if (startIndex < 0) {
					startIndex = 0;
				}

				//TODO changed pertaining to Mapping
				endIndex = (int) Math.floor(((float) clip.x + clip.width) / ((float) activityWidth)) + 1;
				if (endIndex >= length) {
					endIndex = length - 1;
				}
			}
		}

		private int curr_idx() {
			return currentIndex;
		}

		public boolean hasNext() {
			return currentIndex < endIndex;
		}

		public void reset() {
			currentIndex = startIndex - 1;
			//TODO changed mapping
			currentX = (currentIndex) * activityWidth;
		}

		public int getIndex() {
			return curr_idx();
		}

		public int getX() {
			return currentX;
		}

		public void next() {
			//TODO changed non contiguous columns
			currentIndex++;
			currentX += activityWidth;
		}

		private String getText() {

			ColumnFilter filter = displayProperties.getAlignment().getColumnFilter(new Integer(curr_idx()));
			String text;
			if (filter == null) {
				text = " ";
			} else {
				text = filter.getDisplayText();
			}
			return text;
		}
	}

	public void paint(Graphics g) {
		g.setColor(Color.white);
		Dimension d = getSize();

		//TODO changed pertaining to Mapping
		g.fillRect(0, 0, d.width, d.height);

		// figure out the range to render
		Rectangle clip = g.getClipBounds();
		int activityWidth = displayProperties.getActivityWidth();
		int activityHeight = displayProperties.getActivityHeight();
		RenderIterator ri = new RenderIterator(clip);

		int font_x = displayProperties.getFontXOffset();
		int font_y = displayProperties.getFontYOffset();

		int cfCurrCol = displayProperties.getAlignment().getColumnFilterCurrentColumn();

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

			g.setColor(Color.black);

			g.drawRect(ri.getX(), 0, activityWidth, activityHeight - 1);
			g.drawString(text, ri.getX() + font_x, font_y);
		}
	}
	
	// 	Mouse Listener interfaces
	public void mousePressed(MouseEvent e) {
		clickedColumnIndex = findColumn(e.getX(), e.getY());

		//TODO changed the condition >=1 to >=0
		isMousePressed = clickedColumnIndex >= 0;

		if ((e.getClickCount() == 1) && isMousePressed) {
			popup(e);
		}

		if ((e.getClickCount() >= 2) && isMousePressed) {
		}
	}

	public void mouseReleased(MouseEvent arg0) {
		isMousePressed = false;
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

	//Action Listener Interface
	public void actionPerformed(ActionEvent e) {
		Alignment alignment = displayProperties.getAlignment();
		alignment.setColumnFilterCurrColumn(clickedColumnIndex);
		repaint();

		//TODO removed the code pertaining to custom option in columnFilter
		Component c = (Component) e.getSource();
		String val = c.getName();
		if (val.equals(ColumnFilter.getDisplayNone())) {
			owner.removeColumnFilter(clickedColumnIndex);
		} else if (val.equals(ColumnFilter.getDisplayCustom())) {
			Point p = getColumnLocationRelativeToOwner(clickedColumnIndex);

			ColumnFilter currFilter = alignment.getCurrentColumnFilter();
			ColumnFilter filter = ColumnFilterDialog.showDialog(owner, displayProperties, clickedColumnIndex,
					currFilter, p);
			if (filter != null) {
				owner.addColumnFilter(filter, clickedColumnIndex);
			}
		} else {
			owner.addColumnFilter(new ColumnFilter(owner.getEncodingLength(), val), clickedColumnIndex);
		}

		popupMenu.setVisible(false);

		// reset
		alignment.setColumnFilterCurrColumn(-1);
		repaint();
	}

	@Override
	public void popupMenuCanceled(PopupMenuEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void popupMenuWillBecomeInvisible(PopupMenuEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void popupMenuWillBecomeVisible(PopupMenuEvent arg0) {
		// TODO Auto-generated method stub
		
	}

}
