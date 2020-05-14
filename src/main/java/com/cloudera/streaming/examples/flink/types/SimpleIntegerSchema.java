package com.cloudera.streaming.examples.flink.types;

import org.apache.flink.api.common.serialization.SerializationSchema;

public class SimpleIntegerSchema implements SerializationSchema<Integer> {

    @Override
    public byte[] serialize(Integer integer) {
        return integer.toString().getBytes();
    }
}
