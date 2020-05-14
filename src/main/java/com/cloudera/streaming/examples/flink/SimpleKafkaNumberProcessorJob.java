package com.cloudera.streaming.examples.flink;

import com.cloudera.streaming.examples.flink.operators.HashingKafkaPartitioner;
import com.cloudera.streaming.examples.flink.utils.Utils;
import org.apache.flink.api.common.serialization.SimpleStringSchema;
import org.apache.flink.api.java.utils.ParameterTool;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.connectors.kafka.FlinkKafkaProducer;

import java.util.Optional;

public class SimpleKafkaNumberProcessorJob {

    private static final String OUTPUT_TOPIC_PARAM = "output.topic";
    private static final String DEFAULT_OUTPUT_TOPIC = "output.topic";


    public static void main(String[] args) throws Exception {
        if (args.length != 1) {
            throw new RuntimeException("Path to the properties file is expected as the only argument.");
        }
        ParameterTool params = ParameterTool.fromPropertiesFile(args[0]);
        new SimpleKafkaNumberProcessorJob()
                .createApplicationPipeline(params)
                .execute("Processor");
    }

    private StreamExecutionEnvironment createApplicationPipeline(ParameterTool params) {

        // Create and configure the StreamExecutionEnvironment
        StreamExecutionEnvironment env = Utils.createExecutionEnvironment();

        // Read number stream
        DataStream<String> numberStream = Utils.readNumberStream(params, env, "Processor");

        // Handle the output of transaction and query results separately
        writeOutput(params, numberStream);

        return env;
    }

    private void writeOutput(ParameterTool params, DataStream<String> numberStream) {
        FlinkKafkaProducer<String> outputSink = new FlinkKafkaProducer<>(
                params.get(OUTPUT_TOPIC_PARAM, DEFAULT_OUTPUT_TOPIC), new SimpleStringSchema(),
                Utils.readKafkaProperties(params),
                Optional.of(new HashingKafkaPartitioner<>()));

        numberStream
                .addSink(outputSink)
                .name("Processor Sink");
    }
}
