package org.luna.core.entity.variants;

import org.luna.core.entity.Entity;
import org.luna.core.util.Utility;

public class MutationB extends Entity {

    public MutationB(int world_scale, int[] gps){
        super(world_scale, gps);
    }

    protected void setStats(){
        //hp, maxHp, xp, maxXp, lvl, dmg, speed, sense, energy, maxEnergy
        stats = new short[]{10,10,0,10,0,1,2,5,10,10};
        this.deathChance = .12f;
        this.replicationChance = .18f;
        type = 2;
    }


    public Entity makeEntity(){
        if(Utility.getRnd().nextFloat() < .2)
            return new MutationC(getScale(), new int[]{getGps()[0], getGps()[1], getGps()[2]});
        return new MutationB(getScale(), new int[]{getGps()[0], getGps()[1], getGps()[2]});
    }
}
