package com.jdl.test;

import com.jdl.curr.statistic.Counter;
import com.jdl.timer.Executor;
import com.jdl.timer.Timer;

public class Main {
	public static void main(String[] args) {
		int i = 0;
		while (i < 1000000) {
			Timer.add(new Executor(System.currentTimeMillis() + 1 * 1000,new Object[] {i}) {
				@Override
				public void run() {
					Counter.add();
				}
			});
			i++;
		}

		try {
			Thread.sleep(10 * 1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		Timer.close();
	}
}
