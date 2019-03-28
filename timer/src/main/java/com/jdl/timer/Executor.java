package com.jdl.timer;

public abstract class Executor implements Runnable {
	
	public final int nSeconds;
	
	protected Object[] parameters;
	
	Executor nextExecutor = null;
	
	public Executor(long nMilliseconds) {
		parameters = new Object[0];
		this.nSeconds = (int)(nMilliseconds / 1000);
	}
	
	public Executor(long nMilliseconds,Object[] parameters) {
		this.parameters = parameters;
		this.nSeconds = (int)(nMilliseconds / 1000);
	}
	
}
