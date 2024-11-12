package com.cdx.bas.domain.message;

import com.cdx.bas.domain.testing.Generated;

@Generated
public class CommonMessages {
    private CommonMessages() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }

    // Context
    public static final String SCHEDULER_CONTEXT = "Scheduler:";
    public static final String BANK_ACCOUNT_CONTEXT = "Bank account:";
    public static final String CUSTOMER_CONTEXT = "Customer:";
    public static final String TRANSACTION_CONTEXT = "Transaction:";
    public static final String CREDIT_TRANSACTION_CONTEXT = "Credit transaction:";
    public static final String DEBIT_TRANSACTION_CONTEXT = "Debit transaction:";
    public static final String DEPOSIT_TRANSACTION_CONTEXT = "Deposit transaction:";
    public static final String WITHDRAW_TRANSACTION_CONTEXT = "Withdraw transaction:";

    // Action
    public static final String STARTING_ACTION = "starting";
    public static final String ENDING_ACTION = "ending";
    public static final String PROCESS_ACTION = "process";
    public static final String SEARCHING_ACTION = "searching";
    public static final String SEARCHING_ALL_ACTION = "searching all";
    public static final String CREATION_ACTION = "creation";
    public static final String UPDATE_ACTION = "update";
    public static final String DELETION_ACTION = "delete";
    public static final String DEBIT_ACTION = "debit";
    public static final String CREDIT_ACTION = "credit";
    public static final String DIGITAL_TRANSACTION_ACTION = "digital transaction";
    public static final String DEPOSIT_ACTION = "deposit";
    public static final String WITHDRAW_ACTION = "withdraw";
    public static final String CASH_TRANSACTION_ACTION = "cash transaction";
    public static final String OUTSTANDING_STATUS_ACTION = "set status to outstanding";
    public static final String CHANGE_STATUS_ACTION = "set status";
    public static final String JSON_PARSE_METADATA = "parse JSON metadata to Map";
    public static final String MAP_PARSE_METADATA = "parse map metadata to JSON";

    // Status
    public static final String DONE_STATUS = "done";
    public static final String IN_PROGRESS_STATUS = "in progress...";
    public static final String SUCCESS_STATUS = "success";
    public static final String FAILED_STATUS = "failed";
    public static final String REFUSED_STATUS = "refused";
    public static final String COMPLETED_STATUS = "completed";
    public static final String ACCEPTED_STATUS = "accepted";
    public static final String UNEXPECTED_STATUS = "unexpected error";
    public static final String NO_LONGER_UNPROCESSED_STATUS = "no longer unprocessed";
    public static final String IS_NULL_STATUS = "is null";

    // Cause
    public static final String NOT_FOUND_CAUSE = "not found";
    public static final String MISSING_ID_CAUSE = "missing id";
    public static final String TRANSACTION_ERROR_CAUSE = "transaction error";
    public static final String DOMAIN_ERROR = "domain error";
    public static final String BANK_ACCOUNT_ERROR_CAUSE = "bank account error";
    public static final String SHOULD_HAVE_POSITIVE_VALUE_CAUSE = "should have positive value";
    public static final String UNEXPECTED_ERROR_CAUSE = "unexpected error";

    // Details
    public static final String QUEUE_DETAIL = "Queue size:";
    public static final String TRANSACTION_ID_DETAIL = "Transaction id:";
    public static final String BANK_ACCOUNT_ID_DETAIL = "Bank account id:";
    public static final String CUSTOMER_ID_DETAIL = "Customer id:";
    public static final String EURO_AMOUNT_DETAIL = "Euro amount:";
    public static final String STATUS_DETAIL = "Status:";
    public static final String ERROR_DETAIL = "Error:";
    public static final String EMITTER_BANK_ACCOUNT_DETAIL = "emitter bank account";
    public static final String RECEIVER_BANK_ACCOUNT_DETAIL = "receiver bank account";
    public static final String DEPOSIT_DETAIL = "deposit:";
    public static final String WITHDRAW_DETAIL = "withdraw:";
    public static final String DEBIT_DETAIL = "debit:";
    public static final String CREDIT_DETAIL = "credit:";
}
