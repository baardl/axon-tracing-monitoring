package io.baardl.axon.tracing;

import io.baardl.axon.tracing.giftcard.CardSummary;
import io.baardl.axon.tracing.giftcard.CardSummaryProjection;
import io.baardl.axon.tracing.giftcard.FetchCardSummariesQuery;
import io.baardl.axon.tracing.giftcard.GiftCard;
import io.baardl.axon.tracing.issue.IssueCommand;
import io.baardl.axon.tracing.redeem.RedeemCommand;
import org.axonframework.commandhandling.gateway.CommandGateway;
import org.axonframework.config.Configuration;
import org.axonframework.config.DefaultConfigurer;
import org.axonframework.config.EventHandlingConfiguration;
import org.axonframework.eventsourcing.eventstore.EmbeddedEventStore;
import org.axonframework.eventsourcing.eventstore.inmemory.InMemoryEventStorageEngine;
import org.axonframework.queryhandling.QueryGateway;
import org.axonframework.queryhandling.responsetypes.ResponseTypes;
import org.slf4j.Logger;

import java.util.concurrent.ExecutionException;

import static org.slf4j.LoggerFactory.getLogger;

/**
 * Hello world!
 */
public class App {
    private static final Logger log = getLogger(App.class);

    public void runServer() throws ExecutionException, InterruptedException {
        CardSummaryProjection projection = new CardSummaryProjection();
        EventHandlingConfiguration eventHandlingConfiguration = new EventHandlingConfiguration();
        eventHandlingConfiguration.registerEventHandler(c -> projection);

        Configuration configuration = DefaultConfigurer.defaultConfiguration()
                .configureAggregate(GiftCard.class) // (1)
                .configureEventStore(c -> new EmbeddedEventStore(new InMemoryEventStorageEngine())) //(2)
                .registerModule(eventHandlingConfiguration) // (3)
                .registerQueryHandler(c -> projection) // (4)
                .buildConfiguration(); // (5)
        configuration.start();

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
    public static void main(String[] args) throws ExecutionException, InterruptedException {
        System.out.println("Hello World!");
        App app = new App();
        app.runServer();
    }
}
