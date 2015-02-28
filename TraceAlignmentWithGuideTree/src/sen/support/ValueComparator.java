package sen.support;

import java.util.Comparator;
import java.util.Map;

public class ValueComparator implements Comparator<Map.Entry<String, Double>> {
	public int compare(Map.Entry<String, Double> mp1, Map.Entry<String, Double> mp2) {
		if (mp2.getValue() - mp1.getValue() > 0)
			return 1;
		return -1;
	}
}
