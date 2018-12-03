package io.baardl.axon.tracing.metrics;

import org.axonframework.config.Configurer;
import org.axonframework.config.DefaultConfigurer;
import org.axonframework.eventhandling.EventBus;
import org.axonframework.messaging.Message;
import org.axonframework.metrics.GlobalMetricRegistry;
import org.axonframework.metrics.MessageCountingMonitor;
import org.axonframework.metrics.MessageTimerMonitor;
import org.axonframework.metrics.PayloadTypeMessageMonitorWrapper;
import org.axonframework.monitoring.MultiMessageMonitor;

import com.codahale.metrics.MetricRegistry;

public class MetricsConfiguration {

	public Configurer buildConfigurer() {
		return DefaultConfigurer.defaultConfiguration();
	}

	public void configureSpecificEventBusMetrics(Configurer configurer, MetricRegistry metricRegistry) {
		// For the EventBus we want to count the messages per type of event being published.
		PayloadTypeMessageMonitorWrapper<MessageCountingMonitor> messageCounterPerType =
				new PayloadTypeMessageMonitorWrapper<>(MessageCountingMonitor::new);
		// And we also want to set a message timer per payload type
		PayloadTypeMessageMonitorWrapper<MessageTimerMonitor> messageTimerPerType =
				new PayloadTypeMessageMonitorWrapper<>(MessageTimerMonitor::new);
		// Which we group in a MultiMessageMonitor
		MultiMessageMonitor<Message<?>> multiMessageMonitor =
				new MultiMessageMonitor<>(messageCounterPerType, messageTimerPerType);
		// And configure through the Configuration API to every EventBus component
		configurer.configureMessageMonitor(EventBus.class, configuration -> multiMessageMonitor);

		// But do not forget to register them to the global MetricRegistry
		MetricRegistry eventBusRegistry = new MetricRegistry();
		eventBusRegistry.register("messageCounterPerType", messageCounterPerType);
		eventBusRegistry.register("messageTimerPerType", messageTimerPerType);
		metricRegistry.register("eventBus", eventBusRegistry);
	}
}

