package com.jdl.timer;

import java.util.Map.Entry;
import java.util.TreeMap;
import java.util.concurrent.locks.ReentrantReadWriteLock;

class TimeHead {
	
	
	private TreeMap<Integer, TimeNode> timeNodeMap;
	
	private ReentrantReadWriteLock headLock;
	
	private volatile int nSeconds;
	
	TimeHead(){
		nSeconds = Integer.MAX_VALUE;
		timeNodeMap = new TreeMap<Integer, TimeNode>();
		headLock = new ReentrantReadWriteLock();
	}
	
	synchronized void add(Executor executor) {
		TimeNode node = timeNodeMap.get(executor.nSeconds);
		if(node == null) {
			node = new TimeNode(executor.nSeconds);
			node.add(executor);
			timeNodeMap.put(executor.nSeconds, node);
		} else {
			node.add(executor);
		}
		
		if(nSeconds > executor.nSeconds) {
			headLock.writeLock().lock();
			nSeconds = executor.nSeconds;
			headLock.writeLock().unlock();
		}
	}
	
	TimeNode getExpiredTimeNode() {
		int nSeconds = (int)(System.currentTimeMillis() / 1000);
		TimeNode node = null;
		headLock.readLock().lock();
		if (nSeconds > this.nSeconds) {
			node = timeNodeMap.remove(this.nSeconds);
			Entry<Integer, TimeNode> temp = timeNodeMap.firstEntry();
			if (temp == null) {
				this.nSeconds = Integer.MAX_VALUE;
			} else {
				this.nSeconds = timeNodeMap.firstEntry().getKey();
			}
		}
		headLock.readLock().unlock();
		return node;
	}
	
	public int size() {
		return timeNodeMap.size();
	}
	
}
