package com.cdx.bas.domain.message;

import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static com.cdx.bas.domain.message.CommonMessages.*;
import static org.assertj.core.api.Assertions.assertThat;

class MessageFormatterTest {

    @Test
    void should_formatMessageWithAllArgs() {
        String context = "Bank account:";
        String action = "creation";
        String status = "failed";
        Optional<String> cause = Optional.of("missing required field 'account name'");
        List<String> details = List.of("Detail: account id 1", "Detail: field required");

        String expected = "Bank account: creation failed missing required field 'account name'"
                + System.lineSeparator() + "Detail: account id 1"
                + System.lineSeparator() + "Detail: field required";

        assertThat(MessageFormatter.format(context, action, status, cause, details)).isEqualTo(expected);
    }

    @Test
    void should_formatMessageWithCauseOnly() {
        String context = "Transaction:";
        String action = "debit";
        String status = "failed";
        Optional<String> cause = Optional.of("insufficient funds");

        String expected = "Transaction: debit failed insufficient funds";

        assertThat(MessageFormatter.format(context, action, status, cause, Collections.emptyList())).isEqualTo(expected);
    }

    @Test
    void should_formatMessageWithDetails() {
        List<String> details = List.of("Detail: account id 2", "Detail: credited amount $500");

        String expected = "Transaction: credit completed"
                + System.lineSeparator() + "Detail: account id 2"
                + System.lineSeparator() + "Detail: credited amount $500";

        assertThat(MessageFormatter.format(TRANSACTION_CONTEXT, CREDIT_ACTION, COMPLETED_STATUS, Optional.empty(), details)).isEqualTo(expected);
    }

    @Test
    void should_formatMessageWithMandatoryValues() {
        String context = "Bank account:";
        String action = "update";
        String status = "success";

        String expected = "Bank account: update success";

        assertThat(MessageFormatter.format(context, action, status)).isEqualTo(expected);
    }

    @Test
    void should_formatMessageWithCauseUsingOverloadedMethod() {
        String context = "Transaction:";
        String action = "withdrawal";
        String status = "failed";
        Optional<String> cause = Optional.of("account locked");

        String expected = "Transaction: withdrawal failed account locked";

        assertThat(MessageFormatter.format(context, action, status, cause)).isEqualTo(expected);
    }

    @Test
    void should_formatMessageWithEmptyCauseAndDetails() {
        String context = "Bank account:";
        String action = "deletion";
        String status = "failed";
        Optional<String> cause = Optional.empty();

        String expected = "Bank account: deletion failed";

        assertThat(MessageFormatter.format(context, action, status, cause, Collections.emptyList())).isEqualTo(expected);
    }
}