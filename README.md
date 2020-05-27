# flink-benchmark
Performance benchmarking tool for Flink component. 

## Installation

### Prerequisites

The following packages are needed to be installed on the host used for monitoring:
 
 - docker
 - docker-compose

### ELK

The following steps are necessary to install ELK stack.

 - Copy ```monitoring/elk``` folder to the designated host
 - Modify ```flink-metrics.conf``` by replacing ```{KAFKA-BROKERS}``` with the actual list of kafka brokers (only non-SSL connection is supported)
 - Execute ```docker-compose up``` in the folder of ```docker-compose.yml```
 - Check availability of Kibana on ```http://monitoring-host:5601```

### Prometheus

The following steps are necessary to install Prometheus-Grafana stack.

 - Copy ```monitoring/prometheus``` folder to the designated host
 - Modify ```monitoring/prometheus/config/prometheus.yml``` by replacing targets under flink job with ```pushgateway-host:9091, kafka-exporter-host:9308```
 - Update ```command``` field of kafka-exporter in ```docker-compose.yml``` with the actual list of kafka brokers (only non-SSL connection is supported)
 - Optional, to separate pushgateway:
   - Remove pushgateway service from ```docker-compose.yml```
   - Install/execute pushgateway based on the instructions at https://github.com/prometheus/pushgateway
   - Modify ```monitoring/prometheus/config/prometheus.yml``` by fixing pushgateway host in targets under flink job
 - Execute ```docker-compose up``` in the folder of ```docker-compose.yml```
 - Check availability of Grafana on ```http://monitoring-host:3000```

## Build

Build the maven project by using the command:

```mvn clean package```

## Execution

For list of available parameters refer to ```config/job.properties.example```.

Copy the ```flink-benchmark-{version}.jar``` from the target folder of the project and a valid ```job.properties``` file to one of the flink nodes.

### ELK
Generator
```
flink run -m yarn-cluster -d -p 3 -ys 1 -ytm 1500 -ynm StringGenerator \
-yD metrics.reporter.kafka.class=org.apache.flink.metrics.kafka.KafkaMetricsReporter \
-yD metrics.reporter.kafka.topic=flink-metrics \
-yD metrics.reporter.kafka.bootstrap.servers="{KAFKA-BROKERS}" \
-yD metrics.reporter.kafka.interval="30 SECONDS" \
-yD metrics.reporter.kafka.log.errors=false \
-c com.cloudera.streaming.examples.flink.GeneratorJob ~/flink-benchmark-{version}.jar job.properties
```
Processor
```
flink run -m yarn-cluster -d -p 9 -ys 1 -ytm 1500 -ynm Processor \
-yD metrics.reporter.kafka.class=org.apache.flink.metrics.kafka.KafkaMetricsReporter \
-yD metrics.reporter.kafka.topic=flink-metrics \
-yD metrics.reporter.kafka.bootstrap.servers="broker1:9092,broker2:9092,broker3:9092" \
-yD metrics.reporter.kafka.interval="30 SECONDS" \
-yD metrics.reporter.kafka.log.errors=false \
-c com.cloudera.streaming.examples.flink.ProcessorJob ~/flinkbenchmark-{version}.jar job.properties
```
 
Steps after first execution:
 
 - Set the replication factor of ```logstash_logs``` index to 0 in Elasticsearch/Kibana
 - Import the dashboard ```dashboards/flink_simplified.ndjson``` in Elasticsearch/Kibana

### Prometheus

Genreator
```
flink run -m yarn-cluster -d -p 3 -ys 1 -ytm 1500 -ynm Generator \
-yD metrics.reporter.promgateway.class=org.apache.flink.metrics.prometheus.PrometheusPushGatewayReporter \
-yD metrics.reporter.promgateway.host={pushgateway-host} \
-yD metrics.reporter.promgateway.port=9091 \
-yD metrics.reporter.promgateway.jobName=Generator \
-yD metrics.reporter.promgateway.randomJobNameSuffix=true \
-yD metrics.reporter.promgateway.deleteOnShutdown=false \
-yD metrics.reporter.promgateway.groupingKey="k1=v1;k2=v2" \
-c com.cloudera.streaming.examples.flink.GeneratorJob ~/flink-benchmark-{version}.jar job.properties
```
Processor
```
flink run -m yarn-cluster -d -p 3 -ys 1 -ytm 1500 -ynm Processor \
-yD metrics.reporter.promgateway.class=org.apache.flink.metrics.prometheus.PrometheusPushGatewayReporter \
-yD metrics.reporter.promgateway.host={pushgateway-host} \
-yD metrics.reporter.promgateway.port=9091 \
-yD metrics.reporter.promgateway.jobName=Processor \
-yD metrics.reporter.promgateway.randomJobNameSuffix=true \
-yD metrics.reporter.promgateway.deleteOnShutdown=false \
-yD metrics.reporter.promgateway.groupingKey="k1=v1;k2=v2" \
-c com.cloudera.streaming.examples.flink.ProcessorJob ~/flink-benchmark-{version}.jar job.properties
```

Steps after first execution:
 - Import ```dashboards-grafana.json``` to Grafana
 