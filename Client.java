package cs455.scaling.client;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.sql.Timestamp;
import java.util.Arrays;
import java.util.LinkedList;

import cs455.scaling.hash.Hash;

public class Client {

	private String serverHost;
	private int serverPort;
	private int messageRate;
	private static SocketChannel clientChan;
	private static ByteBuffer byteBuffer;
	private int totalMessagesSent = 0, totalMessagesRec = 0;
	private SenderThread sendData;
	private LinkedList<String> hashCodes = new LinkedList<>();
	
	private Client(String serverHost, String serverPort, String messageRate) {
		this.serverHost = serverHost;
		this.serverPort = Integer.parseInt(serverPort);
		this.messageRate = Integer.parseInt(messageRate);
	}
	
	public void stop() {
		try {
			clientChan.close();
			byteBuffer = null;
		} catch (IOException e) {
			System.out.println("Error closing server: " + e.getMessage());
		}
	}
	
	private void searchHashCode(String serverResponse) {
		for(int i = 0; i < hashCodes.size(); i++) {
			if(serverResponse.equals(hashCodes.get(i))) {
				hashCodes.remove(i);
				totalMessagesRec++;
			}
		}
	}
	
	Thread t = new Thread() {
		Timestamp ts;
		ClientStatistics cStats;
		public void run() {
			cStats = new ClientStatistics(new Timestamp(System.currentTimeMillis()), 
				totalMessagesSent, totalMessagesRec);
			while(true) {
				cStats.printStats();
				synchronized(currentThread()) {
					try {
						currentThread().wait((long) 20000);
						ts = new Timestamp(System.currentTimeMillis());
						cStats.changeData(ts, totalMessagesSent, totalMessagesRec);
						currentThread().notify();
						totalMessagesSent = 0; totalMessagesRec = 0;
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}
		}
	};
	
	public static void main(String[] args) throws IOException {
		Client client = new Client(args[0], args[1], args[2]);
		Hash hashing = new Hash();
		ClientStatistics cStats;
		Timestamp time;
		clientChan = SocketChannel.open(new InetSocketAddress("localhost", client.serverPort));
		byteBuffer = ByteBuffer.allocate(100);
		client.t.start();
		while(true) {
			SenderThread sendData = new SenderThread();
			sendData.run();
			String hash = hashing.SHA1FromBytes(sendData.data);
			client.hashCodes.add(hash);
			byteBuffer = ByteBuffer.wrap(hash.getBytes());
			String response = null;
			clientChan.write(byteBuffer);
			byteBuffer.clear();
			client.totalMessagesSent++;
			int i = clientChan.read(byteBuffer);
			if(i > 0) {
				response = new String(byteBuffer.array()).trim();
				client.searchHashCode(response);
				byteBuffer.clear();
			}
			try {
				Thread.sleep(1000/client.messageRate);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
}
