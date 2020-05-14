package com.cloudera.streaming.examples.flink;

import com.cloudera.streaming.examples.flink.operators.RandomNumberGeneratorSource;
import com.cloudera.streaming.examples.flink.types.SimpleIntegerSchema;
import com.cloudera.streaming.examples.flink.utils.Utils;
import org.apache.flink.api.java.utils.ParameterTool;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.connectors.kafka.FlinkKafkaProducer;

import java.util.Optional;

public class SimpleKafkaNumberGeneratorJob {

    public static final String INPUT_TOPIC_PARAM = "input.topic";
    public static final String DEFAULT_INPUT_TOPIC = "input.topic";

    public static void main(String[] args) throws Exception {
        if (args.length != 1) {
            throw new RuntimeException("Path to the properties file is expected as the only argument.");
        }
        ParameterTool params = ParameterTool.fromPropertiesFile(args[0]);

        StreamExecutionEnvironment env = Utils.createExecutionEnvironment();
        DataStream<Integer> generatedInput =
                env.addSource(new RandomNumberGeneratorSource(params))
                        .name("Generator");

        FlinkKafkaProducer<Integer> kafkaSink = new FlinkKafkaProducer<>(
                params.get(INPUT_TOPIC_PARAM, DEFAULT_INPUT_TOPIC),
                new SimpleIntegerSchema(),
                Utils.readKafkaProperties(params),
                Optional.empty());

        generatedInput.addSink(kafkaSink).name("Generator Sink");
        env.execute("Generator");
    }
}
