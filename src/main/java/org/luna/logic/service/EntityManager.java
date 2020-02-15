package org.luna.logic.service;

import org.luna.core.entity.Entity;
import org.luna.core.entity.Personality;
import org.luna.core.entity.variants.MutationA;
import org.luna.core.map.LunaMap;
import org.luna.core.map.Tile;
import org.luna.core.reporting.Report;
import org.luna.core.util.ImageUtility;
import org.luna.core.util.ManagerCmd;
import org.luna.core.util.Utility;

import java.awt.*;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

// Hold and manager all the entities in the game
// - note: entities will be
public class EntityManager implements Manager {

    private static PersonalityManager personalityManager = new PersonalityManager();
    private static ImageUtility imgUtil = new ImageUtility();
    private static Utility utility = new Utility();
    public static List<Entity> entities;
    public static List<List<List<List<Integer[]>>>> entityRef; // map, y, x, entities at tile list
    private static List<Integer> sizesPerStep;
    private static List<Integer[]> variantCountPerStep;
    private final int numVariants = 4; // TODO: Update this every time a variant is added
    private int h, w, s;
    private int turnSize;
    private int simCount;
    private static int counter = 0;
    private static Report entityReport; // log type and position for each entity
    //private static Report entityDetailReport; // TODO: Log entity specific details for each step

    EntityManager(int HEIGHT, int WIDTH, int world_scale, int numMaps, int turnStep){
        if(counter == 0){
            Utility.deleteFolder("./logs/EntityLog/");
        }
        entityReport = new Report("./logs/EntityLog/entity_report_" + counter + ".txt");
        this.h = HEIGHT;
        this.w = WIDTH;
        this.s = world_scale;
        if(personalityManager.getBasePersonalities().size() == 0) {
            int result = personalityManager.loadPersonalityFile();
            System.out.println("result from load personalities : " + result);
        }
        entities = new ArrayList<>();
        entityRef = new ArrayList<>();
        sizesPerStep = new ArrayList<>();
        for(int i = 0; i < numMaps; i++) {
            entityRef.add(new ArrayList<>());
            for(int y = 0; y < HEIGHT/world_scale; y++){
                entityRef.get(i).add(new ArrayList<>());
                for(int x = 0; x < WIDTH / world_scale; x++){
                    entityRef.get(i).get(y).add(new ArrayList<>());
                }
            }
        }
        //

        // spawn some entities
        int spawns = 10;
        for(int i = 0; i < spawns; i++){
            Entity e = makeEntity(0);
            entities.add(e);
        }

        variantCountPerStep = new ArrayList<>();
        this.turnSize = turnStep;
        counter ++;
        // TODO: store entity mutation data to a type log
    }

    public List<ManagerCmd> update(int step, int visibleMap, LunaMap map) {
        List<ManagerCmd> cmds = new ArrayList<>();
        if(step % turnSize == 0)
            entityReport.write(step + " ");
        List<Entity> addBuffer = new ArrayList<>();
        Integer[] count = new Integer[numVariants];
        for(int i = 0; i < numVariants; i++)
            count[i] = 0;
        for(int i = 0; i < entities.size(); i++){
            List<String> cmdList = entities.get(i).update(step, turnSize, map);
            if(cmdList.size() > 0) {
                for(String cmd : cmdList)
                    cmds.add(new ManagerCmd(cmd, null));
            }
            if(step % turnSize == 0) {
                if (i < entities.size() - 1)
                    entityReport.write(entities.get(i).makeReportLine() + ",");
                else
                    entityReport.write(entities.get(i).makeReportLine());
            }
            if(entities.get(i).getType() > 0)
                count[entities.get(i).getType()-1]++;
            else
                count[0]++;
            if(entities.get(i).replicate() && step % turnSize == 0 && step > 0){
                // TODO: add ref to personality manager here to make the new entities personality
                Personality p = personalityManager.makePersonality();
                Entity tmp = entities.get(i).makeEntity();
                tmp.setPersonality(p);
                addBuffer.add(tmp);
            }else if(entities.get(i).isDead() && step % turnSize == 0 && step > 0){
                entities.get(i).deleteSelfFromRef();
                entities.remove(i);
                i--;
            }

            //output = "REMOVE,OBJECT," + targetObject.getGps()[0] + "," + targetObject.getGps()[1] + "," + targetObject.getListId();
            if(cmdList.size() > 0){
                for(String cmd : cmdList){
                    if(cmd.contains("REMOVE")){
                        // need to process removes on the map so that the entities can have the correct info
                        String[] split = cmd.split(",");
                        if(cmd.contains("OBJECT")){
                            int y = Integer.parseInt(split[2])/s;
                            int x = Integer.parseInt(split[3])/s;
                            int idx = Integer.parseInt(split[4]);
                            map.getObjectsInMap().get(y).get(x).remove(idx);
                        }
                    }
                }
            }//
        }

        if(step % turnSize == 0)
            entityReport.write("\n");

        entities.addAll(addBuffer);
        if(step % turnSize == 0) {
            int alive = entities.size();
            sizesPerStep.add(alive);
            variantCountPerStep.add(count);
        }
        return cmds;
    }

    private Entity makeEntity(int map){
        int x = Utility.getRnd().nextInt(w-s);
        int y = Utility.getRnd().nextInt(h-s);
        Personality p = personalityManager.makePersonality();
        Entity e = new MutationA(s, new int[]{y,x,map});
        e.setPersonality(p);
        return e;
    }


    @Override
    public List<ManagerCmd> update(int step, int x) {
        int alive = entities.size();
        sizesPerStep.add(alive);
        return null;
    }

    @Override
    public void render(int visibleMap, int step, Graphics2D g) {

        // go through the list and render the entity if it is in the visible map
        for(Entity e : entities){
            if(e.getGps()[2] == visibleMap)
                e.render(g);
        }

        // draw stats to the right
        g.setColor(Color.black);
        g.setFont(Utility.getSmallFont());
        int maxWidth = s*3;
        int startY = s;
        g.drawString("Step      : " + step, w + s/2, startY);
        g.drawString("Entities  : " + entities.size(), w + s/2, startY + (startY/2));
        g.drawString("avg       : " + String.format("%.2f", Utility.getAverage(sizesPerStep.toArray()) ), w + s/2, startY + 2*(startY/2));

        if(variantCountPerStep.size() > 0) {
            for (int i = 0; i < numVariants; i++) {
                int count = variantCountPerStep.get(variantCountPerStep.size() - 1)[i];
                // draw graph
                Color tmp = imgUtil.getMainImageColor("left_"+Entity.typeNames[1+i]);
                int width = (maxWidth * count) / entities.size();
                int y = startY + 3*(startY/2) + (16*i + 5);
                g.setColor(tmp);
                g.fillRect(w + s/2, y, width, 16);
                g.setColor(Color.black);
                g.drawString("" + count, (w + s/2) + (width+5), y+9);
            }


        }
    }

    @Override
    public Object getVar(int id) {
        return null;
    }

    @Override
    public void shutdown(){
        // todo:
        //  log all data from this run
        shutdownReport();
    }

    public boolean reset(){
        return entities.size() <= 0;
    }

    public String getReportLine(){
        String output = "";

        output += "[" + entities.size() + "] ";
        output += "[" + String.format("%.2f", Utility.getAverage(sizesPerStep.toArray()) ) + "] ";
        if(variantCountPerStep.size() > 0) {
            StringBuilder countOut = new StringBuilder();
            for (int i = 0; i < numVariants; i++) {
                if (countOut.length() > 0)
                    countOut.append("_").append(variantCountPerStep.get(variantCountPerStep.size() - 1)[i]);
                else
                    countOut.append(variantCountPerStep.get(variantCountPerStep.size() - 1)[i]);
            }
            output += "[" + countOut.toString() + "]";
        }

        return output;
    }

    public void shutdownReport(){
        entityReport.closeReport();
    }

    //
    public void databasePush(){
        // TODO: setup connecting to the PI, this will prep files for data transfer
        //  - the PI will receive files and process them
        //  - Once files have been preped it will connect to the database
    }
}

