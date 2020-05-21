package com.cloudera.streaming.examples.flink.operators;

import org.apache.flink.api.java.functions.KeySelector;
import org.apache.flink.api.java.tuple.Tuple2;

import java.util.Random;

public class KeyGenerator implements KeySelector<String, Integer> {

	private final int numKeys;

	public KeyGenerator(int numKeys) {
		this.numKeys = numKeys;
	}

	@Override
	public Integer getKey(String message) throws Exception {
		return Math.abs(message.hashCode()) % numKeys;
	}
}
