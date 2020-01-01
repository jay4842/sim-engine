package org.luna.core.entity.variants;

import org.luna.core.entity.Entity;
import org.luna.core.util.Utility;


public class MutationD extends Entity {

    public MutationD(int world_scale, int[] gps){
        super(world_scale, gps);
    }

    protected void setStats(){
        //hp, maxHp, xp, maxXp, lvl, dmg, speed, sense, energy, maxEnergy
        stats = new short[]{10,10,0,10,0,1,2,5,10,10};
        this.deathChance = .13f;
        this.replicationChance = .21f;
        type = 4;
    }

    public Entity makeEntity(){
        if(Utility.getRnd().nextFloat() < .32)
            return new MutationC(getScale(), new int[]{getGps()[0], getGps()[1], getGps()[2]});
        return new MutationD(getScale(), new int[]{getGps()[0], getGps()[1], getGps()[2]});
    }
}
