package io.baardl.axon.tracing.redeem;

public class RedeemedEvent {

    private final String id;
    private final Integer amount;

    public RedeemedEvent(String id, Integer amount) {
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