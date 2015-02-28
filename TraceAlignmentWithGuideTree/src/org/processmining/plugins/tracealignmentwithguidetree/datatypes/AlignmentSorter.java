package org.processmining.plugins.tracealignmentwithguidetree.datatypes;

import java.util.Arrays;
import java.util.Comparator;


public class AlignmentSorter {
	private final Alignment alignment;
	private final Comparator<Sequence> comparator;

	public AlignmentSorter(Alignment alignment, Comparator<Sequence> comparator) {
		this.alignment = alignment;
		this.comparator = comparator;
	}

	public void sort() throws Exception {
		//System.out.println("AlignmentSorter - sort");
		Sequence[] seqs = alignment.getAllSequences();
		//System.out.println("All sequences:"+seqs);

		Arrays.sort(seqs, comparator);

		for (int i = seqs.length - 1; i >= 0; i--) {
			int orig_idx = alignment.getIndex(seqs[i]);

			if (orig_idx != i) {
				alignment.swapSequence(i, orig_idx);
			}
		}
	}
}
