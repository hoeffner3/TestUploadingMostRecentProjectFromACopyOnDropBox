package org.processmining.plugins.tracealignmentwithguidetree.swingx;

import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Point;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.io.File;

import javax.swing.JPopupMenu;
import javax.swing.KeyStroke;
import javax.swing.text.DefaultEditorKit;
import javax.swing.text.JTextComponent;
import javax.swing.text.Keymap;

/**
 * Some miscellaneous utilities.
 * 
 */
public class Utils {

	// clipboard keybindings for non-windows machines
	public static void addClipboardBindings(JTextComponent c) {
		JTextComponent.KeyBinding[] defaultBindings = {
				new JTextComponent.KeyBinding(KeyStroke.getKeyStroke(KeyEvent.VK_C, InputEvent.CTRL_MASK),
						DefaultEditorKit.copyAction),
				new JTextComponent.KeyBinding(KeyStroke.getKeyStroke(KeyEvent.VK_V, InputEvent.CTRL_MASK),
						DefaultEditorKit.pasteAction),
				new JTextComponent.KeyBinding(KeyStroke.getKeyStroke(KeyEvent.VK_X, InputEvent.CTRL_MASK),
						DefaultEditorKit.cutAction) };

		Keymap k = c.getKeymap();

		JTextComponent.loadKeymap(k, defaultBindings, c.getActions());
	}

	// get the binary directory for the system os
	public static String getOSDirectoryName() {
		String os = System.getProperty("os.name").toLowerCase();

		if (os.equals("linux")) {
			return "linux";
		} else if (os.equals("windows nt") || os.equals("windows 2000") || os.equals("windows 98")
				|| os.equals("windows 95")) {
			return "win32";
		} else if (os.equals("irix")) {
			return "irix";
		} else if (os.equals("mac os")) {
			return "macos";
		} else if (os.equals("solaris")) {
			return "solaris";
		} else {
			return "linux";
		}
	}

	public static String getCmdOptionChar() {
		String optionChar = "-";
		String os = System.getProperty("os.name").toLowerCase();

		if (os.equals("windows nt") || os.equals("windows 2000") || os.equals("windows 98") || os.equals("windows 95")) {
			optionChar = "/";
		}
		return optionChar;
	}

	// get the base of a file name
	public static String getBaseFileName(File f) {
		String s = f.getName();
		int i = s.lastIndexOf('.');

		if ((i > 0) && (i < s.length() - 1)) {
			return s.substring(0, i);
		} else {
			return s;
		}
	}

	// add a component to a GridBag
	public static void addToGridBag(Container ct, GridBagLayout gb, Component comp, int x, int y, int w, int h, int wx,
			int wy, int fill, int a) {
		GridBagConstraints c = new GridBagConstraints();

		c.gridx = x;
		c.gridy = y;
		c.gridwidth = w;
		c.gridheight = h;
		c.weightx = wx;
		c.weighty = wy;
		c.fill = fill;
		c.anchor = a;
		gb.setConstraints(comp, c);
		ct.add(comp);
	}

	// add a component to a GridBag
	public static void addToGridBag(Container ct, GridBagLayout gb, Component comp, int x, int y, int w, int h, int wx,
			int wy, int fill, int a, Insets insets) {
		GridBagConstraints c = new GridBagConstraints();

		c.gridx = x;
		c.gridy = y;
		c.gridwidth = w;
		c.gridheight = h;
		c.weightx = wx;
		c.weighty = wy;
		c.fill = fill;
		c.anchor = a;
		c.insets = insets;
		gb.setConstraints(comp, c);
		ct.add(comp);
	}

	// popup a menu without going off the bottom or side of the screen
	public static void showPopup(JPopupMenu popup, Component c, int x, int y) {
		Dimension screen_dim = c.getToolkit().getScreenSize();
		Point p = c.getLocationOnScreen();

		p.translate(x, y);
		Dimension popup_dim = popup.getSize();

		p.translate(popup_dim.width, popup_dim.height);
		if (p.x > screen_dim.width) {
			x -= p.x - screen_dim.width;
		}
		if (p.y > screen_dim.height) {
			y -= p.y - screen_dim.height;
		}
		popup.show(c, x, y);
	}

}
