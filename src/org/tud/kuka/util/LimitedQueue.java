package org.tud.kuka.util;

import java.util.LinkedList;

public class LimitedQueue<E> extends LinkedList<E> {

    /**
	 * 
	 */
	private static final long serialVersionUID = 634647665828618541L;
	
	private int limit;

    public LimitedQueue(int limit) {
        this.limit = limit;
    }

    @Override
    public boolean add(E o) {
        boolean added = super.add(o);
        while (added && size() > limit) {
           super.remove();
        }
        return added;
    }
}