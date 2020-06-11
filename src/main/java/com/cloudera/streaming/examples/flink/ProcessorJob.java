package com.cloudera.streaming.examples.flink;

import org.apache.flink.api.common.serialization.SimpleStringSchema;
import org.apache.flink.api.java.utils.ParameterTool;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.connectors.kafka.FlinkKafkaConsumer;
import org.apache.flink.streaming.connectors.kafka.FlinkKafkaProducer;

import com.cloudera.streaming.examples.flink.operators.MessageParser;
import com.cloudera.streaming.examples.flink.operators.StateGenerator;
import com.cloudera.streaming.examples.flink.types.Message;
import com.cloudera.streaming.examples.flink.utils.Utils;

import java.util.Optional;

public class ProcessorJob {

	public static void main(String[] args) throws Exception {
		if (args.length != 1) {
			throw new RuntimeException("Path to the properties file is expected as the only argument.");
		}
		ParameterTool params = ParameterTool.fromPropertiesFile(args[0]);
		StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();

		FlinkKafkaConsumer<String> kafkaSource = new FlinkKafkaConsumer<String>(
				params.getRequired(Utils.INPUT_TOPIC_PARAM),
				new SimpleStringSchema(),
				Utils.readKafkaProperties(params));

		if (params.getBoolean(Utils.FROM_EARLIEST_PARAM)) {
			kafkaSource.setStartFromEarliest();
		}
		DataStream<String> stream = env
				.addSource(kafkaSource)
				.name("KafkaSource");

		DataStream<Message> messages = stream.map(new MessageParser(params));

		if (params.getInt(Utils.STATE_SIZE_PARAM) > 0) {
			messages = messages.keyBy("fakeKey")
					.map(new StateGenerator(params));
		}

		stream = messages.map(message -> message.value);

		FlinkKafkaProducer<String> kafkaSink = new FlinkKafkaProducer<>(
				params.get(Utils.OUTPUT_TOPIC_PARAM),
				new SimpleStringSchema(),
				Utils.readKafkaProperties(params), Optional.empty());

		stream.addSink(kafkaSink).name("KafkaSink");
		env.execute(ProcessorJob.class.getSimpleName());
	}
}
