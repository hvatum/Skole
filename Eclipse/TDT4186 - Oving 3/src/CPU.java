public class CPU {
	private long maxCpuTime;
	private Queue cpuQueue;
	private Statistics statistics;

	/** The amount of memory in the memory device */
	private long memorySize;
	/** The amount of free memory in the memory device */
	private long freeMemory;
	private Process currentProccess;

	public CPU(Queue cpuQueue, long maxCpuTime, Statistics statistics) {
		this.cpuQueue = cpuQueue;
		this.maxCpuTime = maxCpuTime;
		this.statistics = statistics;
	}

	public void timePassed(long timePassed) {
		statistics.cpuQueueLengthTime += cpuQueue.getQueueLength() * timePassed;
		if (cpuQueue.getQueueLength() > statistics.memoryQueueLargestLength) {
			statistics.cpuQueueLargestLength = cpuQueue.getQueueLength();
		}
		if (currentProccess != null) {
			currentProccess.giveCpuTime(maxCpuTime);
		}
	}

	public void insertProcess(Process p) {
		cpuQueue.insert(p);
		if (currentProccess == null) {
			switchProcess();
		}
	}

	public void endCurrentProcess() {
		if (currentProccess != null && currentProccess.getCpuNeeded() <= 0) {
			deallocate(currentProccess);
			currentProccess = null;
			
		}
	}

	public void switchProcess() {
		if (currentProccess != null) {
			cpuQueue.insert(currentProccess);
		}
		currentProccess = (Process) cpuQueue.removeNext();
	}

	public void deallocate(Process p) {
		p.updateStatistics(statistics);
	}

	public long getMaximumCpuTime() {
		return maxCpuTime;
	}
}