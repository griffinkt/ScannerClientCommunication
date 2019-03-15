package cs455.scaling.server;

import java.sql.Timestamp;
import java.util.ArrayList;

public class ServerStatistics {
	
	private Timestamp time;
	private int connections, totalMessages;
	private ArrayList<Integer> messages = null;
	
	ServerStatistics(Timestamp time, int connections, int totalMessages,
			ArrayList<Integer> messages) {
		this.time = time;
		this.connections = connections;
		this.totalMessages = totalMessages;
		this.messages = new ArrayList<>();
	}
	
	public void printStatistics() {
		double meanThroughput = 0, stddevThroughput = 0;
		if(messages == null || messages.size() == 0) {
			meanThroughput = 0;
			stddevThroughput = 0;
		}
		else {
			for(int i = 0; i < messages.size(); i++) {
				meanThroughput += (messages.get(i)/20);
			}
			meanThroughput = meanThroughput/messages.size();
			for(int i = 0; i < messages.size(); i++) {
				stddevThroughput += Math.pow(((messages.get(i)/20) - meanThroughput), 2);
			}
			stddevThroughput = Math.sqrt(stddevThroughput/messages.size());
		}
		System.out.printf("%s Server Throughput: %.4f  messages/s, Active Client "
				+ "Connections: %d, Mean-per Client Throughput: %.4f, Standard "
				+ "Deviation-per Client Throughput: %.4f\n", time.toString(), 
				totalMessages/20.0, connections, meanThroughput, stddevThroughput);
	}
	
	public void changeStat(Timestamp time, int connections, int totalMessages, 
			ArrayList<Integer> messages) {
		this.time = time;
		this.connections = connections;
		this.totalMessages = totalMessages;
		this.messages = messages;
	}
	
	/*public static void main(String[] args) {
		int[] messages = new int[12];
		for(int i = 0; i < messages.length; i++) {
			messages[i] = (i+1)*100;
		}
		long time = System.currentTimeMillis();
		Timestamp ts = new Timestamp(time);
		ServerStatistics serverstats = new ServerStatistics(ts, 12, 28500, messages);
		serverstats.printStatistics();
	}*/
	
}
