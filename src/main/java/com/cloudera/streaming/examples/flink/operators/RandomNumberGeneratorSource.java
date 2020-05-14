package com.cloudera.streaming.examples.flink.operators;

import org.apache.flink.api.java.utils.ParameterTool;
import org.apache.flink.streaming.api.functions.source.ParallelSourceFunction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ThreadLocalRandom;

public class RandomNumberGeneratorSource implements ParallelSourceFunction<Integer> {

    private static final String SLEEP_MILLIS_PARAM = "sleep.millis";
    private static final String SLEEP_NANOS_PARAM = "sleep.nanos";
    private static final String MAX_NUM_MESSAGES_PARAM = "number.of.messages";
    private static final Logger LOG = LoggerFactory.getLogger(RandomNumberGeneratorSource.class);
    private final long sleepMillis;
    private final int sleepNanos;
    private final int maxNumberOfMessages;
    private volatile boolean isRunning = true;

    public RandomNumberGeneratorSource(ParameterTool params) {
        this.sleepMillis = params.getInt(SLEEP_MILLIS_PARAM, 0);
        this.sleepNanos = params.getInt(SLEEP_NANOS_PARAM, 0);
        this.maxNumberOfMessages = params.getInt(MAX_NUM_MESSAGES_PARAM, 0);
    }

    @Override
    public void run(SourceContext<Integer> ctx) throws Exception {
        ThreadLocalRandom rnd = ThreadLocalRandom.current();
        LOG.info("Starting random number generator with throttling {} ms {} ns", sleepMillis, sleepNanos);
        int cnt = 0;
        while (this.isRunning && (this.maxNumberOfMessages <= 0 || this.maxNumberOfMessages > cnt)) {
            int number = rnd.nextInt(Integer.MAX_VALUE);
            cnt++;
            synchronized (ctx.getCheckpointLock()) {
                ctx.collect(number);
            }
            if (sleepMillis > 0) {
                Thread.sleep(sleepMillis);
            }
            if (sleepNanos > 0) {
                final long INTERVAL = sleepNanos;
                long start = System.nanoTime();
                long end;
                do {
                    end = System.nanoTime();
                } while (start + INTERVAL >= end);
            }
        }
    }

    @Override
    public void cancel() {
        this.isRunning = false;
    }
}
