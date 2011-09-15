package example;

import java.util.Iterator;

import org.stringtree.Context;
import org.stringtree.Proxy;
import org.stringtree.util.IntegerNumberUtils;

public class Incrementer implements Context<Object>, Proxy<Integer>, LiveObjectWrapper, Iterator<Integer>, Iterable<Integer> {
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

	@Override public Object get(String name) {
		Integer next = next();
System.err.println("Integrator.getObject returning " + next);
		return next;
	}

	@Override public Integer getValue() {
		Integer next = next();
System.err.println("Integrator.getValue returning " + next);
		return next;
	}

	@Override public Object getObject() {
		Integer next = next();
System.err.println("Integrator.getObject returning " + next);
		return next;
	}

	@Override public Object getRaw() {
		return this;
	}
	
	@Override public String toString() {
		return "" + next();
	}

	@Override public Integer next() {
		return current++;
	}

	@Override public boolean hasNext() {
		return true;
	}

	@Override public void remove() {
		throw new UnsupportedOperationException("can't remove from the set of all integers");
	}

	@Override public Iterator<Integer> iterator() {
		return this;
	}
}
