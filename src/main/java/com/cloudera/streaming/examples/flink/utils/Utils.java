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

import com.cloudera.streaming.examples.flink.SimpleKafkaNumberGeneratorJob;
import org.apache.flink.api.common.serialization.SimpleStringSchema;
import org.apache.flink.api.java.utils.ParameterTool;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.connectors.kafka.FlinkKafkaConsumer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Properties;

public class Utils {

    private static Logger LOG = LoggerFactory.getLogger(Utils.class);
    private static final int MAX_PARALLELISM = 360;
    private static final String FROM_EARLIEST_PARAM = "from.earliest";
    private static final String KAFKA_PREFIX = "kafka.";

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

    public static DataStream<String> readNumberStream(ParameterTool params,
                                                      StreamExecutionEnvironment env, String name) {
        FlinkKafkaConsumer<String> numberSource = new FlinkKafkaConsumer<>(
                params.get(SimpleKafkaNumberGeneratorJob.INPUT_TOPIC_PARAM,
                        SimpleKafkaNumberGeneratorJob.DEFAULT_INPUT_TOPIC), new SimpleStringSchema(),
                Utils.readKafkaProperties(params));

        numberSource.setCommitOffsetsOnCheckpoints(true);
        if (params.getBoolean(FROM_EARLIEST_PARAM)) {
            numberSource.setStartFromEarliest();
        } else {
            numberSource.setStartFromLatest();
        }

        return env.addSource(numberSource)
                .name(name);
    }

    public static StreamExecutionEnvironment createExecutionEnvironment() {
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();

        // We set max parallelism to a number with a lot of divisors
        env.setMaxParallelism(MAX_PARALLELISM);

        return env;
    }
}
