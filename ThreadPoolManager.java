package cs455.scaling.server;

import java.sql.Timestamp;
import java.util.Arrays;
import java.util.LinkedList;


public class ThreadPoolManager {
	
	private boolean threadFound = false, addTask = false;
	private final int poolSize; private final int batchSize; 
	private final int batchTime;
	private LinkedList<Task> taskQueue = new LinkedList<Task>();
	private final LinkedList<WorkerThread> workerTds;
	public Timestamp start; private Timestamp end;
	public Server server;
	
	ThreadPoolManager(int poolSize, int batchSize, int batchTime) {
		this.poolSize = poolSize;
		workerTds = new LinkedList<WorkerThread>();
		for(int i = 0; i < poolSize; i++) {
			workerTds.add(new WorkerThread());
		}
		this.batchSize = batchSize;
		this.batchTime = batchTime;
	}
	
	public void addServer(Server server) {
		this.server = server;
	}
	
	public boolean addTask(Task task) {
		if(taskQueue.size() < batchSize) {	
			taskQueue.add(task);
			threadFound = assignThread(task);
			addTask = true;
			return threadFound;
		}
		else {
			addTask = false;
		}
		return addTask;
	}
	
	private synchronized boolean assignThread(Task t) {
		boolean foundWork = false;
		for(int i = 0; i < workerTds.size(); i++) {
			if(!workerTds.get(i).isWorking) {
				workerTds.get(i).isWorking = true;
				workerTds.get(i).addTask(t);
				foundWork = true;
				break;
			}
		}
		return foundWork;
	}
	
	public synchronized void performTask() {
		Task task = taskQueue.removeFirst();
		for(int i = 0; i < workerTds.size(); i++) {
			if(workerTds.get(i).isWorking) {
				workerTds.get(i).addServer(server);
				workerTds.get(i).run();
				workerTds.get(i).isWorking = false;
				break;
			}
		}
	}
	
	public synchronized void performTasks() {
		Task task = taskQueue.removeFirst();
		String codeResult = "";
		for(int i = 0; i < workerTds.size(); i++) {
			if(workerTds.get(i).task.key.channel() == task.key.channel()
					&& workerTds.get(i).isWorking) {
				workerTds.get(i).addServer(server);
				workerTds.get(i).run();
				workerTds.get(i).isWorking = false;
				break;
			}
		}
		end = new Timestamp(System.currentTimeMillis());
		int duration = (int) (end.getTime() - start.getTime())/1000;
		if(taskQueue.size() != 0 || duration != batchTime) {
			performTask();
		}
	}
	
	/*public static void main(String[] args) {
		ThreadPoolManager tpm = new ThreadPoolManager(2, 10, 10);
		byte[] data = new byte[8000];
		Task t = new Task(data);
		boolean result = tpm.addTask(t);
		while(result) {
			result = tpm.addTask(t);
		}
		String hash = tpm.performTask();
		while(hash != "") {
			System.out.println(hash);
			hash = tpm.performTask();
		}
	}*/
	
}
