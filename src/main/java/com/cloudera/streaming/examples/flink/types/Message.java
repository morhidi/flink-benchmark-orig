package com.cloudera.streaming.examples.flink.types;

public class Message {
	public int fakeKey;
	public String value;

	public Message() {
	}

	public Message(int fakeKey, String value) {
		this.fakeKey = fakeKey;
		this.value = value;
	}
}
