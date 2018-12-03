package io.baardl.axon.tracing.giftcard;

public class CardSummary {

    private final String id;
    private final Integer initialAmount;
    private final Integer remainingAmount;

    public CardSummary(String id, Integer initialAmount, Integer remainingAmount) {
        this.id = id;
        this.initialAmount = initialAmount;
        this.remainingAmount = remainingAmount;
    }

    public String getId() {
        return id;
    }

    public Integer getInitialAmount() {
        return initialAmount;
    }

    public Integer getRemainingAmount() {
        return remainingAmount;
    }

    public CardSummary deductAmount(Integer toBeDeducted) {
        return new CardSummary(id, initialAmount, remainingAmount - toBeDeducted);
    }

    @Override
    public String toString() {
        return "CardSummary{" +
                "id='" + id + '\'' +
                ", initialAmount=" + initialAmount +
                ", remainingAmount=" + remainingAmount +
                '}';
    }
}
