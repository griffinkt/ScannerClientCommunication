package cs455.scaling.client;

import java.sql.Timestamp;

public class ClientStatistics {
	
	private int totalMessagesSent = 0, totalMessagesRec = 0;
	private Timestamp time;
	
	ClientStatistics(Timestamp time, int totalMessagesSent, int totalMessagesRec) {
		this.time = time;
		this.totalMessagesRec = totalMessagesRec;
		this.totalMessagesSent = totalMessagesSent;
	}
	
	public void printStats() {
		System.out.printf("%s Total Sent Count: %d"
				+ " Total Received Count: %d \n", time, 
				totalMessagesSent, totalMessagesRec);
	}
	
	public void changeData(Timestamp time, int sent, int received) {
		this.time = time;
		this.totalMessagesSent = sent;
		this.totalMessagesRec = received;
	}
	
	/*public static void main(String[] args) {
		Timestamp ts = new Timestamp(System.currentTimeMillis());
		ClientStatistics cs = new ClientStatistics(ts, 12, 10);
		Thread t = new Thread() {
			Timestamp ts;
			public void run() {
				while(true) {
					cs.printStats();
					synchronized(currentThread()) {
						try {
							currentThread().wait((long) 20000);
							ts = new Timestamp(System.currentTimeMillis());
							cs.changeData(ts, 20, 10);
							currentThread().notify();
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					}
				}
			}
		};
		t.start();
	}*/
	
}
