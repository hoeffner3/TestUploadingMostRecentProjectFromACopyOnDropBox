package org.processmining.plugins.tracealignmentwithguidetree.swingx;

import java.awt.Font;

import javax.swing.JTextField;

/**
 * JTextField which always uses a fixed width font, good for having sequences
 * line up, etc.
 */
@SuppressWarnings("serial")
public class FixedWidthTextField extends JTextField {
	private static Font fixed_width_font = new Font("Courier", Font.PLAIN, 12);

	private final int width;

	public FixedWidthTextField(String text, int length) {
		// make a little bigger because JTextField doesn't factor in borders
		super(text, length + 1);
		setFont(fixed_width_font);
		//TODO check if we need to multiply this by encodingLength
		width = getFontMetrics(fixed_width_font).stringWidth("Q"); // *Alignment.encodingLength;
	}

	public int getCharWidth() {
		return width;
	}
}
