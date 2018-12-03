package io.baardl.axon.tracing.giftcard;

import io.baardl.axon.tracing.issue.IssuedEvent;
import io.baardl.axon.tracing.redeem.RedeemedEvent;
import org.axonframework.eventhandling.EventHandler;
import org.axonframework.queryhandling.QueryHandler;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Collectors;

public class CardSummaryProjection {

    private final List<CardSummary> cardSummaries = new CopyOnWriteArrayList<>();

    @EventHandler // (1)
    public void on(IssuedEvent evt) {
        CardSummary cardSummary = new CardSummary(evt.getId(), evt.getAmount(), evt.getAmount());
        cardSummaries.add(cardSummary);
    }

    @EventHandler
    public void on(RedeemedEvent evt) {
        cardSummaries.stream()
                .filter(cs -> evt.getId().equals(cs.getId()))
                .findFirst()
                .ifPresent(cardSummary -> {
                    CardSummary updatedCardSummary = cardSummary.deductAmount(evt.getAmount());
                    cardSummaries.remove(cardSummary);
                    cardSummaries.add(updatedCardSummary);
                });
    }

    @QueryHandler // (2)
    public List<CardSummary> fetch(FetchCardSummariesQuery query) {
        return cardSummaries.stream()
                .skip(query.getOffset())
                .limit(query.getSize())
                .collect(Collectors.toList());
    }
}
