# flink-benchmark
Collection of Flink jobs used for benchmarking.

## Execution
Generator:
```
flink run -m yarn-cluster -d -p 3 -ys 1 -ytm 1500 -ynm Generator \
-yD metrics.reporter.kafka.class=org.apache.flink.metrics.kafka.KafkaMetricsReporter \
-yD metrics.reporter.kafka.topic=flink-metrics \
-yD metrics.reporter.kafka.bootstrap.servers="broker1:9092,broker2:9092,broekr3:9092" \
-yD metrics.reporter.kafka.interval="30 SECONDS" \
-yD metrics.reporter.kafka.log.errors=false \
-c com.cloudera.streaming.examples.flink.SimpleKafkaNumberGeneratorJob ~/flink-benchmark-{version}.jar job.properties
```
Processor:
```
flink run -m yarn-cluster -d -p 9 -ys 1 -ytm 1500 -ynm Processor \
-yD metrics.reporter.kafka.class=org.apache.flink.metrics.kafka.KafkaMetricsReporter \
-yD metrics.reporter.kafka.topic=flink-metrics \
-yD metrics.reporter.kafka.bootstrap.servers="broker1:9092,broker2:9092,broker3:9092" \
-yD metrics.reporter.kafka.interval="30 SECONDS" \
-yD metrics.reporter.kafka.log.errors=false \
-c com.cloudera.streaming.examples.flink.SimpleKafkaNumberProcessorJob ~/flinkbenchmark-{version}.jar job.properties
```
Reader:
```
flink run -m yarn-cluster -d -p 9 -ys 1 -ytm 1500 -ynm Processor \
-yD metrics.reporter.kafka.class=org.apache.flink.metrics.kafka.KafkaMetricsReporter \
-yD metrics.reporter.kafka.topic=flink-metrics \
-yD metrics.reporter.kafka.bootstrap.servers="broker1:9092,broker2:9092,broker3:9092" \
-yD metrics.reporter.kafka.interval="30 SECONDS" \
-yD metrics.reporter.kafka.log.errors=false \
-c com.cloudera.streaming.examples.flink.SimpleKafkaNumberReaderJob ~/flinkbenchmark-{version}.jar job.properties
```