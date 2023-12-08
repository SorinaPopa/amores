import com.rabbitmq.client.*;

import java.sql.Connection;

public class RabbitMQConsumer {

    private final static String QUEUE_NAME = "hello";

    public void consumeMessages() throws Exception {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");

        try (com.rabbitmq.client.Connection connection = factory.newConnection();
             Channel channel = connection.createChannel()) {
            channel.queueDeclare(QUEUE_NAME, false, false, false, null);

            DeliverCallback deliverCallback = (consumerTag, delivery) -> {
                String message = new String(delivery.getBody(), "UTF-8");
                System.out.println(" [x] Received '" + message + "'");
            };

            channel.basicConsume(QUEUE_NAME, true, deliverCallback, consumerTag -> {});

            System.out.println(" [*] Waiting for messages. To exit, press Ctrl+C");
            Thread.sleep(5000); // Adjust as needed
        }
    }
}
