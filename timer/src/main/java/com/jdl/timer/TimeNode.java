package com.jdl.timer;

public class TimeNode{
	
	final int nSeconds;
	
	TimeNode nextTimeNode;
	
	Executor headExecutor  = null;
	
	Executor tailExecutor = null;
	
	TimeNode(int nSeconds) {
		this.nSeconds = nSeconds;
	}
	
	public synchronized void add(Executor executor) {
		if (headExecutor == null) {
			headExecutor = executor;
			tailExecutor = executor;
		} else {
			tailExecutor.nextExecutor = executor;
			tailExecutor = executor;
		}
	}
	
}
