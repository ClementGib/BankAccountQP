package com.cdx.bas.domain.bank.transaction.id;

import java.util.Objects;

public class IdPair {
    private Long emitterAccountId;
    private Long receiverAccountId;

    public IdPair(Long emitterAccountId, Long receiverAccountId) {
        this.emitterAccountId = emitterAccountId;
        this.receiverAccountId = receiverAccountId;
    }

    public Long getEmitterAccountId() {
        return emitterAccountId;
    }

    public void setEmitterAccountId(Long emitterAccountId) {
        this.emitterAccountId = emitterAccountId;
    }

    public Long getReceiverAccountId() {
        return receiverAccountId;
    }

    public void setReceiverAccountId(Long receiverAccountId) {
        this.receiverAccountId = receiverAccountId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        IdPair idPair = (IdPair) o;
        return Objects.equals(emitterAccountId, idPair.emitterAccountId) && Objects.equals(receiverAccountId, idPair.receiverAccountId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(emitterAccountId, receiverAccountId);
    }
}
