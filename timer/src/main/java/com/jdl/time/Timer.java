package com.jdl.time;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

// 以秒为基本单位
public class Timer {

	public final static long SECOND = 1000;
	
	public final static long MINUTE = 60000;

	public final static long HOUR = 3600000;

	public final static long DAY = 24 * 60 * 60 * 1000;

	private static class Singleton {
		public final static Timer timer = new Timer();
	}

	// 只是用来计时任务用
	private class TimerImpl extends Thread {

		// 当前时间于下一秒的差值，用来等待的时间
		private long differ = 1000;

		public void run() {
			long now;

			while (bStatus) {
				now = System.currentTimeMillis();
				differ = 1000 - (now % 1000);
				try {
					Thread.sleep(differ);
				} catch (InterruptedException e) {
					continue;
				}
				// 执行任务
				executeTask(now);
			}
		}
	}

	// 获取实例
	public static Timer getInstance() {
		return Singleton.timer;
	}

	private TimerImpl timerImpl;

	// 计时器状态器
	private volatile boolean bStatus;

	// 当前分钟的任务，每秒遍历
	private List<Task> secondTask;
	
	// 当前小时的任务，每分钟遍历
	private List<Task> minuteTask;
	
	// 当前日期的任务，每小时遍历
	private List<Task> hourTask;
	
	// 长时间任务，每天遍历
	private List<Task> dayTask;

	private long nNextMinute;
	private long nNextHour;
	private long nNextDay;

	// 单例模式，并开始执行计时器
	private Timer() {
		bStatus = true;
		secondTask = new LinkedList<Task>();
		minuteTask = new LinkedList<Task>();
		hourTask = new LinkedList<Task>();
		dayTask = new LinkedList<Task>();

		long nNow = System.currentTimeMillis();
		nNextMinute = nNow - (nNow % MINUTE) + MINUTE;
		nNextHour = nNow - (nNow % HOUR) + HOUR;
		nNextDay = nNow - (nNow % DAY) + DAY;

		timerImpl = new TimerImpl();
		timerImpl.start();
	}

	public synchronized void addTask(Task task) {
		long nTime = task.getTaskTime();

		if (nTime < nNextMinute) {
			secondTask.add(task);
		} else if (nTime < nNextHour) {
			minuteTask.add(task);
		} else if (nTime < nNextDay) {
			hourTask.add(task);
		} else {
			dayTask.add(task);
		}
	}

	public synchronized void removeAllTask() {
		secondTask.clear();
		minuteTask.clear();
		hourTask.clear();
		dayTask.clear();
	}

	public synchronized void removeTask(Task task) {

		for (int i = 0; i < secondTask.size(); i++) {
			if (task == secondTask.get(i)) {
				secondTask.remove(i);
				return;
			}
		}

		for (int i = 0; i < minuteTask.size(); i++) {
			if (task == minuteTask.get(i)) {
				minuteTask.remove(i);
				return;
			}
		}

		for (int i = 0; i < hourTask.size(); i++) {
			if (task == hourTask.get(i)) {
				hourTask.remove(i);
				return;
			}
		}

		for (int i = 0; i < dayTask.size(); i++) {
			if (task == dayTask.get(i)) {
				dayTask.remove(i);
				return;
			}
		}
	}

	public synchronized List<Task> getAllTask(){
		List<Task> taskList = new ArrayList<Task>();
		for (int i = 0; i < secondTask.size(); i++) {
			taskList.add(secondTask.get(i));
		}

		for (int i = 0; i < minuteTask.size(); i++) {
			taskList.add(minuteTask.get(i));
		}

		for (int i = 0; i < hourTask.size(); i++) {
			taskList.add(hourTask.get(i));
		}

		for (int i = 0; i < dayTask.size(); i++) {
			taskList.add(dayTask.get(i));
		}
		
		return taskList;
	}
	
	private synchronized void executeTask(long time) {

		// 时间过界限的时候更新任务分布;
		if (time >= nNextDay) {
			updateDayTask(time);
			nNextDay += DAY;
			nNextHour += HOUR;
			nNextMinute += MINUTE;
		} else if (time >= nNextHour) {
			updateHourTask(time);
			nNextHour += HOUR;
			nNextMinute += MINUTE;
		} else if (time >= nNextMinute) {
			updateMinuteTask(time);
			nNextMinute += MINUTE;
		}

		Task task = null;
		// 遍历所有秒级任务,删除不需要再被执行的任务
		for (int i = 0; i < secondTask.size(); i++) {
			task = secondTask.get(i);
			
			if (task.getTaskTime() <= time) {
				if (!task.start()) {
					secondTask.remove(i);
					i--;
				}
			}
		}
	}

	private void updateDayTask(long nTime) {
		for (int i = 0; i < dayTask.size(); i++) {
			if (dayTask.get(i).getTaskTime() <= nTime) {
				secondTask.add(dayTask.remove(i));
				i--;
			}
		}
		updateHourTask(nTime);
	}

	private void updateHourTask(long nTime) {
		for (int i = 0; i < hourTask.size(); i++) {
			if (hourTask.get(i).getTaskTime() <= nTime) {
				secondTask.add(hourTask.remove(i));
				i--;
			}
		}
		updateMinuteTask(nTime);
	}

	private void updateMinuteTask(long nTime) {
		for (int i = 0; i < minuteTask.size(); i++) {
			if (minuteTask.get(i).getTaskTime() <= nTime) {
				secondTask.add(minuteTask.remove(i));
				i--;
			}
		}
	}

	public void stop() {
		bStatus = false;
	}
}
