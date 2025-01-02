package com.cdx.bas.producer.requestor;

import com.cdx.bas.domain.bank.transaction.category.NewDigitalTransaction;
import com.cdx.bas.mapper.CustomObjectMapper;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import static com.arjuna.ats.jbossatx.logging.jbossatxLogger.logger;

public class TransactionSender {
    public static void send(NewDigitalTransaction transaction) {
        try {
            // Create an HttpClient instance
            HttpClient client = HttpClient.newHttpClient();
            String payload = CustomObjectMapper.getCustomObjectMapper().writeValueAsString(transaction);

            // Define the request
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(new URI("http://localhost:8080/transactions/digital"))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(payload))
                    .build();

            // Send the request and get the response
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            // Print the response
            logger.info("Response Code: " + response.statusCode());
            logger.info("Response Body: " + response.body());
        } catch (Exception exception) {
            logger.info("Impossible to send transaction from id " + transaction.emitterAccountId() + ": " + exception.getCause());
        }
    }
}
