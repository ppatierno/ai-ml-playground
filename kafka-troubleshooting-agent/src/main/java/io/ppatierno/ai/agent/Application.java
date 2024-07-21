/*
 * Copyright Paolo Patierno.
 * License: Apache License 2.0 (see the file LICENSE or http://apache.org/licenses/LICENSE-2.0.html).
 */
package io.ppatierno.ai.agent;

import dev.langchain4j.memory.chat.MessageWindowChatMemory;
import dev.langchain4j.model.azure.AzureOpenAiChatModel;
import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.model.localai.LocalAiChatModel;
import dev.langchain4j.model.ollama.OllamaChatModel;
import dev.langchain4j.model.openai.OpenAiChatModel;
import dev.langchain4j.service.AiServices;
import io.ppatierno.ai.tools.KafkaAdminTool;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.time.Duration;

public class Application {

    private static final Logger LOGGER = LogManager.getLogger(Application.class);


    // with running localAI on laptop but interacting via standard OpenAI API
    /*private static ChatLanguageModel model = OpenAiChatModel.builder()
            .baseUrl("http://localhost:8080")
            //.modelName("mistral-7b-instruct-v0.3")
            .modelName("LocalAI-llama3-8b-function-call-v0.2")
            .apiKey(System.getenv("OPENAI_API_KEY"))
            .timeout(Duration.ofHours(1))
            .logRequests(true)
            .logResponses(true)
            .build();*/

    // with running localAI using specific localAI API (not working)
    /*private static ChatLanguageModel model = LocalAiChatModel.builder()
            .baseUrl("http://localhost:8080")
            //.modelName("mistral-7b-instruct-v0.3")
            .modelName("LocalAI-llama3-8b-function-call-v0.2")
            .maxTokens(3)
            .timeout(Duration.ofHours(1))
            .logRequests(true)
            .logResponses(true)
            .build();*/

    // to use with Azure OpenAI service
    private static ChatLanguageModel model = AzureOpenAiChatModel.builder()
            .apiKey(System.getenv("AZURE_OPENAI_KEY"))
            .endpoint(System.getenv("AZURE_OPENAI_ENDPOINT"))
            .deploymentName(System.getenv("AZURE_OPENAI_DEPLOYMENT_NAME"))
            .logRequestsAndResponses(true)
            .build();

    // with running ollama locally (not working, langchain4j doesn't support tools with it)
    /*private static ChatLanguageModel model = OllamaChatModel.builder()
            .baseUrl("http://localhost:11434")
            .modelName("mistral")
            .timeout(Duration.ofHours(1))
            .logRequests(true)
            .logResponses(true)
            .build();*/

    public static void main(String[] args) throws Exception {
        KafkaTroubleshooterAgent kafkaTroubleshooterAgent = AiServices.builder(KafkaTroubleshooterAgent.class)
                .chatLanguageModel(model)
                .chatMemory(MessageWindowChatMemory.withMaxMessages(10))
                .tools(new KafkaAdminTool())
                .build();

        /*
        String answer = kafkaTroubleshooterAgent.chat("Hi, please return the topics list for the Apache Kafka cluster with bootstrap servers localhost:9092");
        LOGGER.info(answer);
        answer = kafkaTroubleshooterAgent.chat("Describe the topic with name \"my-topic\".");
        LOGGER.info(answer);
        */

        KafkaTroubleshooterServer kafkaTroubleshooterServer = new KafkaTroubleshooterServer(kafkaTroubleshooterAgent);
        kafkaTroubleshooterServer.startHttpServer();
        LOGGER.info("KafkaTroubleshooterServer started!");

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            try {
                kafkaTroubleshooterServer.stopHttpServer();
                LOGGER.info("KafkaTroubleshooterServer stopped!");
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }));
    }
}
