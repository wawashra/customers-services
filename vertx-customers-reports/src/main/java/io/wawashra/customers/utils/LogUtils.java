package io.wawashra.customers.utils;

public enum LogUtils {

    REGULAR_CALL_SUCCESS_MESSAGE("%s called w/ success - %s"),
    REGULAR_CALL_ERROR_MESSAGE("%s called w/ error - %s"),
    NO_CUSTOMER_WITH_ID_MESSAGE("No customer with id %d"),
    RUN_HTTP_SERVER_SUCCESS_MESSAGE("HTTP server running on port %s"),
    RUN_HTTP_SERVER_ERROR_MESSAGE("Cannot run HTTP server"),
    RUN_APP_SUCCESSFULLY_MESSAGE("vertx-customers-crud-rest-api service started successfully in %d ms");

    private final String message;

    LogUtils(final String message) {
        this.message = message;
    }

    public String buildMessage(Object... argument) {
        return String.format(message, argument);
    }

}
