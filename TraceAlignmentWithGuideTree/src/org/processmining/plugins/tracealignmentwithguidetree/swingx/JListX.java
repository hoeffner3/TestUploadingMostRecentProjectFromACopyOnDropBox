/*
 * 
 * Extended functionality JList. Able to disable individual elements... NOTE:
 * currently only supports SINGLE SELECTION lists
 */
package org.processmining.plugins.tracealignmentwithguidetree.swingx;

import java.awt.Component;
import java.util.HashSet;
import java.util.Vector;

import javax.swing.DefaultListCellRenderer;
import javax.swing.JList;
import javax.swing.ListModel;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

/**
 * Extended functionality JList. Able to disable individual elements... NOTE:
 * currently only supports SINGLE SELECTION lists...assumes the USEr of this
 * class will make it a single selection list
 * 
 * @author DanaP
 * 
 */
@SuppressWarnings("serial")
public class JListX extends JList implements ListSelectionListener {
	HashSet<Integer> disabled;

	// implements saving of state for SINGLE selection lists...
	// TODO: improve this class to allow multiple selections as well
	int selectedIndex;
	boolean fixingSelection;

	/**
	 * Constructs a JList with an empty model.
	 */
	public JListX() {
		super();
		init();
	}

	/**
	 * Constructs a JList that displays the elements in the specified, non-null
	 * model.
	 * 
	 * @param dataModel
	 *            list data model
	 */
	public JListX(ListModel dataModel) {
		super(dataModel);
		init();
	}

	/**
	 * Constructs a JList that displays the elements in the specified array.
	 * 
	 * @param listData
	 *            array of list elements
	 */
	public JListX(Object[] listData) {
		super(listData);
		init();
	}

	/**
	 * Constructs a JList that displays the elements in the specified Vector.
	 * 
	 * @param listData
	 *            vector giving list elements
	 */
	@SuppressWarnings("rawtypes")
	public JListX(Vector listData) {
		super(listData);
		init();
	}

	protected void init() {
		// only single selection lists for now...
		// assumes the user will set this up...
		selectedIndex = -1;
		fixingSelection = false;

		disabled = new HashSet<Integer>();
		setCellRenderer(new JListXRenderer());
		addListSelectionListener(this);
	}

	public boolean isElementEnabled(int index) throws IllegalArgumentException {
		if ((index < 0) || (index >= getModel().getSize())) {
			throw new IllegalArgumentException();
		}

		if (disabled.contains(new Integer(index))) {
			return false;
		}

		return true;
	}

	public void setElementEnabled(int index, boolean enabled) throws IllegalArgumentException {
		if ((index < 0) || (index >= getModel().getSize())) {
			throw new IllegalArgumentException();
		}

		if (enabled) {
			disabled.remove(new Integer(index));
		} else {
			disabled.add(new Integer(index));
		}
	}

	protected boolean enforceDisabledElements() {
		int[] sel = getSelectedIndices();
		Vector<Integer> unSel = new Vector<Integer>();
		for (int i = 0; i < sel.length; i++) {
			if (!isElementEnabled(sel[i])) {
				unSel.add(new Integer(sel[i]));
			}
		}

		fixingSelection = true;

		ListSelectionModel lsm = getSelectionModel();
		for (int i = 0; i < unSel.size(); i++) {
			Integer integer = unSel.get(i);
			int n = integer.intValue();
			lsm.removeIndexInterval(n, n);
		}

		fixingSelection = false;

		return unSel.size() > 0;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * javax.swing.event.ListSelectionListener#valueChanged(javax.swing.event
	 * .ListSelectionEvent)
	 */
	public void valueChanged(ListSelectionEvent e) {
		if (fixingSelection) {
			return;
		}

		if (enforceDisabledElements()) {
			// put old selection back...
			if ((selectedIndex >= 0) && isElementEnabled(selectedIndex)) {
				fixingSelection = true;
				setSelectedIndex(selectedIndex);
				fixingSelection = false;
			}
		} else {
			// save current selection
			selectedIndex = getSelectedIndex();
		}
	}
}

@SuppressWarnings("serial")
class JListXRenderer extends DefaultListCellRenderer {

	public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected,
			boolean cellHasFocus) {
		JListX listX = (JListX) list;

		if (!listX.isElementEnabled(index)) {
			Component c = super.getListCellRendererComponent(list, value, index, isSelected, false); // we draw selection state cause we supress it and should never see it...if we see it thats a signal to debug something!
			c.setEnabled(false);
			return c;
		} else {
			return super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
		}
	}
}
