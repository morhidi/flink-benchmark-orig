package com.cloudera.streaming.examples.flink.operators;

import org.apache.flink.api.common.functions.MapFunction;
import org.apache.flink.util.StringUtils;

import java.util.concurrent.ThreadLocalRandom;

public class FanOut implements MapFunction<String, String> {
	final String newMessage;

	public FanOut(int origSize, float fanout) {
		int size = Math.round(origSize * fanout);
		newMessage = StringUtils.generateRandomAlphanumericString(ThreadLocalRandom.current(), size);
	}

	@Override
	public String map(String s) throws Exception {
		return newMessage;
	}
}
