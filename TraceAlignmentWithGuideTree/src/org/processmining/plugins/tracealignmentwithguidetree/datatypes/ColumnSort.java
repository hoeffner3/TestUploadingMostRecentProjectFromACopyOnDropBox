package org.processmining.plugins.tracealignmentwithguidetree.datatypes;

import org.processmining.plugins.tracealignmentwithguidetree.visualization.AlignmentFrame;

public class ColumnSort {
	int encodingLength;
	
	public static final int EqualsThisLetter = 0;
	public static final int HasAnyOfTheseLetters = 1;
	public static final int HasNoneOfTheseLetters = 2;
	public static final int noOptions = 3;
	
	public static String[] optionNames = { "Equals This Letter", "Has Any Of These Letters",
		"Has None Of These Letters", };
	
	String[] filterActivitiesArray;
	
	int selectedOption;
	int priority;
		
	public ColumnSort(int encodingLength, int option, String filterActivitiesString, int priority) {
		this.encodingLength = encodingLength;
		setFilterActivities(option, filterActivitiesString);
		setPriority(priority);
	}
	
	public ColumnSort(int encodingLength, String letter, int priority) {
		this(encodingLength, EqualsThisLetter, letter, priority);
	}

	public void setFilterActivities(int option, String filterActivitiesString) {
		if ((option < 0) || (option >= noOptions)) {
			option = 0;
		}
		if ((filterActivitiesString == null) || (filterActivitiesString.length() < 1)) {
			filterActivitiesString = new String("!");
		}

		selectedOption = option;
		filterActivitiesArray = getArrayFromString(filterActivitiesString);
	}
	
	public void setPriority(int priority) {
		this.priority = priority;
	}
	
	public static final String[] getDisplayOpNames() {
		return optionNames;
	}

	public static final String getDisplayNone() {
		return "None";
	}

	public static final String getDisplayCustom() {
		return "Edit";
	}

	public static final String[] getDisplayCustomOpNames() {
		String[] s = new String[optionNames.length - 1];
		for (int i = 1; i < optionNames.length; i++) {
			s[i - 1] = optionNames[i];
		}
		return s;
	}

	private static String[] optionText = { " ", // actually whatever is in
			// filterActivitiesArray[0]
			"+", "-", };

	public String getDisplayText() {
		if (selectedOption == EqualsThisLetter) {
			return getFilterActivitiesString();
		}
		return optionText[selectedOption];
	}

	public static String[] getArrayFromString(String filterActivitiesString) {
		// count how many activities

		String[] filterActivitiesStringSplit = filterActivitiesString.split(",");
		return filterActivitiesStringSplit;
	}

	public int getSelectedOption() {
		return selectedOption;
	}

	public String getFilterActivitiesString() {
		StringBuilder strBuilder = new StringBuilder();

		for (int i = 0; i < filterActivitiesArray.length - 1; i++) {
			strBuilder.append(filterActivitiesArray[i]).append(",");
		}
		strBuilder.append(filterActivitiesArray[filterActivitiesArray.length - 1]);

		return strBuilder.toString();
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

	public boolean optionTest(String encodedActivity) {
		String filterActivity;
		if ((encodedActivity == null) || (encodedActivity.length() < 1)) {
			filterActivity = "!";
		} else {
			filterActivity = encodedActivity;
		}

		if (selectedOption == EqualsThisLetter) {
			return filterActivitiesArray[0].equals(filterActivity);
		}

		if ((selectedOption == HasAnyOfTheseLetters) || (selectedOption == HasNoneOfTheseLetters)) {
			for (int i = 0; i < filterActivitiesArray.length; i++) {
				if (filterActivitiesArray[i].equals(filterActivity)) {
					return selectedOption == HasAnyOfTheseLetters;
				}
			}
			return selectedOption != HasAnyOfTheseLetters;
		}

		// we pass any unknown test...
		return true;
	}
	
	public int optionTestGetPriorityBit(String activity) {
		boolean passesTest = optionTest(activity);

		if (!passesTest) {
			return 0;
		}

		// highest priority is "1" , then "2" etc
		// so priority of 1 sets the HIGHEST bit
		// and then we sort DESCENDING order
		return 1 << (AlignmentFrame.MaxPriority - priority);
	}
	
	public int getPriority() {
		return priority;
	}
}	

