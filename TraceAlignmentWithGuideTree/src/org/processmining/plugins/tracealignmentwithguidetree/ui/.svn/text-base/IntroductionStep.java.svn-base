package org.processmining.plugins.tracealignmentwithguidetree.ui;

import org.processmining.plugins.tracealignmentwithguidetree.settings.TraceAlignmentWithGuideTreeSettingsListener;

import info.clearthought.layout.TableLayout;

import com.fluxicon.slickerbox.factory.SlickerFactory;

@SuppressWarnings("serial")
public class IntroductionStep extends myStep {
	TraceAlignmentWithGuideTreeSettingsListener listener;
	
	public IntroductionStep(){
		initComponents();
	}
	
	private void initComponents(){
		double size[][] = {{TableLayout.FILL},{TableLayout.FILL}};
    	setLayout(new TableLayout(size));
    	String body = "<p>This wizard will guide you through the process of configuring this plugin.</p>";
    	body += "<p>The configuration options for trace alignment (with guide tree) can be divided into two steps:<ol>";
    	body += "<li>options for configuring the scoring matrices and gap penalty</li>";
    	body += "<li>options for configuring the alignment algorithm</li></p>";
    	body += "<p>The wizard will allow you to configure these two categories in the given order.</p>";
    	add(SlickerFactory.instance().createLabel("<html><h1>Introduction</h1>" + body), "0, 0, l, t");
	}
	
	public boolean precondition() {
		return true;
	}

	public void readSettings() {

	}

	public void setListener(TraceAlignmentWithGuideTreeSettingsListener listener) {
		this.listener = listener;
	}
}
