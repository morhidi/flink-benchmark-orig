package com.cloudera.streaming.examples.flink.operators;

import org.apache.flink.api.common.functions.RichMapFunction;
import org.apache.flink.api.common.state.ValueState;
import org.apache.flink.api.common.state.ValueStateDescriptor;
import org.apache.flink.api.java.utils.ParameterTool;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.util.StringUtils;

import com.cloudera.streaming.examples.flink.types.Message;
import com.cloudera.streaming.examples.flink.utils.Utils;

import java.util.concurrent.ThreadLocalRandom;

public class StateGenerator extends RichMapFunction<Message, Message> {
	private transient ValueState<String> valueState;
	private final int size;

	public StateGenerator(ParameterTool params) {
		this.size = params.getInt(Utils.STATE_SIZE_PARAM);
	}

	@Override
	public void open(Configuration parameters) throws Exception {
		ValueStateDescriptor<String> desc = new ValueStateDescriptor<String>("fakestate", String.class);
		valueState = getRuntimeContext().getState(desc);
	}

	@Override
	public Message map(Message message) throws Exception {
		String value = valueState.value();
		if (value == null) {
			valueState.update(StringUtils.generateRandomAlphanumericString(ThreadLocalRandom.current(), size));
		} else {
			valueState.update(value);
		}
		return message;
	}
}
