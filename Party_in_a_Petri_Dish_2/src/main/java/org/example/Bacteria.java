package org.example;

import com.rabbitmq.client.AlreadyClosedException;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.MessageProperties;

import java.io.IOException;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.Random;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class Bacteria implements Runnable {
    private final ThreadPoolExecutor executor;
    private final Channel channel;
    private final String queue;
    private Timer timer;
    private static final Random random = new Random();
    private PetriDish map;
    private Boolean isAlive;
    private int x, y, moveX, moveY, T_full, T_starve, eat_counter;
    private final String sexuality;


    public Bacteria(ThreadPoolExecutor executor, Channel channel, String queue, PetriDish map, int x, int y, String sexuality) {
        this.executor = executor;
        this.channel = channel;
        this.queue = queue;
        this.timer = new Timer();
        this.map = map;
        isAlive = true;
        this.x = x;
        this.y = y;
        this.moveX = 0;
        this.moveY = 0;
        T_full = 5;
        T_starve = 5;
        this.eat_counter = 0;
        this.sexuality = sexuality;

        this.map.addBacteria(this);
        publishMessage("New " + this.sexuality + " bacteria " + this.toString() + " spawned at (" + x + ", " + y + ")");
    }

    public void run() {
        this.startHungerTimer();
        while (isAlive) {
            seekAndConsume();
        }
    }

    public void startHungerTimer() {
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                if (T_full > 0) {
                    T_full--;
                    System.out.println(this.toString() + " T_full " + T_full);
                } else {
                    if (T_starve > 0) {
                        T_starve--;
                        System.out.println(this.toString() + " T_starve " + T_starve);
                    } else {
                        die();
                        timer.cancel();
                    }
                }
            }
        }, 0, 1000);
    }

    private void die() {
        isAlive = false;
        this.map.eraseBacteria(this);
        publishMessage(this.toString() + " died");
        map.spawnFoodUnit(random.nextInt(5));
    }

    public void seekAndConsume() {
        List<FoodUnit> foodUnits = this.map.getFoodUnits();
        int[] currentPosition = new int[]{this.x, this.y};

        if (!foodUnits.isEmpty()) {
            FoodUnit nearestFoodUnit = findNearestFoodUnit(currentPosition, foodUnits);
            if (nearestFoodUnit != null) {
                ReentrantLock foodUnitLock = nearestFoodUnit.getLock();
                if (foodUnitLock.tryLock()) {
                    try {
                        int[] targetPosition = nearestFoodUnit.getPosition();
                        moveTowards(targetPosition, this.map);
                    } finally {
                        foodUnitLock.unlock();
                    }
                }
            }
        }
    }


    private FoodUnit findNearestFoodUnit(int[] currentPosition, List<FoodUnit> foodUnits) {
        FoodUnit nearestFoodUnit = null;
        double minDistance = Double.MAX_VALUE;

        for (FoodUnit foodUnit : foodUnits) {
            int[] foodUnitPosition = foodUnit.getPosition();
            double distance = calculateDistance(currentPosition, foodUnitPosition);

            if (distance < minDistance) {
                minDistance = distance;
                nearestFoodUnit = foodUnit;
            }
        }
        return nearestFoodUnit;
    }

    private double calculateDistance(int[] position1, int[] position2) {
        int deltaX = position2[0] - position1[0];
        int deltaY = position2[1] - position1[1];
        return Math.sqrt(deltaX * deltaX + deltaY * deltaY);
    }

    private void moveTowards(int[] targetPosition, PetriDish map) {
        int currentX = this.x;
        int currentY = this.y;

        int targetX = targetPosition[0];
        int targetY = targetPosition[1];

        int deltaX = targetX - currentX;
        int deltaY = targetY - currentY;

        if (deltaX == 0 && deltaY == 0) {
            publishMessage(this.toString() + " reached the target food unit at position (" + targetPosition[0] + ", " + targetPosition[1] + ")");
            for (FoodUnit foodUnit : this.map.getFoodUnits()) {
                if (foodUnit.getX() == targetX && foodUnit.getY() == targetY) {
                    if (this.T_full >= 0) {
                        this.T_full += 2;
                    } else {
                        this.T_starve += 2;
                    }
                    this.map.eraseFoodUnit(foodUnit);
                    break;
                }
            }
            this.eat_counter++;
            if (eat_counter == 5) {
                multiply();
            } else
                return;
        }

        moveX = (deltaX > 0) ? 1 : (deltaX < 0) ? -1 : 0;
        moveY = (deltaY > 0) ? 1 : (deltaY < 0) ? -1 : 0;

        this.x += moveX;
        this.y += moveY;

        this.map.updateMap(this);
    }

    public void multiply() {
        publishMessage(this.sexuality + " " + this.toString() + " ready to multiply");

        if (this.isAsexual()) {
            int newX = this.x + getRandomOffset();
            int newY = this.y + getRandomOffset();

            newX = Math.max(0, Math.min(newX, map.getDimension()[0] - 1));
            newY = Math.max(0, Math.min(newY, map.getDimension()[1] - 1));

            this.executor.submit(new Bacteria(this.executor, this.channel, this.queue, this.map, newX, newY, "asexual"));
        } else {
            // Sexual reproduction
            // Implement logic to find a mate and spawn a new bacterium
        }
    }

    private int getRandomOffset() {
        return random.nextInt(3) - 1;
    }


    public Boolean isAsexual() {
        return this.sexuality.equals("asexual");
    }

    public int getMoveX() {
        return moveX;
    }

    public int getMoveY() {
        return moveY;
    }

    public int[] getPosition() {
        return new int[]{this.x, this.y};
    }

    public void publishMessage(String message) {
        try {
            channel.basicPublish("", queue, MessageProperties.PERSISTENT_TEXT_PLAIN, message.getBytes());
        } catch (AlreadyClosedException | IOException e) {
            e.printStackTrace();
        }
    }
}

