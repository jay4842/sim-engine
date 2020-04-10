# sim-engine
A simulation project focused mainly on making AI agents.  
[![Codacy Badge](https://api.codacy.com/project/badge/Grade/3a112f367b2649818085bda0a68c8c29)](https://www.codacy.com?utm_source=github.com&amp;utm_medium=referral&amp;utm_content=jay4842/sim-engine&amp;utm_campaign=Badge_Grade)  
![alt text][hardhat]  
  
## The Simulation
The project centers around Jellies(entities), these are objects that can grow and change overtime. Each one has their own personality and current status variables that influence how they make their decisions. Currently they can move around the map, eat, consume energy, talk to one another, pick up items(food to save for later), and replicate.

I handle all of the moving parts in the engine by splitting each object focus up by services. This allows me to effectively separate the scope of everything in the game making it easier to add additional components and debug any issues that pop up. Currently there is a Master service, world service, entity service, object service, personality service, species service and an item service. These all implement an interface to keep similar methods between them that define a service.To manage concurrency in the engine, the master service calls all of the additional services, when other services are called they return a list of messages that can be interpreted and sent to other services. This helps manage concurrency within the engine. 

During runtime the entries will move around the map, eat food that is generated, talk with one another, and once they reach a certain age they can replicate. While it is running the app will generate various log files which are then used to be pushed to the database.

## Personalities
The personality system is based on the five core [personality traites](https://positivepsychology.com/big-five-personality-theory/), extroversion, agreeableness, consientiousness, neurotocism, and creativity. Each trait has a value from 0 to 1 and is the classified using similar concepts as the [16 personalities](https://www.16personalities.com/articles/our-theory). In the future, a personality will be used the most when the jellies interact with one aother. 

## installing
I recommend you install by importing a maven project.    

## running
Currently the best way to run it is in the IDE. Eventually a standalone execute file will be created.  

## What's next?
Cleaning code, tracking my progress through Projects, adding a wiki in the near future to expand on the overall processes that make up the world I am developing. I also am working on developing the web app to interact with the database this project updaes.  

![alt text][screeny1]  ![alt text][screeny2]  


[hardhat]: https://github.com/jay4842/sim-engine/blob/dev/res/entity/hardhat_jelly.png "Pardon my dust"  
[screeny1]: https://github.com/jay4842/sim-engine/blob/master/res/screenshots/screeny_1.PNG "screenshot"  
[screeny2]: https://github.com/jay4842/sim-engine/blob/master/res/screenshots/screeny_2.PNG "screenshot"  
