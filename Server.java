package cs455.scaling.server;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Set;

public class Server {
	
	public Selector keySelector; public SelectionKey key;
	private int batchSize, batchTime, threadPoolSize, portNum;
	private int connections = 0, totalMessages = 0;
	private ArrayList<Integer> messages = new ArrayList<Integer>(); 
	private ArrayList<SocketChannel> sockets = new ArrayList<SocketChannel>();
	public ServerSocketChannel serverChan;
	private ThreadPoolManager threadManager;
	public ByteBuffer buffer;
	
	private Server(String port, String poolSize, String batchSize, String batchTime) {
		this.batchSize = Integer.parseInt(batchSize);
		this.batchTime = Integer.parseInt(batchTime);
		this.threadPoolSize = Integer.parseInt(poolSize);
		this.portNum = Integer.parseInt(port);
		threadManager = 
				new ThreadPoolManager(threadPoolSize, this.batchSize, this.batchTime);
	}
	
	//Read and send a response to clients (done by ThreadPool)
	public void response(ByteBuffer buffer, SelectionKey key) {
		SocketChannel client =  (SocketChannel) key.channel();
		int result;
		for(int i = 0; i < sockets.size(); i++) {
			if(sockets.get(i) == client) {
				result = messages.get(i);
				messages.set(i, result+1);
			}
		}
		try {
			client.read(buffer);
			buffer.flip();
			client.write(buffer);
			buffer.clear();
			totalMessages++;
		} catch (IOException e) {
			System.out.println("Trouble reading from client: " + e.getMessage());
		}
	}

	//register clients (done by ThreadPool)
	public void register(Selector selector, ServerSocketChannel serverSocket) {
		try {
			SocketChannel client = serverSocket.accept();
			client.configureBlocking(false);
			client.register(selector, SelectionKey.OP_READ);
			sockets.add(client);
			messages.add(0);
			connections++;
		} catch (IOException e) {
			System.out.println("Trouble registering client: " + e.getMessage());
		}
	}
	
	//create a thread to allow the server to show statistics since
	//the statistics are not tasks done by the thread pool
	Thread t = new Thread() {
		Timestamp ts;
		ServerStatistics sStats;
		public void run() {
			sStats = new ServerStatistics(new Timestamp(System.currentTimeMillis()), 
				connections, totalMessages, messages);
			while(true) {
				sStats.printStatistics();
				synchronized(currentThread()) {
					try {
						currentThread().wait((long) 20000);
						ts = new Timestamp(System.currentTimeMillis());
						sStats.changeStat(ts, connections, totalMessages, messages);
						currentThread().notify();
						connections = 0; totalMessages = 0;
						messages = new ArrayList<Integer>();
						sockets = new ArrayList<SocketChannel>();
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}
		}
	};
	
	public void prepare() {
		try {
			keySelector = Selector.open();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void connect() {
		try {
			serverChan = ServerSocketChannel.open();
			serverChan.bind(new InetSocketAddress("localhost", portNum));
			serverChan.configureBlocking(false);
			serverChan.register(keySelector, SelectionKey.OP_ACCEPT);
			buffer = ByteBuffer.allocate(100);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args) throws IOException {
		Server server = new Server(args[0], args[1], args[2], args[3]);
		server.threadManager.addServer(server);
		boolean result;
		server.threadManager.addTask(new Task(null, "prepare"));
		server.threadManager.performTask();
		server.threadManager.addTask(new Task(null, "connect"));
		server.threadManager.performTask();
		server.t.start();
		while(true) {
			server.keySelector.select();
			Set<SelectionKey> selectedKeys = server.keySelector.selectedKeys();
			Iterator<SelectionKey> iterateKeys = selectedKeys.iterator();
			while(iterateKeys.hasNext()) {
				server.key = iterateKeys.next();
				if(server.key.isValid() == false) {
					continue;
				}
				if(server.key.isAcceptable()) {
					result = server.threadManager.addTask(new Task(server.key, "register"));
					if(result == false) {
						server.threadManager.start = new Timestamp(System.currentTimeMillis());
						server.threadManager.performTask();
					}
				}
				if(server.key.isReadable()) {
					server.response(server.buffer, server.key);
					result = server.threadManager.addTask(new Task(server.key, "response"));
					if(result == false) {
						server.threadManager.start = new Timestamp(System.currentTimeMillis());
						server.threadManager.performTask();
					}
				}
				iterateKeys.remove();
			}
		}
	}
	
}
