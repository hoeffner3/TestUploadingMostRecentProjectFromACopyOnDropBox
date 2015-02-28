package org.processmining.plugins.tracealignmentwithguidetree.visualization;

import java.awt.Color;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.AbstractCellEditor;
import javax.swing.JButton;
import javax.swing.JColorChooser;
import javax.swing.JDialog;
import javax.swing.JTable;
import javax.swing.table.TableCellEditor;

import org.processmining.plugins.tracealignmentwithguidetree.listeners.ActivityColorListener;

import com.fluxicon.slickerbox.factory.SlickerFactory;

@SuppressWarnings("serial")
public class ColorEditor extends AbstractCellEditor implements TableCellEditor,
		ActionListener {

	Color currentColor;
	JButton button;
	JColorChooser colorChooser;
	JDialog dialog;
	protected static final String EDIT = "edit";

	ActivityColorListener listener;
	
	public ColorEditor(){
		button = SlickerFactory.instance().createButton("");
        button.setActionCommand(EDIT);
        button.addActionListener(this);
        button.setBorderPainted(false);

        //Set up the dialog that the button brings up.
        colorChooser = new JColorChooser();
        dialog = JColorChooser.createDialog(button,
                                        "Pick a Color",
                                        true,  //modal
                                        colorChooser,
                                        this,  //OK button handler
                                        null); //no CANCEL button handler
	}
	
	public Component getTableCellEditorComponent(JTable table, Object value,
			boolean isSelected, int row, int column) {
		currentColor = (Color)value;
        return button;
	}

	public Object getCellEditorValue() {
		return currentColor;
	}

	public void actionPerformed(ActionEvent e) {
		if (EDIT.equals(e.getActionCommand())) {
	        //The user has clicked the cell, so
	        //bring up the dialog.
	        button.setBackground(currentColor);
	        colorChooser.setColor(currentColor);
	        dialog.setVisible(true);
	        //Make the renderer reappear.
	        fireEditingStopped();

	    } else { 
	    	//User pressed dialog's "OK" button.
	        currentColor = colorChooser.getColor();
//	        System.out.println("Color Changed");
	        listener.activityColorMappingChanged(currentColor);
	    }
	}
	
	public void setListener(ActivityColorListener listener){
		this.listener = listener;
	}
}
