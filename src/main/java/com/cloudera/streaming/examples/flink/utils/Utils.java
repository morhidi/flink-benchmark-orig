/*
 * Licensed to Cloudera, Inc. under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.cloudera.streaming.examples.flink.utils;

import org.apache.flink.api.java.utils.ParameterTool;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Properties;

public class Utils {

	private static Logger LOG = LoggerFactory.getLogger(Utils.class);

	private static final String KAFKA_PREFIX = "kafka.";

	public static final String SLEEP_MILLIS_PARAM = "sleep.millis";
	public static final String SLEEP_NANOS_PARAM = "sleep.nanos";
	public static final String MAX_NUM_MESSAGES_PARAM = "number.of.messages";
	public static final String MESSAGE_SIZE = "message.size";

	public static final String INPUT_TOPIC_PARAM = "input.topic";
	public static final String OUTPUT_TOPIC_PARAM = "output.topic";
	public static final String FANOUT_PARAM = "fanout.ratio";
	public static final String FROM_EARLIEST_PARAM = "from.earliest";
	public static final String NUM_KEYS_PARAM = "number.of.keys";
	public static final String STATE_SIZE_PARAM = "state.size";

	public static Properties readKafkaProperties(ParameterTool params) {
		Properties properties = new Properties();
		for (String key : params.getProperties().stringPropertyNames()) {
			if (key.startsWith(KAFKA_PREFIX)) {
				properties.setProperty(key.substring(KAFKA_PREFIX.length()), params.get(key));
			}
		}

		LOG.info("### Kafka parameters:");
		for (String key : properties.stringPropertyNames()) {
			LOG.info("Kafka param: {}={}", key, properties.get(key));
		}
		return properties;
	}

}
