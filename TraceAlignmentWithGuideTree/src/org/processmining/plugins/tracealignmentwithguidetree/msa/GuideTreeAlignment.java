package org.processmining.plugins.tracealignmentwithguidetree.msa;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.ConcurrentLinkedQueue;

import javax.swing.JFrame;
import javax.swing.JOptionPane;

import org.processmining.plugins.guidetreeminer.tree.GuideTree;
import org.processmining.plugins.guidetreeminer.tree.GuideTreeNode;
import org.processmining.plugins.tracealignmentwithguidetree.TraceAlignmentWithGuideTreeInput;
import org.processmining.plugins.tracealignmentwithguidetree.tree.AlignmentTree;
import org.processmining.plugins.tracealignmentwithguidetree.tree.AlignmentTreeNode;
import org.processmining.plugins.tracealignmentwithguidetree.util.IndelSubstitutionScore;

public class GuideTreeAlignment {
	GuideTree guideTree;
	AlignmentTree alignmentTree;
	IndelSubstitutionScore indelSubstitutionScore;
	TraceAlignmentWithGuideTreeInput input;
	
	Map<Integer, Set<GuideTreeNode>> levelGuideTreeNodeSetMap;
	int[] sortedGuideTreeLevelArray;
	
	Map<Integer, Set<AlignmentTreeNode>> levelAlignmentTreeNodeSetMap;
	int[] sortedAlignmentTreeLevelArray;
	
	public GuideTreeAlignment(GuideTree guideTree, TraceAlignmentWithGuideTreeInput input, IndelSubstitutionScore indelSubstitutionScore){
		this.guideTree = guideTree;
		this.input = input;
		this.indelSubstitutionScore = indelSubstitutionScore;
		
//		System.out.println("Level Order Guide Tree Traversal");
		levelOrderGuideTreeTraversal();
		
		initializeAlignmentTree();
		
//		System.out.println("Level Order Alignment Tree Traversal");
		levelOrderAlignmentTreeTraversal();
		
		doAlignment();
	}
	
	private void initializeAlignmentTree(){
		alignmentTree = new AlignmentTree();
		
		List<List<String>> clusterEncodedTraceList = guideTree.getClusters(4);
		System.out.println("No. Clusters: "+clusterEncodedTraceList.size());
		System.out.println("No. Elements: "+guideTree.getNoElements());
		
		int noLevels = sortedGuideTreeLevelArray.length;
//		int encodingLength = guideTree.getEncodingLength();
		String encodedTrace;
		
		Set<GuideTreeNode> levelGuideTreeNodeSet = levelGuideTreeNodeSetMap.get(sortedGuideTreeLevelArray[0]);
		
		alignmentTree.setLog(guideTree.getLog());
		alignmentTree.setNoElements(guideTree.getNoElements());
		alignmentTree.setEncodingLength(guideTree.getEncodingLength());
		alignmentTree.setNoUniqueTraces(levelGuideTreeNodeSet.size());
		alignmentTree.setActivityCharMap(guideTree.getActivityCharMap());
		alignmentTree.setCharActivityMap(guideTree.getCharActivityMap());
		alignmentTree.setEncodedTraceIdenticalIndicesMap(guideTree.getEncodedTraceIdenticalIndicesMap());

		System.out.println("No. Unique Traces: "+levelGuideTreeNodeSet.size());
		
//		System.out.println("Guide Tree ActivityCharMap Size: "+guideTree.getActivityCharMap().size());
		
		Map<GuideTreeNode, AlignmentTreeNode> guideTreeNodeAlignmentTreeNodeMap = new HashMap<GuideTreeNode, AlignmentTreeNode>();
		
		for(GuideTreeNode guideTreeNode : levelGuideTreeNodeSet){
			AlignmentTreeNode alignmentTreeNode = new AlignmentTreeNode(sortedGuideTreeLevelArray[0], guideTreeNode.getStep(), guideTree.getEncodingLength(), guideTreeNode.getEncodedTrace());
			alignmentTreeNode.setLeft(null);
			alignmentTreeNode.setRight(null);
			encodedTrace = guideTreeNode.getEncodedTrace();

			String[] originalAlignment = new String[1];
			originalAlignment[0] = encodedTrace;
			
			alignmentTreeNode.setOriginalAlignment(guideTree.getEncodingLength(), originalAlignment);
			guideTreeNodeAlignmentTreeNodeMap.put(guideTreeNode, alignmentTreeNode);
		}
		
		for(int i = 1; i < noLevels; i++){
			levelGuideTreeNodeSet = levelGuideTreeNodeSetMap.get(sortedGuideTreeLevelArray[i]);
			for(GuideTreeNode guideTreeNode : levelGuideTreeNodeSet){
				AlignmentTreeNode alignmentTreeNode = new AlignmentTreeNode(guideTreeNode.getLevel(), guideTreeNode.getStep(), guideTree.getEncodingLength(), guideTreeNode.getEncodedTrace());
				guideTreeNodeAlignmentTreeNodeMap.put(guideTreeNode, alignmentTreeNode);
			}
		}
		
		//set parents
		for(int i = 0; i < noLevels; i++){
			levelGuideTreeNodeSet = levelGuideTreeNodeSetMap.get(sortedGuideTreeLevelArray[i]);
			for(GuideTreeNode guideTreeNode : levelGuideTreeNodeSet){
				AlignmentTreeNode alignmentTreeNode = guideTreeNodeAlignmentTreeNodeMap.get(guideTreeNode);
				if(guideTreeNode.getParent() != null){
					AlignmentTreeNode parent = guideTreeNodeAlignmentTreeNodeMap.get(guideTreeNode.getParent());
					alignmentTreeNode.setParent(parent);
				}else{
					alignmentTreeNode.setParent(null);
				}
			}
		}
		
		//set children
		for(int i = 1; i < noLevels; i++){
			levelGuideTreeNodeSet = levelGuideTreeNodeSetMap.get(sortedGuideTreeLevelArray[i]);
			for(GuideTreeNode guideTreeNode : levelGuideTreeNodeSet){
				AlignmentTreeNode alignmentTreeNode = guideTreeNodeAlignmentTreeNodeMap.get(guideTreeNode);
				if(guideTreeNode.getLeft() != null){
					AlignmentTreeNode left = guideTreeNodeAlignmentTreeNodeMap.get(guideTreeNode.getLeft());
					alignmentTreeNode.setLeft(left);
				}else{
					alignmentTreeNode.setLeft(null);
				}
				
				if(guideTreeNode.getRight() != null){
					AlignmentTreeNode right = guideTreeNodeAlignmentTreeNodeMap.get(guideTreeNode.getRight());
					alignmentTreeNode.setRight(right);
				}else{
					alignmentTreeNode.setRight(null);
				}
			}
		}
		
		alignmentTree.setRoot(guideTreeNodeAlignmentTreeNodeMap.get(guideTree.getRoot()));
		guideTreeNodeAlignmentTreeNodeMap = null;
		List<AlignmentTreeNode> clusterAlignmentTreeNodeList = alignmentTree.getClusterNodes(4);
		System.out.println("No. AlignmentTree Nodes Clusters: "+clusterAlignmentTreeNodeList.size());
	}
	
	protected void levelOrderGuideTreeTraversal(){
		levelGuideTreeNodeSetMap = new HashMap<Integer, Set<GuideTreeNode>>();
		Set<GuideTreeNode> levelNodeSet;
		Queue<GuideTreeNode> queue = new ConcurrentLinkedQueue<GuideTreeNode>();
		 
		GuideTreeNode root = guideTree.getRoot();
        if(root != null)
        {
            queue.add(root);
            while(!queue.isEmpty())
            {
                root = (GuideTreeNode)queue.remove();
                if(levelGuideTreeNodeSetMap.containsKey(root.getLevel())){
                	levelNodeSet = levelGuideTreeNodeSetMap.get(root.getLevel());
                }else{
                	levelNodeSet = new HashSet<GuideTreeNode>();
                }
                levelNodeSet.add(root);
                levelGuideTreeNodeSetMap.put(root.getLevel(), levelNodeSet);
                
                if(root.getRight() != null)
                    queue.add(root.getRight());
                if(root.getLeft() != null)
                    queue.add(root.getLeft());
            }
        }
        
        TreeSet<Integer> sortedLevelSet = new TreeSet<Integer>();
        sortedLevelSet.addAll(levelGuideTreeNodeSetMap.keySet());
        int noLevels = sortedLevelSet.size();
        sortedGuideTreeLevelArray = new int[noLevels];
        
        int index = 0;
        for(Integer level : sortedLevelSet)
        	sortedGuideTreeLevelArray[index++] = level;
        
/*        
        for(int i = 1; i < index; i++){
        	levelNodeSet = levelGuideTreeNodeSetMap.get(sortedGuideTreeLevelArray[i]);
        	
        	System.out.println("Level: "+sortedGuideTreeLevelArray[i]);
        	for(GuideTreeNode node : levelNodeSet){
        		if(node.getLeft() != null && node.getRight() != null)
        			System.out.println(node.getEncodedTrace()+"\tL:"+node.getLeft().getEncodedTrace()+"\tR:"+node.getRight().getEncodedTrace());
        		else if(node.getLeft() != null){
        			System.out.println(node.getEncodedTrace()+"\tL:"+node.getLeft().getEncodedTrace());
        		}else if(node.getRight() != null){
        			System.out.println(node.getEncodedTrace()+"\tR:"+node.getRight().getEncodedTrace());
        		}
        	}
        }
  */      
        sortedLevelSet = null;
	}

	protected void levelOrderAlignmentTreeTraversal(){
		levelAlignmentTreeNodeSetMap = new HashMap<Integer, Set<AlignmentTreeNode>>();
		Set<AlignmentTreeNode> levelNodeSet;
		Queue<AlignmentTreeNode> queue = new ConcurrentLinkedQueue<AlignmentTreeNode>();
		 
		AlignmentTreeNode root = alignmentTree.getRoot();
        if(root != null)
        {
            queue.add(root);
            while(!queue.isEmpty())
            {
                root = (AlignmentTreeNode)queue.remove();
                if(levelAlignmentTreeNodeSetMap.containsKey(root.getLevel())){
                	levelNodeSet = levelAlignmentTreeNodeSetMap.get(root.getLevel());
                }else{
                	levelNodeSet = new HashSet<AlignmentTreeNode>();
                }
                levelNodeSet.add(root);
                levelAlignmentTreeNodeSetMap.put(root.getLevel(), levelNodeSet);
                
                if(root.getRight() != null)
                    queue.add(root.getRight());
                if(root.getLeft() != null)
                    queue.add(root.getLeft());
            }
        }
        
        TreeSet<Integer> sortedLevelSet = new TreeSet<Integer>();
        sortedLevelSet.addAll(levelAlignmentTreeNodeSetMap.keySet());
        int noLevels = sortedLevelSet.size();
        sortedAlignmentTreeLevelArray = new int[noLevels];
        
        int index = 0;
        for(Integer level : sortedLevelSet)
        	sortedAlignmentTreeLevelArray[index++] = level;
        
  /*      
        for(int i = 0; i < index; i++){
        	levelNodeSet = levelAlignmentTreeNodeSetMap.get(sortedAlignmentTreeLevelArray[i]);
        	
        	System.out.println("Level: "+sortedAlignmentTreeLevelArray[i]);
        	for(AlignmentTreeNode node : levelNodeSet){
        		if(node.getLeft() != null && node.getRight() != null)
        			System.out.println(node.getEncodedTrace()+"\tL:"+node.getLeft().getEncodedTrace()+"\tR:"+node.getRight().getEncodedTrace());
        		else if(node.getLeft() != null){
        			System.out.println(node.getEncodedTrace()+"\tL:"+node.getLeft().getEncodedTrace());
        		}else if(node.getRight() != null){
        			System.out.println(node.getEncodedTrace()+"\tR:"+node.getRight().getEncodedTrace());
        		}
        	}
        }
   */     
        sortedLevelSet = null;
	}
	
	@SuppressWarnings("unused")
	protected void doAlignment0(){
		String[] p1 = {"jgc-l-eb-d-klfebdklebd-----i",
				"jgc-l-eb-dfkl-ebdklebd-----i",
				"jgc-l-ebfd-kl-ebdklebd-----i",
				"jgcfl-eb-d-kl-ebdklebd-----i",
				"jgc-lfeb-d-kl-ebdklebd-----i",
				"jgc-lfeb-d-kl-ebdklebdklebdi"};
		String p2 = "jgclfebdklebdklebdklebdi";
		String[] p3 = {"jgclf-eb-d-kl-eb-di",
				"jgc-fleb-d-kl-eb-di",
				"jgc--lebfd-kl-eb-di",
				"jgc--leb-d-kl-ebfdi",
				"jgc--leb-dfkl-eb-di",
				"jgc--leb-dfkl-eb-d-",
				"jgc--leb-d-klfeb-di",
				"jgc--leb-d-klfeb-d-"};
		
		SequenceAlignment sequenceAlignment = new SequenceAlignment(guideTree.getEncodingLength(), indelSubstitutionScore.getSubstitutionScoreMap(), indelSubstitutionScore.getIndelRightGivenLeftScoreMap(), indelSubstitutionScore.getIndelLeftGivenRightScoreMap());
		String[] alignment = sequenceAlignment.getPairWiseGlobalAlignment(p1, p3);
		for(String align : alignment)
			System.out.println(align);
	}
	
	protected void doAlignment1(){
		String[] profileAlignment = {"jgc--ahbfd-ka-hbd-ka-hbdi",
				"jgc--ahbfd-ka-hbd-ka-hbd-",
				"jgc-fahb-d-ka-hbd-ka-hbdi",
				"jgc-fahb-d-ka-hbd-ka-hbd-",
				"jgcaf-hb-d-ka-hbd-ka-hbdi",
				"jgcaf-hb-d-ka-hbd-ka-hbd-",
				"jgc--ahb-d-ka-hbdfka-hbdi",
				"jgc--ahb-d-ka-hbdfka-hbd-",
				"jgc--ahb-dfka-hbd-ka-hbdi",
				"jgc--ahb-d-ka-hbd-kafhbdi",
				"jgc--ahb-d-ka-hbd-kafhbd-",
				"jgc--ahb-d-kafhbd-ka-hbdi",
				"jgc--ahb-d-kafhbd-ka-hbd-"};
		String sequence = "jgcahfbdkahbdkahbdi";
		SequenceAlignment sequenceAlignment = new SequenceAlignment(guideTree.getEncodingLength(), indelSubstitutionScore.getSubstitutionScoreMap(), indelSubstitutionScore.getIndelRightGivenLeftScoreMap(), indelSubstitutionScore.getIndelLeftGivenRightScoreMap());
		String[] alignment = sequenceAlignment.getPairWiseGlobalAlignment(profileAlignment, sequence);
		for(String align : alignment)
			System.out.println(align);
	}
	
	private void doAlignment(){
//		int encodingLength = guideTree.getEncodingLength();
		int noLevels = sortedAlignmentTreeLevelArray.length;
		Set<AlignmentTreeNode> levelNodeSet;
		RefineAlignment refineAlignment = new RefineAlignment(guideTree.getEncodingLength());
		SequenceAlignment sequenceAlignment = new SequenceAlignment(guideTree.getEncodingLength(), indelSubstitutionScore.getSubstitutionScoreMap(), indelSubstitutionScore.getIndelRightGivenLeftScoreMap(), indelSubstitutionScore.getIndelLeftGivenRightScoreMap());
		
		String[] alignment;
		
		levelNodeSet = levelAlignmentTreeNodeSetMap.get(sortedAlignmentTreeLevelArray[1]);
		for(AlignmentTreeNode node : levelNodeSet){
//			System.out.println(node.getEncodedTrace()+"\tL:"+node.getLeft().getEncodedTrace()+"\tR:"+node.getRight().getEncodedTrace());
			if(node.getLeft().getOriginalAlignment().length == 1 && node.getRight().getOriginalAlignment().length == 1){
				alignment = sequenceAlignment.getPairWiseGlobalAlignment(node.getLeft().getEncodedTrace(), node.getRight().getEncodedTrace());
				node.setOriginalAlignment(guideTree.getEncodingLength(), alignment);
//				System.out.println("Global");
//				System.out.println(alignment[0]);
//				System.out.println(alignment[1]);
			}else{
				JOptionPane.showMessageDialog(new JFrame(), "Something Wrong: The node at level 1 should have as its children the leaf nodes with original alignment array length = 1", "Error", JOptionPane.ERROR_MESSAGE);
			}
//			System.out.println();
		}
		
		for(int i = 2; i < noLevels; i++){
			levelNodeSet = levelAlignmentTreeNodeSetMap.get(sortedAlignmentTreeLevelArray[i]);
			for(AlignmentTreeNode node : levelNodeSet){
//				System.out.println(node.getEncodedTrace()+"\tL:"+node.getLeft().getEncodedTrace()+"\tR:"+node.getRight().getEncodedTrace());
				if(node.getLeft().getOriginalAlignment().length > 1 && node.getRight().getOriginalAlignment().length > 1){
					//Profile-Profile Alignment
					alignment = sequenceAlignment.getPairWiseGlobalAlignment(node.getLeft().getOriginalAlignment(), node.getRight().getOriginalAlignment());
				}else if(node.getLeft().getOriginalAlignment().length > 1){
					alignment = sequenceAlignment.getPairWiseGlobalAlignment(node.getLeft().getOriginalAlignment(), node.getRight().getEncodedTrace());
				}else{
					alignment = sequenceAlignment.getPairWiseGlobalAlignment(node.getRight().getOriginalAlignment(), node.getLeft().getEncodedTrace());
				}
//				System.out.println("Global");
//				for(int j = 0; j < alignment.length; j++)
//					System.out.println(alignment[j]);
				
				if(!input.isPerformRealignment())
					node.setOriginalAlignment(guideTree.getEncodingLength(), alignment);
				else{
					String[] refinedAlignment; 
					
					switch(input.getRealignmentStrategy()){
						case BlockShift:
							refinedAlignment = refineAlignment.performBlockShiftLeft(alignment);
							node.setOriginalAlignment(guideTree.getEncodingLength(), refinedAlignment);
							break;
						case ConcurrencyFilteredRealignment:
							refinedAlignment =  refineAlignment.refineConcurrent(alignment, true);
							node.setOriginalAlignment(guideTree.getEncodingLength(), refinedAlignment);
							break;
					}
					if(!refineAlignment.isValidRefinement()){
						JOptionPane.showMessageDialog(new JFrame(), "Refinement Error", "Not a valid Refinement. Resetting to original alignment", JOptionPane.ERROR_MESSAGE);
						node.setOriginalAlignment(guideTree.getEncodingLength(), alignment);
					}
					
					/*
	//				System.out.println("Refining");
					String[] refinedAlignment =  refineAlignment.refineConcurrent(encodingLength, alignment, true);
					if(!refineAlignment.isValidRefinement())
						System.out.println("NOT A VALID REFINEMENT");
					node.setOriginalAlignment(refinedAlignment);
					
//					System.out.println("Refined Alignment");
//					for(String refinedTrace : refinedAlignment)
//						System.out.println(refinedTrace);
					 * 
					 */
				}
				
				
				System.out.println("Semi-Global");
				alignment = sequenceAlignment.getPairWiseSemiGlobalAlignment(node.getLeft().getEncodedTrace(), node.getRight().getEncodedTrace());
				System.out.println(alignment[0]);
				System.out.println(alignment[1]);
				System.out.println();
			}
//			break;
		}
	}
	
	public AlignmentTree getAlignmentTree(){
		return alignmentTree;
	}
}
