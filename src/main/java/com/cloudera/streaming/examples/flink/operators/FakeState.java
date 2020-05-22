package com.cloudera.streaming.examples.flink.operators;

import org.apache.flink.api.common.functions.RichMapFunction;
import org.apache.flink.api.common.state.ValueState;
import org.apache.flink.api.common.state.ValueStateDescriptor;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.util.StringUtils;

import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

public class FakeState extends RichMapFunction<String, String> {
	private transient ValueState<String> valueState;

	private final String fakeState;

	public FakeState(int size) {
		fakeState = StringUtils.generateRandomAlphanumericString(ThreadLocalRandom.current(), size);
	}

	@Override
	public void open(Configuration parameters) throws Exception {
		ValueStateDescriptor<String> desc = new ValueStateDescriptor<String>("fakestate", String.class);
		valueState = getRuntimeContext().getState(desc);
	}

	@Override
	public String map(String s) throws Exception {
		String value = valueState.value();
		if (value == null) {
			valueState.update(fakeState);
		} else {
			valueState.update(value);
		}
		return s;
	}
}
