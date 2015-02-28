package org.processmining.plugins.tracealignmentwithguidetree.visualization;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.Vector;

import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JFileChooser;
import javax.swing.JInternalFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.JSlider;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.deckfour.xes.model.XLog;
import org.processmining.plugins.guidetreeminer.swingx.ErrorDialog;
import org.processmining.plugins.guidetreeminer.swingx.ScrollableGridLayout;
import org.processmining.plugins.tracealignmentwithguidetree.datatypes.Alignment;
import org.processmining.plugins.tracealignmentwithguidetree.datatypes.ColumnFilter;
import org.processmining.plugins.tracealignmentwithguidetree.datatypes.ColumnSort;
import org.processmining.plugins.tracealignmentwithguidetree.datatypes.ConsensusSequence;
import org.processmining.plugins.tracealignmentwithguidetree.datatypes.DisplayProperties;
import org.processmining.plugins.tracealignmentwithguidetree.datatypes.Sequence;
import org.processmining.plugins.tracealignmentwithguidetree.listeners.ActivityColorListener;
import org.processmining.plugins.tracealignmentwithguidetree.listeners.AlignmentListener;
import org.processmining.plugins.tracealignmentwithguidetree.listeners.DisplayPropertiesListener;
import org.processmining.plugins.tracealignmentwithguidetree.msa.RefineAlignment;
import org.processmining.plugins.tracealignmentwithguidetree.swingx.JListX;
import org.processmining.plugins.tracealignmentwithguidetree.swingx.LazyBoundedRangeModel;
import org.processmining.plugins.tracealignmentwithguidetree.swingx.ScrollableViewport;
import org.processmining.plugins.tracealignmentwithguidetree.tree.AlignmentTree;
import org.processmining.plugins.tracealignmentwithguidetree.tree.AlignmentTreeNode;
import org.processmining.plugins.tracealignmentwithguidetree.util.FileIO;

import sen.outlierdetector.EventInfo;
import sen.outlierdetector.OutlierDetector;

import com.fluxicon.slickerbox.factory.SlickerFactory;

@SuppressWarnings("serial")
public class AlignmentFrame extends JInternalFrame implements AlignmentListener, DisplayPropertiesListener, ActivityColorListener{
	
	JMenu viewMenu, editMenu, analysisMenu;
	
	// Should alignment be enabled for column Filtering
	protected boolean columnFiltering = false;

	// Should alignment be enabled for column Sorting
	protected boolean columnSorting = true;

	// View Menu Check Box Menu Items
	JCheckBoxMenuItem columnFilteringMenuItem, columnSortingMenuItem;

	// Whether gaps are to be rendered or not
	JCheckBoxMenuItem renderGapsMenuItem;

	// Whether encoded activity is to be rendered or not
	JCheckBoxMenuItem renderEncodedActivityMenuItem;

	// Whether activity background is square or not
	JCheckBoxMenuItem squareMenuItem;

	// Whether activity background is dot or not
	JCheckBoxMenuItem dotMenuItem;

	JCheckBoxMenuItem concurrentActivityRefinementMenuItem;
	JCheckBoxMenuItem blockShiftRefinementMenuItem;
	
	JPanel overallPanel;
	JTabbedPane clusterAlignmentTabbedPane;
	JPanel clusterAlignmentTabbedPanePanel;
	JPanel noClustersPanel;
	ScrollableGridLayout noClustersPanelLayout;
	
	JCheckBoxMenuItem displayConcurrentActivityCheckBoxMenuItem;
	JCheckBoxMenuItem displayConcurrentActivityAcrossAllTracesCheckBoxMenuItem;
	
	List<JPanel> concurrentActivityPanelList = new ArrayList<JPanel>();
	List<JPanel> concurrentActivityAcrossAllTracesPanelList = new ArrayList<JPanel>();
	
	String selectedConcurrentActivity;
	
	ActivityCharEncodingTable activityCharEncodingTable;
	JTable activityCharMapTable;
	Map<String, Color> encodedActivityColorMap;
	
	int encodingLength;
	int noClusters;
	
	String dash;	
	AlignmentTree alignmentTree;
	
	// Font size for displaying the sequences; Default is set to 12 
	private final int fontSize = 16;
	
	int noColumnSorts;
	public static final int MaxPriority = 20;
	Vector<ColumnSort> columnSorts;
	
	List<AlignmentTreeNode> clusterAlignmentTreeNodeList;
	List<JPanel> clusterAlignmentPanelList = new ArrayList<JPanel>();
	List<Alignment> alignmentList = new ArrayList<Alignment>();
	
	List<AlignmentPanel> alignmentPanelList = new ArrayList<AlignmentPanel>();
	List<AlignmentNamePanel> alignmentNamePanelList = new ArrayList<AlignmentNamePanel>();
	List<ColumnSortComponent> columnSortComponentList = new ArrayList<ColumnSortComponent>();
	List<ColumnFilterComponent> columnFilterComponentList = new ArrayList<ColumnFilterComponent>();
	
	List<ConsensusComponent> consensusComponentList = new ArrayList<ConsensusComponent>();
	List<JPanel> topPanelList = new ArrayList<JPanel>();
	
	List<DisplayProperties> alignmentDisplayPropertiesList = new ArrayList<DisplayProperties>();
	
	List<ScrollableGridLayout> clusterAlignmentPanelLayoutList = new ArrayList<ScrollableGridLayout>();
	
	List<ScrollableViewport> alignmentViewPortList = new ArrayList<ScrollableViewport>();
	List<ScrollableViewport> leftViewPortList = new ArrayList<ScrollableViewport>();
	List<ScrollableViewport> topViewPortList = new ArrayList<ScrollableViewport>();
	List<ScrollableViewport> bottomViewPortList = new ArrayList<ScrollableViewport>();
	
	List<JScrollBar> alignmentHorizontalScrollBarList = new ArrayList<JScrollBar>();
	List<JScrollBar> alignmentVerticalScrollBarList = new ArrayList<JScrollBar>();
	
	XLog log;
	
	ArrayList<EventInfo> outliers = new ArrayList<EventInfo>();
	
	public AlignmentFrame(AlignmentTree tree, Map<String, Color> activityColorMap){
		super("Trace Alignment Viewer");
		this.alignmentTree = tree;
		this.log = alignmentTree.getLog();
		initialize();
		
		prepareActivityCharMapTable2();
		
		Map<String, String> activityCharMap = tree.getActivityCharMap();
		this.encodedActivityColorMap = new HashMap<String, Color>();
		
		Map<String, Color> tempEncodedActivityColorMap = activityCharEncodingTable.getEncodedActivityColorMap();
		
		Set<String> unmappedActititySet = new HashSet<String>();
		for(String activity : activityCharMap.keySet()){
			if(activityColorMap.containsKey(activity)){
				this.encodedActivityColorMap.put(activityCharMap.get(activity), activityColorMap.get(activity));
			}else{
				unmappedActititySet.add(activity);
				this.encodedActivityColorMap.put(activityCharMap.get(activity), tempEncodedActivityColorMap.get(activityCharMap.get(activity)));
			}
		}
		this.encodedActivityColorMap.put(dash, Color.white);
	
		activityCharEncodingTable.prepareTable(encodedActivityColorMap);
		activityCharMapTable = activityCharEncodingTable.getActivityCharMapTable();
		
		if(unmappedActititySet.size() > 0){
			ErrorDialog.showErrorDialog(this, "The following activities do not have a color coding; So using the default color coding for them "+unmappedActititySet);
		}
//		System.out.println("Char Activity Map KeySet: "+tree.getCharActivityMap().keySet());
//		System.out.println("Encoded Actvity Color Map KeySet: "+this.encodedActivityColorMap.keySet());
		
		createMenu();
		
		buildNoClustersPanel();
		buildClusterAlignmentTabbedPanePanel();
		
		overallPanel = new JPanel();
		ScrollableGridLayout overallPanelLayout = new ScrollableGridLayout(overallPanel, 2, 1, 0, 0);
		overallPanel.setLayout(overallPanelLayout);
		
		overallPanelLayout.setColumnFixed(1, true);

		overallPanelLayout.setPosition(clusterAlignmentTabbedPanePanel, 0, 0);
		overallPanel.add(clusterAlignmentTabbedPanePanel);

		JScrollPane noClustersPanelScrollPane = new JScrollPane(noClustersPanel);
		overallPanelLayout.setPosition(noClustersPanelScrollPane, 1, 0);
		overallPanel.add(noClustersPanelScrollPane);
		
		getContentPane().add(overallPanel);
		getContentPane().validate();
		getContentPane().repaint();
		pack();

		this.show();
	}
	
	public AlignmentFrame(AlignmentTree tree){
		super("Trace Alignment Viewer");
		this.alignmentTree = tree;
		
		//Pass the log data for outlier detector
		this.log = alignmentTree.getLog();
		initialize();
		
		prepareActivityCharMapTable();
		
		createMenu();
		
		buildNoClustersPanel();
		buildClusterAlignmentTabbedPanePanel();
		
		overallPanel = new JPanel();
		ScrollableGridLayout overallPanelLayout = new ScrollableGridLayout(overallPanel, 2, 1, 0, 0);
		overallPanel.setLayout(overallPanelLayout);
		
		overallPanelLayout.setColumnFixed(1, true);

		overallPanelLayout.setPosition(clusterAlignmentTabbedPanePanel, 0, 0);
		overallPanel.add(clusterAlignmentTabbedPanePanel);

		JScrollPane noClustersPanelScrollPane = new JScrollPane(noClustersPanel);
		overallPanelLayout.setPosition(noClustersPanelScrollPane, 1, 0);
		overallPanel.add(noClustersPanelScrollPane);
		
		getContentPane().add(overallPanel);
		getContentPane().validate();
		getContentPane().repaint();
		pack();

		this.show();
	}
	
	private void initialize(){
		this.encodingLength = alignmentTree.getEncodingLength();
		dash = "-";
		for(int i = 1; i < encodingLength; i++){
			dash += "-";
		}
		
		noColumnSorts = 0;
		columnSorts = new Vector<ColumnSort>();
		for (int i = 0; i < MaxPriority; i++) {
			columnSorts.add(null);
		}
	}
	
	private void prepareActivityCharMapTable2(){
		activityCharEncodingTable = new ActivityCharEncodingTable(alignmentTree.getCharActivityMap(), dash);
		activityCharEncodingTable.setListener(this);
		activityCharEncodingTable.prepareTable();
		activityCharMapTable = activityCharEncodingTable.getActivityCharMapTable();
	}
	
	private void prepareActivityCharMapTable(){
		activityCharEncodingTable = new ActivityCharEncodingTable(alignmentTree.getCharActivityMap(), dash);
		activityCharEncodingTable.setListener(this);
		activityCharEncodingTable.prepareTable();
		encodedActivityColorMap = activityCharEncodingTable.getEncodedActivityColorMap();
		activityCharMapTable = activityCharEncodingTable.getActivityCharMapTable();
	}
	
	private void createMenu(){
		JMenuBar menuBar = new JMenuBar();
		setJMenuBar(menuBar);

		initializeFileMenu(menuBar);
		initializeEditMenu(menuBar);
		initializeViewMenu(menuBar);
		initializeAnalysisMenu(menuBar);
	}
	
	private void initializeFileMenu(JMenuBar menuBar){
		JMenu fileMenu = new JMenu("File");
		fileMenu.setMnemonic('F');
		menuBar.add(fileMenu);
		
		JMenu exportMenu = new JMenu("Export");
		exportMenu.setMnemonic('E');
		fileMenu.add(exportMenu);

		JMenuItem exportAlignmentMenuItem = new JMenuItem("Export Alignment to png");
		exportAlignmentMenuItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				JFileChooser fileChooser = new JFileChooser("./");
				fileChooser.setDialogType(JFileChooser.SAVE_DIALOG);
				fileChooser.setSelectedFile(new File("Alignment"));
				if (fileChooser.showDialog(AlignmentFrame.this, "Export Alignment As PNG") == JFileChooser.APPROVE_OPTION) {
					try {
						String outputDir = fileChooser.getSelectedFile().getParent();
						FileIO io = new FileIO();
						io.writeToFile(outputDir, "EncodedActivityColorMap.txt", encodedActivityColorMap, "\\^");
						String fileName;
						File file;
						Image alignmentNamePanelImage, alignmentPanelImage, consensusComponentImage, headerComponentImage;
						BufferedImage appendedImage;
						Graphics2D g2d;
						Dimension dim;
						int alignmentNamePanelWidth, alignmentPanelWidth, alignmentNamePanelHeight, consensusComponentHeight, headerComponentHeight;
						boolean overwrite = false;
						for (int i = 0; i < noClusters; i++) {
							fileName = fileChooser.getSelectedFile().getAbsolutePath() + i + ".png";
							if (!overwrite && new File(fileName).exists()) {
								if (JOptionPane.showConfirmDialog(AlignmentFrame.this,
										"Warning: this file exists. Overwrite it?", "Save file " + fileName,
										JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE) == JOptionPane.NO_OPTION) {
									return;
								} else {
									overwrite = true;
								}
							}
							file = new File(fileName);

							if (alignmentList.get(i).isColumnSorting()) {
								dim = columnSortComponentList.get(i).getPreferredSize();
								headerComponentHeight = dim.height;
								headerComponentImage = columnSortComponentList.get(i).createImage(dim.width, dim.height);
								columnSortComponentList.get(i).paint(headerComponentImage.getGraphics());
							} else {
								dim = columnFilterComponentList.get(i).getPreferredSize();
								headerComponentHeight = dim.height;
								headerComponentImage = columnFilterComponentList.get(i).createImage(dim.width, dim.height);
								columnFilterComponentList.get(i).paint(headerComponentImage.getGraphics());
							}

							dim = alignmentNamePanelList.get(i).getPreferredSize();
							alignmentNamePanelWidth = dim.width;
							alignmentNamePanelHeight = dim.height;
							alignmentNamePanelImage = alignmentNamePanelList.get(i).createImage(dim.width, dim.height);
							alignmentNamePanelList.get(i).paint(alignmentNamePanelImage.getGraphics());

							dim = alignmentPanelList.get(i).getPreferredSize();
							alignmentPanelWidth = dim.width;
							alignmentPanelImage = alignmentPanelList.get(i).createImage(dim.width, dim.height);
							alignmentPanelList.get(i).paint(alignmentPanelImage.getGraphics());

							dim = consensusComponentList.get(i).getPreferredSize();
							consensusComponentHeight = dim.height;
							consensusComponentImage = consensusComponentList.get(i).createImage(dim.width, dim.height);
							consensusComponentImage.getGraphics().setClip(0, 0, dim.width, dim.height);
							consensusComponentList.get(i).paint(consensusComponentImage.getGraphics());

							appendedImage = new BufferedImage(alignmentNamePanelWidth + alignmentPanelWidth,
									headerComponentHeight + alignmentNamePanelHeight + consensusComponentHeight,
									BufferedImage.TYPE_3BYTE_BGR);
							g2d = appendedImage.createGraphics();
							g2d.drawImage(headerComponentImage, alignmentNamePanelWidth, 0, AlignmentFrame.this);
							g2d.drawImage(alignmentNamePanelImage, 0, headerComponentHeight, AlignmentFrame.this);
							g2d.drawImage(alignmentPanelImage, alignmentNamePanelWidth, headerComponentHeight,
									AlignmentFrame.this);
							g2d.drawImage(consensusComponentImage, alignmentNamePanelWidth, headerComponentHeight
									+ alignmentNamePanelHeight, AlignmentFrame.this);
							g2d.setColor(Color.white);
							g2d.fillRect(0, 0, alignmentNamePanelWidth, headerComponentHeight);
							g2d.fillRect(0, headerComponentHeight + alignmentNamePanelHeight, alignmentNamePanelWidth,
									consensusComponentHeight);

							ImageIO.write(appendedImage, "png", file);
						}
					} catch (IOException e) {
						JOptionPane.showMessageDialog(AlignmentFrame.this, "Export Error", e.getMessage(), JOptionPane.ERROR_MESSAGE);
					}
				}
			}
		});
		exportMenu.add(exportAlignmentMenuItem);
	}
	
	private void initializeEditMenu(JMenuBar menuBar){
		editMenu = new JMenu("Edit");
		editMenu.setMnemonic('E');
		menuBar.add(editMenu);

		JMenu deleteMenu = new JMenu("Delete");
		JMenuItem deleteAllGapColumnsMenuItem = new JMenuItem("All Gap Columns");
		deleteAllGapColumnsMenuItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				int clusterNo = clusterAlignmentTabbedPane.getSelectedIndex();
				alignmentList.get(clusterNo).deselectAllColumns(alignmentDisplayPropertiesList.get(clusterNo));
				alignmentList.get(clusterNo).removeAllGapColumns();
			}
		});
		deleteMenu.add(deleteAllGapColumnsMenuItem);
		
		editMenu.add(deleteMenu);

	}
	
	private void initializeViewMenu(JMenuBar menuBar){
		viewMenu = new JMenu("View");
		viewMenu.setMnemonic('V');

		menuBar.add(viewMenu);

		JMenu colorSchemeMenu = new JMenu("Color Scheme");
		JMenuItem colorSchemeMenuItem1 = new JMenuItem("Color Scheme 1");
		colorSchemeMenuItem1.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				JOptionPane.showMessageDialog(AlignmentFrame.this, "Color Scheme Functionality Yet to be Implemented", "Change Color Scheme Message", JOptionPane.INFORMATION_MESSAGE);
			}
		});
		colorSchemeMenu.add(colorSchemeMenuItem1);

		viewMenu.add(colorSchemeMenu);
		viewMenu.addSeparator();

		columnFilteringMenuItem = new JCheckBoxMenuItem("Filter by Column", columnFiltering);
		columnFilteringMenuItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				setColumnFiltering(columnFilteringMenuItem.isSelected(), true);
//				JOptionPane.showMessageDialog(AlignmentFrame.this, "Column Filtering Functionality Yet to be Implemented", "Column Filtering Message", JOptionPane.INFORMATION_MESSAGE);
			}
		});
		viewMenu.add(columnFilteringMenuItem);

		columnSortingMenuItem = new JCheckBoxMenuItem("Sort by Column", columnSorting);
		columnSortingMenuItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				setColumnSorting(columnSortingMenuItem.isSelected(), true);
//				JOptionPane.showMessageDialog(AlignmentFrame.this, "Column Sorting Functionality Yet to be Implemented", "Column Sorting Message", JOptionPane.INFORMATION_MESSAGE);
			}
		});
		viewMenu.add(columnSortingMenuItem);

		viewMenu.addSeparator();

		renderGapsMenuItem = new JCheckBoxMenuItem("Render Gaps", true);
		renderGapsMenuItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				int clusterNo = clusterAlignmentTabbedPane.getSelectedIndex();
				alignmentDisplayPropertiesList.get(clusterNo).setRenderGaps(renderGapsMenuItem.getState());
			}
		});
		viewMenu.add(renderGapsMenuItem);

		viewMenu.addSeparator();
		renderEncodedActivityMenuItem = new JCheckBoxMenuItem("Render Encoded Activity", true);
		renderEncodedActivityMenuItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				int clusterNo = clusterAlignmentTabbedPane.getSelectedIndex();
				alignmentDisplayPropertiesList.get(clusterNo).setRenderEncodedActivity(renderEncodedActivityMenuItem.getState());
			}
		});
		viewMenu.add(renderEncodedActivityMenuItem);

		viewMenu.addSeparator();
		JMenu backgroundMenu = new JMenu("Activity Background");
		squareMenuItem = new JCheckBoxMenuItem("Square");
		dotMenuItem = new JCheckBoxMenuItem("Dot");
		squareMenuItem.setSelected(true);
		dotMenuItem.setSelected(false);

		viewMenu.add(backgroundMenu);
		backgroundMenu.add(squareMenuItem);
		backgroundMenu.add(dotMenuItem);

		squareMenuItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				int clusterNo = clusterAlignmentTabbedPane.getSelectedIndex();
				alignmentDisplayPropertiesList.get(clusterNo).setActivityBackgroundSquare(squareMenuItem.isSelected());
				dotMenuItem.setSelected(false);
			}
		});

		dotMenuItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				int clusterNo = clusterAlignmentTabbedPane.getSelectedIndex();
				alignmentDisplayPropertiesList.get(clusterNo).setActivityBackgroundSquare(!dotMenuItem.isSelected());
				squareMenuItem.setSelected(false);
			}
		});
	}

	private void initializeAnalysisMenu(JMenuBar menuBar){
		analysisMenu = new JMenu("Analysis");
		analysisMenu.setMnemonic('A');
		menuBar.add(analysisMenu);
		
		JMenu refineAlignmentMenu = new JMenu("Refine Alignment");
		
		blockShiftRefinementMenuItem = new JCheckBoxMenuItem("Block Shift");
		blockShiftRefinementMenuItem.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				if(blockShiftRefinementMenuItem.isSelected()){
					int clusterNo = clusterAlignmentTabbedPane.getSelectedIndex();
					
					if(alignmentDisplayPropertiesList.get(clusterNo).isAlignmentBlockShifted()){
						JOptionPane.showMessageDialog(AlignmentFrame.this, "Alignment already refined", "Refine Alignment Message", JOptionPane.INFORMATION_MESSAGE);
					}else{
						RefineAlignment refineAlignment = new RefineAlignment(encodingLength);
						String[] refinedAlignment = refineAlignment.performBlockShiftLeft(alignmentList.get(clusterNo).getAlignedTraces());
						if(refineAlignment.isValidRefinement()){
							clusterAlignmentTreeNodeList.get(clusterNo).setRefinedAlignment(encodingLength, refinedAlignment);
							clusterAlignmentTabbedPane.setComponentAt(clusterNo, prepareAlignment(clusterNo, refinedAlignment));
							alignmentDisplayPropertiesList.get(clusterNo).setIsAlignmentBlockShifted(true);
							revalidateAndRepaintAll();
						}else{
							System.out.println("Not a valid refinement");
						}
					}
				}else{
					JOptionPane.showMessageDialog(AlignmentFrame.this, "Use reset alignment functionality to undo", "Refine Alignment Message", JOptionPane.INFORMATION_MESSAGE);
				}
			}
		});
		
		refineAlignmentMenu.add(blockShiftRefinementMenuItem);
		
		concurrentActivityRefinementMenuItem = new JCheckBoxMenuItem("Process Concurrent Activities");
		concurrentActivityRefinementMenuItem.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				if(concurrentActivityRefinementMenuItem.isSelected()){
					int clusterNo = clusterAlignmentTabbedPane.getSelectedIndex();
					
					if(alignmentDisplayPropertiesList.get(clusterNo).isAlignmentConcurrentRefined()){
						JOptionPane.showMessageDialog(AlignmentFrame.this, "Alignment already refined", "Refine Alignment Message", JOptionPane.INFORMATION_MESSAGE);
					}else{
						RefineAlignment refineAlignment = new RefineAlignment(encodingLength);
						String[] refinedAlignment = refineAlignment.refineConcurrent(alignmentList.get(clusterNo).getAlignedTraces(), false);
						if(refineAlignment.isValidRefinement()){
							clusterAlignmentTreeNodeList.get(clusterNo).setRefinedAlignment(encodingLength, refinedAlignment);
							clusterAlignmentTabbedPane.setComponentAt(clusterNo, prepareAlignment(clusterNo, refinedAlignment));
							alignmentDisplayPropertiesList.get(clusterNo).setIsAlignmentConcurrentRefined(true);
							revalidateAndRepaintAll();
						}else{
							System.out.println("Not a valid refinement");
						}
					}
				}else{
					JOptionPane.showMessageDialog(AlignmentFrame.this, "Use reset alignment functionality to undo", "Refine Alignment Message", JOptionPane.INFORMATION_MESSAGE);
				}
			}
		});
		
		refineAlignmentMenu.add(concurrentActivityRefinementMenuItem);
		
		analysisMenu.add(refineAlignmentMenu);
		
		JMenu resetAlignmentMenu = new JMenu("Reset Alignment");
		
		JMenuItem resetAlignmentCurrentClusterMenuItem = new JMenuItem("Current Cluster");
		resetAlignmentCurrentClusterMenuItem.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				int clusterNo = clusterAlignmentTabbedPane.getSelectedIndex();
				clusterAlignmentTreeNodeList.get(clusterNo).reset();
				clusterAlignmentTabbedPane.setComponentAt(clusterNo, prepareAlignment(clusterNo, clusterAlignmentTreeNodeList.get(clusterNo).getOriginalAlignment()));
				alignmentDisplayPropertiesList.get(clusterNo).setIsAlignmentBlockShifted(false);
				alignmentDisplayPropertiesList.get(clusterNo).setIsAlignmentConcurrentRefined(false);
				concurrentActivityRefinementMenuItem.setSelected(false);
				blockShiftRefinementMenuItem.setSelected(false);
				revalidateAndRepaintAll();
			}
		});
		
		JMenuItem resetAlignmentAllClustersMenuItem = new JMenuItem("All Clusters");
		resetAlignmentAllClustersMenuItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				concurrentActivityRefinementMenuItem.setSelected(false);
				blockShiftRefinementMenuItem.setSelected(false);
				prepareAlignment();
			}
		});
		
		resetAlignmentMenu.add(resetAlignmentCurrentClusterMenuItem);
		resetAlignmentMenu.add(resetAlignmentAllClustersMenuItem);
		
		analysisMenu.addSeparator();
		analysisMenu.add(resetAlignmentMenu);
		
		displayConcurrentActivityCheckBoxMenuItem = new JCheckBoxMenuItem("Display Concurrent Activities", false);
		displayConcurrentActivityAcrossAllTracesCheckBoxMenuItem = new JCheckBoxMenuItem("Display Concurrent Activities across All Traces", false);

		displayConcurrentActivityCheckBoxMenuItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				int clusterNo = clusterAlignmentTabbedPane.getSelectedIndex();
				if(displayConcurrentActivityCheckBoxMenuItem.isSelected()){
					prepareConcurrentActivityPanel(clusterNo);
					alignmentDisplayPropertiesList.get(clusterNo).setIsDisplayConcurrentActivities(true);
					noClustersPanel.remove(3);
					noClustersPanelLayout.setPosition(concurrentActivityPanelList.get(clusterNo), 0, 3);
					noClustersPanel.add(concurrentActivityPanelList.get(clusterNo));
//					noClustersPanel.add(concurrentActivityPanelList.get(clusterNo),3);
					noClustersPanel.getComponent(3).setVisible(true);
				}
				else{
					alignmentDisplayPropertiesList.get(clusterNo).setIsDisplayConcurrentActivities(false);
					noClustersPanel.getComponent(3).setVisible(false);
				}
				noClustersPanel.revalidate();
				noClustersPanel.repaint();
			}
		});

		displayConcurrentActivityAcrossAllTracesCheckBoxMenuItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				int clusterNo = clusterAlignmentTabbedPane.getSelectedIndex();
				if(displayConcurrentActivityAcrossAllTracesCheckBoxMenuItem.isSelected()){
					prepareConcurrentActivityAcrossAllTracesPanel(clusterNo);
					alignmentDisplayPropertiesList.get(clusterNo).setIsDisplayConcurrentActivitiesAcrossAllTraces(true);
					noClustersPanel.remove(4);
					noClustersPanelLayout.setPosition(concurrentActivityAcrossAllTracesPanelList.get(clusterNo), 0, 4);
					noClustersPanel.add(concurrentActivityAcrossAllTracesPanelList.get(clusterNo));

//					noClustersPanel.add(concurrentActivityPanelList.get(clusterNo),4);
					noClustersPanel.getComponent(4).setVisible(true);
				}else{
					alignmentDisplayPropertiesList.get(clusterNo).setIsDisplayConcurrentActivitiesAcrossAllTraces(false);
					noClustersPanel.getComponent(4).setVisible(false);
				}
				noClustersPanel.revalidate();
				noClustersPanel.repaint();
			}
		});
		
		analysisMenu.addSeparator();
		analysisMenu.add(displayConcurrentActivityCheckBoxMenuItem);
		analysisMenu.addSeparator();
		analysisMenu.add(displayConcurrentActivityAcrossAllTracesCheckBoxMenuItem);
	}

	private void buildNoClustersPanel(){
		noClustersPanel = SlickerFactory.instance().createRoundedPanel();
		noClustersPanel.setBackground(this.getBackground());
		
		noClustersPanelLayout = new ScrollableGridLayout(noClustersPanel, 1, 6, 0, 0);
		noClustersPanelLayout.setRowFixed(0, true);
		noClustersPanelLayout.setRowFixed(1, true);
		noClustersPanelLayout.setRowFixed(2, true);
		noClustersPanelLayout.setRowFixed(3, true);
		noClustersPanelLayout.setRowFixed(4, true);
		noClustersPanelLayout.setRowFixed(5, true);

		noClustersPanelLayout.setColumnFixed(0, true);
		noClustersPanel.setLayout(noClustersPanelLayout);
		
		final JSlider noClustersSlider = SlickerFactory.instance().createSlider(JSlider.VERTICAL);
		
		noClustersSlider.setMinimum(1);
		noClustersSlider.setMaximum(alignmentTree.getNoUniqueTraces());
		System.out.println("No. Unique Traces: "+alignmentTree.getNoUniqueTraces());
		noClustersSlider.setValue(4);
		noClusters = 4;

		final JTextField noClustersTextField = new JTextField(10);
		noClustersTextField.setText("4");

		
		final JLabel noClustersLabel = SlickerFactory.instance().createLabel("       No. Clusters: "+noClusters);
		
		
		noClustersSlider.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent arg0) {
				if(!noClustersSlider.getValueIsAdjusting()){
					noClusters = noClustersSlider.getValue();
					noClustersTextField.setText(noClusters+"");
					noClustersLabel.setText("       No. Clusters: "+noClusters);
				}
			}
		});

		
		noClustersPanelLayout.setPosition(noClustersSlider, 0, 0);
		noClustersPanel.add(noClustersSlider);
		
		noClustersPanelLayout.setPosition(noClustersTextField, 0, 1);
		noClustersPanel.add(noClustersTextField);
		
		noClustersPanelLayout.setPosition(noClustersLabel, 0, 2);
		noClustersPanel.add(noClustersLabel);
		
		JButton displayAlignmentButton = SlickerFactory.instance().createButton("Display Alignment");
		displayAlignmentButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				noClusters = new Integer(noClustersTextField.getText()).intValue();
				noClustersLabel.setText("       No. Clusters: "+noClusters);
				noClustersSlider.setValue(noClusters);
				if (clusterAlignmentTabbedPane != null) {
					ChangeListener[] changeListeners = clusterAlignmentTabbedPane.getChangeListeners();
					for (ChangeListener c : changeListeners) {
						clusterAlignmentTabbedPane.removeChangeListener(c);
					}
					clusterAlignmentTabbedPane.removeAll();
				}
				
				
				clusterAlignmentTreeNodeList = alignmentTree.getClusterNodes(noClusters);
				System.out.println("No. Cluster Nodes: "+clusterAlignmentTreeNodeList.size());
				for(AlignmentTreeNode node : clusterAlignmentTreeNodeList){
					if(node.getNoChildren() > 0)
						System.out.println(node.getEncodedTrace());
					else{
						int traceIndex = alignmentTree.getEncodedTraceIdenticalIndicesMap().get(node.getEncodedTrace()).first();
		        		System.out.println(alignmentTree.getLog().get(traceIndex).getAttributes().get("concept:name").toString());
					}
				}
				
				prepareAlignment();
//				setAlignment();
			}
		});
		
		noClustersPanelLayout.setPosition(displayAlignmentButton, 0, 3);
		noClustersPanel.add(displayAlignmentButton);
		
		
		//Outlier Detector button added, caution: Panel parameters in all this file related must be changed!
		JButton OutlierDetectorButton = SlickerFactory.instance().createButton("Outlier Detector");
		OutlierDetectorButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				OutlierDetector outlier = new OutlierDetector(log);
				outliers = outlier.getOutlier();
			}
		});
		
		
		noClustersPanelLayout.setPosition(OutlierDetectorButton, 0, 4);
		noClustersPanel.add(OutlierDetectorButton);
	}
	
	private void prepareAlignment(){
		resetMenuSelections();
		
		ChangeListener[] changeListeners = clusterAlignmentTabbedPane.getChangeListeners();
		for (ChangeListener c : changeListeners) {
			clusterAlignmentTabbedPane.removeChangeListener(c);
		}
		
		clusterAlignmentTabbedPane.removeAll();
		
		concurrentActivityPanelList.clear();
		concurrentActivityAcrossAllTracesPanelList.clear();
		
		System.out.println("No.Components: "+noClustersPanel.getComponentCount());
		if(noClustersPanel.getComponentCount() > 6){
			noClustersPanel.remove(5);
			System.out.println("No.Components: "+noClustersPanel.getComponentCount());
			noClustersPanel.remove(5);
		}else if(noClustersPanel.getComponentCount() > 5){
			noClustersPanel.remove(5);
		}
		alignmentList.clear();
		alignmentDisplayPropertiesList.clear();
		
		alignmentPanelList.clear();
		alignmentNamePanelList.clear();
		
		columnSortComponentList.clear();
		columnFilterComponentList.clear();
		consensusComponentList.clear();
		
		alignmentViewPortList.clear();
		leftViewPortList.clear();
		topViewPortList.clear();
		bottomViewPortList.clear();
		
		Map<String, TreeSet<Integer>> encodedTraceIdenticalIndicesMap = alignmentTree.getEncodedTraceIdenticalIndicesMap();
		int noTraces = 0;
		for(int i = 0; i < noClusters; i++){
			prepareConcurrentActivityPanel(i);
			prepareConcurrentActivityAcrossAllTracesPanel(i);
			String[] alignment = clusterAlignmentTreeNodeList.get(i).getOriginalAlignment();
			noTraces = 0;
			for(String alignedSeq : alignment){
				String encodedTrace = alignedSeq.replaceAll(dash, "");
				if(encodedTraceIdenticalIndicesMap.containsKey(encodedTrace))
					noTraces += encodedTraceIdenticalIndicesMap.get(encodedTrace).size();
			}
			clusterAlignmentTabbedPane.addTab("Cluster "+(i+1)+" ("+clusterAlignmentTreeNodeList.get(i).getOriginalAlignment().length+"/"+noTraces+")",prepareAlignment(i, clusterAlignmentTreeNodeList.get(i).getOriginalAlignment()));
		}
		
		noClustersPanelLayout.setPosition(concurrentActivityPanelList.get(0), 0, 5);
		noClustersPanel.add(concurrentActivityPanelList.get(0));
		noClustersPanel.getComponent(5).setVisible(false);
		
		noClustersPanelLayout.setPosition(concurrentActivityAcrossAllTracesPanelList.get(0), 0, 6);
		noClustersPanel.add(concurrentActivityAcrossAllTracesPanelList.get(0));
		noClustersPanel.getComponent(6).setVisible(false);
		noClustersPanel.revalidate();
		noClustersPanel.repaint();
		
		JScrollPane scrollPane = new JScrollPane(activityCharMapTable);
		clusterAlignmentTabbedPane.addTab("Activity Char Encoding", new ImageIcon(), scrollPane,
				"Activity-Char Encoding");
		
		clusterAlignmentTabbedPane.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent arg0) {
				if (clusterAlignmentTabbedPane.getComponents().length > 0) {
					int clusterNo = clusterAlignmentTabbedPane.getSelectedIndex();
					if (alignmentList.size() > clusterNo) {
						editMenu.setEnabled(true);
						viewMenu.setEnabled(true);
						
						if (alignmentDisplayPropertiesList.get(clusterNo).isGapRendered()) {
							renderGapsMenuItem.setSelected(true);
						} else {
							renderGapsMenuItem.setSelected(false);
						}

						if (alignmentDisplayPropertiesList.get(clusterNo).isEncodedActivityRendered()) {
							renderEncodedActivityMenuItem.setSelected(true);
						} else {
							renderEncodedActivityMenuItem.setSelected(false);
						}

						if (alignmentDisplayPropertiesList.get(clusterNo).isActivityBackgroundSquare()) {
							squareMenuItem.setSelected(true);
							dotMenuItem.setSelected(false);
						} else {
							squareMenuItem.setSelected(false);
							dotMenuItem.setSelected(true);
						}
						
						if(alignmentDisplayPropertiesList.get(clusterNo).isAlignmentConcurrentRefined()){
							concurrentActivityRefinementMenuItem.setSelected(true);
						}else{
							concurrentActivityRefinementMenuItem.setSelected(false);
						}
						
						if(alignmentDisplayPropertiesList.get(clusterNo).isAlignmentBlockShifted()){
							blockShiftRefinementMenuItem.setSelected(true);
						}else{
							blockShiftRefinementMenuItem.setSelected(false);
						}

						noClustersPanel.remove(5);
						noClustersPanelLayout.setPosition(concurrentActivityPanelList.get(clusterNo), 0, 5);
						noClustersPanel.add(concurrentActivityPanelList.get(clusterNo));
						
						if(alignmentDisplayPropertiesList.get(clusterNo).isDisplayConcurrentActivities()){
							displayConcurrentActivityCheckBoxMenuItem.setSelected(true);
							noClustersPanel.getComponent(5).setVisible(true);
						}else{
							displayConcurrentActivityCheckBoxMenuItem.setSelected(false);
							noClustersPanel.getComponent(5).setVisible(false);
						}
						
						noClustersPanel.remove(6);
						noClustersPanelLayout.setPosition(concurrentActivityAcrossAllTracesPanelList.get(clusterNo), 0, 6);
						noClustersPanel.add(concurrentActivityAcrossAllTracesPanelList.get(clusterNo));
						
						if(alignmentDisplayPropertiesList.get(clusterNo).isDisplayConcurrentActivitiesAcrossAllTraces()){
							displayConcurrentActivityAcrossAllTracesCheckBoxMenuItem.setSelected(true);
							noClustersPanel.getComponent(6).setVisible(true);
						}else{
							displayConcurrentActivityAcrossAllTracesCheckBoxMenuItem.setSelected(false);
							noClustersPanel.getComponent(6).setVisible(false);
						}
						
						noClustersPanel.revalidate();
						noClustersPanel.repaint();
					} else {
						viewMenu.setEnabled(false);
						editMenu.setEnabled(false);
					}
				}
			}
		});
	}

	private void prepareConcurrentActivityPanel(final int clusterNo){
		/*
		 * Get always the refined alignment concurrent activities as this is the
		 * one the resembles what the analyst sees in visualization
		 */
		Set<String> concurrentActivitySet = clusterAlignmentTreeNodeList.get(clusterNo).getConcurrentActivitySet();
		Vector<String> concurrentActivityVector = new Vector<String>(concurrentActivitySet);
		
		System.out.println("Cluster: "+clusterNo);
		System.out.println("Concurrent Activity Set: "+concurrentActivitySet);

		JPanel concurrentActivityPanel = SlickerFactory.instance().createRoundedPanel();
		concurrentActivityPanel.setBorder(BorderFactory.createTitledBorder("Concurrent Activities"));
		ScrollableGridLayout concurrentActivityPanelLayout = new ScrollableGridLayout(concurrentActivityPanel, 1, 2, 0, 0);
		concurrentActivityPanelLayout.setRowFixed(0, true);
		concurrentActivityPanelLayout.setRowFixed(1, true);
		
		concurrentActivityPanel.setLayout(concurrentActivityPanelLayout);
		final JListX concurrentActivityList = new JListX(concurrentActivityVector);
		concurrentActivityList.setBackground(concurrentActivityPanel.getBackground());
		
		JScrollPane concurrentActivityListScrollPane = new JScrollPane(concurrentActivityList);
		
		JPanel buttonPanel = SlickerFactory.instance().createRoundedPanel();
		ScrollableGridLayout buttonPanelLayout = new ScrollableGridLayout(buttonPanel, 2, 1, 0, 0);
		buttonPanelLayout.setRowFixed(0, true);
		buttonPanelLayout.setColumnFixed(0, true);
		buttonPanelLayout.setColumnFixed(1, true);
		buttonPanel.setLayout(buttonPanelLayout);
		
		JButton removeButton = SlickerFactory.instance().createButton("Remove");
		buttonPanelLayout.setPosition(removeButton, 0, 0);
		buttonPanel.add(removeButton);
		
		removeButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				Object[] selectedConcurrentActivities = concurrentActivityList.getSelectedValues();
				RefineAlignment refineAlignment = new RefineAlignment(encodingLength);
				String[] refinedAlignment = clusterAlignmentTreeNodeList.get(clusterNo).getRefinedAlignment();
				for(Object concurrentActivity : selectedConcurrentActivities){
					refinedAlignment = refineAlignment.removeConcurrent(refinedAlignment, concurrentActivity.toString());
					if(refineAlignment.isValidRefinement()){
						System.out.println("Valid Refinement");
					}
				}
			}
		});
		concurrentActivityPanelLayout.setPosition(concurrentActivityListScrollPane, 0, 0);
		concurrentActivityPanel.add(concurrentActivityListScrollPane);
		concurrentActivityPanelLayout.setPosition(buttonPanel, 0, 1);
		concurrentActivityPanel.add(buttonPanel);
		
		if(concurrentActivityPanelList.size() <= clusterNo)
			concurrentActivityPanelList.add(concurrentActivityPanel);
		else
			concurrentActivityPanelList.set(clusterNo, concurrentActivityPanel);
	}
	
	private void prepareConcurrentActivityAcrossAllTracesPanel(final int clusterNo){
		Set<String> concurrentActivityAcrossAllTracesSet = clusterAlignmentTreeNodeList.get(clusterNo).getConcurrentActivityAcrossAllTracesSet();
		Vector<String> concurrentActivityVector = new Vector<String>(concurrentActivityAcrossAllTracesSet);
		
		System.out.println("Cluster: "+clusterNo);
		System.out.println("Concurrent Activity Set: "+concurrentActivityAcrossAllTracesSet);

		JPanel concurrentActivityPanel = SlickerFactory.instance().createRoundedPanel();
		concurrentActivityPanel.setBorder(BorderFactory.createTitledBorder("Across All Traces"));
		ScrollableGridLayout concurrentActivityPanelLayout = new ScrollableGridLayout(concurrentActivityPanel, 1, 2, 0, 0);
		concurrentActivityPanelLayout.setRowFixed(0, true);
		concurrentActivityPanelLayout.setRowFixed(1, true);
		
		concurrentActivityPanel.setLayout(concurrentActivityPanelLayout);
		final JListX concurrentActivityList = new JListX(concurrentActivityVector);
		concurrentActivityList.setBackground(concurrentActivityPanel.getBackground());
		JScrollPane concurrentActivityListScrollPane = new JScrollPane(concurrentActivityList);
		
		JPanel buttonPanel = SlickerFactory.instance().createRoundedPanel();
		ScrollableGridLayout buttonPanelLayout = new ScrollableGridLayout(buttonPanel, 2, 1, 0, 0);
		buttonPanelLayout.setRowFixed(0, true);
		buttonPanelLayout.setColumnFixed(0, true);
		buttonPanelLayout.setColumnFixed(1, true);
		buttonPanel.setLayout(buttonPanelLayout);
		
		JButton removeButton = SlickerFactory.instance().createButton("Remove");
		buttonPanelLayout.setPosition(removeButton, 0, 0);
		buttonPanel.add(removeButton);
		
		removeButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				Object[] selectedConcurrentActivities = concurrentActivityList.getSelectedValues();
				Set<String> selectedConcurrentActivitySet = new HashSet<String>();
				for(Object selectedConcurrentActivity : selectedConcurrentActivities)
					selectedConcurrentActivitySet.add(selectedConcurrentActivity.toString());
				
				RefineAlignment refineAlignment = new RefineAlignment(encodingLength);
				String[] refinedAlignment = clusterAlignmentTreeNodeList.get(clusterNo).getRefinedAlignment().clone();
				Set<String> unProcessedConcurrentActivitySet = new HashSet<String>();
				for(String concurrentActivity : selectedConcurrentActivitySet){
					refinedAlignment = refineAlignment.removeConcurrent(refinedAlignment, concurrentActivity.toString());
					if(!refineAlignment.isValidRefinement()){
						unProcessedConcurrentActivitySet.add(concurrentActivity.toString());
					}
				}
				if(unProcessedConcurrentActivitySet.size() != selectedConcurrentActivities.length){
					Set<String> processableConcurrentActivitySet = new HashSet<String>();
					processableConcurrentActivitySet.addAll(selectedConcurrentActivitySet);
					processableConcurrentActivitySet.removeAll(unProcessedConcurrentActivitySet);
					TreeSet<Integer> selectedColumnIndicesSet = refineAlignment.getColumnIndices(clusterAlignmentTreeNodeList.get(clusterNo).getRefinedAlignment(), processableConcurrentActivitySet);
					System.out.println("Processable Concurrent Activity Set: "+processableConcurrentActivitySet);
					List<Integer> selectedColumnList = new ArrayList<Integer>();
					selectedColumnList.addAll(selectedColumnIndicesSet);
					
					System.out.println("Selected Column Indices List: "+selectedColumnList);
					int neighbors, column, left, right;
					List<Integer> rangeCols = new ArrayList<Integer>();
					for (int i = 0; i < selectedColumnList.size(); i++) {
						neighbors = 0;
						column = ((Integer) selectedColumnList.get(i)).intValue();
						left = i == 0 ? column : ((Integer) selectedColumnList.get(i - 1)).intValue();
						right = i == selectedColumnList.size() - 1 ? column : ((Integer) selectedColumnList.get(i + 1))
								.intValue();
						if (column - left == 1) {
							neighbors++;
						}
						if (right - column == 1) {
							neighbors++;
						}
						if (neighbors == 0) {
							rangeCols.add(new Integer(column));
							rangeCols.add(new Integer(column));
						}
						if (neighbors == 1) {
							rangeCols.add(new Integer(column));
						}
					}
					int start, end;
					System.out.println("Range Cols: "+rangeCols);
					Alignment alignment = alignmentDisplayPropertiesList.get(clusterNo).getAlignment();
					Sequence sequence;
					for (int row = 0; row < alignment.getNoSequences(); row++) {
						sequence = alignment.getSequence(row);
						for (int i = rangeCols.size() - 1; i >= 1; i -= 2) {
							start = ((Integer) rangeCols.get(i)).intValue();
							end = ((Integer) rangeCols.get(i - 1)).intValue();
							System.out.println(start+" @ "+end);
							try {
								sequence.shiftActivity(start + 1, end - start - 1, true);
							} catch (Exception exp) {
								exp.printStackTrace();
								JOptionPane.showMessageDialog(AlignmentFrame.this, "Unable to delete " + "column "+(start+1)+" "+end+" in sequence "
										+ sequence.getName(), exp.toString(), JOptionPane.ERROR_MESSAGE);
							}
						}
//						alignment.deselectAllColumns(alignmentDisplayPropertiesList.get(clusterNo));
					}

					
					//Some activities have been processed; So adjust the o/p
					System.out.println("Resetting");
					System.out.println("Refined Alignment Length: "+refinedAlignment[0].length()/encodingLength);
					System.out.println("Original Alignment Length: "+clusterAlignmentTreeNodeList.get(clusterNo).getRefinedAlignment()[0].length()/encodingLength);
					clusterAlignmentTreeNodeList.get(clusterNo).setRefinedAlignment(encodingLength, refinedAlignment);
//					clusterAlignmentTabbedPane.setComponentAt(clusterNo, prepareAlignment(clusterNo, refinedAlignment));
					prepareConcurrentActivityPanel(clusterNo);
					prepareConcurrentActivityAcrossAllTracesPanel(clusterNo);
					concurrentActivityList.removeAll();
					concurrentActivityList.setListData(unProcessedConcurrentActivitySet.toArray());
					noClustersPanel.revalidate();
					noClustersPanel.repaint();
				}
			}
		});
		
		concurrentActivityPanelLayout.setPosition(concurrentActivityListScrollPane, 0, 0);
		concurrentActivityPanel.add(concurrentActivityListScrollPane);
		concurrentActivityPanelLayout.setPosition(buttonPanel, 0, 1);
		concurrentActivityPanel.add(buttonPanel);
		
		if(concurrentActivityAcrossAllTracesPanelList.size() <= clusterNo)
			concurrentActivityAcrossAllTracesPanelList.add(concurrentActivityPanel);
		else
			concurrentActivityAcrossAllTracesPanelList.set(clusterNo, concurrentActivityPanel);
	}

	private void resetMenuSelections(){
		editMenu.setEnabled(true);
		viewMenu.setEnabled(true);
		renderGapsMenuItem.setSelected(true);
		renderEncodedActivityMenuItem.setSelected(true);
		squareMenuItem.setSelected(true);
		dotMenuItem.setSelected(false);
		concurrentActivityRefinementMenuItem.setSelected(false);
		blockShiftRefinementMenuItem.setSelected(false);
	}
	
	@SuppressWarnings("deprecation")
	private JPanel prepareAlignment(int clusterNo, String[] alignedTraces){
		JPanel overallAlignmentPanel = new JPanel();
		overallAlignmentPanel.setBackground(Color.white);
		
		ScrollableGridLayout overallAlignmentPanelLayout = new ScrollableGridLayout(overallAlignmentPanel, 3, 4, 0, 0); 
		overallAlignmentPanelLayout.setRowFixed(0, true);
		overallAlignmentPanelLayout.setRowFixed(2, true);
		overallAlignmentPanelLayout.setRowFixed(3, true);

		overallAlignmentPanelLayout.setColumnFixed(0, true);
		overallAlignmentPanelLayout.setColumnFixed(2, true);

		overallAlignmentPanel.setLayout(overallAlignmentPanelLayout);

		ScrollableViewport alignmentViewPort = new ScrollableViewport();
		alignmentViewPort.setBackingStoreEnabled(true);
		alignmentViewPort.putClientProperty("EnableWindowBlit", null);
		alignmentViewPort.setBackground(Color.white);
		alignmentViewPort.add(new JLabel("Alignment Viewport"));
		overallAlignmentPanelLayout.setPosition(alignmentViewPort, 1, 1);
		overallAlignmentPanel.add(alignmentViewPort);
		
		ScrollableViewport leftViewPort = new ScrollableViewport();
		leftViewPort.setBackingStoreEnabled(true);
		leftViewPort.putClientProperty("EnableWindowBlit", null);
		leftViewPort.setBackground(Color.white);
		leftViewPort.add(new JLabel("Left ViewPort"));
		overallAlignmentPanelLayout.setPosition(leftViewPort, 0, 1);
		overallAlignmentPanel.add(leftViewPort);
		
		ScrollableViewport topViewPort = new ScrollableViewport();
		topViewPort.setBackingStoreEnabled(true);
		topViewPort.putClientProperty("EnableWindowBlit", null);
		topViewPort.setBackground(Color.white);
		topViewPort.add(new JLabel("Top ViewPort"));
		overallAlignmentPanelLayout.setPosition(topViewPort, 1, 0);
		overallAlignmentPanel.add(topViewPort);
		
		ScrollableViewport bottomViewPort = new ScrollableViewport();
		bottomViewPort.setBackingStoreEnabled(true);
		bottomViewPort.putClientProperty("EnableWindowBlit", null);
		bottomViewPort.setBackground(Color.white);
		bottomViewPort.add(new JLabel("Bottom ViewPort"));
		overallAlignmentPanelLayout.setPosition(bottomViewPort, 1, 3);
		overallAlignmentPanel.add(bottomViewPort);
		
		JScrollBar alignmentHorizontalScrollBar = new JScrollBar(JScrollBar.HORIZONTAL);
		LazyBoundedRangeModel hsbModel = new LazyBoundedRangeModel(false);
		alignmentHorizontalScrollBar.setModel(hsbModel);
		overallAlignmentPanelLayout.setPosition(alignmentHorizontalScrollBar, 1, 2);
		overallAlignmentPanel.add(alignmentHorizontalScrollBar);

		alignmentViewPort.setHorizontalScrollbar(alignmentHorizontalScrollBar);
		topViewPort.setHorizontalViewport(alignmentViewPort);
		bottomViewPort.setHorizontalViewport(alignmentViewPort);
		
		JScrollBar alignmentVerticalScrollBar = new JScrollBar(JScrollBar.VERTICAL);
		LazyBoundedRangeModel vsbModel = new LazyBoundedRangeModel(false);
		alignmentVerticalScrollBar.setModel(vsbModel);
		overallAlignmentPanelLayout.setPosition(alignmentVerticalScrollBar, 2, 1);
		overallAlignmentPanel.add(alignmentVerticalScrollBar);

		alignmentViewPort.setVerticalScrollbar(alignmentVerticalScrollBar);
		leftViewPort.setVerticalViewport(alignmentViewPort);

		XLog log = alignmentTree.getLog();
		Map<String, TreeSet<Integer>> encodedTraceIdenticalIndicesSetMap = alignmentTree.getEncodedTraceIdenticalIndicesMap();
		TreeSet<Integer> encodedTraceIdenticalIndicesSet;
		
		int alignmentLength = alignedTraces[0].length()/encodingLength;
		String[] seq;
		int noTracesInAlignment = alignedTraces.length;
		Sequence[] sequencesInArray = new Sequence[noTracesInAlignment];
		
		StringBuilder seqWithoutGapsStringBuilder = new StringBuilder();
		List<String> traceNameList = new ArrayList<String>();
		String traceName;
		
		for (int j = 0; j < noTracesInAlignment; j++) {
			seq = new String[alignmentLength];
			seqWithoutGapsStringBuilder.setLength(0);
			for (int k = 0; k < alignmentLength; k++) {
				seq[k] = alignedTraces[j].substring(k * encodingLength, (k + 1) * encodingLength);
				if (!seq[k].equals(dash)) {
					seqWithoutGapsStringBuilder.append(seq[k]);
				}
			}
			// Setting the name of the trace here

			traceNameList.clear();
			if (encodedTraceIdenticalIndicesSetMap.containsKey(seqWithoutGapsStringBuilder.toString())) {
				encodedTraceIdenticalIndicesSet = encodedTraceIdenticalIndicesSetMap.get(seqWithoutGapsStringBuilder.toString());
				for (Integer traceIndex : encodedTraceIdenticalIndicesSet) {
					traceNameList.add(log.get(traceIndex).getAttributes().get("concept:name").toString().trim());
				}
			} else {
				JOptionPane.showMessageDialog(this,
						"Couldn't find encoded trace in the traceIdenticalIndicesMap for trace "+seqWithoutGapsStringBuilder.toString(), "Error Message", JOptionPane.ERROR_MESSAGE);
			}
			if (traceNameList.size() == 0) {
				traceNameList.add("No Name");
			}
			traceName = traceNameList.get(0);
			sequencesInArray[j] = new Sequence(dash, traceName, traceNameList, seq);
			// Add the duplicate trace names etc here
		}
		
		Alignment alignment = new Alignment(encodingLength, this, sequencesInArray);
		alignment.addListener(this);

		Font font = new Font("Sans Serif", Font.PLAIN, fontSize);
		DisplayProperties displayProperties = new DisplayProperties(alignment, font, getFontMetrics(font), true,
				true);
		displayProperties.addListener(this);

		AlignmentPanel alignmentPanel = new AlignmentPanel(this, alignment, displayProperties);
		alignmentViewPort.setView(alignmentPanel);

		AlignmentNamePanel alignmentNamePanel = new AlignmentNamePanel(this, displayProperties);
		JPanel leftPanel = new JPanel();
		leftPanel.setBackground(Color.white);
		
		ScrollableGridLayout leftPanelLayout = new ScrollableGridLayout(leftPanel, 1, 1, 50, 50);
		leftPanelLayout.setRowFixed(0, true);
		leftPanelLayout.setColumnFixed(0, true);

		leftPanel.setLayout(leftPanelLayout);
		leftPanelLayout.setPosition(alignmentNamePanel, 0, 0);
		leftPanel.add(alignmentNamePanel);
		
		leftViewPort.setView(leftPanel);
		
		ConsensusSequence consensusSequence = new ConsensusSequence(this, displayProperties);
		ConsensusComponent consensusComponent = new ConsensusComponent(consensusSequence, displayProperties);
		
		JPanel bottomPanel = new JPanel();
		bottomPanel.setBackground(Color.white);
		bottomPanel.setLayout(new BoxLayout(bottomPanel, BoxLayout.Y_AXIS));
		bottomPanel.add(consensusComponent);
		
		// The top panel contains the ruler and ruler annotations
		JPanel topPanel = new JPanel();
		topPanel.setBackground(Color.white);
		topPanel.setLayout(new BoxLayout(topPanel, BoxLayout.Y_AXIS));

		topPanel.add(new RulerComponent(displayProperties));
		topPanel.add(new RulerAnnotationComponent(displayProperties, this));
		
		ColumnSortComponent columnSortComponent = new ColumnSortComponent(this, displayProperties);
		columnSortComponent.setVisible(columnSorting);
		topPanel.add(columnSortComponent);
		
		ColumnFilterComponent columnFilterComponent = new ColumnFilterComponent(this, displayProperties);
		columnFilterComponent.setVisible(columnFiltering);
		topPanel.add(columnFilterComponent);
		
		topViewPort.setView(topPanel);
		bottomViewPort.setView(bottomPanel);
		
		if(alignmentList.size() > clusterNo)
			alignmentList.set(clusterNo, alignment);
		else
			alignmentList.add(clusterNo, alignment);
		
		if(alignmentDisplayPropertiesList.size() > clusterNo)
			alignmentDisplayPropertiesList.set(clusterNo, displayProperties);
		else
			alignmentDisplayPropertiesList.add(clusterNo, displayProperties);
		
		if(alignmentViewPortList.size() > clusterNo)
			alignmentViewPortList.set(clusterNo, alignmentViewPort);
		else
			alignmentViewPortList.add(clusterNo, alignmentViewPort);
		
		if(bottomViewPortList.size() > clusterNo)
			bottomViewPortList.set(clusterNo, bottomViewPort);
		else
			bottomViewPortList.add(clusterNo, bottomViewPort);
		
		if(topViewPortList.size() > clusterNo)
			topViewPortList.set(clusterNo, topViewPort);
		else
			topViewPortList.add(clusterNo, topViewPort);
		
		if(leftViewPortList.size() > clusterNo)
			leftViewPortList.set(clusterNo, leftViewPort);
		else
			leftViewPortList.add(clusterNo, leftViewPort);
		
		if(alignmentPanelList.size() > clusterNo)
			alignmentPanelList.set(clusterNo, alignmentPanel);
		else
			alignmentPanelList.add(clusterNo, alignmentPanel);
		
		if(alignmentNamePanelList.size() > clusterNo)
			alignmentNamePanelList.set(clusterNo, alignmentNamePanel);
		else
			alignmentNamePanelList.add(clusterNo, alignmentNamePanel);
	
		
		if(consensusComponentList.size() > clusterNo)
			consensusComponentList.set(clusterNo, consensusComponent);
		else
			consensusComponentList.add(clusterNo, consensusComponent);
		
		if(columnSortComponentList.size() > clusterNo)
			columnSortComponentList.set(clusterNo, columnSortComponent);
		else
			columnSortComponentList.add(clusterNo, columnSortComponent);
			
		if(columnFilterComponentList.size() > clusterNo)
			columnFilterComponentList.set(clusterNo, columnFilterComponent);
		else
			columnFilterComponentList.add(clusterNo, columnFilterComponent);
		
		return overallAlignmentPanel;
	}
	
	@SuppressWarnings({ "deprecation", "unused" })
	private void setAlignment(){
		ChangeListener[] changeListeners = clusterAlignmentTabbedPane.getChangeListeners();
		for (ChangeListener c : changeListeners) {
			clusterAlignmentTabbedPane.removeChangeListener(c);
		}
		clusterAlignmentTabbedPane.removeAll();

		clusterAlignmentPanelList.clear();
		alignmentList.clear();
		alignmentPanelList.clear();
		alignmentNamePanelList.clear();
		columnSortComponentList.clear();
		columnFilterComponentList.clear();
		consensusComponentList.clear();
		topPanelList.clear();
		
		alignmentDisplayPropertiesList.clear();
		
		alignmentViewPortList.clear();
		leftViewPortList.clear();
		topViewPortList.clear();
		bottomViewPortList.clear();
		
		clusterAlignmentPanelLayoutList.clear();
		
		LazyBoundedRangeModel vsbModel, hsbModel;
		
		for(int i = 0; i < noClusters; i++){
			JPanel clusterAlignmentPanel = new JPanel();
			clusterAlignmentPanel.setBackground(Color.white);
			
			ScrollableGridLayout clusterAlignmentPanelLayout = new ScrollableGridLayout(clusterAlignmentPanel, 3, 4, 0, 0); 
			clusterAlignmentPanelLayout.setRowFixed(0, true);
			clusterAlignmentPanelLayout.setRowFixed(2, true);
			clusterAlignmentPanelLayout.setRowFixed(3, true);

			clusterAlignmentPanelLayout.setColumnFixed(0, true);
			clusterAlignmentPanelLayout.setColumnFixed(2, true);

			clusterAlignmentPanel.setLayout(clusterAlignmentPanelLayout);
			
			ScrollableViewport alignmentViewPort = new ScrollableViewport();
			alignmentViewPort.setBackingStoreEnabled(true);
			alignmentViewPort.putClientProperty("EnableWindowBlit", null);
			alignmentViewPort.setBackground(Color.white);
			alignmentViewPort.add(new JLabel("Cluster" + i));
			clusterAlignmentPanelLayout.setPosition(alignmentViewPort, 1, 1);
			clusterAlignmentPanel.add(alignmentViewPort);
			
			ScrollableViewport leftViewPort = new ScrollableViewport();
			leftViewPort.setBackingStoreEnabled(true);
			leftViewPort.putClientProperty("EnableWindowBlit", null);
			leftViewPort.setBackground(Color.white);
			leftViewPort.add(new JLabel("Left ViewPort"));
			clusterAlignmentPanelLayout.setPosition(leftViewPort, 0, 1);
			clusterAlignmentPanel.add(leftViewPort);
			
			ScrollableViewport topViewPort = new ScrollableViewport();
			topViewPort.setBackingStoreEnabled(true);
			topViewPort.putClientProperty("EnableWindowBlit", null);
			topViewPort.setBackground(Color.white);
			topViewPort.add(new JLabel("Top ViewPort"));
			clusterAlignmentPanelLayout.setPosition(topViewPort, 1, 0);
			clusterAlignmentPanel.add(topViewPort);
			
			ScrollableViewport bottomViewPort = new ScrollableViewport();
			bottomViewPort.setBackingStoreEnabled(true);
			bottomViewPort.putClientProperty("EnableWindowBlit", null);
			bottomViewPort.setBackground(Color.white);
			bottomViewPort.add(new JLabel("Bottom ViewPort"));
			clusterAlignmentPanelLayout.setPosition(bottomViewPort, 1, 3);
			clusterAlignmentPanel.add(bottomViewPort);
			
			JScrollBar alignmentHorizontalScrollBar = new JScrollBar(JScrollBar.HORIZONTAL);
			hsbModel = new LazyBoundedRangeModel(false);
			alignmentHorizontalScrollBar.setModel(hsbModel);
			clusterAlignmentPanelLayout.setPosition(alignmentHorizontalScrollBar, 1, 2);
			clusterAlignmentPanel.add(alignmentHorizontalScrollBar);

			alignmentViewPort.setHorizontalScrollbar(alignmentHorizontalScrollBar);
			topViewPort.setHorizontalViewport(alignmentViewPort);
			bottomViewPort.setHorizontalViewport(alignmentViewPort);
			
			JScrollBar alignmentVerticalScrollBar = new JScrollBar(JScrollBar.VERTICAL);
			vsbModel = new LazyBoundedRangeModel(false);
			alignmentVerticalScrollBar.setModel(vsbModel);
			clusterAlignmentPanelLayout.setPosition(alignmentVerticalScrollBar, 2, 1);
			clusterAlignmentPanel.add(alignmentVerticalScrollBar);

			alignmentViewPort.setVerticalScrollbar(alignmentVerticalScrollBar);
			leftViewPort.setVerticalViewport(alignmentViewPort);
			
			clusterAlignmentPanelList.add(clusterAlignmentPanel);
			clusterAlignmentPanelLayoutList.add(clusterAlignmentPanelLayout);
			
			alignmentViewPortList.add(alignmentViewPort);
			leftViewPortList.add(leftViewPort);
			topViewPortList.add(topViewPort);
			bottomViewPortList.add(bottomViewPort);
			
			alignmentHorizontalScrollBarList.add(alignmentHorizontalScrollBar);
			alignmentVerticalScrollBarList.add(alignmentVerticalScrollBar);
			
			clusterAlignmentTabbedPane.addTab("Cluster " + (i + 1) + " (" + clusterAlignmentTreeNodeList.get(i).getOriginalAlignment().length + ")",
					new ImageIcon(), clusterAlignmentPanel, "Cluster " + (i + 1));
		}
		
		JScrollPane scrollPane = new JScrollPane(activityCharMapTable);
		clusterAlignmentTabbedPane.addTab("Activity Char Encoding", new ImageIcon(), scrollPane,
				"Activity-Char Encoding");
		
		clusterAlignmentTabbedPane.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent arg0) {
				if (clusterAlignmentTabbedPane.getComponents().length > 0) {
					int clusterNo = clusterAlignmentTabbedPane.getSelectedIndex();
					System.out.println("Selected Cluster No: "+clusterNo);
					if (alignmentList.size() > clusterNo) {
						editMenu.setEnabled(true);
						viewMenu.setEnabled(true);
						
						if (alignmentDisplayPropertiesList.get(clusterNo).isGapRendered()) {
							renderGapsMenuItem.setSelected(true);
						} else {
							renderGapsMenuItem.setSelected(false);
						}

						if (alignmentDisplayPropertiesList.get(clusterNo).isEncodedActivityRendered()) {
							renderEncodedActivityMenuItem.setSelected(true);
						} else {
							renderEncodedActivityMenuItem.setSelected(false);
						}

						if (alignmentDisplayPropertiesList.get(clusterNo).isActivityBackgroundSquare()) {
							squareMenuItem.setSelected(true);
							dotMenuItem.setSelected(false);
						} else {
							squareMenuItem.setSelected(false);
							dotMenuItem.setSelected(true);
						}
						System.out.println("No. Components: "+noClustersPanel.getComponentCount());
						if(noClustersPanel.getComponents().length > 3){
							System.out.println("HERE");
							noClustersPanel.remove(3);
						}
						noClustersPanelLayout.setPosition(concurrentActivityPanelList.get(clusterNo), 0, 3);
						noClustersPanel.add(concurrentActivityPanelList.get(clusterNo));
						
						noClustersPanel.revalidate();
						noClustersPanel.repaint();
					} else {
						viewMenu.setEnabled(false);
						editMenu.setEnabled(false);
					}
				}
			}
		});
		
		StringBuilder seqWithoutGaps = new StringBuilder();
		String traceName;
		Set<Integer> identicalTraceIndicesSet;
		List<String> traceNameList;
		Map<String, TreeSet<Integer>> encodedTraceIdenticalIndicesMap = alignmentTree.getEncodedTraceIdenticalIndicesMap();
		System.out.println("Encoded Trace Identical Indices Map Size: "+encodedTraceIdenticalIndicesMap.size());
		XLog log = alignmentTree.getLog();
		
		for (int i = 0; i < noClusters; i++) {
			String[] alignedTracesInCluster = clusterAlignmentTreeNodeList.get(i).getOriginalAlignment();
			Sequence[] sequencesInArray = new Sequence[alignedTracesInCluster.length];
			int alignmentLength = alignedTracesInCluster[0].length() / encodingLength;
			String[] seq;
			int noTracesInAlignment = alignedTracesInCluster.length;
			
			for (int j = 0; j < noTracesInAlignment; j++) {
				seq = new String[alignmentLength];
				//outliers store the outlier information
				seqWithoutGaps.setLength(0);
				for (int k = 0; k < alignmentLength; k++) {
					seq[k] = alignedTracesInCluster[j].substring(k * encodingLength, (k + 1) * encodingLength);
					if (!seq[k].equals(dash)) {
						seqWithoutGaps.append(seq[k]);
					}
				}
				// Setting the name of the trace here

				traceNameList = new ArrayList<String>();
				if (encodedTraceIdenticalIndicesMap.containsKey(seqWithoutGaps.toString())) {
					identicalTraceIndicesSet = encodedTraceIdenticalIndicesMap.get(seqWithoutGaps.toString());
					for (Integer traceIndex : identicalTraceIndicesSet) {
						traceNameList.add(log.get(traceIndex).getAttributes().get("concept:name").toString().trim());
					}
					//					traceIndex = encodedTraceIdenticalIndicesMap.get(seqWithoutGaps.toString()).first();
					//					traceName = log.get(traceIndex).getAttributes().get("concept:name").toString();
				} else {
					JOptionPane.showMessageDialog(this,
							"Couldn't find encoded trace in the traceIdenticalIndicesMap in cluster: " + i+" trace "+seqWithoutGaps.toString(), "Error Message", JOptionPane.ERROR_MESSAGE);
				}
				if (traceNameList.size() == 0) {
					traceNameList.add("No Name");
				}
				traceName = traceNameList.get(0);
				sequencesInArray[j] = new Sequence(dash, traceName, traceNameList, seq);
				// Add the duplicate trace names etc here
			}
			
			Alignment alignment = new Alignment(encodingLength, this, sequencesInArray);
			alignment.addListener(this);

			Font font = new Font("Sans Serif", Font.PLAIN, fontSize);
			DisplayProperties displayProperties = new DisplayProperties(alignment, font, getFontMetrics(font), true,
					true);
			displayProperties.addListener(this);

			AlignmentPanel alignmentPanel = new AlignmentPanel(this, alignment, displayProperties);
			alignmentViewPortList.get(i).setView(alignmentPanel);

			
			AlignmentNamePanel alignmentNamePanel = new AlignmentNamePanel(this, displayProperties);
			JPanel leftPanel = new JPanel();
			leftPanel.setBackground(Color.white);
			
			ScrollableGridLayout leftPanelLayout = new ScrollableGridLayout(leftPanel, 1, 1, 50, 50);
			leftPanelLayout.setRowFixed(0, true);
			leftPanelLayout.setColumnFixed(0, true);

			leftPanel.setLayout(leftPanelLayout);
			leftPanelLayout.setPosition(alignmentNamePanel, 0, 0);
			leftPanel.add(alignmentNamePanel);
			
			leftViewPortList.get(i).setView(leftPanel);
			
			ConsensusSequence consensusSequence = new ConsensusSequence(this, displayProperties);
			ConsensusComponent consensusComponent = new ConsensusComponent(consensusSequence, displayProperties);
			
			JPanel bottomPanel = new JPanel();
			bottomPanel.setBackground(Color.white);
			bottomPanel.setLayout(new BoxLayout(bottomPanel, BoxLayout.Y_AXIS));
			bottomPanel.add(consensusComponent);
			
			// The top panel contains the ruler and ruler annotations
			JPanel topPanel = new JPanel();
			topPanel.setBackground(Color.white);
			topPanel.setLayout(new BoxLayout(topPanel, BoxLayout.Y_AXIS));

			topPanel.add(new RulerComponent(displayProperties));
			topPanel.add(new RulerAnnotationComponent(displayProperties, this));
			
			ColumnSortComponent columnSortComponent = new ColumnSortComponent(this, displayProperties);
			columnSortComponent.setVisible(columnSorting);
			topPanel.add(columnSortComponent);
			
			ColumnFilterComponent columnFilterComponent = new ColumnFilterComponent(this, displayProperties);
			columnFilterComponent.setVisible(columnFiltering);
			topPanel.add(columnFilterComponent);
			
			topPanelList.add(topPanel);
			columnSortComponentList.add(columnSortComponent);
			columnFilterComponentList.add(columnFilterComponent);
			
			topViewPortList.get(i).setView(topPanel);
			bottomViewPortList.get(i).setView(bottomPanel);
			
			alignmentList.add(alignment);
			alignmentPanelList.add(alignmentPanel);
			alignmentNamePanelList.add(alignmentNamePanel);
			consensusComponentList.add(consensusComponent);
			
			alignmentDisplayPropertiesList.add(displayProperties);
		}
	}
	
	private void buildClusterAlignmentTabbedPanePanel() {
		clusterAlignmentTabbedPane = new JTabbedPane(JTabbedPane.TOP);
		clusterAlignmentTabbedPanePanel = new JPanel();

		ScrollableGridLayout clusterAlignmentTabbedPanePanelLayout = new ScrollableGridLayout(
				clusterAlignmentTabbedPanePanel, 1, 1, 0, 0);
		clusterAlignmentTabbedPanePanel.setLayout(clusterAlignmentTabbedPanePanelLayout);

		clusterAlignmentTabbedPanePanelLayout.setPosition(clusterAlignmentTabbedPane, 0, 0);
		clusterAlignmentTabbedPanePanel.add(clusterAlignmentTabbedPane);
	}
	
	public Map<String, Color> getEncodedActivityColorMap(){
		return encodedActivityColorMap;
	}
	
	public void incrementColumnSorts(){
		noColumnSorts++;
		if (noColumnSorts > MaxPriority + 1) {
			System.err.println("ERROR: Too many column sorts, max = " + MaxPriority);
		}
	}
	
	public int getNextFreePriority() {
		for (int i = 1; i <= MaxPriority; i++) {
			if (columnSorts.get(i - 1) == null) {
				return i;
			}
		}
		return 1;
	}
	
	@SuppressWarnings("unused")
	private void makeRoom(int priority) {
		// if this slot is free then do nothing
		if (columnSorts.get(priority - 1) == null) {
			return;
		}

		// if there is a free spot below this spot
		// then move all things below it down
		// otherwise move all things above it up
		int freeSpace = 0;
		for (int i = priority + 1; i <= MaxPriority; i++) {
			if (columnSorts.get(i - 1) == null) {
				freeSpace = i;
				break;
			}
		}

		// if there is a free space below then use it
		if (freeSpace > 0) {
			makeRoomBetween(priority, freeSpace, 1);
			return;
		}

		// search for free space above
		for (int i = 1; i < priority; i++) {
			if (columnSorts.get(i - 1) == null) {
				freeSpace = i;
				break;
			}
		}

		if (freeSpace > 0) {
			makeRoomBetween(priority, freeSpace, -1);
		} else {
			System.err.println("ERROR: could not find free space in column sort priority list");
		}
	}
	
	private void makeRoomBetween(int priority, int freeSpace, int dir) {
		// move stuff over...
		for (int i = freeSpace; i != priority; i -= dir) {
			// move it over
			ColumnSort columnSort = (ColumnSort) columnSorts.get(i - dir - 1);
			columnSorts.set(i - 1, columnSort);
			columnSort.setPriority(i);
		}

		columnSorts.set(priority - 1, null);
	}
	

	// called when this column sort is (about to be) "deleted"
	public void removed(int priority) {
		columnSorts.set(priority - 1, null);
		noColumnSorts--;
	}
	
	public int getNoColumnSorts(){
		return noColumnSorts;
	}
	
	public void addColumnSort(ColumnSort columnSort, int column) {
		System.out.println("Adding Column Sort with Priority: "+columnSort.getPriority());
		int clusterNo = clusterAlignmentTabbedPane.getSelectedIndex();
		if ((columnSort == null) || (column < 0) || (column > alignmentList.get(clusterNo).getMaxLength())) {
			return;
		}

		// call on alignment
		alignmentList.get(clusterNo).addColumnSort(columnSort, column);
		columnSorts.set(columnSort.getPriority()-1, columnSort);
		incrementColumnSorts();
		updateSort();
	}

	public void removeColumnSort(int column) {
		int clusterNo = clusterAlignmentTabbedPane.getSelectedIndex();
		// call on alignment
		ColumnSort columnSort = alignmentList.get(clusterNo).getColumnSort(column);
		columnSorts.set(columnSort.getPriority()-1, null);
		alignmentList.get(clusterNo).removeColumnSort(column);
		noColumnSorts--;
		updateSort();
	}

	public void updateSort() {
		int clusterNo = clusterAlignmentTabbedPane.getSelectedIndex();
		// tell alignment to sort sequence components based on column sorts
		try {
			
			int originalCursorRow = alignmentDisplayPropertiesList.get(clusterNo).getCursorRow();
			Sequence sequenceWithCursor = alignmentList.get(clusterNo).getSequence(originalCursorRow);
			alignmentList.get(clusterNo).sortBasedOnColumnSorts();
			int newCursorRow = alignmentList.get(clusterNo).getIndex(sequenceWithCursor);
			alignmentDisplayPropertiesList.get(clusterNo).updateCursor(newCursorRow, alignmentDisplayPropertiesList.get(clusterNo).getCursorColumn());
		} catch (Exception e) {
			JOptionPane.showMessageDialog(this, "Error swapping sequences while soring");
		}

		// for debug print total
		// System.out.println(ColumnSort.getNumColumnSorts());
	}

	public void removeColumnFilter(int column) {
		System.out.println("Selected in Cluster: " + clusterAlignmentTabbedPane.getSelectedIndex());
		int clusterNo = clusterAlignmentTabbedPane.getSelectedIndex();
		// call on alignment
		alignmentList.get(clusterNo).removeColumnFilter(column);

		// update collapsed based on this
		updateCollapsed();
	}
	
	public void addColumnFilter(ColumnFilter filter, int column) {
		int clusterNo = clusterAlignmentTabbedPane.getSelectedIndex();
		if ((filter == null) || (column < 0) || (column > alignmentList.get(clusterNo).getMaxLength())) {
			return;
		}

		// call on alignment
		alignmentList.get(clusterNo).addColumnFilter(filter, column);

		// update collapsed based on this
		updateCollapsed();
	}

	public void updateCollapsed() {
		int clusterNo = clusterAlignmentTabbedPane.getSelectedIndex();
		// reset collapsed sequences based on current criteria..
		Sequence[] seqs = alignmentList.get(clusterNo).getAllSequences();

		// start by setting all to NOT collapsed and NOT hidden
		setAllSequenceCollapsed(false, false);
		// setAllSequencesHidden(false); //Cannot set as not hidden as hide
		// selected uses it.

		// if (columnFiltering) {
		if (alignmentList.get(clusterNo).getColumnFiltering()) {
			setAllSequencesHidden(false);
			Iterator<Integer> iter = alignmentList.get(clusterNo).getColumnFilterColumnIterator();
			while (iter.hasNext()) {
				Integer key = iter.next();
				int column = key.intValue();
				ColumnFilter filter = alignmentList.get(clusterNo).getColumnFilter(key);
				for (int i = 0; i < seqs.length; i++) {
					Sequence seq = seqs[i];
					String encodedActivity = seq.getEncodedActivity(column);

					if (!filter.optionTest(encodedActivity)) {
						setSequenceCollapsed(i, true, false);
						alignmentDisplayPropertiesList.get(clusterNo).setSeqHidden(seqs[i], true);
					}
					/*
					 * else{ props.setSeqHidden(seqs[i],false); }
					 */
				}
			}
		}

		// now hide those that are hidden
		// need to repeat setSequenceCollapsed as this is also used by hide
		// selected seqs
		for (int i = 0; i < seqs.length; i++) {
			if (alignmentDisplayPropertiesList.get(clusterNo).isSequenceHidden(i)) {
				setSequenceCollapsed(i, true, false);
			}
		}

		revalidateAndRepaintAll();
	}

	public void revalidateAndRepaintAll() {
		int clusterNo = clusterAlignmentTabbedPane.getSelectedIndex();
		// System.out.println("revalidateAndRepaintAll().....");
		// and NOW revalidate and repaint
		
		alignmentPanelList.get(clusterNo).revalidate();
		alignmentPanelList.get(clusterNo).repaint();
		
		alignmentNamePanelList.get(clusterNo).revalidate();
		alignmentNamePanelList.get(clusterNo).repaint();

		alignmentViewPortList.get(clusterNo).revalidate();
		alignmentViewPortList.get(clusterNo).repaint();

		// TODO removed alignmentAnnPanel

		/*
		columnFilterComponentList.get(clusterNo).revalidate();
		columnFilterComponentList.get(clusterNo).repaint();
		*/
		
		bottomViewPortList.get(clusterNo).revalidate();
		bottomViewPortList.get(clusterNo).repaint();
		
//		clusterAlignmentPanelList.get(clusterNo).revalidate();
//		clusterAlignmentPanelList.get(clusterNo).repaint();
	}

	public void setColumnFiltering(boolean columnFiltering) {
		setColumnFiltering(columnFiltering, true);
	}

	public void setColumnFiltering(boolean columnFiltering, boolean setMenu) {
		int clusterNo = clusterAlignmentTabbedPane.getSelectedIndex();
		// if we already are do nothing
		// if (this.columnFiltering == columnFiltering)
		// return;

		alignmentList.get(clusterNo).setColumnFiltering(columnFiltering);
		// this.columnFiltering = columnFiltering;

		if (setMenu) {
			columnFilteringMenuItem.setSelected(columnFiltering);
		}

		// if we turn column filtering on then we must turn column sorting off
		if (columnFiltering) {
			setColumnSorting(false);
		}

		// if not init do nothing else...
		if (columnFilterComponentList.get(clusterNo) == null) {
			return;
		}

		updateCollapsed();
		columnFilterComponentList.get(clusterNo).setVisible(columnFiltering);
	}

	public void setColumnSorting(boolean columnSorting) {
		setColumnSorting(columnSorting, true);
	}

	public void setColumnSorting(boolean columnSorting, boolean setMenu) {
		int clusterNo = clusterAlignmentTabbedPane.getSelectedIndex();
		// if we already are do nothing
		// if (this.columnSorting == columnSorting)
		// return;

		alignmentList.get(clusterNo).setColumnSorting(columnSorting);
		// this.columnSorting = columnSorting;

		if (setMenu) {
			columnSortingMenuItem.setSelected(columnSorting);
		}

		// if we turn column sorting on then we must turn column filtering off
		if (columnSorting) {
			setColumnFiltering(false);
		}

		// if we are before alignment loads...do nothing else...
		if (columnSortComponentList.get(clusterNo) == null) {
			return;
		}

		updateSort();
		columnSortComponentList.get(clusterNo).setVisible(columnSorting);
	}
	
	public void setAllSequenceCollapsed(boolean collapsed, boolean repaint) {
		int clusterNo = clusterAlignmentTabbedPane.getSelectedIndex();
		alignmentPanelList.get(clusterNo).setAllSequenceCollapsed(collapsed, repaint);
		alignmentNamePanelList.get(clusterNo).setAllSequenceCollapsed(collapsed, repaint);
		// TODO alignmentAnnPanel code removed
	}
	
	public void setAllSequencesHidden(boolean hidden) {
		int clusterNo = clusterAlignmentTabbedPane.getSelectedIndex();
		Sequence[] seqs = alignmentList.get(clusterNo).getAllSequences();
		for (int i = 0; i < seqs.length; i++) {
			alignmentDisplayPropertiesList.get(clusterNo).setSeqHidden(seqs[i], hidden);
		}
	}
	
	public void setSequenceCollapsed(int idx, boolean collapsed, boolean repaint) {
		int clusterNo = clusterAlignmentTabbedPane.getSelectedIndex();
		alignmentPanelList.get(clusterNo).setSequenceCollapsed(idx, collapsed, repaint);
		alignmentNamePanelList.get(clusterNo).setSequenceCollapsed(idx, collapsed, repaint);
		// TODO alignmentAnnPanel code removed
	}
	
	@Override
	public void displayAnnViewChanged(DisplayProperties displayProperties,
			Sequence sequence, boolean show) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void displaySeqSelectChanged(DisplayProperties displayProperties,
			Sequence sequence, boolean select) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void displayFontChanged(DisplayProperties displayProperties) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void displayRenderGapsChanged(DisplayProperties displayProperties) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void displayGroupEditingChanged(DisplayProperties displayProperties) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void displayOverwriteChanged(DisplayProperties displayProperties) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void displayHighlightsChanged(DisplayProperties displayProperties,
			Sequence sequence) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void displayHighlightsChanged(DisplayProperties displayProperties,
			Sequence[] sequenceArray) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void activityBackgroundChanged(DisplayProperties displayProperties) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void alignmentNameChanged(Alignment alignment) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void alignmentSeqDeleted(Alignment alignment, int i,
			Sequence sequence) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void alignmentSeqSwapped(Alignment alignment, int i, int j) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void alignmentSeqActivityChanged(Alignment alignment,
			Sequence sequence) {
		// TODO Auto-generated method stub
		
	}

	public void activityColorMappingChanged(Color color) {
		System.out.println("In AlignmentFrame activityColorMappingChanged");
		encodedActivityColorMap = activityCharEncodingTable.getEncodedActivityColorMap();
	}

	public void activityColorChanged(String encodedActivity, Color color) {
		System.out.println("In AlignmentFrame activityColorChanged");
		try{
			encodedActivityColorMap.put(encodedActivity, color);
			System.out.println("Changed Color: "+encodedActivity+" @ "+color);
		}finally{
			for(AlignmentPanel alignmentPanel : alignmentPanelList)
				alignmentPanel.fireActivityBackgroundChanged();
		}

//		for(AlignmentPanel alignmentPanel : alignmentPanelList)
//			alignmentPanel.fireActivityBackgroundChanged();
	}
	
	public boolean isValidEncodedActivity(String encodedActivity){
		return encodedActivityColorMap.containsKey(encodedActivity);
	}
	
	public int getEncodingLength(){
		return this.encodingLength;
	}
	
	public boolean hasAnyActivities(String s) {
		if (s.length() < encodingLength) {
			return false;
		} else if ((s.length() > encodingLength) && !s.contains(",")) {
			return false;
		}

		String[] sSplit = s.split(",");
		for (int i = 0; i < sSplit.length; i++) {
			if (sSplit[i].length() != encodingLength) {
				return false;
			}
		}

		return true;
	}
	
	public Map<String, String> getCharActivityMap(){
		return alignmentTree.getCharActivityMap();
	}
	
	public ArrayList<EventInfo> getOutliers() {
		return outliers;
	}
	

}
