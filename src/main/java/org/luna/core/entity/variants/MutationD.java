package org.luna.core.entity.variants;

import org.luna.core.entity.Entity;
import org.luna.core.util.SimUtility;


public class MutationD extends Entity {

    MutationD(int world_scale, int[] gps, int sim){
        super(world_scale, gps, sim);
    }

    protected void setStats(){
        short lifeSpan = (short)(SimUtility.getRnd().nextInt(5) + 140);
        replicationAge = (short)(lifeSpan/3); // once an entity is 1/3 through its life it can replicate
        //hp, maxHp, xp, maxXp, lvl, dmg, speed, sense, energy, maxEnergy, lifeSpanInTurns, replications, TODO: add strength
        stats = new short[]{10,10,0,10,0,1,2,5,20,20,lifeSpan,3};
        this.deathChance = .13f;
        this.replicationChance = .21f;
        baseEnergyCost = 0.2f;
        refreshStep = 9;
        type = 4;
    }

    public Entity makeEntity(){
        if(SimUtility.getRnd().nextFloat() < .32)
            return new MutationC(getScale(), new int[]{getGps()[0], getGps()[1], getGps()[2]}, getSimId());
        return new MutationD(getScale(), new int[]{getGps()[0], getGps()[1], getGps()[2]}, getSimId());
    }
}
