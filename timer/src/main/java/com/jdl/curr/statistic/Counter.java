package com.jdl.curr.statistic;

public class Counter {
	
	public static int nCount = 0;
	
	public synchronized static void add() {
		nCount ++;
	}
	
	public synchronized static void outValue() {
		System.out.println(nCount);
	}
	
}
