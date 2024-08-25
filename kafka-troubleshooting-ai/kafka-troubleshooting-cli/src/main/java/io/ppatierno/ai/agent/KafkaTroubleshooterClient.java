/*
 * Copyright Paolo Patierno.
 * License: Apache License 2.0 (see the file LICENSE or http://apache.org/licenses/LICENSE-2.0.html).
 */
package io.ppatierno.ai.agent;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

/**
 * Represents a client for the Kafka troubleshooter agent
 * by using HTTP protocol to communicate with it
 */
public class KafkaTroubleshooterClient {

    private final HttpClient client;
    private final URI agentEndpoint;

    /**
     * Constructor
     *
     * @param agentEndpoint endpoint of the Kafka troubleshooter agent
     * @throws URISyntaxException
     */
    public KafkaTroubleshooterClient(String agentEndpoint) throws URISyntaxException {
        this.agentEndpoint = new URI(agentEndpoint);
        this.client = HttpClient.newHttpClient();
    }

    /**
     * Send a request to the Kafka troubleshooter agent
     *
     * @param question  question to ask to the agent
     * @return  answer got from the agent
     * @throws IOException
     * @throws InterruptedException
     */
    public String chat(String question) throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(this.agentEndpoint)
                .headers("Content-Type", "text/plain;charset=UTF-8")
                .POST(HttpRequest.BodyPublishers.ofString(question))
                .build();

        HttpResponse<String> response = this.client.send(request, HttpResponse.BodyHandlers.ofString());
        return response.body();
    }
}
