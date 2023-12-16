# Project Specifications

Topic chosen: Game of Life

Title: Party in a Petri Dish

There are 2 types of bacteria, sexual and asexual , and they can consume food from the Petri Dish (represented by a matrix). Each bacterium has a counter (in seconds) to tell how much time there is left until starvation (T_full) and then it has a counter to tell how much time there is left until death(T_starve).

- Bacteria are focused on eating. After a bacterium feeds the specific number of times (5), it will focus on reproducing. 
- When a sexual bacterium is ready to multiply and it meets another sexual bacterium, it spawns a sexual bacterium.
- When an asexual bacterium is ready to multiply, it spawns another asexual bacterium (one bacterium splits in 2).
- The initial ones and these new created bacteria take the entire cycle again, all of them back to being hungry.


# Concurrency Problems:

Race Conditions:
Bacteria are focused on finding the closest food unit. In case the distance from 2 bacteria and a single food unit is the same, there might be a problem if both of them will want to eat it.

Deadlocks:
As previously mentioned, if the two bacteria get closer to the food unit, it might cause a deadlock if both wait for the other to eat. Or if they walk towards each other, they might not know how to pass each other, being stuck.

Resource Contention:
The moment where there is no food left on the Dish, the bacteria might not know what else to do.

Exclusive Cell Access:
To prevent conflicts, only one elements can occupy a cell at a time. And new food units or bacteria children cannot spawn on an occupied cell. 


# Classes:

Bacteria:
This class represents individual bacterium in the simulation. Each bacterium will be a distinct thread.
Properties include type (sexual or asexual), hunger level, starvation level and position.
Methods include: eating, reproducing, and dying.

run
startHungerTimer
die
seekAndConsume
findNearestFoodUnit
findNearestSexualBacteria
calculateDistance
moveTowards
multiply
moveTowardsMate
calculateMeetPoint


FoodUnit:
Represents the food units available in the Petri Dish.
Attributes could include the position and T_full it provides.

PetriDish:
Manages the game world and all its components, including bacterium and food units.
Responsible for death and creation of bacterium and food units.
It remembers how many bacterium exist.

Main:




Optionally:

UserInterface:
Represents the game's user interface.
Displays information such as bacterium count, score, etc.
May have controls for starting, pausing, or resetting the simulation.


# Threads and Interactions:

Bacteria Threads:
Each bacteria (both asexual and sexual) is a separate thread.
These threads run concurrently and simulate the life cycle of bacterium.

Game Loop:
The main game loop controls the progression of time and updates the state of the Petri Dish.
It should handle collisions, reproduction, and bacteria deaths(which turns into food).


# Entry Points:

Main Class:
The main class will serve as the entry point for the simulation.
It initializes the game world, user interface, and starts the game loop.

User Interface Interaction:
The user interface may provide options for starting, pausing, and resetting the simulation.