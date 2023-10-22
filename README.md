# Project Specifications

Topic chosen: Game of Life

Title: Party in a Petri Dish

There are 2 types of bacteria, sexual and asexual, each represented by a colour, and their food is random dots on the screen.
Each bacteria has a counter to remind how many times it has eaten and it's time before dying that will apear if you press/hover on it.
The bacteria is focused on eating. After the bacteria feeds the specific number of times (10), it will focus on reproducing.
For sexual bacterium, from 2 adult bacterium there is 1 new child bacteria.
For asexual bacterium, one bacteria splits in 2.
The initial ones and these new created bacterium take the entire cycle again, all of them back to being hungry.


As a user, you can observe the simulation based on the specific number of parameters you are asked to give:
B_num - the number of bacterium that the Petri Dish has in the beginning
F_num - the number of food units the Petri Dish has in the beginning
T_full - how much time before one bacteria starts starving
T_starve - how much time before one bacteria is about to die

You can chose a bacteria as a favourite and see how much time it survives and how many children it has created.
There is a score, each time there is a new bacteria, the score increases.


# Concurrency Problems:

Race Conditions:
Bacterium are focused on finding the closest food unit. In case the distance from 2 bacterium and a single food unit is the same, there might be a problem if both of them will want to eat it.

Deadlocks:
As previously mentioned, if the two bacteria get closer to the food unit, it might cause a deadlock if both wait for the other to eat. Or if they walk towards each other, they might not know how to pass each other, being stuck.

Resource Contention:
The moment where there is no food left on the Dish, the bacterium might not know what else to do.


# Modules and Classes:

Bacteria:
This class represents individual bacteria in the simulation. Each bacteria will be a distinct thread.
Properties include type (sexual or asexual), hunger level, starvation level and position.
Methods include: eating, reproducing, and dying.

FoodUnit:
Represents the food units available in the Petri Dish.
Attributes could include the position and T_full it provides.

PetriDish:
Manages the game world and all its components, including bacterium and food units.
Responsible for death and creation of bacterium and food units.
It remembers how many bacterium exist.

GameRenderer:
Handles the graphical rendering of the game world.
Renders bacterium, food units, and their interactions.

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