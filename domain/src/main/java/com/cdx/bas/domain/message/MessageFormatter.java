package com.cdx.bas.domain.message;

import lombok.experimental.UtilityClass;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import org.apache.commons.lang3.StringUtils;

/**
 * Utility class for formatting standardized messages used in various operations, following the structure:
 * <code>{context} {action} {status} {optional cause} {optional details}</code>.
 * Each optional detail appears on a new line.
 * This class enables consistent and clear formatting for logs and error messages across the application.
 */
@UtilityClass
public class MessageFormatter {

    /**
     * A constant for a single space character used to separate message components.
     */
    public static final String SEPARATOR = "-";

    /**
     * Formats a message with context, action, status, an optional cause, and optional details.
     * The message follows the structure: <code>{context} {action} {status} {optional cause}</code>,
     * with each detail on a new line if provided.
     *
     * @param context       the main area involved (e.g., "Bank account:")
     * @param action        the action being performed (e.g., "creation")
     * @param status        the outcome of the action (e.g., "failed")
     * @param optionalCause an optional explanation for issues (e.g., "missing required field")
     * @param details       an optional list of extra details, each appearing on a new line
     * @return a formatted message string based on the provided inputs
     */
    public String format(String context, String action, String status, Optional<String> optionalCause, List<String> details) {
        StringBuilder message = new StringBuilder();
        message.append(context)
                .append(StringUtils.SPACE)
                .append(action)
                .append(StringUtils.SPACE)
                .append(status);

        optionalCause.ifPresent(cause -> message
                .append(StringUtils.SPACE)
                .append(SEPARATOR)
                .append(StringUtils.SPACE)
                .append(cause));

        details.forEach(detail -> message.append(System.lineSeparator()).append(detail));

        return message.toString();
    }

    /**
     * Formats a message with context, action, status, and an optional cause, without additional details.
     *
     * @param context       the main area involved (e.g., "Transaction:")
     * @param action        the action being performed (e.g., "debit")
     * @param status        the outcome of the action (e.g., "completed")
     * @param optionalCause an optional explanation for issues (e.g., "insufficient funds")
     * @return a formatted message string with the cause if specified
     */
    public String format(String context, String action, String status, Optional<String> optionalCause) {
        return format(context, action, status, optionalCause, Collections.emptyList());
    }

    /**
     * Formats a message with context, action, status, and an optional list of details, without a cause.
     *
     * @param context the main area involved (e.g., "Bank account:")
     * @param action  the action being performed (e.g., "update")
     * @param status  the outcome of the action (e.g., "in progress")
     * @param details an optional list of details, each appearing on a new line
     * @return a formatted message string with context, action, status, and details if provided
     */
    public String format(String context, String action, String status, List<String> details) {
        return format(context, action, status, Optional.empty(), details);
    }

    /**
     * Formats a message with context, action, and status only, without a cause or additional details.
     *
     * @param context the main area involved (e.g., "Bank account:")
     * @param action  the action being performed (e.g., "update")
     * @param status  the outcome of the action (e.g., "success")
     * @return a formatted message string with just context, action, and status
     */
    public String format(String context, String action, String status) {
        return format(context, action, status, Optional.empty(), Collections.emptyList());
    }
}
