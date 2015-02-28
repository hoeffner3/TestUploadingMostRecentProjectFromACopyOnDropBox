package sen.outlierdetector;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeSet;

import org.deckfour.xes.model.XEvent;
import org.deckfour.xes.model.XLog;
import org.deckfour.xes.model.XTrace;
import org.processmining.plugins.tracealignmentwithguidetree.enumtypes.EventClassification;

import sen.support.ValueComparator;
import sen.support.ValueComparator_Event;
import sen.support.ValueComparator_Eventarray;

/**
 * @author Sen Yang
 * @date 13 Feb 2015
 * @version 1.0
 * @email sy358@scarletmail.rutgers.edu
 * @copyright Sen Yang Rutgers University
 */

public class OutlierDetector {
	//~~~Input log
	XLog log;

	//~~~Transform Matrix Encoding Table
	Hashtable<Object, Integer> activityIndex = new Hashtable<Object, Integer>();
	Hashtable<Integer, Object> indexActivity = new Hashtable<Integer, Object>();

	//~~~Transform Matrices
	int[][] oneDistanceTransformMatrix;
	int[][] twoDistanceTransformMatrix;
	int[][] threeDistanceTransformMatrix;
	int[][] fourDistanceTransformMatrix;
	int[][] fiveDistanceTransformMatrix;

	//~~~Scoring matrix list
	ArrayList<double[][]> ScoringMatrixList = new ArrayList<double[][]>();
	ArrayList<double[][]> CompensateScoringMatrixList = new ArrayList<double[][]>();

	//~~~Scoring Matrix
	double[][] oneDistanceScoringMatrix;
	double[][] twoDistanceScoringMatrix;
	double[][] threeDistanceScoringMatrix;
	double[][] fourDistanceScoringMatrix;
	double[][] fiveDistanceScoringMatrix;
	int index = 0;

	double[][] CompensateoneDistanceScoringMatrix;
	double[][] CompensatetwoDistanceScoringMatrix;
	double[][] CompensatethreeDistanceScoringMatrix;
	double[][] CompensatefourDistanceScoringMatrix;
	double[][] CompensatefiveDistanceScoringMatrix;
	//~~~Matrices List
	ArrayList<int[][]> matricesList = new ArrayList<int[][]>();

	//~~~Activity numbers, also the matrix size
	int activityNumber;

	//~~~Log size, also the case number
	int caseNumber;

	//~~~Activity Set, sorted based on natural order
	TreeSet<Object> activitySet = new TreeSet<Object>();

	String[] trace_ID;

	//Store trace log in formated form with virtual activities
	String[] virtual_events = { "Av_01", "Av_02", "Av_03", "Av_04", "Av_05", "Zv_06", "Zv_07", "Zv_08", "Zv_09",
			"Zv_10" };
	ArrayList<ArrayList<String>> trace_log = new ArrayList<ArrayList<String>>();

	Hashtable<String, Integer> activity_count = new Hashtable<String, Integer>();
	HashMap<String, Double> activity_score = new HashMap<String, Double>();
	ArrayList<EventInfo> outlier_array = new ArrayList<EventInfo>();

	public OutlierDetector(XLog log) {
		this.log = log;
		IniComponents();
		IniTransformMatrix();
		ConvertTransformMatrixToScoringMatrix();
		ComputeCompensateScoringMatrix();
		DetectOutlier();
	}

	/*
	 * Transform Matrix Encoding Table
	 */
	public void IniComponents() {
		//~~~Initialize the log size
		caseNumber = log.size();

		//~~~Initialize the activity set
		for (int i = 0; i < 5; i++) {
			activitySet.add(virtual_events[i]);
		}

		int k = 0;
		trace_ID = new String[log.size()];
		for (XTrace trace : log) {
			ArrayList<String> unit_trace = new ArrayList<String>();
			trace_ID[k] = trace.getAttributes().get("concept:name").toString();
			k++;
			for (int i = 0; i < 5; i++) {
				unit_trace.add(virtual_events[i]);
				if (activity_count.containsKey(virtual_events[i]))
					activity_count.replace(virtual_events[i], activity_count.get(virtual_events[i]) + 1);
				else
					activity_count.put(virtual_events[i], 1);
			}

			for (XEvent event : trace) {
				String this_event = event.getAttributes().get("concept:name").toString();
				activitySet.add(this_event);
				if (activity_count.containsKey(this_event))
					activity_count.replace(this_event, activity_count.get(this_event) + 1);
				else
					activity_count.put(this_event, 1);
				activity_score.put(this_event, 0.0);
				unit_trace.add(this_event);
			}
			for (int i = 0; i < 5; i++) {
				unit_trace.add(virtual_events[i + 5]);
				if (activity_count.containsKey(virtual_events[i + 5]))
					activity_count.replace(virtual_events[i + 5], activity_count.get(virtual_events[i + 5]) + 1);
				else
					activity_count.put(virtual_events[i + 5], 1);
			}
			trace_log.add(unit_trace);
		}
		System.out.println(trace_log);

		for (int i = 0; i < 5; i++) {
			activitySet.add(virtual_events[i + 5]);
		}

		System.out.println(activitySet);

		//~~~Transform Matrix encoding Table

		for (Object activity : activitySet) {
			activityIndex.put(activity, index);
			indexActivity.put(index, activity);
			index++;
		}

		//~~~Initialize the matrix size
		this.activityNumber = activitySet.size();

		//~~~Initialize the matrices List
		oneDistanceTransformMatrix = new int[activityNumber][activityNumber];
		twoDistanceTransformMatrix = new int[activityNumber][activityNumber];
		threeDistanceTransformMatrix = new int[activityNumber][activityNumber];
		fourDistanceTransformMatrix = new int[activityNumber][activityNumber];
		fiveDistanceTransformMatrix = new int[activityNumber][activityNumber];

		matricesList.add(oneDistanceTransformMatrix);
		matricesList.add(twoDistanceTransformMatrix);
		matricesList.add(threeDistanceTransformMatrix);
		matricesList.add(fourDistanceTransformMatrix);
		matricesList.add(fiveDistanceTransformMatrix);

		oneDistanceScoringMatrix = new double[activityNumber][activityNumber];
		twoDistanceScoringMatrix = new double[activityNumber][activityNumber];
		threeDistanceScoringMatrix = new double[activityNumber][activityNumber];
		fourDistanceScoringMatrix = new double[activityNumber][activityNumber];
		fiveDistanceScoringMatrix = new double[activityNumber][activityNumber];
		//~~~Initialize the Scoring matix list
		ScoringMatrixList.add(oneDistanceScoringMatrix);
		ScoringMatrixList.add(twoDistanceScoringMatrix);
		ScoringMatrixList.add(threeDistanceScoringMatrix);
		ScoringMatrixList.add(fourDistanceScoringMatrix);
		ScoringMatrixList.add(fiveDistanceScoringMatrix);
		//Initialize Compensate scoring matrix
		CompensateoneDistanceScoringMatrix = new double[activityNumber][activityNumber];
		CompensatetwoDistanceScoringMatrix = new double[activityNumber][activityNumber];
		CompensatethreeDistanceScoringMatrix = new double[activityNumber][activityNumber];
		CompensatefourDistanceScoringMatrix = new double[activityNumber][activityNumber];
		CompensatefiveDistanceScoringMatrix = new double[activityNumber][activityNumber];
		//~~~Initialize the Scoring matix list
		CompensateScoringMatrixList.add(CompensateoneDistanceScoringMatrix);
		CompensateScoringMatrixList.add(CompensatetwoDistanceScoringMatrix);
		CompensateScoringMatrixList.add(CompensatethreeDistanceScoringMatrix);
		CompensateScoringMatrixList.add(CompensatefourDistanceScoringMatrix);
		CompensateScoringMatrixList.add(CompensatefiveDistanceScoringMatrix);
	}

	/*
	 * Initialize all the transform matrices
	 */
	public void IniTransformMatrix() {
		int distance = 0;
		while (distance < 5) {
			distance++;
			switch (distance) {
				case 1 :
					ComputeTransformMatrix(oneDistanceTransformMatrix, distance);
					break;
				case 2 :
					ComputeTransformMatrix(twoDistanceTransformMatrix, distance);
					break;
				case 3 :
					ComputeTransformMatrix(threeDistanceTransformMatrix, distance);
					break;
				case 4 :
					ComputeTransformMatrix(fourDistanceTransformMatrix, distance);
					break;
				case 5 :
					ComputeTransformMatrix(fiveDistanceTransformMatrix, distance);
					break;
				default :
					System.out.println("error");
					return;
			}
		}
	}

	/*
	 * Sen: Compute the transform Matrix
	 */
	public void ComputeTransformMatrix(int[][] matrix, int distance) {
		//Compute the Transform Matrix
		for (ArrayList<String> trace : trace_log) {
			for (int i = 0; i < trace.size() - distance; i++) {
				Object currentActivity = trace.get(i);
				Object nextActivity = trace.get(i + distance);

				int rowIndex = activityIndex.get(currentActivity);
				int columnIndex = activityIndex.get(nextActivity);
				matrix[rowIndex][columnIndex]++;
			}
		}

		PrintTransformMatrix(matrix);
	}

	/*
	 * convert transform matrix to scoring matrix
	 */
	private void ConvertTransformMatrixToScoringMatrix() {

		for (int matrix_num = 0; matrix_num < 5; matrix_num++) {
			for (int i = 0; i < activityNumber; i++) {
				for (int j = 0; j < activityNumber; j++) {
					ScoringMatrixList.get(matrix_num)[i][j] = (double) ((caseNumber / (matricesList.get(matrix_num)[i][j] + 1)));
				}
			}
		}
		for (int matrix_num = 0; matrix_num < 5; matrix_num++) {
			for (int i = 0; i < activityNumber; i++) {
				for (int j = 0; j < activityNumber; j++) {
					double ref = activity_count.get(indexActivity.get(i).toString());
					ScoringMatrixList.get(matrix_num)[i][j] = ScoringMatrixList.get(matrix_num)[i][j] * ref;
				}
			}
			PrintScoringMatrix(ScoringMatrixList.get(matrix_num));
		}

	}

	private void ComputeCompensateScoringMatrix() {
		for (int matrix_num = 0; matrix_num < 5; matrix_num++) {
			for (int i = 0; i < activityNumber; i++) {
				for (int j = 0; j < activityNumber; j++) {
					switch (matrix_num) {
						case 0 :
							CompensateScoringMatrixList.get(matrix_num)[i][j] = 25 * ScoringMatrixList.get(0)[i][j]
									+ 16 * ScoringMatrixList.get(1)[i][j] + 9 * ScoringMatrixList.get(2)[i][j] + 4
									* ScoringMatrixList.get(3)[i][j] + ScoringMatrixList.get(4)[i][j];
							break;
						case 1 :
							CompensateScoringMatrixList.get(matrix_num)[i][j] = 9 * ScoringMatrixList.get(0)[i][j] + 16
									* ScoringMatrixList.get(1)[i][j] + 9 * ScoringMatrixList.get(2)[i][j] + 4
									* ScoringMatrixList.get(3)[i][j] + ScoringMatrixList.get(4)[i][j];
							break;
						case 2 :
							CompensateScoringMatrixList.get(matrix_num)[i][j] = 1 * ScoringMatrixList.get(0)[i][j] + 4
									* ScoringMatrixList.get(1)[i][j] + 9 * ScoringMatrixList.get(2)[i][j] + 4
									* ScoringMatrixList.get(3)[i][j] + ScoringMatrixList.get(4)[i][j];
							break;
						case 3 :
							CompensateScoringMatrixList.get(matrix_num)[i][j] = 1 * ScoringMatrixList.get(2)[i][j] + 4
									* ScoringMatrixList.get(3)[i][j] + ScoringMatrixList.get(4)[i][j];
							break;
						case 4 :
							CompensateScoringMatrixList.get(matrix_num)[i][j] = ScoringMatrixList.get(4)[i][j];
							break;
					}
				}
			}
			PrintScoringMatrix(CompensateScoringMatrixList.get(matrix_num));
		}
	}

	public void DetectOutlier() {
		Double score = 0.0;
		Double threshold = 78395.0;
		for (int case_num = 0; case_num < trace_log.size(); case_num++) {
			ArrayList<String> trace = trace_log.get(case_num);
			HashMap<String, EventInfo> temp_activity_score = new HashMap<String, EventInfo>();

			System.out.println("Case " + trace_ID[case_num] + ":");

			for (int i = 5; i < trace.size() - 5; i++) {
				String this_event = trace.get(i);

				Double temp_score = 0.0;
				for (int distance = 1; distance < 6; distance++) {
					temp_score += CompensateScoringMatrixList.get(distance - 1)[activityIndex.get(trace.get(i
							- distance))][activityIndex.get(this_event)];
					temp_score += CompensateScoringMatrixList.get(distance - 1)[activityIndex.get(this_event)][activityIndex
							.get(trace.get(i + distance))];
				}
				EventInfo temp_info = new EventInfo(temp_score, trace_ID[case_num], i - 4);

				temp_info.setClassification(EventClassification.Outlier);  //TODO: stevenote: I only need to set the event classification to outlier once but i do it twice, find where it is unnecessary
				try {
					if (temp_activity_score.get(this_event).getScore() < temp_score) {
						temp_activity_score.put(this_event, temp_info);
					}
				} catch (Exception e) {
					temp_activity_score.put(this_event, temp_info);
				}

				//				System.out.println(this_event + " score: " + temp_score + " Location: " + (i - 4));
				score = temp_score + activity_score.get(this_event);
				activity_score.replace(this_event, score);

			}
			List<Map.Entry<String, EventInfo>> sorted_activity_score = new ArrayList<>();
			sorted_activity_score.addAll(temp_activity_score.entrySet());
			ValueComparator_Event vc = new ValueComparator_Event();
			Collections.sort(sorted_activity_score, vc);

			for (Iterator<Entry<String, EventInfo>> it = sorted_activity_score.iterator(); it.hasNext();) {
				Object key = it.next();
				//Store outliers in array for later use, threshold is set
				if (((Entry<String, EventInfo>) key).getValue().getScore() >= threshold) {
					EventInfo temp_event = new EventInfo(((Entry<String, EventInfo>) key).getValue().getScore(),
							((Entry<String, EventInfo>) key).getValue().getcase_id(), ((Entry<String, EventInfo>) key)
									.getValue().getlocation(), ((Entry<String, EventInfo>) key).getKey());
					temp_event.setClassification(EventClassification.Outlier);  //TODO: stevenote: I only need to set the event classification to outlier once but i do it twice, find where it is unnecessary
					outlier_array.add(temp_event);
				}
				System.out.println(((Entry<String, EventInfo>) key).getKey() + " Score: "
						+ ((Entry<String, EventInfo>) key).getValue().getScore() + " Location: "
						+ ((Entry<String, EventInfo>) key).getValue().getlocation());
			}
		}
		ValueComparator_Eventarray outlier_compare = new ValueComparator_Eventarray();
		Collections.sort(outlier_array, outlier_compare);
		System.out.println("Outliers:");
		for(int i = 0;i<outlier_array.size();i++)
		{
			System.out.println(outlier_array.get(i).getactivity()+" "+outlier_array.get(i).getScore() );
		}
		
		List<Map.Entry<String, Double>> sorted_activity_score = new ArrayList<>();
		sorted_activity_score.addAll(activity_score.entrySet());
		ValueComparator vc = new ValueComparator();
		Collections.sort(sorted_activity_score, vc);
		System.out.println("Sorted activity score in All");
		for (Iterator<Entry<String, Double>> it = sorted_activity_score.iterator(); it.hasNext();) {
			System.out.println(it.next());
		}
		System.out.println("Sorted!");
	}

	public ArrayList<EventInfo> getOutlier() {
		return outlier_array;
	}

	/*
	 * Sen: print the transform Matrix in the console
	 */
	private void PrintTransformMatrix(int[][] matrix) {
		System.out.println("Transform Matrix");
		for (Object activity : activitySet) {
			System.out.print("\t" + activity.toString());
		}
		System.out.println();
		for (int i = 0; i < activityNumber; i++) {
			for (int j = 0; j < activityNumber; j++) {
				System.out.print("\t" + matrix[i][j]);
			}
			System.out.println();
		}
	}

	private void PrintScoringMatrix(double[][] matrix) {
		System.out.println("Scoring Matrix");
		for (Object activity : activitySet) {
			System.out.print("\t" + activity.toString());
		}
		System.out.println();
		for (int i = 0; i < activityNumber; i++) {
			for (int j = 0; j < activityNumber; j++) {
				System.out.print("\t" + matrix[i][j]);
			}
			System.out.println();
		}
	}

}
