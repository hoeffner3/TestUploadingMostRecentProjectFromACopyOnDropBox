package sen.support;
import java.util.Comparator;

import sen.outlierdetector.EventInfo;

public class ValueComparator_Eventarray implements Comparator<EventInfo> {
	public int compare(EventInfo mp1, EventInfo mp2) {
		if (mp2.getScore() - mp1.getScore() > 0)
			return 1;
		return -1;
	}
}
