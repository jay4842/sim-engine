package org.luna.logic.service;

import org.luna.core.entity.Entity;
import org.luna.core.entity.variants.MutationA;
import org.luna.core.map.LunaMap;
import org.luna.core.map.Tile;
import org.luna.core.reporting.Report;
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

    private static Utility utility = new Utility();
    public static List<Entity> entities;
    public static List<List<Integer[]>> entityRef;
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
        entities = new ArrayList<>();
        entityRef = new ArrayList<>();
        sizesPerStep = new ArrayList<>();
        for(int i = 0; i < numMaps; i++)
            entityRef.add(new ArrayList<>());
        // spawn some entities
        int spawns = 20;
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

        if(step % turnSize == 0)
            entityReport.write(step + " ");
        List<Entity> addBuffer = new ArrayList<>();
        Integer[] count = new Integer[numVariants];
        for(int i = 0; i < numVariants; i++)
            count[i] = 0;
        for(int i = 0; i < entities.size(); i++){
            entities.get(i).update(step, turnSize, map);
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
                addBuffer.add(entities.get(i).makeEntity());
            }else if(entities.get(i).isDead() && step % turnSize == 0 && step > 0){
                entities.get(i).deleteSelfFromRef();
                entities.remove(i);
                i--;
            }
        }

        if(step % turnSize == 0)
            entityReport.write("\n");

        entities.addAll(addBuffer);
        if(step % turnSize == 0) {
            int alive = entities.size();
            sizesPerStep.add(alive);
            variantCountPerStep.add(count);
        }
        return null;
    }

    private Entity makeEntity(int map){
        int x = Utility.getRnd().nextInt(w-s);
        int y = Utility.getRnd().nextInt(h-s);
        return new MutationA(s, new int[]{y,x,map});
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
        int startY = s;
        g.drawString("Step      : " + step, w + s/2, startY);
        g.drawString("Entities  : " + entities.size(), w + s/2, startY + (startY/2));
        g.drawString("avg       : " + String.format("%.2f", Utility.getAverage(sizesPerStep.toArray()) ), w + s/2, startY + 2*(startY/2));

        if(variantCountPerStep.size() > 0) {
            StringBuilder countOut = new StringBuilder();
            for (int i = 0; i < numVariants; i++)
                countOut.append(" ").append(variantCountPerStep.get(variantCountPerStep.size() - 1)[i]);
            g.drawString("Variants :" + countOut.toString(), w + s/2, startY + 3*(startY/2));
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

    public void databasePush(){
        // TODO: write data from the current log file
        //  - closes the log before sending it to the DBO
    }
}

