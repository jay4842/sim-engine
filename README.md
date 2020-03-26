# sim-engine
A simulation project focused mainly on making AI agents.  
[![Codacy Badge](https://api.codacy.com/project/badge/Grade/3a112f367b2649818085bda0a68c8c29)](https://www.codacy.com?utm_source=github.com&amp;utm_medium=referral&amp;utm_content=jay4842/sim-engine&amp;utm_campaign=Badge_Grade)  
![alt text][hardhat]  
  
## The Simulation
Currently this project centers around Jellies(entities); these are objects that can grow and change overtime. Each one has thier own personality and current status variables that influence how they make thier decisions. Currently they can move around the map, eat, consume energy, talk to one another, pick up items(food to save for later), and replicate.  
  
## How the simulation works
The simulation has several services to manage the specific types of objects, say entity service, world object service, item service, personality manager, and a few others that have not been intigrated completely yet(this are in the future work section).  
  
during runtime, you will see entities move around the map and interact with the world. While it is running the app will generate various log files which are then used to be pused to the database. (Note: this funcitonality is still being worked on meaning everyone cannot push to the database yet).  

## Personalities
The personality system is based on the five core [personality traites](https://positivepsychology.com/big-five-personality-theory/), extroversion, agreeableness, consientiousness, neurotocism, and creativity. Each trait has a value from 0 to 1 and is the classified using similar concepts as the [16 personalities](https://www.16personalities.com/articles/our-theory). In the future, a personality will be used the most when the jellies interact with one aother. 

## installing
I recommend you install by importing a maven project.    

## running
Currently the best way to run it is in the IDE. Eventually a standalone execute file will be created.  

## What's next?
Cleaning code, tracking my progress through Projects, adding a wiki in the near future to expand on the overall processes that make up the world I am developing. I also am working on developing a web app that interacts with the database this applicaiton pushes to so I can show metrics and other information from the simulations that run.  

![alt text][screeny1]  ![alt text][screeny2]  


[hardhat]: https://github.com/jay4842/sim-engine/blob/dev/res/entity/hardhat_jelly.png "Pardon my dust"  
[screeny1]: https://github.com/jay4842/sim-engine/blob/master/res/screenshots/screeny_1.PNG "screenshot"  
[screeny2]: https://github.com/jay4842/sim-engine/blob/master/res/screenshots/screeny_2.PNG "screenshot"  
