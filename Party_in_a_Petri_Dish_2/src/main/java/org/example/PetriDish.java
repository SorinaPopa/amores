package org.example;

import com.rabbitmq.client.AlreadyClosedException;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.MessageProperties;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class PetriDish {
    private final int[] dimension;
    private final Channel channel;
    private final String queue;
    private final Random random;
    private Object[][] matrix;
    private List<Bacteria> bacteriaList = new ArrayList<>();
    private List<FoodUnit> foodUnitList = new ArrayList<>();

    public PetriDish(int[] dimension, Channel channel, String queueName) {
        this.dimension = dimension;
        this.channel = channel;
        this.random = new Random();
        this.queue = queueName;
        this.matrix = new Object[dimension[0]][dimension[1]];
        initializeMap();
    }

    private void initializeMap() {
        for (int i = 0; i < dimension[0]; i++) {
            for (int j = 0; j < dimension[1]; j++) {
                matrix[i][j] = null;
            }
        }
    }

    public void addBacteria(Bacteria bacteria) {
        this.bacteriaList.add(bacteria);
    }

    public void spawnFoodUnit(int numberOfFoodUnits) {
        for (int i = 0; i < numberOfFoodUnits; i++) {
            int x = random.nextInt(dimension[0]);
            int y = random.nextInt(dimension[1]);

            FoodUnit newFoodUnit = new FoodUnit(x, y, 5);
            matrix[x][y] = newFoodUnit;
            foodUnitList.add(newFoodUnit);

            publishMessage("New food unit spawned at (" + x + ", " + y + ")");
        }
    }

    public void eraseBacteria(Bacteria bacteria) {
        if (this.bacteriaList.contains(bacteria)) {
            foodUnitList.remove(bacteria);
        }
    }

    public void eraseFoodUnit(FoodUnit foodUnit) {
        if (this.foodUnitList.contains(foodUnit)) {
            foodUnitList.remove(foodUnit);
        }
    }

    public void updateMap(Bacteria bacteria) {
        matrix[bacteria.getPosition()[0] - bacteria.getMoveX()][bacteria.getPosition()[1] - bacteria.getMoveY()] = null;
        matrix[bacteria.getPosition()[0]][bacteria.getPosition()[1]] = this;
    }

    public List<Bacteria> getBacteria() {
        return bacteriaList;
    }

    public List<FoodUnit> getFoodUnits() {
        return foodUnitList;
    }

    public void printMatrix() {
        for (int i = 0; i < dimension[0]; i++) {
            for (int j = 0; j < dimension[1]; j++) {
                if (matrix[i][j] == null) {
                    System.out.print("null ");
                } else {
                    System.out.print(matrix[i][j].toString() + " ");
                }
            }
            System.out.println();
        }
    }

    public void publishMessage(String message) {
        try {
            channel.basicPublish("", queue, MessageProperties.PERSISTENT_TEXT_PLAIN, message.getBytes());
        } catch (AlreadyClosedException | IOException e) {
            e.printStackTrace();
        }
    }

    public int[] getDimension(){
        return dimension;
    }
}
