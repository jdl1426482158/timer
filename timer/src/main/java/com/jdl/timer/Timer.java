package com.jdl.timer;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.jdl.curr.statistic.Counter;

public class Timer {
	private static volatile boolean status = true;
	
	private static TimeHead head = new TimeHead();
	
	private static ExecutorService service = Executors.newCachedThreadPool();
	
	static {
		new Thread(new Runnable() {
			@Override
			public void run() {
				while(status) {
					Counter.outValue();
					try {
						Thread.sleep(1000);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					TimeNode timeNode = head.getExpiredTimeNode();
					
					if (timeNode != null) {
						Executor executor = timeNode.headExecutor;
						while (executor != null) {
							service.execute(executor);
							executor = executor.nextExecutor;
						}
					}
				}
				service.shutdown();
			}
		}).start();
	}
	
	public static void add(Executor executor) {
		head.add(executor);
	}
	
	public static void close() {
		status = false;
	}
	
}
