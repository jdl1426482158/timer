package com.jdl.time;

public interface TaskCondiction {
	
	// 表示任务是否需要再一次被执行
	public boolean next();
	
	public static class DefaultTaskCondiction implements TaskCondiction{
		public boolean next() {
			
			return true;
			
		}
	}
}
