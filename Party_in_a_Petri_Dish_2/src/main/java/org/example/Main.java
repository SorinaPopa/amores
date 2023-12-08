package org.example;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class Main {
    public static void main(String[] args) throws Exception {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");
        Connection connection = factory.newConnection();
        Channel channel = connection.createChannel();

        // Declare a queue
        channel.queueDeclare("Queue", false, false, false, null);

        // set how big the petri dish is
        PetriDish petriDish = new PetriDish(new int[]{10, 10});
        petriDish.spawnFoodUnit(5);

        ThreadPoolExecutor executor = new ThreadPoolExecutor(
                5,
                10,
                60,
                TimeUnit.SECONDS,
                new LinkedBlockingQueue<>()
        );

        // Submit the Bacteria task to the executor
        executor.submit(new Bacteria("sexual", 3, 4, petriDish, channel, "Queue"));

        // Shutdown the executor and wait for its tasks to complete
        executor.shutdown();
        executor.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);

        // Close the channel and connection after the executor has completed
        channel.close();
        connection.close();
    }
}
