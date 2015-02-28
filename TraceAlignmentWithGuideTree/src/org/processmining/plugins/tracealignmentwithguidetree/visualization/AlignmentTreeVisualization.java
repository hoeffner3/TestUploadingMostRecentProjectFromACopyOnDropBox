package org.processmining.plugins.tracealignmentwithguidetree.visualization;

import javax.swing.JComponent;

import org.processmining.contexts.uitopia.annotations.Visualizer;
import org.processmining.framework.plugin.PluginContext;
import org.processmining.framework.plugin.annotations.Plugin;
import org.processmining.framework.plugin.annotations.PluginVariant;
import org.processmining.plugins.tracealignmentwithguidetree.tree.AlignmentTree;

@Plugin(name = "Alignment Tree Visualization", 
		returnLabels = { "Visualizion of Alignment Tree" }, 
		returnTypes = { JComponent.class }, 
		parameterLabels = {"AlignmentTree"},
		userAccessible = false)
@Visualizer
public class AlignmentTreeVisualization {
	@PluginVariant(requiredParameterLabels = {0})
	public JComponent visualize(PluginContext context, 
			AlignmentTree alignmentTree){
		if(alignmentTree == null)
			return null;
		AlignmentFrame alignmentFrame = new AlignmentFrame(alignmentTree);
		return alignmentFrame;
	}
}
