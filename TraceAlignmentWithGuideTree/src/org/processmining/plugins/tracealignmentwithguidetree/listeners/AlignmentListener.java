package org.processmining.plugins.tracealignmentwithguidetree.listeners;

import org.processmining.plugins.tracealignmentwithguidetree.datatypes.Alignment;
import org.processmining.plugins.tracealignmentwithguidetree.datatypes.Sequence;

public interface AlignmentListener {
	public void alignmentNameChanged(Alignment alignment);
	public void alignmentSeqDeleted(Alignment alignment, int i, Sequence sequence);
	public void alignmentSeqSwapped(Alignment alignment, int i, int j);
	public void alignmentSeqActivityChanged(Alignment alignment, Sequence sequence);
}
