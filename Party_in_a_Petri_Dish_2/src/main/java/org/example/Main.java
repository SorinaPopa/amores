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
        String queue = "Queue";

        channel.queueDeclare(queue, false, false, false, null);

        PetriDish petriDish = new PetriDish(new int[]{10, 10}, channel, queue);
        petriDish.spawnFoodUnit(20);
        //petriDish.printMatrix();

        ThreadPoolExecutor executor = new ThreadPoolExecutor(
                5,
                10,
                60,
                TimeUnit.SECONDS,
                new LinkedBlockingQueue<>()
        );

        executor.submit(new Bacteria(executor, channel, queue, petriDish, 3, 4, "asexual"));
        executor.submit(new Bacteria(executor, channel, queue, petriDish, 7, 8, "sexual"));
        executor.submit(new Bacteria(executor, channel, queue, petriDish, 7, 4, "sexual"));

        if (petriDish.getBacteria().isEmpty()) {
            channel.close();
            connection.close();
        }
    }
}
