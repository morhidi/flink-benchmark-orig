package com.cloudera.streaming.examples.flink;

import org.apache.flink.api.common.serialization.SimpleStringSchema;
import org.apache.flink.api.java.utils.ParameterTool;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.connectors.kafka.FlinkKafkaProducer;

import com.cloudera.streaming.examples.flink.operators.RandomStringGeneratorSource;
import com.cloudera.streaming.examples.flink.types.SimpleIntegerSchema;
import com.cloudera.streaming.examples.flink.utils.Utils;

import java.util.Optional;

public class SimpleKafkaStringGeneratorJob {

    public static final String INPUT_TOPIC_PARAM = "input.topic";
    public static final String DEFAULT_INPUT_TOPIC = "input.topic";

    public static void main(String[] args) throws Exception {
        if (args.length != 1) {
            throw new RuntimeException("Path to the properties file is expected as the only argument.");
        }
        ParameterTool params = ParameterTool.fromPropertiesFile(args[0]);
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();

        DataStream<String> stream = env
                .addSource(new RandomStringGeneratorSource(params))
                .name("StringGenerator");

        FlinkKafkaProducer<String> kafkaSink = new FlinkKafkaProducer<>(
                params.get(INPUT_TOPIC_PARAM, DEFAULT_INPUT_TOPIC),
                new SimpleStringSchema(),
                Utils.readKafkaProperties(params),
                Optional.empty());

        stream.addSink(kafkaSink).name("KafkaSink");
        env.execute("GeneratorJob");
    }
}
