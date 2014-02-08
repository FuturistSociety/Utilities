/**
 * @author Steven L. Moxley
 * @version 1.0
 */
package org.futurist.util.jobs;

import java.sql.Date;
import java.util.AbstractQueue;
import java.util.Iterator;
import java.util.concurrent.ConcurrentHashMap;

public class DeadlineQueue<E> extends AbstractQueue<E> {

	public static final int defaultTime = 24*60*60*1000;	// 1 day, the default time from now by which an object must have been removed from the DeadlineQueue 

	private int ttl;
	private ConcurrentHashMap<Date, E> queue;

	/**
	 * Default constructor that uses the default expiration time of 1 day for new items added to the queue.
	 */
	public DeadlineQueue() {
		ttl = defaultTime;
		queue = new ConcurrentHashMap<Date, E>();
	}
	
	/**
	 * Constructor that uses the given expiration time for new items added to the queue.
	 * @param expiration the expiration time in seconds.
	 */
	public DeadlineQueue(int expiration) {
		ttl = expiration;
		queue = new ConcurrentHashMap<Date, E>();
	}
	
	@Override
	public boolean offer(E e) {
		Date deadline = new Date(System.currentTimeMillis() + ttl);
		return offer(deadline, e);
	}
	
	public boolean offer(Date deadline, E e) {
		queue.put(deadline, e);
		if(queue.containsValue(e)) {
			return true;
		} else {
			return false;
		}
	}

	@Override
	public E peek() {
		Date soonest = new Date(0);
		for(Date d : queue.keySet()) {
			if(d.after(soonest)) {
				soonest = d;
			}
		}
		//System.out.println("The next deadline in the queue is " + soonest.toString());
		return queue.get(soonest);
	}

	@Override
	public E poll() {
		E soonest = peek();
		queue.remove(soonest);
		return soonest;
	}

	@Override
	public Iterator<E> iterator() {
		return queue.values().iterator();
	}

	@Override
	public int size() {
		return queue.size();
	}

}
