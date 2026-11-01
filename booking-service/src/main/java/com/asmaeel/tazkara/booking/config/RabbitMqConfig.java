package com.asmaeel.tazkara.booking.config;

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

    //اذا ضل وقت رح اعملهم ما ضلش للاسف راحت علينا US-9 !!!

    //////////////////////////////////////////////////////

    @Value("${rabbitmq.queue.event-published}")
    private String eventPublishedQueueName;

    @Value("${rabbitmq.queue.event-cancelled}")
    private String eventCancelledQueueName;

    ///////////////////////////////////////////////////////

    @Value("${rabbitmq.routing-key.event-published}")
    private String eventPublishedRoutingKey;

    @Value("${rabbitmq.routing-key.event-cancelled}")
    private String eventCancelledRoutingKey;

    @Value("${rabbitmq.exchange}")
    private String exchange;

    @Bean
    public Queue eventPublishedQueue() {
        return new Queue(eventPublishedQueueName, true);
    }

    @Bean
    public Queue eventCancelledQueue() {
        return new Queue(eventCancelledQueueName, true);
    }


    @Bean
    public TopicExchange tazkaraExchange() {
        return new TopicExchange(exchange);
    }

    @Bean
    public Binding eventPublishedBinding(Queue eventPublishedQueue, TopicExchange tazkaraExchange) {
        return BindingBuilder.bind(eventPublishedQueue)
                .to(tazkaraExchange)
                .with(eventPublishedRoutingKey);
    }

    @Bean
    public Binding eventCancelledBinding(Queue eventCancelledQueue, TopicExchange tazkaraExchange) {
        return BindingBuilder.bind(eventCancelledQueue)
                .to(tazkaraExchange)
                .with(eventCancelledRoutingKey);
    }

    @Bean
    public MessageConverter jsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }


}
