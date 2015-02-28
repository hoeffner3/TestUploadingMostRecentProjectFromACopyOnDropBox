package org.processmining.plugins.tracealignmentwithguidetree.visualization;

import java.awt.Color;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import javax.swing.JTable;
import javax.swing.table.AbstractTableModel;

import org.processmining.plugins.tracealignmentwithguidetree.listeners.ActivityColorListener;

public class ActivityCharEncodingTable implements ActivityColorListener{
	Map<String, String> charActivityMap;
	JTable activityCharMapTable;
	Map<String, Color> encodedActivityColorMap;
	String dash;
	
	ActivityColorListener listener;
	
	public ActivityCharEncodingTable(Map<String, String> charActivityMap, String dash){
		this.charActivityMap = charActivityMap;
		this.dash = dash;
	}
	
	public void prepareTable(){
		generateEncodedActivityRandomColorMap();
		
		activityCharMapTable = new JTable(new ActivityCharEncodingTableModel());
		activityCharMapTable.setAutoCreateRowSorter(true);
		activityCharMapTable.getColumnModel().getColumn(2).setPreferredWidth(50);
		
		//Set up renderer and editor for the Favorite Color column.
        activityCharMapTable.setDefaultRenderer(Color.class,
                                 new ColorRenderer(true));
        ColorEditor colorEditor = new ColorEditor();
        colorEditor.setListener(this);
        activityCharMapTable.setDefaultEditor(Color.class,
                               colorEditor);
	}
	
	public void prepareTable(Map<String, Color> encodedActivityColorMap){
		this.encodedActivityColorMap = encodedActivityColorMap;
		activityCharMapTable = new JTable(new ActivityCharEncodingTableModel());
		activityCharMapTable.setAutoCreateRowSorter(true);
		activityCharMapTable.getColumnModel().getColumn(2).setPreferredWidth(50);
		
		//Set up renderer and editor for the Favorite Color column.
        activityCharMapTable.setDefaultRenderer(Color.class,
                                 new ColorRenderer(true));
        ColorEditor colorEditor = new ColorEditor();
        colorEditor.setListener(this);
        activityCharMapTable.setDefaultEditor(Color.class,
                               colorEditor);
	}
	
	private void generateEncodedActivityRandomColorMap() {
		encodedActivityColorMap = new HashMap<String, Color>();
		Color c;
		Random random = new Random();
		Set<Color> chosenColorSet = new HashSet<Color>();
		for (String encodedActivity : charActivityMap.keySet()) {
			do {
				c = new Color(random.nextInt()).brighter();
			} while ((c == Color.white) || (c == Color.black) || chosenColorSet.contains(c));
			chosenColorSet.add(c);
			encodedActivityColorMap.put(encodedActivity, c);
		}
		encodedActivityColorMap.put(dash, Color.white);
	}
	
	@SuppressWarnings("serial")
	class ActivityCharEncodingTableModel extends AbstractTableModel {
        private String[] columnNames = {"Activity Name",
                                        "Char Encoding",
                                        "Color"};
        private Object[][] data = new Object[charActivityMap.size()][3];

        public ActivityCharEncodingTableModel(){
        	int index = 0;
        	for(String encodedActivity: charActivityMap.keySet()){
        		data[index][0] = charActivityMap.get(encodedActivity);
        		data[index][1] = encodedActivity;
        		data[index][2] = encodedActivityColorMap.get(encodedActivity);
        		index++;
        	}
        }
        
        public int getColumnCount() {
            return columnNames.length;
        }

        public int getRowCount() {
            return data.length;
        }

        public String getColumnName(int col) {
            return columnNames[col];
        }

        public Object getValueAt(int row, int col) {
            return data[row][col];
        }

        /*
         * JTable uses this method to determine the default renderer/
         * editor for each cell.  If we didn't implement this method,
         * then the last column would contain text ("true"/"false"),
         * rather than a check box.
         */
        @SuppressWarnings({ "rawtypes", "unchecked" })
		public Class getColumnClass(int c) {
            return getValueAt(0, c).getClass();
        }

        public boolean isCellEditable(int row, int col) {
            //Note that the data/cell address is constant,
            //no matter where the cell appears onscreen.
            if (col < 1) {
                return false;
            } else {
                return true;
            }
        }

        public void setValueAt(Object value, int row, int col) {
            data[row][col] = value;
            fireTableCellUpdated(row, col);
        }
    }

	public JTable getActivityCharMapTable() {
		return activityCharMapTable;
	}
	
	public void setListener(ActivityColorListener listener){
		this.listener = listener;
	}
	
	public Map<String, Color> getEncodedActivityColorMap(){
		int noRows = activityCharMapTable.getRowCount();
		encodedActivityColorMap.clear();
		for(int i = 0; i < noRows; i++){
			encodedActivityColorMap.put((String)activityCharMapTable.getValueAt(i, 1), (Color)activityCharMapTable.getValueAt(i, 2));
		}
		encodedActivityColorMap.put(dash, Color.white);
		return encodedActivityColorMap;
	}

	public void activityColorMappingChanged(Color color) {
//		System.out.println("In ActivityCharEncoding activityColorMappingChanged");
		//Get the selected row for which the color has been changed
		int selectedRow = activityCharMapTable.getSelectedRow();
		String encodedActivity = (String)activityCharMapTable.getValueAt(selectedRow, 1);
//		Color color = (Color)activityCharMapTable.getValueAt(selectedRow, 2);
//		System.out.println("Color Changed: "+encodedActivity+" "+color);
		encodedActivityColorMap.put(encodedActivity, color);
		listener.activityColorChanged(encodedActivity, color);
	}

	public void activityColorChanged(String encodedActivity, Color color) {
//		System.out.println("In ActivityCharEncoding activityColorChanged");
		listener.activityColorChanged(encodedActivity, color);
	}
}
