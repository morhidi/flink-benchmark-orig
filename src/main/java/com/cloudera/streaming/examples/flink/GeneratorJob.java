package com.cloudera.streaming.examples.flink;

import org.apache.flink.api.common.serialization.SimpleStringSchema;
import org.apache.flink.api.java.utils.ParameterTool;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.connectors.kafka.FlinkKafkaProducer;

import com.cloudera.streaming.examples.flink.operators.StringGeneratorSource;
import com.cloudera.streaming.examples.flink.utils.Utils;

import java.util.Optional;

import static com.cloudera.streaming.examples.flink.utils.Utils.INPUT_TOPIC_PARAM;

public class GeneratorJob {

	public static void main(String[] args) throws Exception {
		if (args.length != 1) {
			throw new RuntimeException("Path to the properties file is expected as the only argument.");
		}
		ParameterTool params = ParameterTool.fromPropertiesFile(args[0]);
		StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();

		DataStream<String> stream = env
				.addSource(new StringGeneratorSource(params))
				.name("StringGenerator");

		FlinkKafkaProducer<String> kafkaSink = new FlinkKafkaProducer<>(
				params.getRequired(INPUT_TOPIC_PARAM),
				new SimpleStringSchema(),
				Utils.readKafkaProperties(params),
				Optional.empty());

		stream.addSink(kafkaSink).name("KafkaSink");
		env.execute(GeneratorJob.class.getSimpleName());
	}
}
