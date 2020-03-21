package org.luna.core.entity.variants;

import org.luna.core.entity.Entity;
import org.luna.core.util.Utility;

public class MutationB extends Entity {

    MutationB(int world_scale, int[] gps, int sim){
        super(world_scale, gps, sim);
    }

    protected void setStats(){
        short lifeSpan = (short)(Utility.getRnd().nextInt(6) + 120);
        replicationAge = (short)(lifeSpan/3); // once an entity is 1/3 through its life it can replicate
        //hp, maxHp, xp, maxXp, lvl, dmg, speed, sense, energy, maxEnergy, lifeSpanInTurns, replications, TODO: add strength
        stats = new short[]{10,10,0,10,0,1,2,5,13,13,lifeSpan,2};
        this.deathChance = .12f;
        this.replicationChance = .18f;
        baseEnergyCost = 0.25f;
        refreshStep = 6;
        type = 2;
    }


    public Entity makeEntity(){
        if(Utility.getRnd().nextFloat() < .2)
            return new MutationC(getScale(), new int[]{getGps()[0], getGps()[1], getGps()[2]}, getSimId());
        return new MutationB(getScale(), new int[]{getGps()[0], getGps()[1], getGps()[2]}, getSimId());
    }
}
