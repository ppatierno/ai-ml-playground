/*
 * Copyright Paolo Patierno.
 * License: Apache License 2.0 (see the file LICENSE or http://apache.org/licenses/LICENSE-2.0.html).
 */
package io.ppatierno.ai.service;

import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.admin.AdminClientConfig;
import org.apache.kafka.clients.admin.KafkaAdminClient;

import java.util.Properties;

/**
 * Represents a service providing a Kafka Admin Client for a specific Kafka cluster
 */
public class KafkaAdminService {

    private String bootstrapServers;
    private AdminClient adminClient;

    /**
     * Creates a new instance of a Kafka Admin client configured to connect to a Kafka cluster
     * with the provided bootstrap servers if it doesn't exist already, or return an existing one.
     * If called multiple times with a different bootstrap servers, the current Kafka Admin client
     * instance is closed and a new one is created.
     *
     * @param bootstrapServers  Bootstrap servers to connect to the Kafka cluster to administrate
     * @return  Kafka Admin client instance
     */
    public AdminClient adminClient(String bootstrapServers) {
        if (this.adminClient == null) {
            return this.create(bootstrapServers);
        } else {
            if (!this.bootstrapServers.equals(bootstrapServers)) {
                this.adminClient.close();
                return this.create(bootstrapServers);
            } else {
                return this.adminClient;
            }
        }
    }

    private AdminClient create(String bootstrapServers) {
        Properties props = new Properties();
        props.put(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        this.bootstrapServers = bootstrapServers;
        this.adminClient = KafkaAdminClient.create(props);
        return this.adminClient;
    }
}
