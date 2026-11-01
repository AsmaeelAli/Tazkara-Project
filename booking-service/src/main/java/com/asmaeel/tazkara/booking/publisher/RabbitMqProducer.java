package com.asmaeel.tazkara.booking.publisher;

import com.asmaeel.tazkara.shared.dto.AvailabilityChangedMessageDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class RabbitMqProducer {

    private static final Logger log = LoggerFactory.getLogger(RabbitMqProducer.class);

    private final RabbitTemplate rabbitTemplate;

    @Value("${rabbitmq.exchange}")
    private String exchange;

    @Value("${rabbitmq.routing-key.availability-changed}")
    private String availabilityChangedRoutingKey;


    public RabbitMqProducer(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }
    /**
     *
     * فهمتها بهاي الطريقة EXCHANGE نعتبره ساعي البريد للخدمة كلها
     *
     *  هون كانه بنقله يا ساعي ارسلي هاي الرسالة في البريد الي اسمه AVAILABILITY_CHANGED_KEY
     *
     *  يعني الراوتر كي او ممكن نحكي عنوان الرسالة
     *
     *  كيف مثلا بتكتب رسائل وبتعطيها للبريد وهو بقرا هاي لمصر وهاي للعراق وهاي لاي مكان
     *
     * */

    public void publishAvailabilityChanged(AvailabilityChangedMessageDto message) {
        log.info("Publishing availability change with key {}", availabilityChangedRoutingKey);
        rabbitTemplate.convertAndSend(exchange, availabilityChangedRoutingKey, message);
    }
}