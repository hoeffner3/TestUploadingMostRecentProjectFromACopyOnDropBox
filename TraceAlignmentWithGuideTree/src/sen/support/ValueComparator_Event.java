package sen.support;

import java.util.Comparator;
import java.util.Map;

import sen.outlierdetector.EventInfo;

public class ValueComparator_Event implements Comparator<Map.Entry<String, EventInfo>> {
	public int compare(Map.Entry<String, EventInfo> mp1, Map.Entry<String, EventInfo> mp2) {
		if (mp2.getValue().getScore() - mp1.getValue().getScore() > 0)
			return 1;
		return -1;
	}
}
