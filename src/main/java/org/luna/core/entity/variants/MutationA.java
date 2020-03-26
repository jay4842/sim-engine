package org.luna.core.entity.variants;

import org.luna.core.entity.Entity;
import org.luna.core.util.SimUtility;


public class MutationA extends Entity {

    public MutationA(int world_scale, int[] gps, int sim){
        super(world_scale, gps, sim);
    }

    protected void setStats(){
        short lifeSpan = 120;
        replicationAge = (short)(lifeSpan/3); // once an entity is 1/3 through its life it can replicate
        //hp, maxHp, xp, maxXp, lvl, dmg, speed, sense, energy, maxEnergy, lifeSpanInTurns, replications, TODO: add strength
        stats = new short[]{10,10,0,10,0,1,2,5,10,10,lifeSpan,2};
        this.deathChance = .15f;
        this.replicationChance = .15f;
        baseEnergyCost = 0.3f;
        refreshStep = 5;
        type = 1;
    }

    public Entity makeEntity(){
        if(SimUtility.getRnd().nextFloat() < .3)
            return new MutationB(getScale(), new int[]{getGps()[0], getGps()[1], getGps()[2]}, getSimId());
        return new MutationA(getScale(), new int[]{getGps()[0], getGps()[1], getGps()[2]}, getSimId());
    }
}
