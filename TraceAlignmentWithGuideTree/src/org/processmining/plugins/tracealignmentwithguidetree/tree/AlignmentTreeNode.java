package org.processmining.plugins.tracealignmentwithguidetree.tree;

import java.util.Queue;
import java.util.Set;
import java.util.concurrent.ConcurrentLinkedQueue;

import org.processmining.plugins.tracealignmentwithguidetree.msa.RefineAlignment;

public class AlignmentTreeNode {
	int encodingLength;
	String encodedTrace;
	String[] originalAlignment;
	String[] refinedAlignment;
	
	int level;
	int step;
	AlignmentTreeNode parent;
	AlignmentTreeNode left;
	AlignmentTreeNode right;
	
	Set<String> concurrentActivityAcrossAllTracesSet;
	Set<String> concurrentActivitySet;
	
	public AlignmentTreeNode(int level, int step, int encodingLength, String encodedTrace){
		this.encodingLength = encodingLength;
		this.level = level;
		this.step = step;
		this.encodedTrace = encodedTrace;
	}
	
	public void setEncodedTrace(String encodedTrace){
		this.encodedTrace = encodedTrace;
	}
	
	public void setLeft(AlignmentTreeNode left){
		this.left = left;
	}
	
	public void setRight(AlignmentTreeNode right){
		this.right = right;
	}
	
	public void setParent(AlignmentTreeNode parent){
		this.parent = parent;
	}
	
	public void setOriginalAlignment(int encodingLength, String[] originalAlignment){
		this.originalAlignment = originalAlignment;
		
		int noTraces = originalAlignment.length;
		this.refinedAlignment = new String[noTraces];
		for(int i = 0; i < noTraces; i++)
			refinedAlignment[i] = originalAlignment[i];
		findRefinedConcurrentActivitySet(encodingLength);
	}
	
	public void setRefinedAlignment(int encodingLength, String[] refinedAlignment){
		this.refinedAlignment = refinedAlignment;
		findRefinedConcurrentActivitySet(encodingLength);
	}
	
	public void reset(){
		int noTraces = originalAlignment.length;
		this.refinedAlignment = new String[noTraces];
		for(int i = 0; i < noTraces; i++)
			refinedAlignment[i] = originalAlignment[i];
		findOriginalConcurrentActivitySet(encodingLength);
	}
	
	public int getLevel(){
		return this.level;
	}
	
	public int getStep(){
		return this.step;
	}
	
	public AlignmentTreeNode getLeft(){
		return this.left;
	}
	
	public AlignmentTreeNode getRight(){
		return this.right;
	}
	
	public String[] getOriginalAlignment(){
		return originalAlignment;
	}
	
	public String[] getRefinedAlignment(){
		return refinedAlignment;
	}
	
	public String getEncodedTrace(){
		return encodedTrace;
	}
	
	public int getNoChildren(){
		AlignmentTreeNode node = this;
		Queue<AlignmentTreeNode> queue = new ConcurrentLinkedQueue<AlignmentTreeNode>();
		if(this.right == null && this.left == null)
			return 0;
		
		queue.add(node);
		int noChildren = 0;
        while(!queue.isEmpty()){
        	 node = queue.remove();
        	 if(node.right == null && node.left == null)
        		 noChildren++;
        	 if(node.right != null)
        		 queue.add(node.right);
        	 if(node.left != null)
        		 queue.add(node.left);
         }
        return noChildren;
     }
	
	public void findOriginalConcurrentActivitySet(int encodingLength){
		RefineAlignment refineAlignment = new RefineAlignment(encodingLength);
		concurrentActivityAcrossAllTracesSet = refineAlignment.getConcurrentActivities(originalAlignment, true);
		concurrentActivitySet = refineAlignment.getConcurrentActivities(originalAlignment, false);
	}
	
	public void findRefinedConcurrentActivitySet(int encodingLength){
		RefineAlignment refineAlignment = new RefineAlignment(encodingLength);
		concurrentActivityAcrossAllTracesSet = refineAlignment.getConcurrentActivities(refinedAlignment, true);
		concurrentActivitySet = refineAlignment.getConcurrentActivities(refinedAlignment, false);
	}
	
	public Set<String> getConcurrentActivitySet(){
		return concurrentActivitySet;
	}
	
	public Set<String> getConcurrentActivityAcrossAllTracesSet(){
		return concurrentActivityAcrossAllTracesSet;
	}
}
