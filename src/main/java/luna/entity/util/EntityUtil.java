package luna.entity.util;

import luna.entity.Entity;
import luna.util.Tile;
import luna.util.Util;
import luna.world.World;
import luna.world.util.ObjectManager;

import java.util.*;

// Some methods that can be called by something
public class EntityUtil {

    private static String[] jobs = {"nomad", "leader", "fighter", "crafter"};
    public EntityUtil(){

    }

    // Will add others soon
    public String getTaskRequest(Entity e){
        // hunger check first
        if(e.isHungry()){
            return "food";
        }

        // TODO: add in a luck trait for this later
        //  - entities should not always be able to run away
        if(e.getHp() <= e.getMax_hp()*.50 && (e.getTargetEntityID() != -1 || Util.random(100) <= 65))
            return "rest";

        // - build camp
        if(e.getTaskWaitTimer() <= 0 && e.getFocus().contains("crafter") && e.getGroupId() != -1 &&
                World.entityManager.groups.get(e.getGroupId()).getBasePos()[0] == -1){
            if(e.hasBasicBuildingSupplies()){
                return "build_camp";
            }else if(!e.hasBasicBuildingSupplies()){
                if(e.getMaterialCount("resource_wood") < 5)
                    return "gather_wood";
                if(e.getMaterialCount("resource_stone") < 5)
                    return "gather_stone";
            }
        }

        // additional items
        if(e.getTaskWaitTimer() <= 0 && (e.getFocus().contains("fighter") || e.getFocus().contains("nomad") || e.getFocus().contains("leader")) &&
                e.getHp() > e.getMax_hp()*.50 && Util.random(100) > 95 && e.notWaitingForHunt()){
            return "hostile";
        }

        if(e.getTaskWaitTimer() <= 0 && Util.random(100) > 95 && e.getInteractTimer() <= 0 && e.getGroupId() == -1 &&
                (e.taskQueueEmpty() || (e.getCurrentTask().getTaskType().equals("wander") || e.getCurrentTask().getTaskType().equals("none"))))
            return "interact";

        if(e.taskQueueEmpty())
            return "wander";


        return "none";
    }

    public int faceOpposite(int dir){
        switch(dir){
            case 0: // left
                return 1;
            case 1:// right
                return 0;
            case 2:// up
                return 3;
            case 3:// down
                return 2;
            default:
                return 0;
        }
    }//

    public static String[] getJobs() {
        return jobs;
    }

    // Will check the surrounding tiles (3x3 kernel) for anything
    // - This can be used to find other things as well
    // - will return a map of found objects
    //   - ex: results.get("objects") = List or object ids found
    public Map<String, List<Integer>> survey(Entity e, List<List<Tile>> tilemap){
        Map<String, List<Integer>> results = new HashMap<>();
        results.put("objects", new ArrayList<>());
        results.put("entities", new ArrayList<>());
        results.put("items", null); // we don't have items setup yet // TODO: add items to results

        int kx, ky, tileX, tileY, mapSize;
        if(e.getPosition() == -1){
            tileX = e.getCurrTileX();
            tileY = e.getCurrTileY();
        }else{
            tileX = e.getSubTileX();
            tileY = e.getSubTileY();
        }// end
        mapSize = tilemap.size()-1;
        // check surroundings
        int start = ((e.getVisionKernel() - 1) / 2) - (e.getVisionKernel() - 1);
        for(int y_ = start; y_ < e.getVisionKernel() - 1; y_++) {
            ky = tileY + y_;
            for (int x_ = start; x_ < e.getVisionKernel() - 1; x_++) {
                kx = tileX + x_;
                if (ky >= 0 && kx >= 0 && ky <= mapSize && kx <= mapSize) {
                    // end of entity itr
                    List<Integer> objsFound = tilemap.get(ky).get(kx).getObjectsInTile();
                    List<Integer> entitiesFound = tilemap.get(ky).get(kx).getEntitiesInTile();
                    //List<Integer> itemsFound;
                    results.get("objects").addAll(objsFound);
                    results.get("entities").addAll(entitiesFound);
                }
            }
        }

        return results;
    }// end of survey

    // how entity 1 interacts with entity 2
    // TODO: enhancements to entity interaction
    //  - add positive and negative interactions
    //  - make interactions influenced by personality
    //
    public boolean interact(Entity e1, Entity e2){
        int bondIdx = e1.inBondList(e2.getEntityID());
        // First case -> they have never met and should make contact
        if (!e1.isLocked() && !e2.isLocked() && e2.isAlive() &&
                e2.getEntityID() != e1.getEntityID() && e1.isCompatible(e2) &&
                e1.getPosition() == e2.getPosition()) {
            // first add to bond list
            if(bondIdx == -1){
                e1.addBond(e2.getEntityID());
                e1.log("Just met Entity [" + e2.getEntityID() + "] for the first time");
                e1.lockEntity(e2.getEntityID());
                return true;
            }
            // seconds case -> they have met before and will make progress towards the bond
            // - Both entities have an interaction call here
            else if(e1.getBondList().get(bondIdx).getBondLevel() < 70){
                e1.lockEntity(e2.getEntityID());
                e1.entityInteraction(e2.getEntityID());
                e1.log("Interacted with Entity [" + e2.getEntityID() + "]");
                return true;

            }
            // third case -> they group up if bond is greater than 70
            else if(e1.getBondList().get(bondIdx).getBondLevel() >= 70){
                // check if in a group
                // - Note: only intelligent entities group
                // they both are not in groups
                if(e1.getGroupId() == -1 && e2.getGroupId() == -1 && e1.getType() < 5){
                    e1.log("Grouping up with Entity [" + e2.getEntityID() + "]");
                    // create a new group
                    World.entityManager.groups.add(new Group());
                    int nextGroupId = World.entityManager.groups.size()-1;
                    e1.setGroupId(nextGroupId);
                    EntityManager.entities.get(e2.getEntityID()).setGroupId(e1.getGroupId());
                    if(Util.random(100) > 50) {
                        e1.setFocus(EntityUtil.getJobs()[1]);
                        World.entityManager.groups.get(e1.getGroupId()).addEntity(e1.getEntityID());
                        World.entityManager.groups.get(e1.getGroupId()).addEntity(e2.getEntityID());
                    }else {
                        EntityManager.entities.get(e2.getEntityID()).setFocus(EntityUtil.getJobs()[1]);
                        World.entityManager.groups.get(e1.getGroupId()).addEntity(e2.getEntityID());
                        World.entityManager.groups.get(e1.getGroupId()).addEntity(e1.getEntityID());
                    }
                    e1.logNoStamp("Created new group -> " + e1.getGroupId());
                }
                // this entity is not in a group but tmp is
                else if(e1.getGroupId() != -1 && e2.getGroupId() == -1 &&
                        World.entityManager.groups.get(e1.getGroupId()).size() < 4 && e1.getType() < 5){
                    EntityManager.entities.get(e2.getEntityID()).setGroupId(e1.getGroupId());
                    World.entityManager.groups.get(e1.getGroupId()).addEntity(e2.getEntityID());
                    e1.logNoStamp("added Entity [" + e2.getEntityID() + "] to group");
                    // this entity is in a group but the other is not
                }else if(e1.getGroupId() == -1 && e2.getGroupId() != -1  &&
                        World.entityManager.groups.get(e2.getGroupId()).size() < 4 && e1.getType() < 5) {
                    e1.setGroupId(e2.getGroupId());
                    World.entityManager.groups.get(e1.getGroupId()).addEntity(e1.getEntityID());
                    e1.logNoStamp("joined Entity [" + e2.getEntityID() + "] in their group");
                    // not an intelligent entity/already in group
                }else if(e1.getType() < 5){
                    e1.lockEntity(e2.getEntityID());
                    e1.entityInteraction(e2.getEntityID());
                    e1.log("Interacted with Entity [" + e2.getEntityID() + "]");
                }
                // And add another interaction
                e1.lockEntity(e2.getEntityID());
                e1.entityInteraction(e2.getEntityID());
                return true;
            }
        }else if(e1.getEntityID() == 0 && e2.getEntityID() != e1.getEntityID()){
            // TODO: remove tests
            // !isLocked() && !tmp.isLocked() && tmp.isAlive() &&
            //                                tmp.getEntityID() != this.getEntityID() && isCompatible(tmp) &&
            //                                getPosition() == tmp.getPosition()
                            /*System.out.println("locked? " + isLocked() + " tmp locked? " + tmp.isLocked());
                            System.out.println("is compatible? " + isCompatible(tmp));
                            System.out.println("ID -> " + tmp.getEntityID());
                            */

        }
        return false;
    }// end of interact

    public List<int[]> getVisibleEdges(Entity e){
        List<int[]> moves = new ArrayList<>();

        int kx, ky, tileX, tileY, mapSize;
        if(e.getPosition() == -1){
            tileX = e.getCurrTileX();
            tileY = e.getCurrTileY();
            mapSize = World.tileMap.size()-1;
        }else{
            tileX = e.getSubTileX();
            tileY = e.getSubTileY();
            mapSize = Objects.requireNonNull(World.getMap(e.getPosition())).getTileMap().size()-1;
        }// end

        // check surroundings
        int start = ((e.getVisionKernel() - 1) / 2) - (e.getVisionKernel() - 1);
        for(int y_ = start; y_ < e.getVisionKernel() - 1; y_++) {
            ky = tileY + y_;
            for (int x_ = start; x_ < e.getVisionKernel() - 1; x_++) {
                kx = tileX + x_;
                if (ky >= 0 && kx >= 0 && ky <= mapSize && kx <= mapSize &&
                    (y_ == start || x_ == start ||
                     y_ == (e.getVisionKernel()-1) || x_ == (e.getVisionKernel()-1))) {
                    int[] move = new int[]{ky, kx};
                    moves.add(move);
                }
            }
        }// done

        return moves;
    }

    public int[] findBuildSpace(Entity e){
        if(e.getGroupId() != -1 && World.entityManager.groups.get(e.getGroupId()).getBasePos()[0] != -1){
            int kx, ky, tileX, tileY, mapSize;
            if(e.getPosition() == -1){
                tileX = e.getCurrTileX();
                tileY = e.getCurrTileY();
                mapSize = World.tileMap.size()-1;
            }else{
                tileX = e.getSubTileX();
                tileY = e.getSubTileY();
                mapSize = Objects.requireNonNull(World.getMap(e.getPosition())).getTileMap().size()-1;
            }// end

            int kernel = 3;
            int tryCount = 0;
            int xPos = tileX;
            int yPos = tileY;
            // check surroundings
            while(tryCount < 50) {
                int hostilesFound = 0;
                for (int y_ = -1; y_ < kernel - 1; y_++) {
                    ky = yPos + y_;
                    for (int x_ = -1; x_ < kernel - 1; x_++) {
                        kx = xPos + x_;
                        if (ky >= 0 && kx >= 0 && ky <= mapSize && kx <= mapSize) {
                            if(World.tileMap.get(ky).get(kx).getObjectsInTile().size() > 0){
                                for(int id : World.tileMap.get(ky).get(kx).getObjectsInTile()){
                                    if(ObjectManager.interactableObjects.get(id).getType().contains("hostile"))
                                        hostilesFound++;
                                }
                            }
                        }
                    }
                }
                if(hostilesFound == 0){
                    return new int[]{yPos, xPos};
                }
                tryCount++;
                xPos = Util.random(mapSize);
                yPos = Util.random(mapSize);
            }
        }else if(e.getGroupId() != -1){
            return World.entityManager.groups.get(e.getGroupId()).getBasePos();
        }

        return new int[]{-1,-1};
    }

}
