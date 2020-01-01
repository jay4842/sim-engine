package org.luna.core.entity.variants;

import org.luna.core.entity.Entity;
import org.luna.core.util.Utility;


public class MutationA extends Entity {

    public MutationA(int world_scale, int[] gps){
        super(world_scale, gps);
    }

    protected void setStats(){
        //hp, maxHp, xp, maxXp, lvl, dmg, speed, sense, energy, maxEnergy
        stats = new short[]{10,10,0,10,0,1,2,5,10,10};
        this.deathChance = .15f;
        this.replicationChance = .15f;
        type = 1;
    }

    public Entity makeEntity(){
        if(Utility.getRnd().nextFloat() < .3)
            return new MutationB(getScale(), new int[]{getGps()[0], getGps()[1], getGps()[2]});
        return new MutationA(getScale(), new int[]{getGps()[0], getGps()[1], getGps()[2]});
    }
}
