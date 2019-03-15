package cs455.scaling.server;

import java.nio.channels.SelectionKey;

public class Task {
	
	public String name;
	public SelectionKey key;

	Task(SelectionKey key, String name) {
		this.key = key;
		this.name = name;
	}
	
}
