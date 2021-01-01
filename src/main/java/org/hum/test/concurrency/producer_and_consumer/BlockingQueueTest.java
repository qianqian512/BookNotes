package org.hum.test.concurrency.producer_and_consumer;

import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class BlockingQueueTest {
	
	private static final ExecutorService ExecutorService = Executors.newFixedThreadPool(2);

	public static void main(String[] args) throws IOException {
		BlockingQueue<Integer> queue = new BlockingQueue<>();
		
		// producer
		ExecutorService.execute(() -> {
			for (int i = 0;; i++) {
				try {
					queue.offer(i);
					System.out.println("put " + i);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		});
		
		// consumer
		ExecutorService.execute(() -> {
			while (true) {
				try {
					System.out.println("poll " + queue.poll());
					Thread.sleep(1000L);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		});
		
		System.in.read();
	}
}
