package org.luna.core.entity.variants;

import org.luna.core.entity.Entity;
import org.luna.core.util.Utility;


public class MutationC extends Entity {

    public MutationC(int world_scale, int[] gps){
        super(world_scale, gps);
    }

    protected void setStats(){
        //hp, maxHp, xp, maxXp, lvl, dmg, speed, sense, energy, maxEnergy
        stats = new short[]{10,10,0,10,0,1,2,5,10,10};
        this.deathChance = .1f;
        this.replicationChance = .19f;
        type = 3;
    }

    public Entity makeEntity(){
        if(Utility.getRnd().nextFloat() < .15)
            return new MutationD(getScale(), new int[]{getGps()[0], getGps()[1], getGps()[2]});
        return new MutationC(getScale(), new int[]{getGps()[0], getGps()[1], getGps()[2]});
    }
}
