package com.cloudera.streaming.examples.flink.operators;

import org.apache.flink.api.common.functions.MapFunction;
import org.apache.flink.api.java.utils.ParameterTool;
import org.apache.flink.util.StringUtils;

import com.cloudera.streaming.examples.flink.types.Message;
import com.cloudera.streaming.examples.flink.utils.Utils;

import java.util.concurrent.ThreadLocalRandom;

public class MessageParser implements MapFunction<String, Message> {
	final String newValue;
	final int keySize;

	public MessageParser(ParameterTool params) {
		newValue = StringUtils.generateRandomAlphanumericString(
				ThreadLocalRandom.current(),
				Math.round(params.getInt(Utils.MESSAGE_SIZE) * params.getFloat(Utils.FANOUT_PARAM)));
		this.keySize = params.getInt(Utils.NUM_KEYS_PARAM);
	}

	@Override
	public Message map(String s) throws Exception {
		return new Message(ThreadLocalRandom.current().nextInt(keySize), newValue);
	}
}
