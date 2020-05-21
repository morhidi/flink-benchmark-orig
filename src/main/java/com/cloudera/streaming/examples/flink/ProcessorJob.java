package com.cloudera.streaming.examples.flink;

import org.apache.flink.api.common.serialization.SimpleStringSchema;
import org.apache.flink.api.java.utils.ParameterTool;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.connectors.kafka.FlinkKafkaConsumer;
import org.apache.flink.streaming.connectors.kafka.FlinkKafkaProducer;

import com.cloudera.streaming.examples.flink.operators.FakeState;
import com.cloudera.streaming.examples.flink.operators.FanOut;
import com.cloudera.streaming.examples.flink.operators.HashingKafkaPartitioner;
import com.cloudera.streaming.examples.flink.operators.KeyGenerator;
import com.cloudera.streaming.examples.flink.utils.Utils;

import java.util.Optional;

public class ProcessorJob {

    public static final String INPUT_TOPIC_PARAM = "input.topic";
    public static final String OUTPUT_TOPIC_PARAM = "output.topic";
    public static final String FANOUT_PARAM = "fanout.ratio";
    private static final String FROM_EARLIEST_PARAM = "from.earliest";
    private static final String NUM_KEYS_PARAM = "number.of.keys";
    private static final String STATE_SIZE_PARAM = "state.size";

    public static void main(String[] args) throws Exception {
        if (args.length != 1) {
            throw new RuntimeException("Path to the properties file is expected as the only argument.");
        }
        ParameterTool params = ParameterTool.fromPropertiesFile(args[0]);
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();

        FlinkKafkaConsumer<String> kafkaSource = new FlinkKafkaConsumer<String>(
                params.getRequired(INPUT_TOPIC_PARAM),
                new SimpleStringSchema(),
                Utils.readKafkaProperties(params));

        if (params.getBoolean(FROM_EARLIEST_PARAM)) {
            kafkaSource.setStartFromEarliest();
        }
        DataStream<String> stream = env
                .addSource(kafkaSource)
                .name("KafkaSource");

        stream = stream.map(new FanOut(params.getFloat(FANOUT_PARAM)));

        if (params.getInt(STATE_SIZE_PARAM) > 0) {
            stream = stream.keyBy(new KeyGenerator(params.getInt(NUM_KEYS_PARAM)))
                    .map(new FakeState(params.getInt(STATE_SIZE_PARAM)));
        }

        FlinkKafkaProducer<String> kafkaSink = new FlinkKafkaProducer<>(
                params.get(OUTPUT_TOPIC_PARAM),
                new SimpleStringSchema(),
                Utils.readKafkaProperties(params), Optional.of(new HashingKafkaPartitioner<>()));

        stream.addSink(kafkaSink).name("KafkaSink");
        env.execute(ProcessorJob.class.getSimpleName());
    }
}
