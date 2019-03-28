package com.jdl.time;

public abstract class Executor implements Runnable {
	
	protected Object[] param = null;
	
	public Executor(Object[] param) {
		this.param = param;
	}
	
}
