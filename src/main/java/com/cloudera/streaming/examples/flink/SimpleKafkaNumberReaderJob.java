package com.cloudera.streaming.examples.flink;

import com.cloudera.streaming.examples.flink.utils.Utils;
import org.apache.flink.api.java.utils.ParameterTool;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.sink.SinkFunction;

public class SimpleKafkaNumberReaderJob {

    public static void main(String[] args) throws Exception {
        if (args.length != 1) {
            throw new RuntimeException("Path to the properties file is expected as the only argument.");
        }
        ParameterTool params = ParameterTool.fromPropertiesFile(args[0]);
        new SimpleKafkaNumberReaderJob()
                .createApplicationPipeline(params)
                .execute("Reader");
    }

    private StreamExecutionEnvironment createApplicationPipeline(ParameterTool params) {

        StreamExecutionEnvironment env = Utils.createExecutionEnvironment();

        // Read number stream
        DataStream<String> numberStream = Utils.readNumberStream(params, env, "Reader");

        numberStream.addSink(new SinkFunction<String>() {
            @Override
            public void invoke(String value, Context context) {

            }
        }).name("Dummy Sink");

        return env;
    }
}
