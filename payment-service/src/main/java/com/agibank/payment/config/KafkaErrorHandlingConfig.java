package com.agibank.payment.config;

import com.agibank.payment.domain.model.dto.PaymentEvent;
import com.agibank.payment.domain.exception.PaymentException;
import org.apache.kafka.common.TopicPartition;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.dao.TransientDataAccessException;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.listener.DeadLetterPublishingRecoverer;
import org.springframework.kafka.listener.DefaultErrorHandler;

import java.util.List;

@Configuration
public class KafkaErrorHandlingConfig {

    @Bean
    public DefaultErrorHandler errorHandler(DeadLetterPublishingRecoverer recoverer) {
        var exceptionsToIgnore = List.of(
                IllegalArgumentException.class,
                PaymentException.class
        );

        var exceptionsToRetry = List.of(
                TransientDataAccessException.class
        );

//        FixedBackOff backOff = new FixedBackOff(1000L, 3);
//        return new DefaultErrorHandler(recoverer, backOff)
//            .excludeRecordExceptions(exceptionsToIgnore.toArray(new Class[0]))
//            .retryOn(exceptionsToRetry);
        return new DefaultErrorHandler(recoverer);
    }

    @Bean
    public DeadLetterPublishingRecoverer recoverer(KafkaTemplate<String, PaymentEvent> template) {
        return new DeadLetterPublishingRecoverer(template,
                (record, ex) -> new TopicPartition(record.topic() + ".DLT", record.partition()));
    }
}
