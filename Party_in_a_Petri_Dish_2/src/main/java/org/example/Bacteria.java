package org.example;

import com.rabbitmq.client.AlreadyClosedException;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.MessageProperties;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;

public class Bacteria implements Runnable {
    private final ThreadPoolExecutor executor;
    private final Channel channel;
    private final String queue;
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
    private static final Random random = new Random();
    private PetriDish map;
    private Boolean isAlive;
    private int x, y, moveX, moveY, T_full, T_starve, eat_counter;
    private final String sexuality;
    private Boolean readyToMultiply = false;
    private ReentrantLock lock = new ReentrantLock();

    public Bacteria(ThreadPoolExecutor executor, Channel channel, String queue, PetriDish map, int x, int y, String sexuality) {
        this.executor = executor;
        this.channel = channel;
        this.queue = queue;
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
        while (isAlive && !readyToMultiply) {
            seekAndConsume();
        }
    }

    public void startHungerTimer() {
        scheduler.scheduleAtFixedRate(() -> {
            if (T_full > 0) {
                T_full--;
                System.out.println(this.toString() + " T_full " + T_full);
            } else {
                if (T_starve > 0) {
                    T_starve--;
                    System.out.println(this.toString() + " T_starve " + T_starve);
                } else {
                    die();
                }
            }
        }, 0, 1, TimeUnit.SECONDS);
    }

    private void die() {
        isAlive = false;
        this.map.eraseBacteria(this);
        publishMessage(this.toString() + " died");
        map.spawnFoodUnit(random.nextInt(5));
        scheduler.shutdown();
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

        List<FoodUnit> foodUnitCopy = new ArrayList<>(foodUnits);

        for (FoodUnit foodUnit : foodUnitCopy) {
            if (foodUnit != null) {
                int[] foodUnitPosition = foodUnit.getPosition();
                double distance = calculateDistance(currentPosition, foodUnitPosition);

                if (distance < minDistance) {
                    minDistance = distance;
                    nearestFoodUnit = foodUnit;
                }
            }
        }
        return nearestFoodUnit;
    }

    private Bacteria findNearestSexualBacteria(int[] currentPosition, List<Bacteria> bacteriaList) {
        Bacteria nearestBacteria = null;
        double minDistance = Double.MAX_VALUE;

        for (Bacteria bacteria : bacteriaList) {
            if (bacteria.readyToMultiply) {
                int[] bacteriaPosition = bacteria.getPosition();
                double distance = calculateDistance(currentPosition, bacteriaPosition);

                if (distance < minDistance) {
                    minDistance = distance;
                    nearestBacteria = bacteria;
                }
            }
        }
        return nearestBacteria;
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
                this.readyToMultiply = true;
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

            this.executor.execute(new Bacteria(this.executor, this.channel, this.queue, this.map, newX, newY, "asexual"));
            this.readyToMultiply = false;
            this.eat_counter = 0;
        } else {
            List<Bacteria> bacteriaList = this.map.getBacteria();
            int[] currentPosition = new int[]{this.x, this.y};

            if (!bacteriaList.isEmpty()) {
                Bacteria nearestBacteria = findNearestSexualBacteria(currentPosition, bacteriaList);
                if (nearestBacteria != null) {
                    ReentrantLock bacteriaLock = nearestBacteria.getLock();
                    if (bacteriaLock.tryLock()) {
                        try {
                            int[] meetpoint = calculateMeetpoint(this, nearestBacteria);
                            moveTowardsMate(meetpoint, this.map);
                            if (meetpoint[0] == this.x && meetpoint[1] == this.y) {
                                // If the current position is the meet point, reproduce
                                int newX = this.x + getRandomOffset();
                                int newY = this.y + getRandomOffset();

                                newX = Math.max(0, Math.min(newX, map.getDimension()[0] - 1));
                                newY = Math.max(0, Math.min(newY, map.getDimension()[1] - 1));

                                this.executor.submit(new Bacteria(this.executor, this.channel, this.queue, this.map, newX, newY, "sexual"));
                                this.readyToMultiply = false;
                                this.eat_counter = 0;
                            }
                        } finally {
                            bacteriaLock.unlock();
                        }
                    }
                }
            }
        }
    }

    private void moveTowardsMate(int[] targetPosition, PetriDish map) {
        int currentX = this.x;
        int currentY = this.y;

        int targetX = targetPosition[0];
        int targetY = targetPosition[1];

        int deltaX = targetX - currentX;
        int deltaY = targetY - currentY;

        if (deltaX == 0 && deltaY == 0) {
            publishMessage(this.toString() + " reached the mate at position (" + targetPosition[0] + ", " + targetPosition[1] + ")");
            int newX = this.x + getRandomOffset();
            int newY = this.y + getRandomOffset();

            newX = Math.max(0, Math.min(newX, map.getDimension()[0] - 1));
            newY = Math.max(0, Math.min(newY, map.getDimension()[1] - 1));

            this.executor.submit(new Bacteria(this.executor, this.channel, this.queue, this.map, newX, newY, "sexual"));
            this.readyToMultiply = false;
            this.eat_counter = 0;
        }

        moveX = (deltaX > 0) ? 1 : (deltaX < 0) ? -1 : 0;
        moveY = (deltaY > 0) ? 1 : (deltaY < 0) ? -1 : 0;

        this.x += moveX;
        this.y += moveY;

        this.map.updateMap(this);
    }

    private int[] calculateMeetpoint(Bacteria b1, Bacteria b2) {
        int midX = (b1.x + b2.x) / 2;
        int midY = (b1.y + b2.y) / 2;

        if (this.map.getFreePoint(midX, midY)) {
            return new int[]{midX, midY};
        } else {
            if (midX < this.map.getDimension()[0] - 1) {
                if (this.map.getFreePoint(midX + 1, midY))
                    return new int[]{midX + 1, midY};
            }
            if (midY < this.map.getDimension()[1] - 1) {
                if (this.map.getFreePoint(midX, midY + 1))
                    return new int[]{midX, midY + 1};
            }
            if (midX < this.map.getDimension()[0] - 1 && midY < this.map.getDimension()[1] - 1) {
                if (this.map.getFreePoint(midX + 1, midY + 1))
                    return new int[]{midX + 1, midY + 1};
            }
            if (midX > 0) {
                if (this.map.getFreePoint(midX - 1, midY))
                    return new int[]{midX - 1, midY};
            }
            if (midY > 0) {
                if (this.map.getFreePoint(midX, midY - 1))
                    return new int[]{midX, midY - 1};
            }
            if (midX > 0 && midY > 0) {
                if (this.map.getFreePoint(midX - 1, midY - 1))
                    return new int[]{midX - 1, midY - 1};
            }
            if (midX > 0 && midY < this.map.getDimension()[1] - 1) {
                if (this.map.getFreePoint(midX - 1, midY + 1))
                    return new int[]{midX - 1, midY + 1};
            }
            if (midX < this.map.getDimension()[0] - 1 && midY > 0) {
                if (this.map.getFreePoint(midX + 1, midY - 1))
                    return new int[]{midX + 1, midY - 1};
            }
        }
        return new int[]{0, 0};
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

    public ReentrantLock getLock() {
        return lock;
    }

    public void publishMessage(String message) {
        try {
            channel.basicPublish("", queue, MessageProperties.PERSISTENT_TEXT_PLAIN, message.getBytes());
        } catch (AlreadyClosedException | IOException e) {
            e.printStackTrace();
        }
    }
}

