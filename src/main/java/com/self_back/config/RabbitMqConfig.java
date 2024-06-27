package com.self_back.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMqConfig {
    public static final String TOPIC_EXCHANGE_NAME = "selfpan";

    public static final String FILE_QUEUE_NAME = "upload_file_queue";

    public static final String FILE_ROUTING_KEY = "upload_file_key";
    public static final String SQL_AND_REDIS_QUEUE_NAME = "sql_and_redis_queue";
    public static final String SQL_AND_REDIS_ROUTING_KEY = "sql_and_redis_key";

    @Bean
    Queue fileQueue() {
        return new Queue(FILE_QUEUE_NAME, true);
    }
    @Bean
    Queue sqlAndRedisQueue() {
        return new Queue(SQL_AND_REDIS_QUEUE_NAME, true);
    }
    @Bean
    TopicExchange exchange() {
        return new TopicExchange(TOPIC_EXCHANGE_NAME);
    }


    @Bean
    Binding fileBinding(Queue fileQueue, TopicExchange exchange) {
        return BindingBuilder.bind(fileQueue).to(exchange).with(FILE_ROUTING_KEY);
    }
    @Bean
    Binding sqlAndRedisBinding(Queue sqlAndRedisQueue, TopicExchange exchange) {
        return BindingBuilder.bind(sqlAndRedisQueue).to(exchange).with(SQL_AND_REDIS_ROUTING_KEY);
    }

}