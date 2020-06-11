package code;

import java.lang.reflect.Array;
import java.util.LinkedList;
import java.util.List;
import java.util.HashSet;
import given.AbstractHashMap;
import given.HashEntry;

/*
 * The file should contain the implementation of a hashmap with:
 * - Separate Chaining for collision handling
 * - Multiply-Add-Divide (MAD) for compression: (a*k+b) mod p
 * - Java's own linked lists for the secondary containers
 * - Resizing (to double its size) and rehashing when the load factor gets above a threshold
 *   Note that for this type of hashmap, load factor can be higher than 1
 * 
 * Some helper functions are provided to you. We suggest that you go over them.
 * 
 * You are not allowed to use any existing java data structures other than for the buckets (which have been 
 * created for you) and the keyset method
 */

public class HashMapSC<Key, Value> extends AbstractHashMap<Key, Value> {

	// The underlying array to hold hash entry Lists
	private LinkedList<HashEntry<Key, Value>>[] buckets;

	// Note that the Linkedlists are still not initialized!
	@SuppressWarnings("unchecked")
	protected void resizeBuckets(int newSize) {
		// Update the capacity
		N = nextPrime(newSize);
		buckets = (LinkedList<HashEntry<Key, Value>>[]) Array.newInstance(LinkedList.class, N);
	}

	/*
	 * ADD MORE FIELDS IF NEEDED
	 * 
	 */

	// The threshold of the load factor for resizing
	protected float criticalLoadFactor;

	/*
	 * ADD A NESTED CLASS IF NEEDED
	 * 
	 */

	public int hashValue(Key key, int iter) {
		return hashValue(key);
	}

	public int hashValue(Key key) {
		// TODO: Implement the hashvalue computation with the MAD method. Will be almost
		// the same as the primaryHash method of HashMapDH
		return Math.abs((Math.abs(key.hashCode()) * a + b) % P) % N;
	}

	// Default constructor
	public HashMapSC() {
		this(101);
	}

	public HashMapSC(int initSize) {
		// High criticalAlpha for representing average items in a secondary container
		this(initSize, 10f);
	}

	public HashMapSC(int initSize, float criticalAlpha) {
		N = initSize;
		criticalLoadFactor = criticalAlpha;
		resizeBuckets(N);

		// Set up the MAD compression and secondary hash parameters
		updateHashParams();

		/*
		 * ADD MORE CODE IF NEEDED
		 * 
		 */
	}

	/*
	 * ADD MORE METHODS IF NEEDED
	 * 
	 */

	@Override
	public Value get(Key k) {
		// if the key is equal to nothing just return null
		if (k == null) {
			return null;
		} else {// if it is not equal to null
			if (buckets[hashValue(k)] != null) {
				int size = buckets[hashValue(k)].size();
				// iterate over the size, because in Separate Chaining in the one index there
				// can be more than 1 key value in one index.
				for (int i = 0; i < size; i++) {
					// If we get the key then just return the value.ttttttttt
					if (buckets[hashValue(k)].get(i).getKey().equals(k)) {
						return buckets[hashValue(k)].get(i).getValue();
					}
				}
			}
		}
		return null;
	}

	@Override
	public Value put(Key k, Value v) {
		// Before checking the conditions I have created the entry that we are going to
		// put.
		HashEntry<Key, Value> newEntry = new HashEntry<Key, Value>(k, v);
		// if the key is equal to nothing just return null
		if (k == null) {
			return null;
		} else {// if it is equal to null
			if (buckets[hashValue(k)] == null) {
				// For Separate Chaining we have to create a list for using list as a container
				// of the entries.
				LinkedList<HashEntry<Key, Value>> list;
				list = new LinkedList<HashEntry<Key, Value>>();
				// add the entry that we created above to the list.
				list.add(newEntry);
				buckets[hashValue(k)] = list;
				n++;
				return null;
			} else {// if it is not equal to null.
				int size = buckets[hashValue(k)].size();
				// We have to iterate till we are going to reach the entry that we are looking
				// for.
				for (int i = 0; i < size; i++) {
					// If it is the searched entry.
					if (buckets[hashValue(k)].get(i).getKey().equals(k)) {
						// get the value and set it to Value v.
						Value value = get(k);
						buckets[hashValue(k)].get(i).setValue(v);
						return value;
					}
				}
				buckets[hashValue(k)].add(newEntry);
				this.n++;
				return null;
			}
		}
	}

	@Override
	public Value remove(Key k) {
		// if the key is equal to nothing just return null
		if (k == null) {
			return null;
		} else {// This loop iterates with the size of the capacity, so that we can reach all
			// the elements with one by one.
			for (int i = 0; i < N; i++) {
				if (buckets[hashValue(k, i)] == null) {
					break;// Checking the conditions for reaching the searched Key k.
				} else if (buckets[hashValue(k)].get(i).getKey().equals(k)) {
					Value oldVal = buckets[hashValue(k)].get(i).getValue();
					buckets[hashValue(k)].remove(i);
					n--;
					return oldVal;
				}
			}
			return null;
		}
	}

	@Override
	public Iterable<Key> keySet() {
		int size = buckets.length;
		// For storing Keys I have used LinkedList to store all of the keys.
		HashSet<Key> iterableSet = new HashSet<Key>();
		// For reaching the all of the keys in the hasEntry we just iterate it.
		for (int i = 0; i < size; i++) {
			if (buckets[i] != null) {
				for (HashEntry<Key, Value> hE : buckets[i]) {
					iterableSet.add(hE.getKey());
				}
			}
		}
		return iterableSet;
	}

	/**
	 * checkAndResize checks whether the current load factor is greater than the
	 * specified critical load factor. If it is, the table size should be increased
	 * to 2*N and recreate the hash table for the keys (rehashing). Do not forget to
	 * re-calculate the hash parameters and do not forget to re-populate the new
	 * array!
	 */
	protected void checkAndResize() {
		if (loadFactor() > criticalLoadFactor) {
			if (loadFactor() > criticalLoadFactor) {
				// For recreating the hash table first we should get the copy of the buckets.
				LinkedList<HashEntry<Key, Value>>[] copy = buckets;
				// the table size increased to 2*N
				resizeBuckets(2 * N);
				updateHashParams();
				// current size of the hash table set to 0.
				n = 0;
				// This loop iterates with the size of capacity.
				for (int i = 0; i < N; i++) {
					// this condition check it out that if the copied buckets are not equal to null
					// just re populate it with putting the current key into the current value
					if (copy[i] != null) {
						for (HashEntry<Key, Value> element : copy[i]) {
							put(element.getKey(), element.getValue());
						}
					}
				}
			}
		}
	}
}