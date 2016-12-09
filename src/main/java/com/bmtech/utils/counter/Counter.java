package com.bmtech.utils.counter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

public class Counter<T> {
	protected HashMap<T, NumCount>map = new HashMap<T, NumCount>();

	public Counter(){

	}

	public int count(T key){
		return this.count(map, key, 1);
	}
	public int count(T key, int num){
		return this.count(map, key, num);
	}
	public int count(T key, NumCount num){
		return this.count(map, key, num.intValue());
	}
	public void count(HashMap<T, NumCount>map,T key, NumCount num){
		count(map, key, num.intValue());
	}
	public int count(HashMap<T, NumCount>map,T key, int num){
		NumCount cnt = map.get(key);
		if(cnt == null){
			map.put(key, new NumCount(num));
			return num;
		}else{
			cnt.increaseCount(num);
			return num + cnt.intValue();
		}
	}
	public List<Entry<T, NumCount>> topEntry(int num){
		return this.topEntry(map, num);
	}
	
	public List<T>topObjects(int num){
		List<Entry<T, NumCount>> lst = this.topEntry(map, num);
		List<T> l = new ArrayList<T>(lst.size());
		for(Entry<T, NumCount> e : lst ) {
			l.add(e.getKey());
		}
		return l;
	}
	public List<Entry<T, NumCount>> topEntry(HashMap<T, NumCount> map, int num){
		ArrayList<Entry<T, NumCount>> lst =new ArrayList<Entry<T, NumCount>>();
		lst.addAll(map.entrySet());

		Collections.sort(lst,new Comparator<Entry<T, NumCount>>(){
			@Override
			public int compare(Entry<T, NumCount> o1,
					Entry<T, NumCount> o2) {
				return o2.getValue().intValue() - o1.getValue().intValue();
			}
		}
		);
		int range = lst.size() > num ? num : lst.size();

		return 	lst.subList(0, range);
	}
	
	public Map<T, NumCount>getMap(){
		return this.map;
	}

	public String toString() {
		return this.map.toString();
	}
}
