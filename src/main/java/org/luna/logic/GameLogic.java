package org.luna.logic;

import org.luna.core.reporting.Report;
import org.luna.core.util.Utility;

import java.awt.*;

// This guy will be where my game logic is held
public class GameLogic {

    private MasterManager manager;
    private int simCount;
    private int step;
    private final int maxSteps = (60) * 30; // 30 seconds
    private final int turnStep = 30; // how many steps makes one turn (30 is good, 15 is a little harder on my mac)

    private int h, w, s;
    private static Color shadow = new Color(0,0,0, 54);

    private Report simReport;

    public GameLogic(int HEIGHT, int WIDTH, int world_scale){
        this.h = HEIGHT;
        this.w = WIDTH;
        this.s = world_scale;
        System.out.println("Making game logic");
        manager = new MasterManager(HEIGHT, WIDTH, world_scale, turnStep);
        step = 0;
        simCount = 0;

        Utility.deleteFolder("./logs/simReports/");
        simReport = new Report("./logs/simReports/sim_report.txt");
        simReport.write("{");
        // will add
    }

    public void update(int fps){
        // TODO
        manager.update(step, 0);
        if(step % turnStep == 0){
            simReport.write("(" + step + " " + manager.getReportLine() + "),");
        }
        step++;

        if(step > maxSteps || manager.reset() || fps < 10){ // reset every minute
            manager.databasePush();
            manager.shutdown();
            manager.resetEntities();
            try{
                Thread.sleep(10);
            }catch (Exception ex){
                ex.printStackTrace();
            }
            step = 0;
            simCount++;
            simReport.write("}\n{");

        }
    }

    public void render(Graphics2D g){
        // TODO
        manager.render(0, step, g);

        int maxWidth = s*3;
        g.setColor(shadow);
        g.fillRect(w + s/2, s/4 , maxWidth, s/4);
        g.setColor(Color.black);
        int barWidht = (maxWidth * step) / maxSteps;
        g.fillRect(w + s/2, s/4 , barWidht, s/4);
        g.drawString(" # " + simCount, w + (s/2) + (maxWidth + s/4), s/2);
    }

    public void registerInput(){

    }

    public void shutdown(){
        simReport.closeReport();
    }
    //
}
