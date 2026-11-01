package com.asmaeel.tazkara.event.config;

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

    @Value("${rabbitmq.exchange}")
    private String tazkaraExchangeName;

    @Value("${rabbitmq.routing-key.availability-changed}")
    private String availabilityChangedRoutingKey;


    @Bean
    public Queue availabilityChangedQueue() {
        return new Queue(availabilityQueueName, true);
    }

    @Bean
    public TopicExchange tazkaraExchange() {
        return new TopicExchange(tazkaraExchangeName);
    }

    @Bean
    public Binding availabilityChangedBinding() {
        return BindingBuilder.bind(availabilityChangedQueue())
                .to(tazkaraExchange())
                .with(availabilityChangedRoutingKey);
    }

    @Bean
    public MessageConverter jsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }
}
