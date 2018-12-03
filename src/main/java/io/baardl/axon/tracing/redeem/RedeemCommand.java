package io.baardl.axon.tracing.redeem;

import org.axonframework.commandhandling.TargetAggregateIdentifier;

public class RedeemCommand {

        @TargetAggregateIdentifier // (1)
        private final String id;
        private final Integer amount;

        public RedeemCommand(String id, Integer amount) {
            this.id = id;
            this.amount = amount;
        }

        public String getId() {
            return id;
        }

        public Integer getAmount() {
            return amount;
        }
}
