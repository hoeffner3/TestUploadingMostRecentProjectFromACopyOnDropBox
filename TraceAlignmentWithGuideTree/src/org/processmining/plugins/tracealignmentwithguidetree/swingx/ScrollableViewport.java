package org.processmining.plugins.tracealignmentwithguidetree.swingx;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.Rectangle;

import javax.swing.BoundedRangeModel;
import javax.swing.JScrollBar;
import javax.swing.JViewport;
import javax.swing.Scrollable;
import javax.swing.SwingConstants;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

/**
 * JViewport that can sync (horizontally or vertically) to scrollbars or other
 * viewports.
 * 
 * @author R.P. Jagadeesh Chandra 'JC' Bose (adapted from pf aa t)
 * @version 0.1
 * @date 18 December 2009
 */
@SuppressWarnings("serial")
public class ScrollableViewport extends JViewport {
	private JScrollBar horizontalScrollBar, verticalScrollBar;
	private ScrollableViewport horizontalMaster, verticalMaster;

	public ScrollableViewport() {
		super();
		addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				syncScrollbar();
			}
		});
	}

	public void setHorizontalViewport(ScrollableViewport hmaster) {
		horizontalMaster = hmaster;
		hmaster.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				syncViewport();
			}
		});
	}

	public void setVerticalViewport(ScrollableViewport vmaster) {
		verticalMaster = vmaster;
		vmaster.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				syncViewport();
			}
		});
	}

	public void setHorizontalScrollbar(JScrollBar hsb) {
		horizontalScrollBar = hsb;
		hsb.getModel().addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				BoundedRangeModel model = (BoundedRangeModel) (e.getSource());
				Point p = getViewPosition();

				p.x = model.getValue();
				setViewPosition(p);
			}
		});
		syncScrollbar();
	}

	public void setVerticalScrollbar(JScrollBar vsb) {
		verticalScrollBar = vsb;
		vsb.getModel().addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				BoundedRangeModel model = (BoundedRangeModel) (e.getSource());
				Point p = getViewPosition();

				p.y = model.getValue();
				setViewPosition(p);
			}
		});
		syncScrollbar();
	}

	public JScrollBar getVerticalScrollBar() {
		return verticalScrollBar;
	}

	private void syncViewport() {
		if ((verticalMaster == null) && (horizontalMaster == null)) {
			return;
		}

		Point p = getViewPosition();

		if (horizontalMaster != null) {
			p.x = horizontalMaster.getViewPosition().x;
		}
		if (verticalMaster != null) {
			p.y = verticalMaster.getViewPosition().y;
		}
		setViewPosition(p);
	}

	private void syncScrollbar() {
		if ((verticalScrollBar == null) && (horizontalScrollBar == null)) {
			return;
		}

		Dimension extentSize = getExtentSize();

		// add this because getViewSize() was crashing
		// during startup when component had zero size...
		if ((extentSize.width == 0) && (extentSize.height == 0)) {
			return;
			// add this because getViewSize() was crashing
		}

		Dimension viewSize = getViewSize();
		Point viewPosition = getViewPosition();
		Component view = getView();

		if (verticalScrollBar != null) {
			int extent = extentSize.height;
			int max = viewSize.height;
			int value = Math.max(0, Math.min(viewPosition.y, max - extent));

			verticalScrollBar.setValues(value, extent, 0, max);
			if (view instanceof Scrollable) {
				Scrollable s = (Scrollable) view;
				Rectangle viewRect = new Rectangle(viewPosition, extentSize);
				int ui = s.getScrollableUnitIncrement(viewRect, SwingConstants.VERTICAL, 1);

				verticalScrollBar.setUnitIncrement(ui);
				int bi = s.getScrollableBlockIncrement(viewRect, SwingConstants.VERTICAL, 1);

				verticalScrollBar.setBlockIncrement(bi);
			}
		}
		if (horizontalScrollBar != null) {
			int extent = extentSize.width;
			int max = viewSize.width;
			int value = Math.max(0, Math.min(viewPosition.x, max - extent));

			horizontalScrollBar.setValues(value, extent, 0, max);
			if (view instanceof Scrollable) {
				Scrollable s = (Scrollable) view;
				Rectangle viewRect = new Rectangle(viewPosition, extentSize);
				int ui = s.getScrollableUnitIncrement(viewRect, SwingConstants.HORIZONTAL, 1);

				horizontalScrollBar.setUnitIncrement(ui);
				int bi = s.getScrollableBlockIncrement(viewRect, SwingConstants.HORIZONTAL, 1);

				horizontalScrollBar.setBlockIncrement(bi);
			}
		}
	}
}
