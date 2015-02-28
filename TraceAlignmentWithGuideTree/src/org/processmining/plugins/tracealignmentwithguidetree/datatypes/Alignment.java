package org.processmining.plugins.tracealignmentwithguidetree.datatypes;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.processmining.plugins.tracealignmentwithguidetree.listeners.AlignmentListener;
import org.processmining.plugins.tracealignmentwithguidetree.visualization.AlignmentFrame;

public class Alignment implements Comparator<Sequence>{
	int encodingLength;
	AlignmentFrame owner;
	
	String dash;
	
	// Alignment name
	private String name;

	// The sequences in this alignment
	Sequence[] sequencesInAlignment;
	
	// Flag to indicate whether any changes have been done to the alignment
	private boolean alignmentChanged = false;
	
	// List of listeners that do the required things when something is changed in the alignment
	private final List<AlignmentListener> listenersList = new ArrayList<AlignmentListener>();
	
	
	private final ArrayList<Boolean> columnsSelectedList = new ArrayList<Boolean>();
	
	int lastColumnIndexSelected;
	private int lastRulerAnnIndexSelected = -1;
	private final List<RulerAnnotation> rulerAnnotationList = new ArrayList<RulerAnnotation>();
	
	private int lastActivityIndexSelected = -1;
	private int lastActivityRowSelected = -1;
	
	int columnSortCurrColumn;
	Map<Integer, ColumnSort> columnSortMap = new HashMap<Integer, ColumnSort>();

	private int columnFilterCurrColumn = -1;
	Map<Integer, ColumnFilter> columnFilterMap = new HashMap<Integer, ColumnFilter>();

	private boolean columnFiltering = false;
	private boolean columnSorting = true;
	
	public Alignment(int encodingLength, AlignmentFrame owner, Sequence[] sequencesInAlignment){
		this.encodingLength = encodingLength;
		this.owner = owner;
		this.sequencesInAlignment = sequencesInAlignment;
		
		dash = "-";
		for(int i = 1; i < encodingLength; i++)
			dash += "-";
	}
	
	public void addListener(AlignmentListener listener) {
		listenersList.add(listener);
	}
	
	public void setColumnSortCurrColumn(int col) {
		columnSortCurrColumn = col;
	}
	
	public void setColumnFilterCurrColumn(int col) {
		columnFilterCurrColumn = col;
	}
	
	public String getName() {
		return name;
	}
	
	public Sequence getSequence(int index) {
		if ((index < 0) || (index >= sequencesInAlignment.length)) {
			return null;
		}

		if (sequencesInAlignment[index] != null) {
			return sequencesInAlignment[index];
		} else {
			return null;
		}
	}
	
	public Sequence getSequence(String sequenceName) {
		for (Sequence sequence : sequencesInAlignment) {
			if (sequence.getName().equals(sequenceName)) {
				return sequence;
			}
		}
		return null;
	}
	
	public Sequence[] getSequencesInAlignment(){
		return sequencesInAlignment;
	}
	
	public int getNoSequences(){
		return sequencesInAlignment.length;
	}

	public int getEncodingLength(){
		return encodingLength;
	}
	
	public int getMaxLength(){
		if(sequencesInAlignment != null && sequencesInAlignment.length > 0)
			return sequencesInAlignment[0].getLengthWithGaps();
		else
			return 0;
	}
	
	public Map<String, Color> getEncodedActivityColorMap(){
		return owner.getEncodedActivityColorMap();
	}
	
	public boolean isColumnSelected() {
		if (columnsSelectedList.isEmpty()) {
			return false;
		}
		return columnsSelectedList.contains(new Boolean(true));
	}
	
	public boolean isColumnSelected(int idx) {
		if (columnsSelectedList.isEmpty()) {
			return false;
		}
		return columnsSelectedList.get(idx).booleanValue();
	}
	
	public void removeColumnRange(int from, int to) {
		for (int i = 0; i < to - from; i++) {
			columnsSelectedList.remove(from);
		}
	}
	
	public void deselectAllColumns(DisplayProperties p) {
		deselectAllColumns(p, true);
	}

	public void deselectAllColumns(DisplayProperties p, boolean quickDeselect) {
		for (int idx = 0; idx < columnsSelectedList.size(); idx++) {
			if (quickDeselect) {
				columnsSelectedList.set(idx, new Boolean(false));
			} else {
				setColumnSelected(idx, false, p);
			}
		}
		lastColumnIndexSelected = -1;
	}

	public void setColumnSelected(int index, boolean b, DisplayProperties displayProperties) {
		if (index < columnsSelectedList.size()) {
			columnsSelectedList.set(index, new Boolean(b));
			int noSequencesInAlignment = getNoSequences();
			for (int row = 0; row < noSequencesInAlignment; row++) {
				displayProperties.setSeqHighlight(this.getSequence(row), index, b, false);
			}
			displayProperties.fireDisplayHighlightsChanged(getAllSequences());
		}
	}
	
	public Sequence[] getAllSequences() {
		Sequence[] tempSequenceArray = new Sequence[sequencesInAlignment.length];
		System.arraycopy(sequencesInAlignment, 0, tempSequenceArray, 0, sequencesInAlignment.length);
		return tempSequenceArray;
	}
	
	public int getColumnsSize() {
		return columnsSelectedList.size();
	}

	public void addColumn() {
		columnsSelectedList.add(new Boolean(false));
	}

	public void removeColumnFromEnd() {
		columnsSelectedList.remove(columnsSelectedList.size() - 1);
	}

	public int getFirstColumnSelected() {
		return columnsSelectedList.indexOf(new Boolean(true));
	}

	public int getLastColumnSelected() {
		return columnsSelectedList.lastIndexOf(new Boolean(true));
	}

	public int getNumberOfColumnsSelected() {
		int noSelectedColumns = 0;
		for (int i = 0; i < columnsSelectedList.size(); i++) {
			if (isColumnSelected(i)) {
				noSelectedColumns++;
			}
		}
		return noSelectedColumns;
	}
	
	public String[] getUniqueActivitiesInColumn(int column){
		Set<String> encodedActivitiesInColumnSet = new HashSet<String>();
		for(Sequence sequence : sequencesInAlignment){
			if(!sequence.getEncodedActivity(column).equals(dash))
				encodedActivitiesInColumnSet.add(sequence.getEncodedActivity(column));
		}
		
		String[] encodedActivitiesInColumnArray = new String[encodedActivitiesInColumnSet.size()];
		int index = 0;
		for (String encodedActivity : encodedActivitiesInColumnSet) {
			encodedActivitiesInColumnArray[index++] = encodedActivity;
		}

		return encodedActivitiesInColumnArray;
	}
	
	public int[] getUniqueActivityCounts(int column, String[] encodedActivitiesArray){
		return getUniqueActivityCounts(column, encodedActivitiesArray, sequencesInAlignment);
	}
	
	public int[] getUniqueActivityCounts(int column, String[] encodedActivitiesArray, Sequence[] sequences) {
		int[] counts = new int[encodedActivitiesArray.length];
		for (int i = 0; i < counts.length; i++) {
			int count = 0;
			String encodedActivity = encodedActivitiesArray[i];
			for (Sequence sequence : sequences) {
				if (!sequence.getEncodedActivity(column).equals(encodedActivity)) {
					continue;
				}
				count++;
			}
			counts[i] = count;
		}

		return counts;
	}
	
	// find the index of a sequence
	public int getIndex(Sequence sequence) {
		int noSequencesInAlignment = getNoSequences();
		for (int i = 0; i < noSequencesInAlignment; i++) {
			if (sequencesInAlignment[i] == sequence) {
				return i;
			}
		}
		return -1;
	}
	
	// swap two sequences
	public void swapSequence(int i, int j) throws Exception {
		if ((i < 0) || (i >= sequencesInAlignment.length) || (j < 0) || (j >= sequencesInAlignment.length)) {
			throw new Exception("Bad indexes for sequence swap");
		}
		if (i == j) {
			return;
		}

		Sequence sequence = sequencesInAlignment[i];
		sequencesInAlignment[i] = sequencesInAlignment[j];
		sequencesInAlignment[j] = sequence;

		setChanged(true);
		for (AlignmentListener alignmentListener : listenersList) {
			alignmentListener.alignmentSeqSwapped(this, i, j);
		}
	}

	public void setChanged(boolean b) {
		alignmentChanged = b;
	}
	
	public boolean isAlignmentChanged(){
		return alignmentChanged;
	}
	
	public void removeAllGapColumns() {
		Sequence sequence;
		List<Integer> allGapColumnList = getGapColumnList();
//		System.out.println("Gap column list: "+allGapColumnList);
		
		List<Integer> rangeCols = new ArrayList<Integer>();
		int left, right, column, neighbors;

		for (int i = 0; i < allGapColumnList.size(); i++) {
			neighbors = 0;
			column = allGapColumnList.get(i).intValue();
			left = i == 0 ? column : ((Integer) allGapColumnList.get(i - 1)).intValue();
			right = i == allGapColumnList.size() - 1 ? column : ((Integer) allGapColumnList.get(i + 1)).intValue();
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

		for (int row = 0; row < getNoSequences(); row++) {
			sequence = getSequence(row);
			for (int i = rangeCols.size() - 1; i >= 1; i -= 2) {
				start = ((Integer) rangeCols.get(i)).intValue();
				end = ((Integer) rangeCols.get(i - 1)).intValue();
				try {
					if (row == 0) {
						removeColumnRange(end, start + 1);
						removeRulerAnnotationRange(end, start + 1);
					}
					sequence.shiftActivity(start + 1, end - start - 1, true);
				} catch (Exception exp) {
					exp.printStackTrace();
				}
			}
		}
	}

	private List<Integer> getGapColumnList() {
		List<Integer> gapColumnList = new ArrayList<Integer>();
		int alignmentLength = getMaxLength();
		int noSequencesInAlignment = getNoSequences();
		String encodedActivity;
		boolean activityFound;
		for (int i = 0; i < alignmentLength; i++) {
			activityFound = false;
			for (int j = 0; j < noSequencesInAlignment; j++) {
				encodedActivity = sequencesInAlignment[j].getEncodedActivity(i);
				if (!encodedActivity.contains("-")) {
					activityFound = true;
					break;
				}
			}
			if (!activityFound) {
				gapColumnList.add(i);
			}
		}
		return gapColumnList;
	}

	public ColumnSort getColumnSort(Integer key) {
		return columnSortMap.get(key);
	}

	public ColumnSort getCurrentColumnSort() {
		return columnSortMap.get(columnSortCurrColumn);
	}
	
	public void removeColumnSort(int column) {
		ColumnSort columnSort = columnSortMap.get(column);
		if (columnSort != null) {
			//TODO removed the invocation of removed() on column sort; mostly dealing with priority settings
			owner.removed(columnSort.getPriority());
			columnSortMap.remove(column);
		}
	}

	
	public void addColumnSort(ColumnSort sort, int column) {
		ColumnSort old = columnSortMap.get(column);
		if (old != null) {
			owner.removed(old.getPriority());
			columnSortMap.remove(column);
		}
		columnSortMap.put(column, sort);
	}
	
	public ColumnFilter getColumnFilter(Integer key) {
		return columnFilterMap.get(key);
	}

	public ColumnFilter getCurrentColumnFilter() {
		return columnFilterMap.get(columnFilterCurrColumn);
	}
	
	public void removeColumnFilter(int column) {
		ColumnFilter columnFilter = columnFilterMap.get(column);
		if (columnFilter != null) {
			columnFilterMap.remove(column);
		}
	}
	
	public void addColumnFilter(ColumnFilter filter, int column) {
		ColumnFilter columnFilter = columnFilterMap.get(column);
		if (columnFilter != null) {
			columnFilterMap.remove(column);
		}
		columnFilterMap.put(column, filter);
	}
	
	public void clearLastActivityIndexSelected() {
		lastActivityIndexSelected = -1;
		lastActivityRowSelected = -1;
	}

	public void setLastActivityIndexSelected(int row, int idx) {
		lastActivityRowSelected = row;
		lastActivityIndexSelected = idx;
	}

	public int getLastActivityIndexSelected() {
		return lastActivityIndexSelected;
	}

	public int getLastActivityRowSelected() {
		return lastActivityRowSelected;
	}
	
	public void sortBasedOnColumnSorts() throws Exception {
		// score all sequences based on column sorts
		calcSortValuesBasedOnColumnSorts();

		// sort sequence components based on the sortValue setup above
		sortBasedOnSortValue();
	}

	public void sortBasedOnSortValue() throws Exception {
		AlignmentSorter sorter = new AlignmentSorter(this, this);
		sorter.sort();
	}

	void calcSortValuesBasedOnColumnSorts() {
		// now for each sequence...
		for (int i = 0; i < sequencesInAlignment.length; i++) {
			// calculate the sort value based on all column sorts
			calcSortValueBasedOnColumnSorts(sequencesInAlignment[i]);
		}
	}

	public void calcSortValueBasedOnColumnSorts(Sequence seq) {
		// start with zero
		int sortValue = 0;

		// add in bits based on column sorts
		java.util.Iterator<Integer> iter = getColumnSortColumnIterator();
		while (iter.hasNext()) {
			Integer column = iter.next();
			ColumnSort cs = columnSortMap.get(column);

			// get activity at this column
			String encodedActivity = seq.getEncodedActivity(column);

			// add in this bit
			sortValue |= cs.optionTestGetPriorityBit(encodedActivity);
		}

		// set this
		seq.setSortValue(sortValue);
	}

	public java.util.Iterator<Integer> getColumnSortColumnIterator() {
		return columnSortMap.keySet().iterator();
	}

	public java.util.Iterator<Integer> getColumnFilterColumnIterator() {
		return columnFilterMap.keySet().iterator();
	}
	
	public int compare(Sequence seq1, Sequence seq2) {
		return seq2.getSortValue() - seq1.getSortValue();
	}
	
	public void setLastRulerAnnIdxSelected(int idx) {
		lastRulerAnnIndexSelected = idx;
		clearLastActivityIndexSelected();
	}
	
	public void clearLastRulerAnnIndexSelected() {
		lastRulerAnnIndexSelected = -1;
	}
	
	public int getLastRulerAnnIndexSelected(){
		return lastRulerAnnIndexSelected;
	}
	
	public void clearLastColumnIndexSelected() {
		lastColumnIndexSelected = -1;
	}

	public int getColumnSortCurrentColumn() {
		return columnSortCurrColumn;
	}
	
	public int getColumnFilterCurrentColumn() {
		return columnFilterCurrColumn;
	}
	
	public boolean getColumnFiltering() {
		return columnFiltering;
	}
	
	public void setColumnFiltering(boolean b) {
		columnFiltering = b;
	}
	
	public void setColumnSorting(boolean b) {
		columnSorting = b;
	}
	
	// Ruler Annotation class
	public static class RulerAnnotation {
		private String letter;
		private Color color;
		private boolean selected;

		public RulerAnnotation(String letter, Color color, boolean selected) {
			this.letter = letter;
			this.color = color;
			this.selected = selected;
		}

		public String getLetter() {
			return letter;
		}

		public Color getColor() {
			return color;
		}

		public boolean isSelected() {
			return selected;
		}

		public void setLetter(String newLetter) {
			letter = newLetter;
		}

		public void setColor(Color newColor) {
			color = newColor;
		}

		public void setSelected(boolean newSelected) {
			selected = newSelected;
		}
	}
	
	public boolean isRulerAnnotationSelected() {
		boolean isRulerAnnotationSelected = false;

		for (RulerAnnotation rulerAnnotation : rulerAnnotationList) {
			if (rulerAnnotation.isSelected()) {
				isRulerAnnotationSelected = true;
			}
		}

		return isRulerAnnotationSelected;
	}

	public int rulerAnnotationsSize() {
		return rulerAnnotationList.size();
	}

	public void addRulerAnnotation() {
		rulerAnnotationList.add(new RulerAnnotation(null, Color.blue, false));
	}

	public void removeRulerAnnotationFromEnd() {
		rulerAnnotationList.remove(rulerAnnotationList.size() - 1);
	}

	public void removeRulerAnnotationRange(int from, int to) {
		for (int i = 0; i < to - from; i++) {
			rulerAnnotationList.remove(from);
		}
	}

	public RulerAnnotation getRulerAnnotation(int idx) {
		if ((idx != -1) && (idx < rulerAnnotationList.size())) {
			return rulerAnnotationList.get(idx);
		} else {
			return (new RulerAnnotation(null, Color.white, false));
		}
	}
	
	public void deselectAllRulerSelections() {
		for (RulerAnnotation rulerAnnotation : rulerAnnotationList) {
			rulerAnnotation.setSelected(false);
		}

		lastRulerAnnIndexSelected = -1;
	}
	
	public int getLastColumnIdxSelected() {
		return lastColumnIndexSelected;
	}

	public void setLastColumnIdxSelected(int idx) {
		lastColumnIndexSelected = idx;
		clearLastActivityIndexSelected();
	}
	
	public String[] getAlignedTraces(){
		int noTraces = sequencesInAlignment.length;
		String[] alignedTraces = new String[noTraces];
		
		for(int i = 0; i < noTraces; i++){
			alignedTraces[i] = sequencesInAlignment[i].toString();
		}
		return alignedTraces;
	}

	public boolean isColumnSorting() {
		return columnSorting;
	}
	
	public AlignmentFrame getAlignmentFrame() {
		return owner;	
		
	}
}
