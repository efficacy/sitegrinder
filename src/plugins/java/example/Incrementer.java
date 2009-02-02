package example;

import org.stringtree.util.IntegerNumberUtils;

public class Incrementer {
	int value;
	
	public Incrementer(String start) {
		value = IntegerNumberUtils.intValue(start, 1);
	}
	
	public String toString() {
		return Integer.toString(value++);
	}
}
