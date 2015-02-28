package sen.support;

public class BinarySearchTree<Key extends Comparable<Key>, Value> {
	private Key[] keys;
	private Value[] vals;
	private int N = 0;
	private long count=0;
	private int Searchcount=0;

	public BinarySearchTree(int size) {
		keys = (Key[]) new Comparable[size];
		vals = (Value[]) new Object[size];
	}

	private void resize(int size) {
		assert size >= N;
		Key[] tempKey = (Key[]) new Comparable[size];
		Value[] tempValue = (Value[]) new Object[size];
		for (int i = 0; i < N; i++) {
			count++;
			tempKey[i] = keys[i];
			tempValue[i] = vals[i];
		}
		vals = tempValue;
		keys = tempKey;
	}

	public boolean contains(Key key) {
		return get(key) != null;
	}

	public int size() {
		return N;
	}

	public boolean isEmpty() {
		return size() == 0;
	}

	public Value get(Key key) {
		if (isEmpty())
			return null;
		int i = rank(key);
		if (i < N && keys[i].compareTo(key) == 0)
			return vals[i];
		return null;
	}

	public int rank(Key key) {
		int lo = 0;
		int hi = N - 1;
		Searchcount = 0;
		while (lo <= hi) {
			Searchcount++;
			count++;			// count comparison number
			int m = lo + (hi - lo) / 2;
			int cmp = key.compareTo(keys[m]);
			if (cmp < 0)
				hi = m - 1;
			else if (cmp > 0)
				lo = m + 1;
			else
				return m;
		}
		return lo;
	}

	public void put(Key key, Value val) {
		if (val == null) {
			delete(key);
			return;
		}
		int i = rank(key);			// compare
		if (i < N && keys[i].compareTo(key) == 0) {
			count++;
			vals[i] = val;
			return;
		}
		if (N == keys.length) {
			resize(2 * keys.length);
		}

		for (int j = N; j > i; j--) {
			keys[j] = keys[j - 1];
			vals[j] = vals[j - 1];
			count++;
		}
		keys[i] = key;
		vals[i] = val;
		N++;
		assert check();
	}

	public void delete(Key key) {
		if (isEmpty())
			return;

		int i = rank(key);

		if (i == N || keys[i].compareTo(key) != 0) {
			return;
		}

		for (int j = i; j < N - 1; j++) {
			keys[j] = keys[j + 1];
			vals[j] = vals[j + 1];
		}

		N--;
		keys[N] = null;
		vals[N] = null;

		if (N > 0 && N == keys.length / 4)
			resize(keys.length / 2);

		assert check();
	}

	private boolean check() {
		return isSorted() && rankCheck();
	}

	private boolean isSorted() {
		for (int i = 1; i < size(); i++)
			if (keys[i].compareTo(keys[i - 1]) < 0)
				return false;
		return true;
	}

	private boolean rankCheck() {
		for (int i = 0; i < size(); i++)
			if (i != rank(select(i)))
				return false;
		for (int i = 0; i < size(); i++)
			if (keys[i].compareTo(select(rank(keys[i]))) != 0)
				return false;
		return true;
	}

	public Key select(int k) {
		if (k < 0 || k >= N)
			return null;
		return keys[k];
	}

	public void print(){
		
		for(int i =0; i<N; i++){
		System.out.println(keys[i] +"  "+vals[i]);
		}
	}
	
	public long getCount(){
		return count;
	}
	
	public int getSearchCount(){
		return Searchcount;
	}
}
