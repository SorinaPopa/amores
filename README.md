# Project Specifications

**_Topic chosen_**: Game of Life

**_Title_**: Party in a Petri Dish

There are 2 types of bacteria, sexual and asexual, and they can consume food from the Petri Dish (represented by a matrix). Each bacterium has a counter (in seconds) to tell how much time there is left until starvation (T_full) and then it has a counter to tell how much time there is left until death(T_starve).

- Bacteria are focused on eating. After a bacterium feeds the specific number of times (5), it will focus on reproducing. 
- When a sexual bacterium is ready to multiply and it meets another sexual bacterium, it spawns a sexual bacterium.
- When an asexual bacterium is ready to multiply, it spawns another asexual bacterium (one bacterium splits in 2).
- The initial ones and these newly created bacteria take the entire cycle again, all of them back to being hungry.


# Concurrency Problems:

**_Race Conditions:_**<br>
Bacteria are focused on finding the closest food unit. In case the distance between 2 bacteria and a single food unit is the same, there might be a problem if both of them will want to eat it.

**_Deadlocks:_**<br>
As previously mentioned, if the two bacteria get closer to the food unit, it might cause a deadlock if both wait for the other to eat. Or if they walk towards each other, they might not know how to pass each other, being stuck.

**_Exclusive Cell Access:_**<br>
To prevent conflicts, only one element can occupy a cell at a time. And new food units or bacteria children cannot spawn on an occupied cell. 

**_Resource Contention:_**<br>
The absence of a controlled mechanism for executing bacteria tasks could lead to inefficient resource usage and contention for shared resources.

**_Hunger and Starvation Issues:_**<br>
There might be an implementation error in tracking hunger and starvation that could lead to difficulties in managing the state of bacteria over time if it is not tracked properly.


# Concurrency Solutions:

Solution for Resource Contention:<br>
**_ThreadPoolExecutor:_**<br>
An instance of ThreadPoolExecutor is created in the Main class to manage the execution of tasks related to bacteria. This thread pool is responsible for handling the concurrent execution of bacteria instances.

**_BlockingQueue (LinkedBlockingQueue):_**<br>
The ThreadPoolExecutor uses a LinkedBlockingQueue to hold tasks that are awaiting execution. This queue helps manage the flow of tasks between the producer (main thread) and the consumer threads (thread pool).

Solution for Hunger and Starvation Issues:<br>
**_ScheduledExecutorService:_**<br>
The Bacteria class uses ScheduledExecutorService to schedule a task that checks the hunger status at fixed intervals. This is used to implement a timer for the bacteria's hunger and starvation mechanism. It helps in managing the hunger state of the bacteria over time.

Solution for Race Conditions and Deadlocks:<br>
**_ReentrantLock:_**<br>
Each instance of the Bacteria and FoodUnit classes has a ReentrantLock to control access to critical sections of code. The lock is used when accessing and modifying shared resources, such as when seeking and consuming food units and finding a mate to reproduce.


# Classes:

**_Main:_**<br>
The main class initializes the RabbitMQ connection, creates a Petri dish, spawns food units, and manages concurrent bacteria execution using ThreadPoolExecutor.<br>
Properties: connection to RabbitMQ, instance of the Petri dish, threadPoolExecutor for managing bacteria tasks.

**_PetriDish:_**<br>
Models the environment where bacteria and food units exist, with a matrix representation of the Petri Dish.<br>
Properties: manages the spawning, erasing, and access to the current list.

**_Bacteria:_**<br>
Represents a bacteria organism that seeks and consumes food units, reproduces, and eventually dies. Utilizes locks for thread safety.<br>
Properties: inclusion of bacteria type (sexual or asexual), hunger and starvation level, positions, and locks for thread safety.

**_FoodUnit:_**<br>
Represents a consumable food unit with a timer in the Petri Dish. <br>
Properties: position and timer of food unit, reentrantLock for thread safety.


# Threads and Interactions:

**_Main Thread:_**<br>
It is responsible for initialising the RabbitMQ connection, creating a Petri Dish and spawning food units. It manages the ThreadPoolExecutor for the concurrent execution of the bacteria.

**_ThreadPoolExecutor Threads:_**<br>
Multiple threads managed by the ThreadPoolExecutor are responsible for executing instances of the Bacteria class concurrently. Each thread represents an individual bacterium with its life cycle and behaviour.

**_ScheduledExecutorService Thread:_**<br>
Manages the hunger timer for each bacterium. Periodically checks the hunger status, initiating starvation if necessary.

**_RabbitMQ Consumer Threads:_**<br>
In the background, RabbitMQ manages its own threads for consuming messages.

**_Interactions:_**<br>
- Bacteria threads interact with the Petri dish, seeking and consuming food units.
- The ThreadPoolExecutor and ScheduledExecutorService manage the execution and timing of bacteria-related tasks.
- RabbitMQ facilitates communication and message passing between threads, including the main thread and bacteria threads.


# Entry Points:

**_Main Class:_**<br>
It contains the main method, serving as the entry point for the program. It initializes the RabbitMQ connection, creates a Petri dish, spawns initial food units, and sets up the ThreadPoolExecutor for bacteria instances.

