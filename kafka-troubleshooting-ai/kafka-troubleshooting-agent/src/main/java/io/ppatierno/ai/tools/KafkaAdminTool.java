/*
 * Copyright Paolo Patierno.
 * License: Apache License 2.0 (see the file LICENSE or http://apache.org/licenses/LICENSE-2.0.html).
 */
package io.ppatierno.ai.tools;

import dev.langchain4j.agent.tool.P;
import dev.langchain4j.agent.tool.Tool;
import io.ppatierno.ai.service.KafkaAdminService;
import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.admin.TopicDescription;
import org.apache.kafka.clients.admin.TopicListing;
import org.apache.kafka.common.TopicCollection;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

/**
 * Provides a series of tools used by the LLM to leverage the KafkaAdminService
 * for administration operations on the Kafka cluster
 */
public class KafkaAdminTool {

    private static final Logger LOGGER = LogManager.getLogger(KafkaAdminTool.class);

    private KafkaAdminService kafkaAdminService = new KafkaAdminService();

    @Tool("Return the clusterId for an Apache Kafka cluster with the provided bootstrap servers")
    public String getClusterId(@P("Bootstrap servers for the Apache Kafka cluster") String bootstrapServers) throws ExecutionException, InterruptedException {
        LOGGER.info("getClusterId on {}", bootstrapServers);
        AdminClient adminClient = kafkaAdminService.adminClient(bootstrapServers);
        String clusterId = adminClient.describeCluster().clusterId().get();
        return clusterId;
    }

    @Tool("Return the list of topics for an Apache Kafka cluster with the provided bootstrap servers")
    public Collection<TopicListing> getTopics(@P("Bootstrap servers for the Apache Kafka cluster") String bootstrapServers) throws ExecutionException, InterruptedException {
        LOGGER.info("getTopics on {}", bootstrapServers);
        AdminClient adminClient = kafkaAdminService.adminClient(bootstrapServers);
        Collection<TopicListing> topics = adminClient.listTopics().listings().get();
        return topics;
    }

    @Tool("Describe the topic for which the name is provided within an Apache Kafka cluster with the provided bootstrap servers")
    public Map<String, TopicDescription> describeTopic(@P("Bootstrap servers for the Apache Kafka cluster") String bootstrapServers, @P("Name of the topic to describe") String topic) throws ExecutionException, InterruptedException {
        LOGGER.info("describeTopics on {}", bootstrapServers);
        AdminClient adminClient = kafkaAdminService.adminClient(bootstrapServers);
        Map<String, TopicDescription> topics = adminClient.describeTopics(TopicCollection.ofTopicNames(List.of(topic))).allTopicNames().get();
        return topics;
    }
}
