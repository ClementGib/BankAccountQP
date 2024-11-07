package com.cdx.bas.domain.message;

import lombok.experimental.UtilityClass;
@UtilityClass
public class CommonMessages {
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
    public static final String OUTSTANDING_STATUS_ACTION = "set status to outstanding";
    public static final String CHANGE_STATUS_ACTION = "set status";
    public static final String JSON_PARSE_METADATA = "parse JSON metadata to Map";
    public static final String MAP_PARSE_METADATA = "parse map metadata to JSON";

    // Status
    public static final String DONE_STATUS = "done";
    public static final String IN_PROGRESS_STATUS = "in progress...";
    public static final String SUCCESS_STATUS = "success";
    public static final String FAILED_STATUS = "failed";
    public static final String COMPLETED_STATUS = "completed";
    public static final String ACCEPTED_STATUS = "accepted";
    public static final String UNEXPECTED_STATUS = "unexpected error";
    public static final String NO_LONGER_UNPROCESSED_STATUS = "no longer unprocessed";
    public static final String IS_NULL_STATUS = "is null";

    // Cause
    public static final String NOT_FOUND_CAUSE = "not found";
    public static final String SHOULD_HAVE_POSITIVE_VALUE_CAUSE = "should have positive value";

    // Details
    public static final String QUEUE_DETAIL = "Queue size:";
    public static final String ID_DETAIL = "Id:";
    public static final String EURO_AMOUNT_DETAIL = "Euro amount:";
    public static final String STATUS_DETAIL = "Status:";



    // Common words
    public static final String BANK_ACCOUNT = "bank account";

    // Transaction Content Messages
    public static final String DEPOSIT_OF_CONTENT = "Deposit of ";
    public static final String WITHDRAW_OF_CONTENT = "Withdraw of ";
    public static final String DEBIT_OF_CONTENT = "Debit of ";
    public static final String CREDIT_OF_CONTENT = "Credit of ";
    public static final String FROM_BANK_ACCOUNT_CONTENT = " from bank account ";
    public static final String TO_BANK_ACCOUNT_CONTENT = " to bank account ";
    public static final String WITH_AMOUNT_CONTENT = " with amount ";
    public static final String VALIDATED_END = " validated.";
    public static final String PROCESSING_END = " processing...";
    public static final String FOR_EMITTER = " for emitter ";

    // Error Messages
    public static final String ERROR_CAUSE = "Error missing: ";
    public static final String ERROR_CREATING_ACCOUNT = "Error creating bank account.";
    public static final String ERROR_UPDATING_ACCOUNT = "Error updating bank account.";
    public static final String ERROR_DELETING_ACCOUNT = "Error deleting bank account.";
    public static final String ERROR_READING_ACCOUNT = "Error reading bank account information.";

    // Transaction-Specific Errors
    public static final String INSUFFICIENT_FUNDS = "Insufficient funds for transaction.";
    public static final String INVALID_TRANSACTION_TYPE = "Invalid transaction type specified.";
    public static final String TRANSACTION_LIMIT_EXCEEDED = "Transaction limit exceeded.";
    public static final String NEGATIVE_AMOUNT_ERROR = "Transaction amount cannot be negative.";
    public static final String UNAUTHORIZED_ACCESS = "Unauthorized access to account.";
    public static final String ACCOUNT_NOT_FOUND = "Specified bank account not found.";
    public static final String TRANSACTION_NOT_FOUND = "Transaction record not found.";

    // Successful Operation Messages
    public static final String SUCCESSFUL_CREATION = "Bank account successfully created.";
    public static final String SUCCESSFUL_UPDATE = "Bank account successfully updated.";
    public static final String SUCCESSFUL_DELETION = "Bank account successfully deleted.";
    public static final String SUCCESSFUL_READ = "Bank account information successfully retrieved.";

    public static final String SUCCESSFUL_DEPOSIT = "Deposit transaction completed successfully.";
    public static final String SUCCESSFUL_WITHDRAWAL = "Withdrawal transaction completed successfully.";
    public static final String SUCCESSFUL_CREDIT = "Credit transaction completed successfully.";
    public static final String SUCCESSFUL_DEBIT = "Debit transaction completed successfully.";

    // Validation Messages
    public static final String INVALID_AMOUNT = "Amount must be greater than zero.";
    public static final String NULL_ACCOUNT_ERROR = "Bank account information cannot be null.";
    public static final String ACCOUNT_LOCKED = "Bank account is locked. Please contact support.";
    public static final String AMOUNT_EXCEEDS_LIMIT = "Amount exceeds allowed transaction limit.";

}
