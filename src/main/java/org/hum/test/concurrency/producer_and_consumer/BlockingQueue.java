package org.hum.test.concurrency.producer_and_consumer;

import java.util.LinkedList;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class BlockingQueue<T> {

	private LinkedList<T> queue = new LinkedList<>();
	private Lock Lock = new ReentrantLock();
	private Condition Cond1 = Lock.newCondition();
	private Condition Cond2 = Lock.newCondition();

	public void offer(T data) throws InterruptedException {
		Lock.lock();
		try {
			if (queue.size() >= 5) {
				Cond2.await();
			}
			queue.offer(data);
			Cond1.signalAll();
		} finally {
			Lock.unlock();
		}
	}

	public T poll() throws InterruptedException {
		Lock.lock();
		try {
			while (queue.isEmpty()) {
				Cond1.await();
			}
			T data = queue.poll();
			Cond2.signalAll();
			return data;
		} finally {
			Lock.unlock();
		}
	}
}
