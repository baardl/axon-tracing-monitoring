package io.baardl.axon.tracing.metrics;

import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.codahale.metrics.ConsoleReporter;
import com.codahale.metrics.Meter;
import com.codahale.metrics.MetricRegistry;

public class AxonMetricsRegistry {
	private static final Logger log = LoggerFactory.getLogger(AxonMetricsRegistry.class);
	private final MetricRegistry metricRegistry;

	public AxonMetricsRegistry() {
		metricRegistry = new MetricRegistry();
	}

	public void startReport() {
		ConsoleReporter reporter = ConsoleReporter.forRegistry(metricRegistry)
												  .convertRatesTo(TimeUnit.SECONDS)
												  .convertDurationsTo(TimeUnit.MILLISECONDS)
												  .build();
		reporter.start(1, TimeUnit.SECONDS);
	}

	public void wait5Seconds() {
		try {
			Thread.sleep(5 * 1000);
		} catch (InterruptedException e) {
		}
	}

	public Meter meter(String key) {
		Meter meter = metricRegistry.meter(key);
		return meter;
	}

	public MetricRegistry getMetricRegistry() {
		return metricRegistry;
	}
}
