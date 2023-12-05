public class Main {

    public static void main(String[] args) {
        try {
            // Create an instance of RabbitMQProducer and send a message
            RabbitMQProducer producer = new RabbitMQProducer();
            producer.produceMessage();

            // Create an instance of RabbitMQConsumer and start listening for messages
            RabbitMQConsumer consumer = new RabbitMQConsumer();
            consumer.consumeMessages();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
