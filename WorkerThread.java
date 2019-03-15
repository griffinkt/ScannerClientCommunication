package cs455.scaling.server;

public class WorkerThread implements Runnable {

	public boolean isWorking = false;
	public Task task;
	public String hashCode;
	private Server server;
	
	public void addTask(Task t) {
		this.task = t;
	}
	
	public void addServer(Server server) {
		this.server = server;
	}
	
	@Override
	public void run() {
		if(task.name.equals("prepare")) {
			server.prepare();
		}
		if(task.name.equals("connect")) {
			server.connect();
		}
		if(task.name.equals("register")) {
			server.register(server.keySelector, server.serverChan);
		}
		if(task.name.equals("readRespond")) {
			server.response(server.buffer, server.key);
		}
	}

}
