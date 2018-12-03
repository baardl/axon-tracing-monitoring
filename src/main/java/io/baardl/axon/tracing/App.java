package io.baardl.axon.tracing;

import io.baardl.axon.tracing.giftcard.CardSummary;
import io.baardl.axon.tracing.giftcard.CardSummaryProjection;
import io.baardl.axon.tracing.giftcard.FetchCardSummariesQuery;
import io.baardl.axon.tracing.giftcard.GiftCard;
import io.baardl.axon.tracing.issue.IssueCommand;
import io.baardl.axon.tracing.metrics.AxonMetricsRegistry;
import io.baardl.axon.tracing.metrics.MetricsConfiguration;
import io.baardl.axon.tracing.redeem.RedeemCommand;

import org.axonframework.commandhandling.gateway.CommandGateway;
import org.axonframework.config.Configuration;
import org.axonframework.config.Configurer;
import org.axonframework.config.DefaultConfigurer;
import org.axonframework.config.EventHandlingConfiguration;
import org.axonframework.eventsourcing.eventstore.EmbeddedEventStore;
import org.axonframework.eventsourcing.eventstore.inmemory.InMemoryEventStorageEngine;
import org.axonframework.queryhandling.QueryGateway;
import org.axonframework.queryhandling.responsetypes.ResponseTypes;
import org.slf4j.Logger;

import java.util.concurrent.ExecutionException;

import static org.slf4j.LoggerFactory.getLogger;

import com.codahale.metrics.Meter;
import com.codahale.metrics.MetricRegistry;

/**
 * Hello world!
 */
public class App {
	private static final Logger log = getLogger(App.class);

	public void runServer(Configuration configuration) throws ExecutionException, InterruptedException {
		configuration.start();
	}

	public void sendCommands(Configuration configuration) throws InterruptedException, ExecutionException {
		CommandGateway commandGateway = configuration.commandGateway();
		QueryGateway queryGateway = configuration.queryGateway();
		commandGateway.sendAndWait(new IssueCommand("gc1", 100));
		commandGateway.sendAndWait(new IssueCommand("gc2", 50));
		commandGateway.sendAndWait(new RedeemCommand("gc1", 10));
		commandGateway.sendAndWait(new RedeemCommand("gc2", 20));

		queryGateway.query(new FetchCardSummariesQuery(2, 0), ResponseTypes.multipleInstancesOf(CardSummary.class))
					.get()
					.forEach(System.out::println);
	}

	public Configuration buildConfiguration(AxonMetricsRegistry axonMetricsRegistry) {
		CardSummaryProjection projection = new CardSummaryProjection();
		EventHandlingConfiguration eventHandlingConfiguration = new EventHandlingConfiguration();
		eventHandlingConfiguration.registerEventHandler(c -> projection);

		Configurer configurer = DefaultConfigurer.defaultConfiguration()
												 .configureAggregate(GiftCard.class)
												 .configureEventStore(c -> new EmbeddedEventStore(new InMemoryEventStorageEngine()))
												 .registerModule(eventHandlingConfiguration)
												 .registerQueryHandler(c -> projection);
//													   .buildConfiguration();
		//Enable Metrics Configuration
		MetricsConfiguration metricsConfiguration = new MetricsConfiguration();
		MetricRegistry metricRegistry = axonMetricsRegistry.getMetricRegistry();
		configurer = metricsConfiguration.configureDefaultMetrics(configurer, metricRegistry);


		Configuration configuration = configurer.buildConfiguration();
		return configuration;
	}

	public static void main(String[] args) throws ExecutionException, InterruptedException {
		App app = new App();

		//MetricsRegistry
		AxonMetricsRegistry metricsRegistry = new AxonMetricsRegistry();
		metricsRegistry.startReport();
		Meter pingMeter = metricsRegistry.meter("ping");
		pingMeter.mark();
		Configuration configuration = app.buildConfiguration(metricsRegistry);

		//Run server
		app.runServer(configuration);

		//Send commands
		app.sendCommands(configuration);

		//Keep server running
		metricsRegistry.wait5Seconds();

	}
}
