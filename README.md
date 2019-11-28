# sim-engine
 
## Dev Notes
Current debug issue:  
When an entity gets into a sub map, once they have a hunger task and once they have moved to the correct tile for food.
Hunger is not replenished but the entity stays in place. It must have to do with the taskIsFinished() call maybe.  
Also there is a similar issue with baseUnintelligent entities as well. It may have to deal with the position variable
and how it carries over between task and entity. Will look into this.