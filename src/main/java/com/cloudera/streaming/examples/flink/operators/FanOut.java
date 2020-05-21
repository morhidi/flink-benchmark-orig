package com.cloudera.streaming.examples.flink.operators;

import org.apache.flink.api.common.functions.MapFunction;
import org.apache.flink.util.StringUtils;

import java.util.concurrent.ThreadLocalRandom;


public class FanOut implements MapFunction<String, String> {
    final float fanout;

    public FanOut(float fanout) {
        this.fanout = fanout;
    }

    @Override
    public String map(String s) throws Exception {
        int size = Math.round(s.length() * fanout);
        return StringUtils.generateRandomAlphanumericString(ThreadLocalRandom.current(), size);
    }
}
