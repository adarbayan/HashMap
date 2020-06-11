package code;

import java.lang.reflect.Array;
import java.util.*;
import java.util.HashSet;
import given.AbstractHashMap;
import given.HashEntry;
import given.iPrintable;

/*
 * The file should contain the implementation of a hashmap with:
 * - Open addressing for collision handling
 * - Double hashing for probing. The double hash function should be of the form: q - (k mod q)
 * - Multiply-Add-Divide (MAD) for compression: (a*k+b) mod p
 * - Resizing (to double its size) and rehashing when the load factor gets above a threshold
 * 
 * Some helper functions are provided to you. We suggest that you go over them.
 * 
 * You are not allowed to use any existing java data structures other than for the keyset method
 */

public class HashMapDH<Key, Value> extends AbstractHashMap<Key, Value> {

	// The underlying array to hold hash entries (see the HashEntry class)
	private HashEntry<Key, Value>[] buckets;
	private HashEntry<Key, Value> DEFUNCT = new HashEntry<Key, Value>(null, null);

	@SuppressWarnings("unchecked")
	protected void resizeBuckets(int newSize) {
		// Update the capacity
		N = nextPrime(newSize);
		buckets = (HashEntry<Key, Value>[]) Array.newInstance(HashEntry.class, N);
	}

	// The threshold of the load factor for resizing
	protected float criticalLoadFactor;

	// The prime number for the secondary hash
	int dhP;

	/*
	 * ADD MORE FIELDS IF NEEDED
	 * 
	 */

	/*
	 * ADD A NESTED CLASS IF NEEDED
	 * 
	 */

	// Default constructor
	public HashMapDH() {
		this(101);
	}

	public HashMapDH(int initSize) {
		this(initSize, 0.6f);
	}

	public HashMapDH(int initSize, float criticalAlpha) {
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

	/**
	 * Calculates the hash value by compressing the given hashcode. Note that you
	 * need to use the Multiple-Add-Divide method. The class variables "a" is the
	 * scale, "b" is the shift, "mainP" is the prime which are calculated for you.
	 * Do not include the size of the array here
	 * 
	 * Make sure to include the absolute value since there maybe integer overflow!
	 */
	// this method calculates the h1(a) with using modulo as described in the
	// slides.
	protected int primaryHash(int hashCode) {
		// TODO: Implement MAD compression given the hash code, should be 1 line
		return Math.abs(((this.a * hashCode + this.b) % this.P));
	}

	/**
	 * The secondary hash function. Remember you need to use "dhP" here!
	 * 
	 */
	// this method calculates the h2(a) with using modulo as described in the
	// slides.
	protected int secondaryHash(int hashCode) {
		return Math.abs(this.dhP - (hashCode % this.dhP));
	}
	// this method calculates the hashValue of a key.
	@Override
	public int hashValue(Key key, int iter) {
		return Math.abs(primaryHash(Math.abs(key.hashCode())) + iter * secondaryHash(Math.abs(key.hashCode()))) % N;
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
			int clone = N;
			// For recreating the hash table first we should get the copy of the buckets.
			HashEntry<Key, Value>[] buckets1 = buckets;
			// the table size increased to 2*N
			resizeBuckets(2 * N);
			// current size of the hash table set to 0.
			n = 0;
			// This loop iterates with the size of capacity.
			for (int i = 0; i < clone; i++) {
				// this condition check it out that if the copied buckets are not equal to null
				// and the current key is not equal to null,
				// just re populate it with putting the current key into the current value
				if (buckets1[i] != null && buckets1[i].getKey() != null) {
					put(buckets1[i].getKey(), buckets1[i].getValue());
				}
			}
		}
	}
	@Override
	public Value get(Key k) {
		// if the key is equal to nothing just return null
		if (k == null) {
			return null;
		} else {
			// This loop iterates with the size of the capacity, so that we can reach all
			// the elements with one by one.
			for (int i = 0; i < N; i++) {
				// if it is null just break it down because we don't want to get the null
				// element.
				if (buckets[hashValue(k, i)] == null) {
					break;
					// While iterating the elements if the current element is not equal to null, and
					// if it is equal to the key as we searching just return that value.
				} else if (buckets[hashValue(k, i)].getKey() != null && buckets[hashValue(k, i)].getKey().equals(k)) {
					return buckets[hashValue(k, i)].getValue();
				}
			}
			return null;
		}
	}
	@Override
	public Value put(Key k, Value v) {
		// if the key is equal to nothing just return null
		if (k == null) {
			return null;
		} else {
			// This loop iterates with the size of the capacity, so that we can reach all
			// the elements with one by one.
			for (int i = 0; i < N; i++) {
				// if it is null just break it down because we don't want to get the null
				// element.
				if (buckets[hashValue(k, i)] == null) {
					break;
					// While iterating the elements if the current element is not equal to null, and
					// if it is equal to the key as we searching we should create the new Entry with
					// Key k and value v.
					// Then we should get the old Value and return it.
				} else if (buckets[hashValue(k, i)].getKey() != null && buckets[hashValue(k, i)].getKey().equals(k)) {
					buckets[hashValue(k, i)] = new HashEntry<>(k, v);
					Value oldVal = buckets[hashValue(k, i)].getValue();
					return oldVal;
				}
			}
			// If the buckets[hashValue(k, i)] == null then we need to put a new Entry so we
			// just repeating creating a new Entry and put it.
			checkAndResize();
			n++;
			for (int i = 0; i < N; i++) {
				if (buckets[hashValue(k, i)] == null || buckets[hashValue(k, i)].getKey() == null) {
					buckets[hashValue(k, i)] = new HashEntry<>(k, v);
					break;
				}
			}
			return null;
		}
	}
	// For the method remove first we should iterate all of the elements and if we
	// get the element as we did in the get Method then just set their Key and Value
	// as null and return the value back at it.
	@Override
	public Value remove(Key k) {
		// if the key is equal to nothing just return null
		if (k == null) {
			return null;
		} else {
			// This loop iterates with the size of the capacity, so that we can reach all
			// the elements with one by one.
			for (int i = 0; i < N; i++) {
				if (buckets[hashValue(k, i)] == null) {
					break;
				} else if (buckets[hashValue(k, i)].getKey() != null && buckets[hashValue(k, i)].getKey().equals(k)) {
					Value oldVal = buckets[hashValue(k, i)].getValue();
					buckets[hashValue(k, i)].setKey(null);
					buckets[hashValue(k, i)].setValue(null);
					n--;
					return oldVal;
				}
			}
			return null;
		}
	}
	// This is the only function you are allowed to use an existing Java data
	// structure!
	@Override
	public Iterable<Key> keySet() {
		int size = buckets.length;
		//For storing Keys I have used LinkedList to store all of the keys. 
		HashSet<Key> keySet = new HashSet<Key>();
		//For reaching the all of the keys in the hasEntry we just iterate it.
		for (int i = 0; i < size; i++) {
			HashEntry<Key, Value> e = buckets[i];
			if (e != null && e.getKey() != null) {
				keySet.add(e.getKey());
			}
		}
		return keySet;
	}
	@Override
	protected void updateHashParams() {
		super.updateHashParams();
		dhP = nextPrime(N / 2);
	}
}