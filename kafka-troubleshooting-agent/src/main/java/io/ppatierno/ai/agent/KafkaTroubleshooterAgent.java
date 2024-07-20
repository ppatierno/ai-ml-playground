/*
 * Copyright Paolo Patierno.
 * License: Apache License 2.0 (see the file LICENSE or http://apache.org/licenses/LICENSE-2.0.html).
 */
package io.ppatierno.ai.agent;

import dev.langchain4j.service.SystemMessage;

/**
 * Interface declaration for an AI Service able to troubleshooting on a Kafka cluster
 */
public interface KafkaTroubleshooterAgent {

    @SystemMessage({
            "You are a virtual assistant capable of troubleshooting an Apache Kafka cluster.",
            "You MUST use the getClusterId function if a user asks to return the clusterId.",
            "You MUST use the getTopics function if a user asks to return the topics list.",
            "You MUST use the describeTopic function if a user asks to describe a topic providing its name."
    })
    String chat(String userMessage);
}
