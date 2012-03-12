package example;

import java.util.Iterator;
import java.util.Map;

import org.rack4java.context.ContextEntry;
import org.stringtree.context.ReadOnlyContext;
import org.stringtree.util.IntegerNumberUtils;

public class Incrementer extends ReadOnlyContext<Integer> implements Iterator<Map.Entry<String,Object>> {
	private int current;

	public Incrementer(int start) {
		this.current = start;
	}

	public Incrementer() {
		this(1);
	}

	public Incrementer(String start) {
		this(IntegerNumberUtils.intValue(start, 1));
	}

	@Override public Integer get(String name) {
		Integer next = nextValue();
System.err.println("Integrator.get returning " + next);
		return next;
	}

	@Override public Object getObject(String key) {
		Integer next = nextValue();
System.err.println("Integrator.getObject returning " + next);
		return next;
	}
	
	@Override public String toString() {
		return Integer.toString(nextValue());
	}

	private Integer nextValue() {
		return current++;
	}

	@Override public Map.Entry<String,Object> next() {
		return new ContextEntry<Object>("value", nextValue());
	}

	@Override public boolean hasNext() {
		return true;
	}

	@Override public void remove() {
		throw new UnsupportedOperationException("can't remove from the set of all integers");
	}

	@Override public Iterator<Map.Entry<String,Object>> iterator() {
		return this;
	}
}
