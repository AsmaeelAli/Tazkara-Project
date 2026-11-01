package com.asmaeel.tazkara.event.config;

import com.asmaeel.tazkara.shared.messaging.MessagingConstants;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMqConfig {

    @Value("${rabbitmq.queue.availability-changed}")
    private String availabilityQueueName;


    @Bean
    public Queue availabilityChangedQueue() {
        return new Queue(availabilityQueueName, true);
    }

    @Bean
    public TopicExchange tazkaraExchange() {
        return new TopicExchange(MessagingConstants.EXCHANGE);
    }

    @Bean
    public Binding availabilityChangedBinding(Queue availabilityChangedQueue, TopicExchange tazkaraExchange) {
        return BindingBuilder.bind(availabilityChangedQueue)
                .to(tazkaraExchange)
                .with(MessagingConstants.AVAILABILITY_CHANGED_KEY);
    }

    @Bean
    public MessageConverter jsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }
}
