package com.jdl.time;

import java.util.Date;
import java.util.Objects;

public class Task {

	private Runnable task;

	// 任务被执行日期
	private Date taskDate;

	// 任务名字
	private String sName;

	// 任务间隔时间
	private long nInterval;

	// 负数表示无限次
	private int ntimes;

	private TaskCondiction condition;

	// 执行一次即结束
	public Task(Runnable task, String sTaskName, Date date) {
		Objects.requireNonNull(task, "任务不能为空");
		this.task = task;
		taskDate = (Date) date.clone();
		sName = sTaskName;
		nInterval = 0;
		ntimes = 1;
		condition = null;
	}

	// 无限此循环执行任务，除非主动终止
	public Task(Runnable task, long nInterval, String nTaskName, Date date) {
		Objects.requireNonNull(task, "任务不能为空");
		this.task = task;
		taskDate = (Date) date.clone();
		sName = nTaskName;
		if (nInterval <= 100) {
			this.nInterval = 1000;
		} else {
			this.nInterval = nInterval;
		}
		ntimes = -1;
		condition = new TaskCondiction.DefaultTaskCondiction();
	}

	// 循环执行times次任务
	public Task(Runnable task, long nInterval, int nTimes, String sTaskName, Date date) {
		Objects.requireNonNull(task, "任务不能为空");
		this.task = task;
		taskDate = (Date) date.clone();
		sName = sTaskName;
		if (nInterval <= 100) {
			this.nInterval = 1000;
		} else {
			this.nInterval = nInterval;
		}
		if (nTimes < 1) {
			this.ntimes = 1;
		} else {
			this.ntimes = nTimes;
		}
		condition = null;
	}

	public Task(Runnable task, long nInterval, TaskCondiction condiction, String sTaskName, Date date) {
		Objects.requireNonNull(task, "任务不能为空");
		this.task = task;
		taskDate = (Date) date.clone();
		sName = sTaskName;
		if (nInterval <= 100) {
			this.nInterval = 1000;
		} else {
			this.nInterval = nInterval;
		}
		this.ntimes = -1;
		if (condiction == null) {
			condition = new TaskCondiction.DefaultTaskCondiction();
		} else {
			this.condition = condiction;
		}
	}

	public Task(Runnable task, long nInterval, int nTimes, TaskCondiction condiction, String nTaskName, Date date) {
		Objects.requireNonNull(task, "任务不能为空");
		this.task = task;
		taskDate = (Date) date.clone();
		sName = nTaskName;
		if (nInterval <= 100) {
			this.nInterval = 1000;
		} else {
			this.nInterval = nInterval;
		}

		if (nTimes < 1) {
			this.ntimes = 1;
		} else {
			this.ntimes = nTimes;
		}
		if (condiction == null) {
			condition = new TaskCondiction.DefaultTaskCondiction();
		} else {
			this.condition = condiction;
		}
	}

	// 如果不许要再被执行则返回false，需要再次被执行则返回true;
	public synchronized boolean start() {

		taskDate = new Date(taskDate.getTime() + nInterval);

		if (condition == null) {
			// 无条件
			// 计数的情况
			ntimes--;
			new Thread(task).start();
			return ntimes > 0;
		} else {
			if (!condition.next()) {
				// 条件不满足的情况
				new Thread(task).start();
				return false;
			} else {
				if (ntimes < 0) {
					// 条件满足，且次数无限
					new Thread(task).start();
					return true;
				} else {
					// 条件满足, 且次数有限
					ntimes--;
					new Thread(task).start();
					return ntimes > 0;
				}
			}
		}
	}

	public long getTaskTime() {
		return taskDate.getTime();
	}

	public String getName() {
		return sName;
	}

}
