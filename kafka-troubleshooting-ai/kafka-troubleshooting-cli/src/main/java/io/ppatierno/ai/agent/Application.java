/*
 * Copyright Paolo Patierno.
 * License: Apache License 2.0 (see the file LICENSE or http://apache.org/licenses/LICENSE-2.0.html).
 */
package io.ppatierno.ai.agent;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Scanner;

public class Application {

    private static final Logger LOGGER = LogManager.getLogger(Application.class);

    public static void main(String[] args) throws URISyntaxException, IOException, InterruptedException {
        KafkaTroubleshooterClient client = new KafkaTroubleshooterClient("http://localhost:8080/v1/chat/");

        try (Scanner scanner = new Scanner(System.in)) {
            while (true) {
                LOGGER.info("User: ");
                String userQuery = scanner.nextLine();

                if ("exit".equalsIgnoreCase(userQuery)) {
                    break;
                }

                String agentAnswer = client.chat(userQuery);
                LOGGER.info("Assistant: \n{}", agentAnswer);
            }
        }
    }
}
