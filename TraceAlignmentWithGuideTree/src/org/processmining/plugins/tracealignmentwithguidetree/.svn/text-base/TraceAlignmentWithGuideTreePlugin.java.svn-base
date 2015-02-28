//Om Ganesayanamaha
package org.processmining.plugins.tracealignmentwithguidetree;

import org.processmining.contexts.uitopia.UIPluginContext;
import org.processmining.contexts.uitopia.annotations.UITopiaVariant;
import org.processmining.framework.plugin.annotations.Plugin;
import org.processmining.framework.plugin.annotations.PluginVariant;
import org.processmining.plugins.guidetreeminer.tree.GuideTree;
import org.processmining.plugins.tracealignmentwithguidetree.tree.AlignmentTree;
import org.processmining.plugins.tracealignmentwithguidetree.ui.TraceAlignmentWithGuideTreeUI;

@Plugin(name = "Trace Alignment (with Guide Tree)", parameterLabels = { "Guide Tree", "Trace Alignment With Guide Tree Input" }, returnLabels = { "Trace Alignment (With Guide Tree)" }, returnTypes = { AlignmentTree.class }, userAccessible = true, help = "Trace Alignment (with Guide Tree)")
public class TraceAlignmentWithGuideTreePlugin {
	@UITopiaVariant(affiliation = UITopiaVariant.EHV, author = "R.P. Jagadeesh Chandra 'JC' Bose", email = "j.c.b.rantham.prabhakara@tue.nl", website = "www.processmining.org")
	@PluginVariant(variantLabel = "Select options to use", requiredParameterLabels = { 0 })
	public static AlignmentTree traceAlignment(UIPluginContext context, GuideTree guideTree) {
		TraceAlignmentWithGuideTreeUI traceAlignmentWithGuideTreeUI = new TraceAlignmentWithGuideTreeUI(context);
		TraceAlignmentWithGuideTreeInput input = traceAlignmentWithGuideTreeUI.readInput();
//		return new AlignmentTree();
		return main(context, guideTree, input);
	}
	
	@PluginVariant(variantLabel = "Select options to use", requiredParameterLabels = {0, 1})
	public static AlignmentTree main(UIPluginContext context, GuideTree guideTree, TraceAlignmentWithGuideTreeInput input) {
		MineAlignment mineAlignment = new MineAlignment();
		return mineAlignment.mine(guideTree, input);
	}
}
