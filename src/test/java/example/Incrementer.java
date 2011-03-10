package example;

import java.util.Iterator;

import org.stringtree.Fetcher;
import org.stringtree.fetcher.LiveObjectWrapper;
import org.stringtree.util.IntegerNumberUtils;
import org.stringtree.util.Proxy;

public class Incrementer implements Fetcher, Proxy<Integer>, LiveObjectWrapper, Iterator<Integer>, Iterable<Integer> {
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

	@Override
	public Object getObject(String name) {
		return next();
	}

	@Override
	public Integer getValue() {
		return next();
	}

	@Override
	public Object getObject() {
		return next();
	}

	@Override
	public Object getRaw() {
		return this;
	}
	
	@Override
	public String toString() {
		return "" + next();
	}

	@Override
	public Integer next() {
		return current++;
	}

	@Override
	public boolean hasNext() {
		return true;
	}

	@Override
	public void remove() {
		throw new UnsupportedOperationException("can't remove from the set of all integers");
	}

	@Override
	public Iterator<Integer> iterator() {
		return this;
	}
}