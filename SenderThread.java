package cs455.scaling.client;

import java.util.Random;

public class SenderThread implements Runnable {

	final byte[] data = new byte[8000];
	
	@Override
	public void run() {
		Random r = new Random();
		r.nextBytes(this.data);
	}

	/*public static void main(String[] args) {
		SenderThread send = new SenderThread();
		send.run();
		System.out.println(Arrays.toString(send.data));
		send.run();
		System.out.println(Arrays.toString(send.data));
	}*/
	
}
