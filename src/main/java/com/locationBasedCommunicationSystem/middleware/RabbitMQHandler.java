package com.locationBasedCommunicationSystem.middleware;

import com.rabbitmq.client.*;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.TimeoutException;
import java.util.function.Consumer;

public class RabbitMQHandler {
    private ConnectionFactory factory;
    private Connection connection;
    private Channel channel;
    private String consumerTag;

    public RabbitMQHandler() {
        factory = new ConnectionFactory();
        factory.setHost("localhost");
    }

    public void startConsuming(String queueName, Consumer<String> onMessage) {
        try {
            connection = factory.newConnection();
            channel = connection.createChannel();
            channel.queueDeclare(queueName, false, false, false, null);

            DeliverCallback deliverCallback = (consumerTag, delivery) -> {
                String message = new String(delivery.getBody(), StandardCharsets.UTF_8);
                onMessage.accept(message);
            };
            consumerTag = channel.basicConsume(queueName, true, deliverCallback, consumerTag -> {});
        } catch (IOException | TimeoutException e) {
            e.printStackTrace();
        }
    }

    public void stopConsuming() {
        try {
            if (channel != null && consumerTag != null) {
                channel.basicCancel(consumerTag);
            }
            if (channel != null) {
                channel.close();
            }
            if (connection != null) {
                connection.close();
            }
        } catch (Exception e) {
            // Ignorar erros de fechamento
        }
        channel = null;
        connection = null;
        consumerTag = null;
    }

    public void sendMessage(String queueName, String message) {
        try (Connection conn = factory.newConnection();
             Channel ch = conn.createChannel()) {
            ch.queueDeclare(queueName, false, false, false, null);
            ch.basicPublish("", queueName, null, message.getBytes(StandardCharsets.UTF_8));
        } catch (IOException | TimeoutException e) {
            e.printStackTrace();
        }
    }
}