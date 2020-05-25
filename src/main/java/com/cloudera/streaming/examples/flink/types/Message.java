package com.cloudera.streaming.examples.flink.types;

public class Message {
	public int key;
	public String value;

	public Message(int key, String value) {
		this.key = key;
		this.value = value;
	}
}
