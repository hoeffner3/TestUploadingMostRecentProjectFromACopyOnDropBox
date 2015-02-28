package org.processmining.plugins.tracealignmentwithguidetree.tree;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.TreeSet;
import java.util.concurrent.ConcurrentLinkedQueue;

import org.deckfour.xes.model.XLog;

public class AlignmentTree {
	XLog log;
	int encodingLength;
	int noUniqueTraces;
	int noElements;
	
	Map<String, String> activityCharMap;
	Map<String, String> charActivityMap;
	Map<String, TreeSet<Integer>> encodedTraceIdenticalIndicesMap;
	
	AlignmentTreeNode root;
	
	public AlignmentTree(){
		
	}
	
	public void setRoot(AlignmentTreeNode root){
		this.root = root;
	}
	
	public AlignmentTreeNode getRoot(){
		return this.root;
	}

	public void setNoUniqueTraces(int noUniqueTraces) {
		this.noUniqueTraces = noUniqueTraces;
	}

	public void setEncodingLength(int encodingLength) {
		this.encodingLength = encodingLength;
	}
	
	public void setActivityCharMap(Map<String, String> activityCharMap){
		this.activityCharMap = activityCharMap;
	}
	
	public void setCharActivityMap(Map<String, String> charActivityMap){
		this.charActivityMap = charActivityMap;
	}
	
	public List<AlignmentTreeNode> getClusterNodes(int noClusters){
		List<AlignmentTreeNode> clusterNodeList = new ArrayList<AlignmentTreeNode>();
		
		AlignmentTreeNode node = root;
		Queue<AlignmentTreeNode> queue = new ConcurrentLinkedQueue<AlignmentTreeNode>();
		queue.add(node);
		
		while(!queue.isEmpty()){
			node = queue.remove();
			if(node.getStep() >= noClusters){
				clusterNodeList.add(node);
			}else{
				if(node.right != null && node.right.getStep() != noElements){
					queue.add(node.right);
				}else if(node.right != null && node.right.getStep() == noElements){
					clusterNodeList.add(node.right);
				}
				
				if(node.left != null && node.left.getStep() != noElements){
					queue.add(node.left);
				}else if(node.left != null && node.left.getStep() == noElements){
					clusterNodeList.add(node.left);
				}
			}
		}
		
		return clusterNodeList;
	}
	
	public int getNoUniqueTraces() {
		return noUniqueTraces;
	}

	public int getEncodingLength() {
		return encodingLength;
	}
	
	public Map<String, String> getActivityCharMap(){
		return activityCharMap;
	}
	
	public Map<String, String> getCharActivityMap(){
		return charActivityMap;
	}

	public Map<String, TreeSet<Integer>> getEncodedTraceIdenticalIndicesMap() {
		return encodedTraceIdenticalIndicesMap;
	}

	public void setEncodedTraceIdenticalIndicesMap(
			Map<String, TreeSet<Integer>> encodedTraceIdenticalIndicesMap) {
		this.encodedTraceIdenticalIndicesMap = encodedTraceIdenticalIndicesMap;
	}

	public XLog getLog() {
		return log;
	}

	public void setLog(XLog log) {
		this.log = log;
	}
	
	public void setNoElements(int noElements){
		this.noElements = noElements;
	}
}
