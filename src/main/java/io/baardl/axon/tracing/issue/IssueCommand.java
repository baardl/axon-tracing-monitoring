package io.baardl.axon.tracing.issue;

public class IssueCommand {

    private final String id;
    private final Integer amount;

    public IssueCommand(String id, Integer amount) {
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
