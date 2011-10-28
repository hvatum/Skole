public class CPU {
	private long maxCpuTime;
	private Queue cpuQueue;
	private Statistics statistics;

	/** The amount of memory in the memory device */
	private long memorySize;
	/** The amount of free memory in the memory device */
	private long freeMemory;
	private Process currentProcess;
	
	private final EventQueue eventQueue;

	public CPU(Queue cpuQueue, long maxCpuTime, Statistics statistics, EventQueue eventQueue) {
		this.cpuQueue = cpuQueue;
		this.maxCpuTime = maxCpuTime;
		this.statistics = statistics;
		this.eventQueue = eventQueue;
	}

	public void timePassed(long timePassed) {
		statistics.cpuQueueLengthTime += cpuQueue.getQueueLength() * timePassed;
		if (cpuQueue.getQueueLength() > statistics.memoryQueueLargestLength) {
			statistics.cpuQueueLargestLength = cpuQueue.getQueueLength();
		}
	}

	public void insertProcess(Process p) {
		cpuQueue.insert(p);
		if (currentProcess == null) {
			switchProcess();
		}
	}
	
	public void work(long clock) {
		if (currentProcess != null) {
			long timeNeeded = currentProcess.getCpuNeeded();
			if (timeNeeded > maxCpuTime) {
				currentProcess.giveCpuTime(maxCpuTime);
				eventQueue.insertEvent(new Event(Simulator.SWITCH_PROCESS, clock + maxCpuTime));
			} else {
				currentProcess.giveCpuTime(timeNeeded);
				eventQueue.insertEvent(new Event(Simulator.END_PROCESS, clock + timeNeeded));
			}
		}
	}

	public Process endCurrentProcess() {
		Process endedProcess = null;
		if (currentProcess != null && currentProcess.getCpuNeeded() <= 0) {
			deallocate(currentProcess);
			endedProcess  = currentProcess;
			currentProcess = null;
		}
		return endedProcess;
	}

	public void switchProcess() {
		if (currentProcess != null) {
			cpuQueue.insert(currentProcess);
		}
		currentProcess = (Process) cpuQueue.removeNext();
	}

	public void deallocate(Process p) {
		p.updateStatistics(statistics);
	}

	public long getMaximumCpuTime() {
		return maxCpuTime;
	}
}
