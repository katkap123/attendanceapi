package com.katta.attendance.config;

import com.katta.attendance.event.AttendanceRecordedEvent;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.support.serializer.JsonSerializer;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class KafkaProducerConfig {

    @Value("${spring.kafka.bootstrap-servers}")
    private String bootstrapServers;

    @Value("${spring.kafka.properties.security.protocol:PLAINTEXT}")
    private String securityProtocol;

    @Value("${spring.kafka.properties.sasl.mechanism:PLAIN}")
    private String saslMechanism;

    @Value("${spring.kafka.properties.sasl.jaas.config:}")
    private String saslJaasConfig;

    @Value("${spring.kafka.properties.ssl.truststore.type:PEM}")
    private String truststoreType;

    @Value("${spring.kafka.properties.ssl.truststore.certificates:}")
    private String truststoreCertificates;

    @Bean
    public ProducerFactory<String, AttendanceRecordedEvent>
            attendanceProducerFactory() {

        Map<String, Object> config = new HashMap<>();

        config.put(
                ProducerConfig.BOOTSTRAP_SERVERS_CONFIG,
                bootstrapServers
        );

        config.put(
                ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG,
                StringSerializer.class
        );

        config.put(
                ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG,
                JsonSerializer.class
        );

        config.put(
                "security.protocol",
                securityProtocol
        );

        if (!"PLAINTEXT".equalsIgnoreCase(securityProtocol)) {

            config.put(
                    "sasl.mechanism",
                    saslMechanism
            );

            config.put(
                    "sasl.jaas.config",
                    saslJaasConfig
            );

            config.put(
                    "ssl.truststore.type",
                    truststoreType
            );

            config.put(
                    "ssl.truststore.certificates",
                    truststoreCertificates
            );
        }

        return new DefaultKafkaProducerFactory<>(config);
    }

    @Bean
    public KafkaTemplate<String, AttendanceRecordedEvent>
            attendanceKafkaTemplate() {

        return new KafkaTemplate<>(
                attendanceProducerFactory()
        );
    }
}