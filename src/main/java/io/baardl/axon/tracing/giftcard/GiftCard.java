package io.baardl.axon.tracing.giftcard;

import io.baardl.axon.tracing.issue.IssueCommand;
import io.baardl.axon.tracing.issue.IssuedEvent;
import io.baardl.axon.tracing.redeem.RedeemCommand;
import io.baardl.axon.tracing.redeem.RedeemedEvent;
import org.axonframework.commandhandling.CommandHandler;
import org.axonframework.commandhandling.model.AggregateIdentifier;
import org.axonframework.commandhandling.model.AggregateLifecycle;
import org.axonframework.eventsourcing.EventSourcingHandler;

public class GiftCard {

        @AggregateIdentifier
        private String id;
        private int remainingValue;

        public GiftCard() {
            // Needed by Axon
        }

        @CommandHandler
        public GiftCard(IssueCommand Command) {
            if(Command.getAmount() <= 0) throw new IllegalArgumentException("amount <= 0");
            AggregateLifecycle.apply(new IssuedEvent(Command.getId(), Command.getAmount()));
        }

        @EventSourcingHandler
        public void on(IssuedEvent Event) {
            id = Event.getId();
            remainingValue = Event.getAmount();
        }

        @CommandHandler
        public void handle(RedeemCommand Command) {
            if(Command.getAmount() <= 0) throw new IllegalArgumentException("amount <= 0");
            if(Command.getAmount() > remainingValue) throw new IllegalStateException("amount > remaining value");
            AggregateLifecycle.apply(new RedeemedEvent(id, Command.getAmount()));
        }

        @EventSourcingHandler
        public void on(RedeemedEvent Event) {
            remainingValue -= Event.getAmount();
        }
}
