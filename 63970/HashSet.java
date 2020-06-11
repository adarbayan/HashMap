package code;

import given.AbstractHashMap;
import given.iPrintable;
import given.iSet;

/*
 * A set class implemented with hashing. Note that there is no "value" here 
 * 
 * You are free to implement this however you want. Two potential ideas:
 * 
 * - Use a hashmap you have implemented with a dummy value class that does not take too much space
 * OR
 * - Re-implement the methods but tailor/optimize them for set operations
 * 
 * You are not allowed to use any existing java data structures
 * 
 */

public class HashSet<Key> implements iSet<Key>, iPrintable<Key>{

  //private HashSet<Key>[] buckets;
  AbstractHashMap<Key,Integer> notTakeTooMuchSpace;
  int size = 0;
  // A default public constructor is mandatory!
  public HashSet() {
   notTakeTooMuchSpace = new HashMapDH<>();

  }
  /*
   * 
   * Add whatever you want!
   * 
   */

  @Override
  public int size() {
    return notTakeTooMuchSpace.size();
  }

  @Override
  public boolean isEmpty() {
	    if(notTakeTooMuchSpace.size() == 0) {
    		return true;
    } else {
    		return false;
    }
  }

  @Override
  public boolean contains(Key k) {
    return notTakeTooMuchSpace.get(k)!=null;
  }

  @Override
  public boolean put(Key k) {
	  if(notTakeTooMuchSpace.put(k, 1) != null) {
			return true;
		} else {
			return false;
		}
  }

  @Override
  public boolean remove(Key k) {
	  if(notTakeTooMuchSpace.get(k) != null) {
		  notTakeTooMuchSpace.remove(k);
  		return true; 
  } else {
  		return false;
  }
  }

  @Override
  public Iterable<Key> keySet() {
    // TODO Auto-generated method stub
    return notTakeTooMuchSpace.keySet();
  }

  @Override
  public Object get(Key key) {
    // Do not touch
    return null;
  }

}