package multiset;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;


/**
 * <p>A MultiSet models a data structure containing elements along with their frequency count i.e., </p>
 * <p>the number of times an element is present in the set.</p>
 * <p>HashMultiSet is a Map-based concrete implementation of the MultiSet concept.</p>
 * 
 * <p>MultiSet a = <{1:2}, {2:2}, {3:4}, {10:1}></p>
 * */
public final class HashMultiSet<T, V extends Number> {

	/**
	 *XXX: data structure backing this MultiSet implementation. 
	 */
	private HashMap<T,V> multiSetMap;
	/**
	 * Sole constructor of the class.
	 **/
	public HashMultiSet() {
		multiSetMap=new HashMap<T,V>();
	}
	
	
	/**
	 * If not present, adds the element to the data structure, otherwise 
	 * simply increments its frequency.
	 * 
	 * @param t T: element to include in the multiset
	 * 
	 * @return V: frequency count of the element in the multiset
	 * */	
	public V addElement(T t) {
		Integer i=1;
	V freq=multiSetMap.putIfAbsent(t,(V)i);

	if(freq==null)
		return (V)i;

	Integer nV=(Integer)multiSetMap.get(t)+i;
		multiSetMap.replace(t,(V)nV);

		return freq;
	}

	/**
	 * Check whether the elements is present in the multiset.
	 * 
	 * @param t T: element
	 * 
	 * @return V: true if the element is present, false otherwise.
	 * */	
	public boolean isPresent(T t) {
		return multiSetMap.containsKey(t);
	}
	
	/**
	 * @param t T: element
	 * @return V: frequency count of parameter t ('0' if not present)
	 * */
	public V getElementFrequency(T t) {
	V dV= (V)(Integer)0;
	V value=multiSetMap.get(t);
	return value.equals(null)?  dV: value;
	}
	
	
	/**
	 * Builds a multiset from a source data file. The source data file contains
	 * a number comma separated elements. 
	 * Example_1: ab,ab,ba,ba,ac,ac -->  <{ab:2},{ba:2},{ac:2}>
	 * Example 2: 1,2,4,3,1,3,4,7 --> <{1:2},{2:1},{3:2},{4:2},{7:1}>
	 * 
	 * @param source Path: source of the multiset
	 * */
	public void buildFromFile(Path source) throws IOException {
		try {
			BufferedReader reader = new BufferedReader(new FileReader(source.toFile()));

			String line = reader.readLine();
			while (line != null) {
				String[] elements = line.split(",");
				for (String element : elements) {
					addElement((T) element);
					System.out.print("	Key: " + element + " Type: " + element.getClass().toString() + ", Value: " + getElementFrequency((T) element) + " Type: " + getElementFrequency((T) element).getClass().toString());
				}
				line = reader.readLine();
			}
		} catch (IOException e) {
			throw new IOException("Method should be invoked with a non null file path");
		}
	}

	/**
	 * Same as before with the difference being the source type.
	 * @param source List<T>: source of the multiset
	 * */
	public void buildFromCollection(List<? extends T> source) {

		if(source!=null) {
			source.stream().forEach(element -> {
				addElement(element);
				System.out.print("	Key: " + element + " Type: " + element.getClass().toString() + ", Value: " + getElementFrequency((T) element) + " Type: " + getElementFrequency((T) element).getClass().toString());
			});
		}else{
			throw new IllegalArgumentException("Method should be invoked with a non null file path");
		}
	}
	
	/**
	 * Produces a linearized, unordered version of the MultiSet data structure.
	 * Example: <{1:2},{2:1}, {3:3}> -> 1 1 2 3 3 3 3
	 * 
	 * @return List<T>: linearized version of the multiset represented by this object.
	 */
	public List<T> linearize() {

		ArrayList<T> linearized=new ArrayList<T>();

		multiSetMap.forEach((k,v)->{
			for (int i=0; i<(Integer)v; i++){
				linearized.add(k);
			}
		});

		return  linearized;
	}
	
	
}
